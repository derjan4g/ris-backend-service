package de.bund.digitalservice.ris.caselaw.adapter.database.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JPANormAbbreviationRepository extends JpaRepository<JPANormAbbreviationDTO, UUID> {
  @Query(value = "select * from norm_abbreviation limit :size", nativeQuery = true)
  List<JPANormAbbreviationDTO> findAll(int size);

  @Query(
      value =
          "select * from norm_abbreviation where abbreviation like :query||'%' order by abbreviation limit :size offset :pageOffset",
      nativeQuery = true)
  List<JPANormAbbreviationDTO> findBySearchQuery(String query, Integer size, Integer pageOffset);

  @Query(
      value =
          "select"
              + " id,"
              + " abbreviation,"
              + " decision_date,"
              + " document_id,"
              + " document_number,"
              + " official_letter_abbreviation,"
              + " official_long_title,"
              + " official_short_title,"
              + " source,"
              + " case"
              + "  when lower(abbreviation) like '' || :directInput || '%' then 2"
              + "  when lower(official_letter_abbreviation) like '' || :directInput || '%' then 1"
              + "  else 0"
              + " end +"
              + " ts_rank_cd(weighted_vector, to_tsquery('german', '' || :tsQuery || '')) rank"
              + " from norm_abbreviation_search"
              + " where weighted_vector @@ to_tsquery('german', '' || :tsQuery || '')"
              + " order by rank desc"
              + " limit :size offset :pageOffset",
      nativeQuery = true)
  List<JPANormAbbreviationDTO> findByAwesomeSearchQuery(
      String directInput, String tsQuery, Integer size, Integer pageOffset);

  Optional<JPANormAbbreviationDTO> findById(UUID normAbbreviationUuid);
}
