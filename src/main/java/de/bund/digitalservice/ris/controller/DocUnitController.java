package de.bund.digitalservice.ris.controller;

import de.bund.digitalservice.ris.datamodel.DocUnit;
import de.bund.digitalservice.ris.service.DocUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping(value = "upload")
    public Mono<ResponseEntity<DocUnit>> uploadFile(@RequestPart("fileToUpload") FilePart filePart) {
        log.info("uploaded file name {}", filePart.filename());

        return service.generateNewDocUnit(filePart);
    }
}
