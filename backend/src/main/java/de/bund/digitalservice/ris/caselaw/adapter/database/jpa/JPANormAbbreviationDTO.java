package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.DocumentTypeNewDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.RegionDTO;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("norm_abbreviation")
public class JPANormAbbreviationDTO {
  @Id UUID id;
  String abbreviation;
  LocalDate decisionDate;
  Integer documentId;
  String documentNumber;
  String officialLetterAbbreviation;
  String officialLongTitle;
  String officialShortTitle;
  Character source;
  @OneToMany() List<DocumentTypeNewDTO> documentTypes;
  @OneToMany() List<RegionDTO> regions;
}
