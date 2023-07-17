package de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc;

import de.bund.digitalservice.ris.caselaw.adapter.database.jpa.JPAFieldOfLawRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.DatabaseFieldOfLawRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawKeywordRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawLinkDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawLinkRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.NormRepository;
import de.bund.digitalservice.ris.caselaw.adapter.transformer.FieldOfLawTransformer;
import de.bund.digitalservice.ris.caselaw.domain.FieldOfLawRepository;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.fieldoflaw.FieldOfLaw;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PostgresFieldOfLawRepositoryImpl implements FieldOfLawRepository {

  DatabaseFieldOfLawRepository databaseFieldOfLawRepository;
  JPAFieldOfLawRepository jPAFieldOfLawRepository;
  FieldOfLawKeywordRepository fieldOfLawKeywordRepository;
  NormRepository normRepository;
  FieldOfLawLinkRepository fieldOfLawLinkRepository;
  DatabaseDocumentUnitRepository databaseDocumentUnitRepository;
  DatabaseDocumentUnitFieldsOfLawRepository databaseDocumentUnitFieldsOfLawRepository;

  public PostgresFieldOfLawRepositoryImpl(
      DatabaseFieldOfLawRepository databaseFieldOfLawRepository,
      FieldOfLawKeywordRepository fieldOfLawKeywordRepository,
      NormRepository normRepository,
      FieldOfLawLinkRepository fieldOfLawLinkRepository,
      DatabaseDocumentUnitRepository databaseDocumentUnitRepository,
      DatabaseDocumentUnitFieldsOfLawRepository databaseDocumentUnitFieldsOfLawRepository,
      JPAFieldOfLawRepository jPAFieldOfLawRepository) {

    this.databaseFieldOfLawRepository = databaseFieldOfLawRepository;
    this.fieldOfLawKeywordRepository = fieldOfLawKeywordRepository;
    this.normRepository = normRepository;
    this.fieldOfLawLinkRepository = fieldOfLawLinkRepository;
    this.databaseDocumentUnitRepository = databaseDocumentUnitRepository;
    this.databaseDocumentUnitFieldsOfLawRepository = databaseDocumentUnitFieldsOfLawRepository;
    this.jPAFieldOfLawRepository = jPAFieldOfLawRepository;
  }

  @Override
  public Flux<FieldOfLaw> findAllByOrderByIdentifierAsc(Pageable pageable) {
    return databaseFieldOfLawRepository
        .findAllByOrderByIdentifierAsc(pageable)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public Mono<FieldOfLaw> findByIdentifier(String identifier) {
    return Mono.just(
        FieldOfLawTransformer.transformJPADTOToDomain(
            jPAFieldOfLawRepository.findFirstByIdentifier(identifier)));
  }

  @Override
  public Mono<FieldOfLaw> findParentByChild(FieldOfLaw child) {
    return databaseFieldOfLawRepository
        .findByIdentifier(child.identifier())
        .flatMap(
            childDTO -> {
              if (childDTO.getParentId() != null) {
                return databaseFieldOfLawRepository.findById(childDTO.getParentId());
              }
              return Mono.just(childDTO);
            })
        .flatMap(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> getTopLevelNodes() {
    return databaseFieldOfLawRepository
        .findAllByParentIdOrderByIdentifierAsc(null)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findAllByParentIdentifierOrderByIdentifierAsc(String identifier) {
    return databaseFieldOfLawRepository
        .findAllByParentIdentifierOrderByIdentifierAsc(identifier)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  private Mono<FieldOfLawDTO> injectKeywords(FieldOfLawDTO fieldOfLawDTO) {
    return fieldOfLawKeywordRepository
        .findAllByFieldOfLawIdOrderByValueAsc(fieldOfLawDTO.getId())
        .collectList()
        .map(
            keywords -> {
              fieldOfLawDTO.setKeywords(keywords);
              return fieldOfLawDTO;
            });
  }

  private Mono<FieldOfLawDTO> injectNorms(FieldOfLawDTO fieldOfLawDTO) {
    return normRepository
        .findAllByFieldOfLawIdOrderByAbbreviationAscSingleNormDescriptionAsc(fieldOfLawDTO.getId())
        .collectList()
        .map(
            norms -> {
              fieldOfLawDTO.setNorms(norms);
              return fieldOfLawDTO;
            });
  }

  private Mono<FieldOfLawDTO> injectLinkedFields(FieldOfLawDTO fieldOfLawDTO) {
    return fieldOfLawLinkRepository
        .findAllByFieldOfLawId(fieldOfLawDTO.getId())
        .map(FieldOfLawLinkDTO::getLinkedFieldOfLawId)
        .flatMap(linkedFieldOfLawId -> databaseFieldOfLawRepository.findById(linkedFieldOfLawId))
        .collectList()
        .map(
            fieldOfLawDTOS -> {
              fieldOfLawDTO.setLinkedFieldsOfLaw(fieldOfLawDTOS);
              return fieldOfLawDTO;
            });
  }

  @Override
  public Mono<List<FieldOfLaw>> findAllForDocumentUnit(UUID documentUnitUuid) {
    return databaseDocumentUnitRepository
        .findByUuid(documentUnitUuid)
        .map(DocumentUnitDTO::getId)
        .flatMapMany(
            documentUnitId ->
                databaseDocumentUnitFieldsOfLawRepository.findAllByDocumentUnitId(documentUnitId))
        .map(DocumentUnitFieldsOfLawDTO::fieldOfLawId)
        .flatMap(databaseFieldOfLawRepository::findById)
        .flatMap(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain)
        .collectList()
        .map(
            fieldOfLawList ->
                fieldOfLawList.stream()
                    .sorted(Comparator.comparing(FieldOfLaw::identifier))
                    .toList());
  }

  @Override
  public Mono<List<FieldOfLaw>> addFieldOfLawToDocumentUnit(
      UUID documentUnitUuid, String identifier) {
    Mono<Long> documentUnitDTOId =
        databaseDocumentUnitRepository.findByUuid(documentUnitUuid).map(DocumentUnitDTO::getId);

    Mono<Long> fieldOfLawDTOId =
        databaseFieldOfLawRepository
            .findByIdentifier(identifier)
            .mapNotNull(FieldOfLawDTO::getId)
            .defaultIfEmpty(-1L);

    return documentUnitDTOId
        .zipWith(fieldOfLawDTOId)
        .flatMap(
            t -> {
              if (t.getT2() == -1L) {
                return getLinkedFieldsOfLaw(t.getT1());
              }

              return databaseDocumentUnitFieldsOfLawRepository
                  .findByDocumentUnitIdAndFieldOfLawId(t.getT1(), t.getT2())
                  .switchIfEmpty(linkFieldOfLawToDocumentUnit(t.getT1(), t.getT2()))
                  .map(DocumentUnitFieldsOfLawDTO::documentUnitId)
                  .then(getLinkedFieldsOfLaw(t.getT1()));
            });
  }

  private Mono<List<FieldOfLaw>> getLinkedFieldsOfLaw(Long documentUnitId) {
    return databaseDocumentUnitFieldsOfLawRepository
        .findAllByDocumentUnitId(documentUnitId)
        .map(DocumentUnitFieldsOfLawDTO::fieldOfLawId)
        .flatMapSequential(databaseFieldOfLawRepository::findById)
        .map(FieldOfLawTransformer::transformToDomain)
        .collectList()
        .map(
            fieldOfLawList ->
                fieldOfLawList.stream()
                    .sorted(Comparator.comparing(FieldOfLaw::identifier))
                    .toList());
  }

  private Mono<DocumentUnitFieldsOfLawDTO> linkFieldOfLawToDocumentUnit(
      Long documentUnitId, Long fieldOfLawId) {

    DocumentUnitFieldsOfLawDTO documentUnitFieldOfLaw =
        DocumentUnitFieldsOfLawDTO.builder()
            .documentUnitId(documentUnitId)
            .fieldOfLawId(fieldOfLawId)
            .build();
    return databaseDocumentUnitFieldsOfLawRepository.save(documentUnitFieldOfLaw);
  }

  @Override
  public Mono<List<FieldOfLaw>> removeFieldOfLawToDocumentUnit(
      UUID documentUnitUuid, String identifier) {
    Mono<Long> documentUnitDTOId =
        databaseDocumentUnitRepository.findByUuid(documentUnitUuid).map(DocumentUnitDTO::getId);

    Mono<Long> fieldOfLawDTOId =
        databaseFieldOfLawRepository
            .findByIdentifier(identifier)
            .mapNotNull(FieldOfLawDTO::getId)
            .defaultIfEmpty(-1L);

    return documentUnitDTOId
        .zipWith(fieldOfLawDTOId)
        .flatMap(
            t ->
                databaseDocumentUnitFieldsOfLawRepository
                    .findByDocumentUnitIdAndFieldOfLawId(t.getT1(), t.getT2())
                    .flatMap(dto -> databaseDocumentUnitFieldsOfLawRepository.delete(dto))
                    .then(getLinkedFieldsOfLaw(t.getT1())));
  }

  @Override
  public Mono<Long> count() {
    return databaseFieldOfLawRepository.count();
  }

  @Override
  public Flux<FieldOfLaw> findBySearchTerms(String[] searchTerms) {
    return databaseFieldOfLawRepository
        .findBySearchTerms(searchTerms)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findByNormStr(String normStr) {
    return databaseFieldOfLawRepository
        .findByNormStr(normStr)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findByNormStrAndSearchTerms(String normStr, String[] searchTerms) {
    return databaseFieldOfLawRepository
        .findByNormStrAndSearchTerms(normStr, searchTerms)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> getAllLimitedOrderByIdentifierLength() {
    return databaseFieldOfLawRepository
        .getAllLimitedOrderByIdentifierLength()
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findByIdentifierSearch(String searchStr) {
    return databaseFieldOfLawRepository
        .findByIdentifierSearch(searchStr)
        .flatMapSequential(this::injectAdditionalInformation)
        .map(FieldOfLawTransformer::transformToDomain);
  }

  private Mono<FieldOfLawDTO> injectAdditionalInformation(FieldOfLawDTO fieldOfLawDTO) {
    return injectKeywords(fieldOfLawDTO)
        .flatMap(this::injectNorms)
        .flatMap(this::injectLinkedFields);
  }
}
