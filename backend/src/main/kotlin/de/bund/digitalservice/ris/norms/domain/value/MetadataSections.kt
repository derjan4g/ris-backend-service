package de.bund.digitalservice.ris.norms.domain.value

import de.bund.digitalservice.ris.norms.domain.entity.MetadatumSection

enum class MetadataSections(val section: MetadatumSection) {
    ANNOUNCEMENTS(MetadatumSection(MetadatumSectionName.ANNOUNCEMENTS, null)),
    PROVIDER(MetadatumSection(MetadatumSectionName.PROVIDER, null)),
    PRINT_ANNOUNCEMENT(MetadatumSection(MetadatumSectionName.PRINT_ANNOUNCEMENT, ANNOUNCEMENTS.section)),
    EXPIRATION_DATE(MetadatumSection(MetadatumSectionName.EXPIRATION_DATE, null))
}

enum class MetadatumSectionName {
    PRINT_ANNOUNCEMENT,
    ANNOUNCEMENTS,
    PROVIDER,
    EXPIRATION_DATE
}
