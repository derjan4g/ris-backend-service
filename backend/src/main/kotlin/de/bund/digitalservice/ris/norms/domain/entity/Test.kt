package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.MetadataType
import java.time.LocalDate

class Test {
    fun main(args: Array<String>) {
        val pageCount = Metadatum(
            value = "1022",
            type = MetadataType.PAGE_COUNT
        )

        val number = Metadatum(
            value = 25,
            type = MetadataType.NUMBER
        )
        val explanation = Metadatum(
            value = "lorem ipsum dolor",
            type = MetadataType.EXPLANATION
        )

        // 1. If setting the value of IsResolutionMajority to string "true", then the flow will break
        val isResolutionMajority = Metadatum(
            value = true,
            type = MetadataType.IS_RESOLUTION_MAJORITY
        )

        val expirationDate = Metadatum(
            value = LocalDate.now(),
            type = MetadataType.EXPIRATION_DATE
        )

        val norm = Norm(
            metadata = listOf(
                pageCount,
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
