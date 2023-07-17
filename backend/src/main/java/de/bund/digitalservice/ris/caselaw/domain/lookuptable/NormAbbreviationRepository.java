package de.bund.digitalservice.ris.caselaw.domain.lookuptable;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NormAbbreviationRepository {
  NormAbbreviation findById(UUID id);

  List<NormAbbreviation> findBySearchQuery(String query, Integer size, Integer page);

  List<NormAbbreviation> findByAwesomeSearchQuery(String query, Integer size, Integer page);
}
