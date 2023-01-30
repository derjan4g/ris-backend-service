package de.bund.digitalservice.ris.norms.domain.entity

import java.util.UUID

// TODO: Add paragraph marker value class.
data class Paragraph(val identifier: Identifier = Identifier(), var marker: String? = null, val text: String)
