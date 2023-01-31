package de.bund.digitalservice.ris.norms.domain.entity

data class ContentList(
    val identifier: Identifier = Identifier(),
    val intro: String?,
    val points: List<Point> = listOf(),
    val outro: String?
)
