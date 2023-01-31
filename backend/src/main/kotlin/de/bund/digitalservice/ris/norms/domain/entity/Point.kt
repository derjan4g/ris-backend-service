package de.bund.digitalservice.ris.norms.domain.entity

data class Point(
    val identifier: Identifier = Identifier(),
    val content: Content
)
