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
@Entity(name = "region")
public class JPARegionDTO {
  @Id private UUID id;
  private String code;
  private String label;
}
