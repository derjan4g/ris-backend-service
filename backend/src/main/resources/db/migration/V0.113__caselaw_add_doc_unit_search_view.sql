CREATE OR REPLACE VIEW
  doc_unit_search_view AS
SELECT
  doc_unit.*,
  status_subquery.publication_status,
  file_number_subquery.file_number,
  file_number_subquery.filenumber_is_deviating,
  UPPER(
    CONCAT(
      doc_unit.documentnumber,
      ' ',
      file_number_subquery.file_number
    )
  ) AS documentnumber_and_file_number
FROM
  doc_unit
  LEFT JOIN (
    SELECT DISTINCT
      ON (document_unit_id) document_unit_id,
      publication_status
    FROM
      public.status
    ORDER BY
      document_unit_id,
      created_at DESC
  ) AS status_subquery ON doc_unit.uuid = status_subquery.document_unit_id
  LEFT JOIN (
    SELECT DISTINCT
      ON (document_unit_id) document_unit_id,
      file_number,
      is_deviating AS filenumber_is_deviating
    FROM
      public.file_number
  ) AS file_number_subquery ON doc_unit.id = file_number_subquery.document_unit_id
WHERE
  data_source in ('NEURIS', 'MIGRATION');
