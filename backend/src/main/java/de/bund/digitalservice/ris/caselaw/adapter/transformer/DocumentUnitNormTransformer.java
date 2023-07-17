package de.bund.digitalservice.ris.caselaw.adapter.transformer;

import de.bund.digitalservice.ris.caselaw.adapter.database.jpa.JPANormDTO;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnitNorm;

public class DocumentUnitNormTransformer {
  private DocumentUnitNormTransformer() {}

  public static DocumentUnitNorm transformToDomain(JPANormDTO normDTO) {
    return DocumentUnitNorm.builder()
        .normAbbreviation(NormAbbreviationTransformer.transformDTO(normDTO.getNormAbbreviation()))
        .singleNorm(normDTO.getSingleNorm())
        .dateOfVersion(normDTO.getDateOfVersion())
        .dateOfRelevance(normDTO.getDateOfRelevance())
        .build();
  }
}
