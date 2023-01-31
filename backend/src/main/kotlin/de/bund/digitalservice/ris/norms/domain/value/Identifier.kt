package de.bund.digitalservice.ris.norms.domain.value

import java.util.UUID

data class Identifier(
    val guid: UUID = UUID.randomUUID(),
    val eid: ExpressionIdentifier,
    val xmlElement: String? = null,
    val order: Int = 0
)

open class Identifiable(
    open val identifier: Identifier,
    private val type: ExpressionIdentifierType
) {
    init {
        require(identifier.eid.type == type) {
            "Element of type $type can not have expression identifier for type ${identifier.eid.type}"
        }
    }
}
