package de.bund.digitalservice.ris.caselaw.domain;

import de.bund.digitalservice.ris.caselaw.domain.validator.SingleNormConstraint;
import lombok.Builder;

@SingleNormConstraint
@Builder
public record SingleNormValidationInfo(String singleNorm, String normAbbreviation) {}
