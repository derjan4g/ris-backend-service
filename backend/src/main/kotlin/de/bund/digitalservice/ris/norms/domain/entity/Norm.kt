package de.bund.digitalservice.ris.norms.domain.entity

data class Norm(
    val identifier: Identifier,
    val metadata: List<Field<*>>,
    val articles: List<Article> = listOf(),
    val sections: List<ArticleSection> = listOf()
)
