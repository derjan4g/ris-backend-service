package de.bund.digitalservice.ris.norms.domain.entity

data class ArticleSection(
    val identifier: Identifier,
    val articles: List<Article> = listOf(),
    val parent: ArticleSection? = null
)
