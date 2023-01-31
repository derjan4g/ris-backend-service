package de.bund.digitalservice.ris.norms.domain.value

enum class MetadatumSectionName (val parentSections: List<MetadatumSectionName>? = null) {
    PRINT_ANNOUNCEMENT,
    ANNOUNCEMENTS,
    PROVIDER,
    EXPIRATION_DATE
}
