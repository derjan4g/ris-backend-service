package de.bund.digitalservice.ris.norms.domain.entity

import de.bund.digitalservice.ris.norms.domain.value.FieldType
import java.time.LocalDate

class Test {
    fun main(args: Array<String>) {
        val pageCount = Field(
            value = "1022",
            type = FieldType.PAGE_COUNT
        )

        val number = Field(
            value = 25,
            type = FieldType.NUMBER
        )
        val explanation = Field(
            value = "lorem ipsum dolor",
            type = FieldType.EXPLANATION
        )

        // 1. If setting the value of IsResolutionMajority to string "true", then the flow will break
        val isResolutionMajority = Field(
            value = true,
            type = FieldType.IS_RESOLUTION_MAJORITY
        )

        val expirationDate = Field(
            value = LocalDate.now(),
            type = FieldType.EXPIRATION_DATE
        )

        val norm = Norm(
            Identifier(),
            listOf(
                pageCount,
                number,
                explanation,
                expirationDate,
                isResolutionMajority,
            )
        )

        println(norm.toString())
        println(expirationDate.section)
    }
}
