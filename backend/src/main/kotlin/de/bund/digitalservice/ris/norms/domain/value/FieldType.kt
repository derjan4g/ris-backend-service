package de.bund.digitalservice.ris.norms.domain.value

import de.bund.digitalservice.ris.norms.domain.entity.FieldSection
import java.time.LocalDate
import kotlin.reflect.KClass

enum class FieldType(val type: KClass<*>, val section: FieldSection) {
    PAGE_COUNT(Int::class, FieldSections.PRINT_ANNOUNCEMENT.section),
    NUMBER(Int::class, FieldSections.PRINT_ANNOUNCEMENT.section),
    EXPLANATION(String::class, FieldSections.PRINT_ANNOUNCEMENT.section),
    IS_RESOLUTION_MAJORITY(Boolean::class, FieldSections.PROVIDER.section),
    EXPIRATION_DATE(LocalDate::class, FieldSections.EXPIRATION_DATE.section)
}
