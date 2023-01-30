package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.MetadataType

data class Metadatum<T>(
    val identifier: Identifier = Identifier(),
    val value: T,
    val type: MetadataType,
    val refersTo: Identifier? = null,
    val version: Int = 0,
    val order: Int = 0,
    val section: MetadatumSection
) {

    init {
        if (value!!::class != type.type || !type.parentSections.contains(section.name)) {
            throw IllegalArgumentException()
        }
    }
}
