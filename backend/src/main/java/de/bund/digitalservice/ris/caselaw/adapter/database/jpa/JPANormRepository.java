package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.DocumentUnitNormDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPANormRepository extends JpaRepository<JPANormDTO, Long> {
  List<DocumentUnitNormDTO> findAllByDocumentUnitId(Long id);
}
