package de.bund.digitalservice.ris.norms.domain.value

import de.bund.digitalservice.ris.norms.domain.entity.FieldSection

enum class FieldSections(val section: FieldSection) {
    ANNOUNCEMENTS(FieldSection(FieldSectionName.ANNOUNCEMENTS, null)),
    PROVIDER(FieldSection(FieldSectionName.PROVIDER, null)),
    PRINT_ANNOUNCEMENT(FieldSection(FieldSectionName.PRINT_ANNOUNCEMENT, ANNOUNCEMENTS.section)),
    EXPIRATION_DATE(FieldSection(FieldSectionName.EXPIRATION_DATE, null))
}

enum class FieldSectionName {
    PRINT_ANNOUNCEMENT,
    ANNOUNCEMENTS,
    PROVIDER,
    EXPIRATION_DATE
}
