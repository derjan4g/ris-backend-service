package de.bund.digitalservice.ris.norms.domain.value

enum class MetadatumSectionName(val parentSection: MetadatumParentSectionName? = null) {
  PRINT_ANNOUNCEMENT(MetadatumParentSectionName.ANNOUNCEMENTS),
  DIGITAL_ANNOUNCEMENT(MetadatumParentSectionName.ANNOUNCEMENTS),
  PROVIDER,
  EXPIRATION_DATE,
}

enum class MetadatumParentSectionName {
  ANNOUNCEMENTS
}

// More generic and powerful approach:
enum class AlternativeSectionName {
  PRINT_ANNOUNCEMENT,
  DIGITAL_ANNOUNCEMENT,
  ANNOUNCEMENTS,
  PROVIDER,
  EXPIRATION_DATE

  val allowedParents: List<AlternativeSectionName>?
    get() = when (this) {
        PRINT_ANNOUNCEMENT -> listOf(AlternativeSectionName.ANNOUNCEMENTS),
        DIGITAL_ANNOUNCEMENT -> listOf(AlternativeSectionName.ANNOUNCEMENTS),
        else -> null
      }
    }
}
