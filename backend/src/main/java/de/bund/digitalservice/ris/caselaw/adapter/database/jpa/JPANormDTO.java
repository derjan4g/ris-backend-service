package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("document_unit_norm")
public class JPANormDTO {
  @Id Long id;
  Long documentUnitId;
  UUID normAbbreviationUuid;
  String singleNorm;
  Instant dateOfVersion;
  String dateOfRelevance;
  @OneToOne JPANormAbbreviationDTO normAbbreviation;
}
