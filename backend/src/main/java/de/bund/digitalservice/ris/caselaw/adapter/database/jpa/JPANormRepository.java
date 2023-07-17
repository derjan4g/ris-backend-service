package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPANormRepository extends JpaRepository<JPANormDTO, Long> {
  List<JPANormDTO> findAllByDocumentUnitId(Long id);
}
