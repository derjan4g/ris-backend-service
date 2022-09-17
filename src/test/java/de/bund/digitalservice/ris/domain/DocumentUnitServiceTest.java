package de.bund.digitalservice.ris.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(SpringExtension.class)
@Import(DocumentUnitService.class)
@TestPropertySource(properties = "otc.obs.bucket-name:testBucket")
class DocumentUnitServiceTest {
  private static final UUID TEST_UUID = UUID.fromString("88888888-4444-4444-4444-121212121212");
  private static final String RECEIVER_ADDRESS = "test@exporter.neuris";
  @SpyBean private DocumentUnitService service;

  @MockBean private DocumentUnitRepository repository;

  @MockBean private DocumentUnitListEntryRepository listEntryRepository;

  @MockBean private DocumentNumberCounterRepository counterRepository;

  @MockBean private PreviousDecisionRepository previousDecisionRepository;

  @MockBean private S3AsyncClient s3AsyncClient;

  @MockBean private EmailPublishService publishService;

  @Test
  void testGenerateNewDocumentUnit() {
    when(repository.save(any(DocumentUnitDTO.class))).thenReturn(Mono.just(DocumentUnitDTO.EMPTY));
    when(counterRepository.getDocumentNumberCounterEntry())
        .thenReturn(Mono.just(DocumentNumberCounter.buildInitial()));
    when(counterRepository.save(any(DocumentNumberCounter.class)))
        .thenReturn(Mono.just(DocumentNumberCounter.buildInitial()));
    // Can we use a captor to check if the document number was correctly created?
    // The chicken-egg-problem is, that we are dictating what happens when
    // repository.save(), so we can't just use a captor at the same time

    StepVerifier.create(service.generateNewDocumentUnit(DocumentUnitCreationInfo.EMPTY))
        .expectNextCount(1) // That it's a DocumentUnit is given by the generic type..
        .verifyComplete();
    verify(repository).save(any(DocumentUnitDTO.class));
  }

  // @Test public void testGenerateNewDocumentUnit_withException() {}

  @Test
  void testAttachFileToDocumentUnit() {
    // given
    var byteBufferFlux = ByteBuffer.wrap(new byte[] {});
    var headerMap = new LinkedMultiValueMap<String, String>();
    headerMap.put("Content-Type", List.of("content/type"));
    headerMap.put("X-Filename", List.of("testfile.docx"));
    var httpHeaders = HttpHeaders.readOnlyHttpHeaders(headerMap);

    var toSave = new DocumentUnitDTO();
    toSave.setUuid(TEST_UUID);
    toSave.setS3path(TEST_UUID.toString());
    toSave.setFiletype("docx");
    toSave.setFilename("testfile.docx");

    var savedDocumentUnit = new DocumentUnitDTO();
    savedDocumentUnit.setUuid(TEST_UUID);
    savedDocumentUnit.setS3path(TEST_UUID.toString());
    savedDocumentUnit.setFiletype("docx");
    when(repository.save(any(DocumentUnitDTO.class))).thenReturn(Mono.just(savedDocumentUnit));
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(savedDocumentUnit));

    doNothing().when(service).checkDocx(any(ByteBuffer.class));
    when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
        .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));

    var putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
    var asyncRequestBodyCaptor = ArgumentCaptor.forClass(AsyncRequestBody.class);

    try (MockedStatic<UUID> mockedUUIDStatic = mockStatic(UUID.class)) {
      mockedUUIDStatic.when(UUID::randomUUID).thenReturn(TEST_UUID);

      // when and then
      StepVerifier.create(service.attachFileToDocumentUnit(TEST_UUID, byteBufferFlux, httpHeaders))
          .consumeNextWith(
              documentUnitDTO -> {
                assertNotNull(documentUnitDTO);
                assertEquals(savedDocumentUnit, documentUnitDTO);
              })
          .verifyComplete();

      verify(s3AsyncClient)
          .putObject(putObjectRequestCaptor.capture(), asyncRequestBodyCaptor.capture());
      assertEquals("testBucket", putObjectRequestCaptor.getValue().bucket());
      assertEquals(TEST_UUID.toString(), putObjectRequestCaptor.getValue().key());
      assertEquals("content/type", putObjectRequestCaptor.getValue().contentType());
      StepVerifier.create(asyncRequestBodyCaptor.getValue())
          .expectNext(ByteBuffer.wrap(new byte[] {}))
          .verifyComplete();
      toSave.setFileuploadtimestamp(savedDocumentUnit.getFileuploadtimestamp());
      verify(repository).save(toSave);
    }
  }

  @Test
  void testRemoveFileFromDocumentUnit() {
    var documentUnitDTOBefore = new DocumentUnitDTO();
    documentUnitDTOBefore.setUuid(TEST_UUID);
    documentUnitDTOBefore.setS3path(TEST_UUID.toString());
    documentUnitDTOBefore.setFilename("testfile.docx");

    var documentUnitDTOAfter = new DocumentUnitDTO();
    documentUnitDTOAfter.setUuid(TEST_UUID);

    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(documentUnitDTOBefore));
    // is the thenReturn ok? Or am I bypassing the actual functionality-test?
    when(repository.save(any(DocumentUnitDTO.class))).thenReturn(Mono.just(documentUnitDTOAfter));
    when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
        .thenReturn(buildEmptyDeleteObjectResponse());

    StepVerifier.create(service.removeFileFromDocumentUnit(TEST_UUID))
        .consumeNextWith(
            documentUnitResponseEntity -> {
              assertNotNull(documentUnitResponseEntity);
              assertEquals(HttpStatus.OK, documentUnitResponseEntity.getStatusCode());
              assertEquals(
                  DocumentUnitBuilder.newInstance()
                      .setDocumentUnitDTO(documentUnitDTOAfter)
                      .build(),
                  documentUnitResponseEntity.getBody());
            })
        .verifyComplete();

    ArgumentCaptor<DocumentUnitDTO> documentUnitDTOCaptor =
        ArgumentCaptor.forClass(DocumentUnitDTO.class);
    verify(repository).save(documentUnitDTOCaptor.capture());
    assertEquals(documentUnitDTOCaptor.getValue(), documentUnitDTOAfter);
  }

  @Test
  void testGenerateNewDocumentUnitAndAttachFile_withExceptionFromBucket() throws S3Exception {
    // given
    var byteBufferFlux = ByteBuffer.wrap(new byte[] {});

    doNothing().when(service).checkDocx(any(ByteBuffer.class));
    when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
        .thenThrow(SdkException.create("exception", null));

    // when and then
    StepVerifier.create(
            service.attachFileToDocumentUnit(TEST_UUID, byteBufferFlux, HttpHeaders.EMPTY))
        .expectErrorMatches(ex -> ex instanceof SdkException)
        .verify();

    verify(s3AsyncClient).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
    verify(repository, times(0)).save(any(DocumentUnitDTO.class));
  }

  @Test
  void testGenerateNewDocumentUnitAndAttachFile_withExceptionFromRepository() {
    // given
    var byteBufferFlux = ByteBuffer.wrap(new byte[] {});

    doNothing().when(service).checkDocx(any(ByteBuffer.class));
    when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
        .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));
    doThrow(new IllegalArgumentException()).when(repository).save(any(DocumentUnitDTO.class));
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(DocumentUnitDTO.EMPTY));

    // when and then
    StepVerifier.create(
            service.attachFileToDocumentUnit(TEST_UUID, byteBufferFlux, HttpHeaders.EMPTY))
        .expectErrorMatches(ex -> ex instanceof IllegalArgumentException)
        .verify();

    verify(s3AsyncClient).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
    verify(repository).save(any(DocumentUnitDTO.class));
  }

  @Test
  void testGetAll() {
    StepVerifier.create(service.getAll())
        .consumeNextWith(Assertions::assertNotNull)
        .verifyComplete();

    verify(listEntryRepository).findAll(Sort.by(Order.desc("documentnumber")));
  }

  @Test
  void testGetByDocumentnumber() {
    when(repository.findByDocumentnumber("ABCDE2022000001"))
        .thenReturn(Mono.just(DocumentUnitDTO.EMPTY));
    when(previousDecisionRepository.findAllByDocumentnumber("ABCDE2022000001"))
        .thenReturn(Flux.just(new PreviousDecision()));
    StepVerifier.create(service.getByDocumentnumber("ABCDE2022000001"))
        .consumeNextWith(
            documentUnitResponseEntity ->
                assertEquals(documentUnitResponseEntity.getBody().getClass(), DocumentUnit.class))
        .verifyComplete();
    verify(repository).findByDocumentnumber("ABCDE2022000001");
  }

  @Nested
  @DisplayName("Test Update DocumentUnit With PreviousDecisions")
  class TestUpdateDocumentUnitWithPreviousDecisions {
    private List<PreviousDecision> previousDecisionsList;
    private List<String> previousDecisionsIdsToDelete;
    private Long count;
    private final String documentNr = "ABCDE2022000001";

    private List<Long> getRemainsIds() {
      return previousDecisionsList.stream().map(previousDecision -> previousDecision.id).toList();
    }

    private Mono<Void> deleteByIds(List<String> ids) {
      previousDecisionsList.removeAll(
          previousDecisionsList.stream()
              .filter(previousDecision -> ids.contains(String.valueOf(previousDecision.id)))
              .toList());
      return null;
    }

    private List<PreviousDecision> saveAll(List<PreviousDecision> pDecisionsList) {
      List<PreviousDecision> pDecisionToInsert =
          pDecisionsList.stream()
              .filter(previousDecision -> previousDecision.id == null)
              .map(
                  previousDecision -> {
                    previousDecision.id = Long.valueOf(++count);
                    return previousDecision;
                  })
              .toList();
      pDecisionToInsert.forEach(previousDecision -> previousDecisionsList.add(previousDecision));
      List<PreviousDecision> pDecisionToUpdate =
          pDecisionsList.stream().filter(previousDecision -> previousDecision.id != null).toList();
      pDecisionToUpdate.forEach(
          decisionToUpdate -> {
            previousDecisionsList =
                new ArrayList<>(
                    previousDecisionsList.stream()
                        .map(
                            pd -> {
                              if (pd.id == decisionToUpdate.id) {
                                return decisionToUpdate;
                              }
                              return pd;
                            })
                        .toList());
          });
      return pDecisionsList;
    }

    @BeforeEach
    void setUp() {
      previousDecisionsIdsToDelete = new ArrayList<>();
      previousDecisionsList = new ArrayList<>();
      previousDecisionsList.add(
          new PreviousDecision(
              1L, "gerTyp 1", "gerOrt 1", "01.01.2022", "aktenzeichen 1", "ABCDE2022000001"));
      previousDecisionsList.add(
          new PreviousDecision(
              2L, "gerTyp 2", "gerOrt 2", "01.02.2022", "aktenzeichen 2", "ABCDE2022000001"));
      previousDecisionsList.add(
          new PreviousDecision(
              3L, "gerTyp 3", "gerOrt 3", "01.03.2022", "aktenzeichen 3", "ABCDE2022000001"));
      previousDecisionsList.add(
          new PreviousDecision(
              4L, "gerTyp 4", "gerOrt 4", "01.04.2022", "aktenzeichen 4", "ABCDE2022000001"));
      previousDecisionsList.add(
          new PreviousDecision(
              5L, "gerTyp 5", "gerOrt 5", "01.05.2022", "aktenzeichen 5", "ABCDE2022000001"));
      count = Long.valueOf(previousDecisionsList.size());
    }

    @Test
    void testGetByDocumentnumberWithPreviousDecisions() {
      when(repository.findByDocumentnumber(documentNr))
          .thenReturn(Mono.just(DocumentUnitDTO.EMPTY));
      when(previousDecisionRepository.findAllByDocumentnumber(documentNr))
          .thenReturn(Flux.fromIterable(previousDecisionsList));
      StepVerifier.create(service.getByDocumentnumber(documentNr))
          .consumeNextWith(
              monoResponse -> {
                assertEquals(monoResponse.getBody().previousDecisions().size(), count);
                PreviousDecision previousDecision =
                    monoResponse.getBody().previousDecisions().get(0);
                assertEquals(previousDecision.id, 1L);
                assertEquals(previousDecision.courtPlace, "gerOrt 1");
                assertEquals(previousDecision.courtType, "gerTyp 1");
                assertEquals(previousDecision.date, "01.01.2022");
                assertEquals(previousDecision.fileNumber, "aktenzeichen 1");
                assertEquals(previousDecision.documentnumber, documentNr);
              })
          .verifyComplete();
      verify(repository).findByDocumentnumber("ABCDE2022000001");
    }

    @Test
    @Disabled(
        "Doesn't work anymore upon introducing DocumentUnit vs. DocumentUnitDTO, but didn't really work beforehand either:"
            + " there was a 'silent' error in the StepVerifier-section that didn't break the test because of the way previousDecision gets injected")
    void testUpdateDocumentUnitWithPreviousDecisionsDelete() {
      previousDecisionsIdsToDelete.add("2");
      previousDecisionsIdsToDelete.add("4");
      var remainPreviousDecision =
          previousDecisionsList.stream()
              .filter(
                  previousDecision -> !previousDecisionsIdsToDelete.contains(previousDecision.id))
              .toList();
      var documentUnitDTO = DocumentUnitDTO.EMPTY.setPreviousDecisions(remainPreviousDecision);
      when(previousDecisionRepository.getAllIdsByDocumentnumber(documentNr))
          .thenReturn(
              Flux.fromIterable(
                  previousDecisionsList.stream()
                      .map(previousDecision -> previousDecision.id)
                      .toList()));
      when(previousDecisionRepository.deleteAllById(previousDecisionsIdsToDelete))
          .thenReturn(deleteByIds(previousDecisionsIdsToDelete));
      when(previousDecisionRepository.saveAll(remainPreviousDecision))
          .thenReturn(Flux.fromIterable(saveAll(remainPreviousDecision)));
      when(repository.save(documentUnitDTO)).thenReturn(Mono.just(documentUnitDTO));

      // TODO replace null when fixing the test
      StepVerifier.create(service.updateDocumentUnit(null))
          .consumeNextWith(
              monoResponse -> {
                assertEquals(previousDecisionsList.size(), 3);
                List<Long> remainIds = getRemainsIds();
                assertTrue(remainIds.contains(1L));
                assertTrue(remainIds.contains(3L));
                assertTrue(remainIds.contains(5L));
                assertFalse(remainIds.contains(2L));
                assertFalse(remainIds.contains(4L));
                // assertEquals(monoResponse.getBody(), documentUnitDTO); TODO reactivate
              })
          .verifyComplete();
      verify(repository).save(documentUnitDTO);
    }

    @Test
    @Disabled(
        "Doesn't work anymore upon introducing DocumentUnit vs. DocumentUnitDTO, but didn't really work beforehand either:"
            + " there was a 'silent' error in the StepVerifier-section that didn't break the test because of the way previousDecision gets injected")
    void testUpdateDocumentUnitWithPreviousDecisionsInsert() {
      var remainPreviousDecision = new ArrayList<>(previousDecisionsList);
      remainPreviousDecision.add(
          new PreviousDecision(
              null, "gerTyp 6", "gerOrt 6", "01.01.2022", "aktenzeichen 6", documentNr));
      remainPreviousDecision.add(
          new PreviousDecision(
              null, "gerTyp 7", "gerOrt 7", "01.01.2022", "aktenzeichen 7", documentNr));
      var documentUnitDTO = DocumentUnitDTO.EMPTY.setPreviousDecisions(remainPreviousDecision);
      when(previousDecisionRepository.getAllIdsByDocumentnumber(documentNr))
          .thenReturn(
              Flux.fromIterable(
                  previousDecisionsList.stream()
                      .map(previousDecision -> previousDecision.id)
                      .toList()));
      when(previousDecisionRepository.deleteAllById(previousDecisionsIdsToDelete))
          .thenReturn(deleteByIds(previousDecisionsIdsToDelete));
      when(previousDecisionRepository.saveAll(remainPreviousDecision))
          .thenReturn(Flux.fromIterable(saveAll(remainPreviousDecision)));
      when(repository.save(documentUnitDTO)).thenReturn(Mono.just(documentUnitDTO));

      // TODO replace null when fixing the test
      StepVerifier.create(service.updateDocumentUnit(null))
          .consumeNextWith(
              monoResponse -> {
                assertEquals(previousDecisionsList.size(), 7);
                List<Long> remainIds = getRemainsIds();
                assertTrue(remainIds.contains(6L));
                assertTrue(remainIds.contains(7L));
                PreviousDecision previousDecision =
                    monoResponse
                        .getBody()
                        .previousDecisions()
                        .get(previousDecisionsList.size() - 2);
                assertEquals(previousDecision.id, 6L);
                assertEquals(previousDecision.courtPlace, "gerOrt 6");
                assertEquals(previousDecision.courtType, "gerTyp 6");
                assertEquals(previousDecision.date, "01.01.2022");
                assertEquals(previousDecision.fileNumber, "aktenzeichen 6");
                previousDecision =
                    monoResponse
                        .getBody()
                        .previousDecisions()
                        .get(previousDecisionsList.size() - 1);
                assertEquals(previousDecision.id, 7L);
                assertEquals(previousDecision.courtPlace, "gerOrt 7");
                assertEquals(previousDecision.courtType, "gerTyp 7");
                assertEquals(previousDecision.date, "01.01.2022");
                assertEquals(previousDecision.fileNumber, "aktenzeichen 7");
                // assertEquals(monoResponse.getBody(), documentUnitDTO); TODO reactivate
              })
          .verifyComplete();
      verify(repository).save(documentUnitDTO);
    }

    @Test
    @Disabled(
        "Doesn't work anymore upon introducing DocumentUnit vs. DocumentUnitDTO, but didn't really work beforehand either:"
            + " there was a 'silent' error in the StepVerifier-section that didn't break the test because of the way previousDecision gets injected")
    void testUpdateDocumentUnitWithPreviousDecisionsUpdate() {
      var remainPreviousDecision = new ArrayList<>(previousDecisionsList);
      remainPreviousDecision.get(0).courtPlace = "new gerOrt";
      remainPreviousDecision.get(0).courtType = "new gerTyp";
      remainPreviousDecision.get(0).date = "30.01.2022";
      remainPreviousDecision.get(0).fileNumber = "new aktenzeichen";
      var documentUnitDTO = DocumentUnitDTO.EMPTY.setPreviousDecisions(remainPreviousDecision);
      when(previousDecisionRepository.getAllIdsByDocumentnumber(documentNr))
          .thenReturn(
              Flux.fromIterable(
                  previousDecisionsList.stream()
                      .map(previousDecision -> previousDecision.id)
                      .toList()));
      when(previousDecisionRepository.deleteAllById(previousDecisionsIdsToDelete))
          .thenReturn(deleteByIds(previousDecisionsIdsToDelete));
      when(previousDecisionRepository.saveAll(remainPreviousDecision))
          .thenReturn(Flux.fromIterable(saveAll(remainPreviousDecision)));
      when(repository.save(documentUnitDTO)).thenReturn(Mono.just(documentUnitDTO));

      // TODO replace null when fixing the test
      StepVerifier.create(service.updateDocumentUnit(null))
          .consumeNextWith(
              monoResponse -> {
                assertEquals(
                    monoResponse.getBody().previousDecisions().size(),
                    previousDecisionsList.size());
                assertTrue(
                    monoResponse.getBody().previousDecisions().containsAll(previousDecisionsList));
                // assertEquals(monoResponse.getBody(), documentUnitDTO); TODO reactivate
              })
          .verifyComplete();
      verify(repository).save(documentUnitDTO);
    }

    @Test
    @Disabled(
        "Doesn't work anymore upon introducing DocumentUnit vs. DocumentUnitDTO, but didn't really work beforehand either:"
            + " there was a 'silent' error in the StepVerifier-section that didn't break the test because of the way previousDecision gets injected")
    void testUpdateDocumentUnitWithPreviousDecisionsInsertUpdateDelete() {
      previousDecisionsIdsToDelete.add("2");
      previousDecisionsIdsToDelete.add("4");
      List<PreviousDecision> remainPreviousDecision =
          new ArrayList<>(
              previousDecisionsList.stream()
                  .filter(
                      previousDecision ->
                          !previousDecisionsIdsToDelete.contains(previousDecision.id))
                  .toList());
      remainPreviousDecision.get(0).courtPlace = "new gerOrt";
      remainPreviousDecision.get(0).courtType = "new gerTyp";
      remainPreviousDecision.get(0).date = "30.01.2022";
      remainPreviousDecision.get(0).fileNumber = "new aktenzeichen";
      remainPreviousDecision.add(
          new PreviousDecision(
              null, "gerTyp 6", "gerOrt 6", "01.01.2022", "aktenzeichen 6", documentNr));
      remainPreviousDecision.add(
          new PreviousDecision(
              null, "gerTyp 7", "gerOrt 7", "01.01.2022", "aktenzeichen 7", documentNr));
      var documentUnitDTO = DocumentUnitDTO.EMPTY.setPreviousDecisions(remainPreviousDecision);
      when(previousDecisionRepository.getAllIdsByDocumentnumber(documentNr))
          .thenReturn(
              Flux.fromIterable(
                  previousDecisionsList.stream()
                      .map(previousDecision -> previousDecision.id)
                      .toList()));
      when(previousDecisionRepository.deleteAllById(previousDecisionsIdsToDelete))
          .thenReturn(deleteByIds(previousDecisionsIdsToDelete));
      when(previousDecisionRepository.saveAll(remainPreviousDecision))
          .thenReturn(Flux.fromIterable(saveAll(remainPreviousDecision)));
      when(repository.save(documentUnitDTO)).thenReturn(Mono.just(documentUnitDTO));

      // TODO replace null when fixing the test
      StepVerifier.create(service.updateDocumentUnit(null))
          .consumeNextWith(
              monoResponse -> {
                assertEquals(previousDecisionsList.size(), 5);
                PreviousDecision previousDecision =
                    monoResponse.getBody().previousDecisions().get(0);
                assertEquals(previousDecision.id, 1L);
                List<Long> remainIds = getRemainsIds();
                assertTrue(remainIds.contains(6L));
                assertTrue(remainIds.contains(7L));
                assertFalse(remainIds.contains(2L));
                assertFalse(remainIds.contains(4L));
                assertEquals(previousDecision.courtPlace, "new gerOrt");
                assertEquals(previousDecision.courtType, "new gerTyp");
                assertEquals(previousDecision.date, "30.01.2022");
                assertEquals(previousDecision.fileNumber, "new aktenzeichen");
                // assertEquals(monoResponse.getBody(), documentUnitDTO); TODO reactivate
              })
          .verifyComplete();
      verify(repository).save(documentUnitDTO);
    }
  }

  @Test
  void testDeleteByUuid_withoutFileAttached() {
    // I think I shouldn't have to insert a specific DocumentUnit object here?
    // But if I don't, the test by itself succeeds, but fails if all tests in this class run
    // something flaky with the repository mock? Investigate this later
    DocumentUnitDTO documentUnitDTO = new DocumentUnitDTO();
    documentUnitDTO.setUuid(TEST_UUID);
    // can we also test that the fileUuid from the DocumentUnit is used? with a captor somehow?
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(documentUnitDTO));
    when(repository.delete(any(DocumentUnitDTO.class))).thenReturn(Mono.just(mock(Void.class)));

    StepVerifier.create(service.deleteByUuid(TEST_UUID))
        .consumeNextWith(
            stringResponseEntity -> {
              assertNotNull(stringResponseEntity);
              assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
              assertEquals("done", stringResponseEntity.getBody());
            })
        .verifyComplete();

    verify(s3AsyncClient, times(0)).deleteObject(any(DeleteObjectRequest.class));
  }

  @Test
  void testDeleteByUuid_withFileAttached() {
    DocumentUnitDTO documentUnitDTO = new DocumentUnitDTO();
    documentUnitDTO.setUuid(TEST_UUID);
    documentUnitDTO.setS3path(TEST_UUID.toString());
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(documentUnitDTO));
    when(repository.delete(any(DocumentUnitDTO.class))).thenReturn(Mono.just(mock(Void.class)));
    when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
        .thenReturn(buildEmptyDeleteObjectResponse());

    StepVerifier.create(service.deleteByUuid(TEST_UUID))
        .consumeNextWith(
            stringResponseEntity -> {
              assertNotNull(stringResponseEntity);
              assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
              assertEquals("done", stringResponseEntity.getBody());
            })
        .verifyComplete();

    verify(s3AsyncClient, times(1)).deleteObject(any(DeleteObjectRequest.class));
  }

  @Test
  void testDeleteByUuid_withoutFileAttached_withExceptionFromBucket() {
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(DocumentUnitDTO.EMPTY));
    when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
        .thenThrow(SdkException.create("exception", null));

    StepVerifier.create(service.deleteByUuid(TEST_UUID))
        .consumeNextWith(
            stringResponseEntity -> {
              assertNotNull(stringResponseEntity);
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());
              assertEquals("Couldn't delete the DocumentUnit", stringResponseEntity.getBody());
            })
        .verifyComplete();
  }

  @Test
  void testDeleteByUuid_withoutFileAttached_withExceptionFromRepository() {
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(DocumentUnitDTO.EMPTY));
    doThrow(new IllegalArgumentException()).when(repository).delete(DocumentUnitDTO.EMPTY);

    StepVerifier.create(service.deleteByUuid(TEST_UUID))
        .consumeNextWith(
            stringResponseEntity -> {
              assertNotNull(stringResponseEntity);
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.getStatusCode());
              assertEquals("Couldn't delete the DocumentUnit", stringResponseEntity.getBody());
            })
        .verifyComplete();
  }

  @Test
  @Disabled(
      "This test fails not when run alone, but when all tests are run. "
          + "The issue seems to be, that there are no previousDecisions. This needs to be "
          + "investigated and fixed once the other previousDecisions tests are getting fixed - "
          + "or when the previousDecision logic is being rewritten.")
  void testUpdateDocumentUnit() {
    var documentUnitDTO = DocumentUnitDTO.EMPTY;
    when(repository.save(documentUnitDTO)).thenReturn(Mono.just(documentUnitDTO));
    DocumentUnit documentUnit =
        DocumentUnitBuilder.newInstance().setDocumentUnitDTO(documentUnitDTO).build();
    StepVerifier.create(service.updateDocumentUnit(documentUnit))
        .consumeNextWith(
            documentUnitResponseEntity ->
                assertEquals(documentUnitResponseEntity.getBody(), documentUnit))
        .verifyComplete();
    verify(repository).save(documentUnitDTO);
  }

  @Test
  void testPublishByEmail() {
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(DocumentUnitDTO.EMPTY));
    XmlMail xmlMail =
        new XmlMail(
            1L,
            123L,
            "receiver address",
            "subject",
            "xml",
            "200",
            "status messages",
            "filename",
            null);
    when(publishService.publish(DocumentUnitDTO.EMPTY, RECEIVER_ADDRESS))
        .thenReturn(Mono.just(new XmlMailResponse(TEST_UUID, xmlMail)));
    when(previousDecisionRepository.findAllByDocumentnumber(DocumentUnitDTO.EMPTY.documentnumber))
        .thenReturn(Flux.just(new PreviousDecision()));
    StepVerifier.create(service.publishAsEmail(TEST_UUID, RECEIVER_ADDRESS))
        .consumeNextWith(
            mailResponse ->
                assertThat(mailResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(new XmlMailResponse(TEST_UUID, xmlMail)))
        .verifyComplete();
    verify(repository).findByUuid(TEST_UUID);
    verify(publishService).publish(DocumentUnitDTO.EMPTY, RECEIVER_ADDRESS);
  }

  @Test
  void testPublishByEmail_withoutDocumentUnitForUuid() {
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.empty());

    StepVerifier.create(service.publishAsEmail(TEST_UUID, RECEIVER_ADDRESS)).verifyComplete();
    verify(repository).findByUuid(TEST_UUID);
    verify(publishService, never()).publish(DocumentUnitDTO.EMPTY, RECEIVER_ADDRESS);
  }

  @Test
  void testGetLastPublishedXmlMail() {
    DocumentUnitDTO documentUnit = new DocumentUnitDTO();
    documentUnit.setId(123L);
    XmlMail xmlMail =
        new XmlMail(
            1L, 123L, "receiver address", "subject", "xml", "200", "message", "filename", null);
    when(repository.findByUuid(TEST_UUID)).thenReturn(Mono.just(documentUnit));
    when(publishService.getLastPublishedXml(123L, TEST_UUID))
        .thenReturn(Mono.just(new XmlMailResponse(TEST_UUID, xmlMail)));

    StepVerifier.create(service.getLastPublishedXmlMail(TEST_UUID))
        .consumeNextWith(
            xmlMailResponse ->
                assertThat(xmlMailResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(new XmlMailResponse(TEST_UUID, xmlMail)))
        .verifyComplete();
    verify(repository).findByUuid(TEST_UUID);
    verify(publishService).getLastPublishedXml(123L, TEST_UUID);
  }

  private CompletableFuture<DeleteObjectResponse> buildEmptyDeleteObjectResponse() {
    return CompletableFuture.completedFuture(DeleteObjectResponse.builder().build());
  }
}
