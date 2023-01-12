package de.bund.digitalservice.ris.norms.framework.adapter.output.database

import de.bund.digitalservice.ris.norms.application.port.output.SearchNormsOutputPort.QueryFields
import de.bund.digitalservice.ris.norms.application.port.output.SearchNormsOutputPort.QueryParameter
import de.bund.digitalservice.ris.norms.domain.value.EuropeanLegalIdentifier
import org.springframework.data.relational.core.query.Criteria
import java.time.LocalDate

class NormsCriteriaBuilder : NormsMapper {
    fun getEliCriteria(gazette: String, year: String, page: String): Criteria {
        val gazetteColumn = queryFieldToDbColumn(QueryFields.PRINT_ANNOUNCEMENT_GAZETTE)
        val pageColumn = queryFieldToDbColumn(QueryFields.PRINT_ANNOUNCEMENT_PAGE)
        val citationDateColumn = queryFieldToDbColumn(QueryFields.CITATION_DATE)
        val announcementDateColumn = queryFieldToDbColumn(QueryFields.ANNOUNCEMENT_DATE)
        return Criteria.from(
            Criteria.where(gazetteColumn).`is`(EuropeanLegalIdentifier.parseGazette(gazette))
                .and(
                    Criteria.where(pageColumn).`is`(page)
                )
                .and(
                    Criteria.where(announcementDateColumn).isNull.and(getYearInDateCriteria(citationDateColumn, year))
                        .or(
                            getYearInDateCriteria(announcementDateColumn, year)
                        )
                )
        )
    }

    fun getSearchCriteria(query: List<QueryParameter>): Criteria {
        var criteria = Criteria.empty()
        query.forEach { criteria = criteria.or(getFieldCriteria(it)) }

        return Criteria.from(criteria)
    }

    private fun getFieldCriteria(queryParameter: QueryParameter): Criteria {
        if (queryParameter.value == null) {
            return Criteria.where(queryFieldToDbColumn(queryParameter.field)).isNull
        }

        if (queryParameter.isFuzzyMatch) {
            return Criteria.where(queryFieldToDbColumn(queryParameter.field)).like("%${queryParameter.value}%")
        }

        return Criteria.where(queryFieldToDbColumn(queryParameter.field)).`is`(queryParameter.value)
    }

    private fun getYearInDateCriteria(dbColumn: String, value: String): Criteria {
        return Criteria.where(dbColumn)
            .between(
                LocalDate.of(value.toInt(), 1, 1),
                LocalDate.of(value.toInt() + 1, 1, 1)
            )
    }
}
