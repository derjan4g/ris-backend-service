package de.bund.digitalservice.ris.controller;

import de.bund.digitalservice.ris.datamodel.DocUnit;
import de.bund.digitalservice.ris.service.DocUnitService;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/docunit")
@Slf4j
public class DocUnitController {
  private final DocUnitService service;

  public DocUnitController(DocUnitService service) {
    Assert.notNull(service, "DocUnitService is null");

    this.service = service;
  }

  @PostMapping(value = "/")
  public Mono<ResponseEntity<DocUnit>> uploadFile(
      @RequestBody Flux<ByteBuffer> byteBufferFlux, @RequestHeader HttpHeaders httpHeaders) {

    return service.generateNewDocUnit(byteBufferFlux, httpHeaders);
  }

  @GetMapping(value = "/")
  public Mono<ResponseEntity<Flux<DocUnit>>> getAll() {
    log.info("All DocUnits were requested");

    return service.getAll();
  }

  @GetMapping(value = "/{id}")
  public Mono<ResponseEntity<Mono<DocUnit>>> getById(@PathVariable int id) {
    return service.getById(id);
  }

  @PostMapping(value = "/{id}")
  public Mono<ResponseEntity<Boolean>> updateById(
      @PathVariable int id, @RequestBody DocUnit docUnit) {
    // TODO
    System.out.println(id + ": " + docUnit.toString());
    return Mono.just(ResponseEntity.ok(true));
  }
}
