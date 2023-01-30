package de.bund.digitalservice.ris.norms.domain.entity

import java.util.UUID

data class Identifier(
    val guid: UUID = UUID.randomUUID(),
    val eid: String? = null,
    val xmlElement: String? = null,
    val order: Int = 0
)
