package de.bund.digitalservice.ris.caselaw.adapter.transformer;

import de.bund.digitalservice.ris.caselaw.adapter.database.jpa.JPARegionDTO;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.Region;

public class RegionTransformer {
  private RegionTransformer() {}

  public static Region transformDTO(JPARegionDTO regionDTO) {
    return Region.builder().code(regionDTO.getCode()).label(regionDTO.getLabel()).build();
  }
}
