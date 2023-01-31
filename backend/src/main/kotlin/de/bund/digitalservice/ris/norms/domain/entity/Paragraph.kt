package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.Marker

data class Paragraph(
    val identifier: Identifier = Identifier(),
    var marker: Marker? = null,
    val content: Content
)
