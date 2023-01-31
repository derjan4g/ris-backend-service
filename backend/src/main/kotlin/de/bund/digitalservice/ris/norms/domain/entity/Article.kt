package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.Marker

data class Article(
    val identifier: Identifier = Identifier(),
    val additionalIdentifiers: List<Identifier>,
    var title: String? = null,
    val marker: Marker,
    val paragraphs: List<Paragraph> = listOf()
)
