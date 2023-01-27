package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.FieldType

data class Field<T>(
    val identifier: Identifier = Identifier(),
    val value: T,
    val type: FieldType,
    val refersTo: Identifier? = null,
    val version: Int = 0,
    val order: Int = 0
) {
    val section: FieldSection
        get() = type.section

    init {
        if (value!!::class != type.type) {
            throw IllegalArgumentException()
        }
    }
}
