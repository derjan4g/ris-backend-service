package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.FieldSectionName

data class FieldSection(
    val name: FieldSectionName,
    val parent: FieldSection? = null
)
