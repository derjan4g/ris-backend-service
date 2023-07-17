package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("norm_abbreviation_region")
public class JPANormAbbreviationRegionDTO implements Persistable<UUID> {
  private UUID normAbbreviationId;
  private UUID regionId;
  @Transient private boolean newEntity;

  @Override
  public UUID getId() {
    return null;
  }

  @Override
  public boolean isNew() {
    return newEntity;
  }
}
