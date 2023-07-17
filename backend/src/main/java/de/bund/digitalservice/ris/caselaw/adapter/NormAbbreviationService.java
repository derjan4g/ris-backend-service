package de.bund.digitalservice.ris.caselaw.adapter;

import de.bund.digitalservice.ris.caselaw.domain.lookuptable.NormAbbreviation;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.NormAbbreviationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NormAbbreviationService {
  private final NormAbbreviationRepository repository;

  public NormAbbreviationService(NormAbbreviationRepository repository) {
    this.repository = repository;
  }

  public NormAbbreviation getNormAbbreviationById(UUID uuid) {
    return repository.findById(uuid);
  }

  public List<NormAbbreviation> getNormAbbreviationBySearchQuery(
      String query, Integer size, Integer page) {

    Integer pageOffset = null;
    if (page != null && size != null) {
      pageOffset = page * size;
    }

    return repository.findBySearchQuery(query, size, pageOffset);
  }

  public List<NormAbbreviation> getNormAbbreviationByAwesomeSearchQuery(
      String query, Integer size, Integer page) {

    Integer pageOffset = null;
    if (page != null && size != null) {
      pageOffset = page * size;
    }

    return repository.findByAwesomeSearchQuery(query, size, pageOffset);
  }
}
