package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.MetadatumSectionName

data class MetadatumSection(
    val name: MetadatumSectionName,
    val parent: MetadatumSection? = null,
    val order: Int = 0
) {
    init {
        if (name.parentSections?.contains(parent?.name) != true) {
            throw IllegalArgumentException()
        }
    }
}