package de.bund.digitalservice.ris.norms.domain.entity

import java.util.UUID

data class Identifier(
    val guid: UUID = UUID.randomUUID(),
    val eid: String? = null,
    val order: Int = 0 // Discuss if we can derive this from the eId too.
)
