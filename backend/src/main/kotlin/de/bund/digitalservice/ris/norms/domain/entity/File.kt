package de.bund.digitalservice.ris.norms.domain.entity

data class File(
    val identifier: Identifier = Identifier(),
    val additionalIdentifiers: List<Identifier>,
    val hash: String,
    val refersTo: Identifier,
    val originalName: String,
    val path: String
)
