package de.bund.digitalservice.ris.norms.domain.value

import java.time.LocalDate
import kotlin.reflect.KClass

enum class MetadataType(val type: KClass<*>, val parentSections: List<MetadatumSectionName>) {
    PAGE_COUNT(Int::class, listOf(MetadatumSectionName.ANNOUNCEMENTS)),
    NUMBER(Int::class, listOf(MetadatumSectionName.ANNOUNCEMENTS)),
    EXPLANATION(String::class, listOf(MetadatumSectionName.ANNOUNCEMENTS)),
    IS_RESOLUTION_MAJORITY(Boolean::class, listOf(MetadatumSectionName.PROVIDER)),
    EXPIRATION_DATE(LocalDate::class, listOf(MetadatumSectionName.EXPIRATION_DATE)),
    START_DATE(LocalDate::class, listOf(MetadatumSectionName.EXPIRATION_DATE, MetadatumSectionName.ANNOUNCEMENTS))
}
