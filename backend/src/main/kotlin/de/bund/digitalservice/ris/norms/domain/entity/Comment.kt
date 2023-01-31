package de.bund.digitalservice.ris.norms.domain.entity

data class Comment(
    // TODO: Discuss how to identify this.
    val content: String,
    val refersTo: Identifier,
    val startPosition: Int,
    val endPosition: Int
)
