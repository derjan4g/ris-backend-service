package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "document_unit_norm")
public class JPANormDTO {
  @Id Long id;

  @Column(name = "document_unit_id")
  Long documentUnitId;

  @Column(name = "single_norm")
  String singleNorm;

  @Column(name = "date_of_version")
  Instant dateOfVersion;

  @Column(name = "date_of_relevance")
  String dateOfRelevance;

  @OneToOne
  @JoinColumn(name = "norm_abbreviation_uuid")
  JPANormAbbreviationDTO normAbbreviation;
}
