package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "document_type")
public class JPADocumentTypeNewDTO {
  @Id private UUID id;
  private String abbreviation;
  private String label;
  private boolean multiple;

  @Column(name = "super_label_1")
  private String superLabel1;

  @Column(name = "super_label_2")
  private String superLabel2;

  @OneToOne
  @JoinColumn(name = "document_category_id")
  JPADocumentCategoryDTO documentCategory;
}
