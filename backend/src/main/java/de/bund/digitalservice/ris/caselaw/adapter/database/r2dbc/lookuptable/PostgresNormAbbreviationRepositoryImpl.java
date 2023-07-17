package de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable;

import de.bund.digitalservice.ris.caselaw.adapter.database.jpa.JPANormAbbreviationRepository;
import de.bund.digitalservice.ris.caselaw.adapter.transformer.NormAbbreviationTransformer;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.NormAbbreviation;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.NormAbbreviationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PostgresNormAbbreviationRepositoryImpl implements NormAbbreviationRepository {
  private final JPANormAbbreviationRepository repository;

  public PostgresNormAbbreviationRepositoryImpl(JPANormAbbreviationRepository repository) {

    this.repository = repository;
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public NormAbbreviation findById(UUID id) {
    return repository.findById(id).map(NormAbbreviationTransformer::transformDTO).orElse(null);
  }

  @Override
  public List<NormAbbreviation> findBySearchQuery(String query, Integer size, Integer pageOffset) {
    return repository.findBySearchQuery(query, size, pageOffset).stream()
        .map(NormAbbreviationTransformer::transformDTO)
        .toList();
  }

  @Override
  public List<NormAbbreviation> findByAwesomeSearchQuery(String query, Integer size, Integer page) {
    String[] queryBlocks = query.replace(",", "").replace(";", "").split(" ");
    StringBuilder tsQuery = new StringBuilder();
    for (int i = 0; i < queryBlocks.length; i++) {
      if (queryBlocks[i].isBlank()) continue;

      if (i > 0) {
        tsQuery.append(" & ");
      }

      tsQuery.append(queryBlocks[i]).append(":*");
    }
    String directInput = "";
    if (queryBlocks[0] != null && !queryBlocks[0].isBlank()) {
      directInput = queryBlocks[0].toLowerCase();
    }

    return repository.findByAwesomeSearchQuery(directInput, tsQuery.toString(), size, page).stream()
        .map(NormAbbreviationTransformer::transformDTO)
        .toList();
  }
}
