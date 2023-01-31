package de.bund.digitalservice.ris.norms.domain.entity

data class Norm(
    val identifier: Identifier,
    val metadata: List<Metadatum<*>>,
    val articles: List<Article> = listOf(),
    val sections: List<NormSection> = listOf()
)
