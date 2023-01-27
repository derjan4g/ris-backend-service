package de.bund.digitalservice.ris.norms.domain.value

import de.bund.digitalservice.ris.norms.domain.entity.MetadatumSection
import java.time.LocalDate
import kotlin.reflect.KClass

enum class MetadataType(val type: KClass<*>, val section: MetadatumSection) {
    PAGE_COUNT(Int::class, MetadataSections.PRINT_ANNOUNCEMENT.section),
    NUMBER(Int::class, MetadataSections.PRINT_ANNOUNCEMENT.section),
    EXPLANATION(String::class, MetadataSections.PRINT_ANNOUNCEMENT.section),
    IS_RESOLUTION_MAJORITY(Boolean::class, MetadataSections.PROVIDER.section),
    EXPIRATION_DATE(LocalDate::class, MetadataSections.EXPIRATION_DATE.section)
}
