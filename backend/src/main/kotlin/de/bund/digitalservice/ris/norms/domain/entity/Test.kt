package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.MetadataType
import de.bund.digitalservice.ris.norms.domain.value.MetadatumSectionName
import java.time.LocalDate

class Test {
    fun main(args: Array<String>) {
        val announcementSection = MetadatumSection(MetadatumSectionName.ANNOUNCEMENTS, null, 0)
        val printAnnouncementSection = MetadatumSection(MetadatumSectionName.PRINT_ANNOUNCEMENT, announcementSection, 0)

        val pageCount = Metadatum(
            value = "1022",
            type = MetadataType.PAGE_COUNT,
            section = printAnnouncementSection,
            order = 0
        )

        val pageCount2 = Metadatum(
            value = "1023",
            type = MetadataType.PAGE_COUNT,
            section = printAnnouncementSection,
            order = 1
        )

        val number = Metadatum(
            value = 25,
            type = MetadataType.NUMBER,
            section = printAnnouncementSection
        )

        val explanation = Metadatum(
            value = "lorem ipsum dolor",
            type = MetadataType.EXPLANATION,
            section = printAnnouncementSection
        )

        val isResolutionMajority = Metadatum(
            value = true,
            type = MetadataType.IS_RESOLUTION_MAJORITY,
            section = printAnnouncementSection
        )

        val expirationDate = Metadatum(
            value = LocalDate.now(),
            type = MetadataType.EXPIRATION_DATE,
            section = printAnnouncementSection
        )

        val norm = Norm(
            metadata = listOf(
                pageCount,
                pageCount2,
                number,
                explanation,
                expirationDate,
                isResolutionMajority
            )
        )

        println(norm.toString())
        println(expirationDate.section)
    }
}
