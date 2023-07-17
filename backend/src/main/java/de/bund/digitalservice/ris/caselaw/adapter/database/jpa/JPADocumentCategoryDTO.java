package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "document_category")
public class JPADocumentCategoryDTO {
  @Id private UUID id;
  private Character label;
}
