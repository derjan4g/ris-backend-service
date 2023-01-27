package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.Eli
import de.bund.digitalservice.ris.norms.domain.value.UndefinedDate
import java.time.LocalDate
import java.util.UUID

data class Norm(
    val identifier: Identifier,
    val metadata: List<Field<*>>,
    val articles: List<Article> = listOf(),
    val sections: List<ArticleSection> = listOf(),
)
