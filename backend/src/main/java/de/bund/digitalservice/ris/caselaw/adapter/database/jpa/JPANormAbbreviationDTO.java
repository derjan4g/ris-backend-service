package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity(name = "norm_abbreviation")
public class JPANormAbbreviationDTO {
  @Id UUID id;
  String abbreviation;

  @Column(name = "decision_date")
  LocalDate decisionDate;

  @Column(name = "document_id")
  Integer documentId;

  @Column(name = "document_number")
  String documentNumber;

  @Column(name = "official_letter_abbreviation")
  String officialLetterAbbreviation;

  @Column(name = "official_long_title")
  String officialLongTitle;

  @Column(name = "official_short_title")
  String officialShortTitle;

  Character source;

  @ManyToMany()
  @JoinTable(
      name = "norm_abbreviation_document_type",
      joinColumns = @JoinColumn(name = "norm_abbreviation_id"),
      inverseJoinColumns = @JoinColumn(name = "document_type_id"))
  List<JPADocumentTypeNewDTO> documentTypes;

  @ManyToMany()
  @JoinTable(
      name = "norm_abbreviation_region",
      joinColumns = @JoinColumn(name = "norm_abbreviation_id"),
      inverseJoinColumns = @JoinColumn(name = "region_id"))
  List<JPARegionDTO> regions;
}
