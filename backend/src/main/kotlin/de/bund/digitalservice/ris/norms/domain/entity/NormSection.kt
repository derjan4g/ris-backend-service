package de.bund.digitalservice.ris.norms.domain.entity

data class NormSection(
    val identifier: Identifier = Identifier(),
    val articles: List<Article> = listOf(),
    val parent: NormSection? = null
)
