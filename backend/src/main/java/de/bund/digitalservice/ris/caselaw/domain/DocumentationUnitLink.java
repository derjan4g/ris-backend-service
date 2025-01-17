package de.bund.digitalservice.ris.caselaw.domain;

import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentationUnitLink(
    UUID parentDocumentationUnitUuid,
    UUID childDocumentationUnitUuid,
    DocumentationUnitLinkType type) {}
