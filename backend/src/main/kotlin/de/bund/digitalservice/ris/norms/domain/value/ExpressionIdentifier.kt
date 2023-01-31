package de.bund.digitalservice.ris.norms.domain.value

data class ExpressionIdentifier(
    val type: ExpressionIdentifierType,
    val position: PositionIndicator,
    val prefix: ExpressionIdentifier?
) {
    val qualifier: String
        get() {
            val position = position.marker?.marker ?: position.ordinal.toString()
            return "${type.symbol}-$position"
        }

    override fun toString(): String {
        return if (prefix == null) {
            qualifier
        } else {
            "${prefix}_$qualifier"
        }
    }
}

class PositionIndicator(val ordinal: Number, val marker: PositionMarker?)

data class PositionMarker(
    override val identifier: Identifier,
    val marker: String,
    val text: String
) : Identifiable(identifier, ExpressionIdentifierType.POSITION_MARKER) {
    init {
        require(identifier.eid.position.marker == null) {
            "Position marker must not have a position marker itself"
        }

        require(identifier.eid.position.ordinal == 1) {
            "Position marker must always have a position ordinal of 1"
        }
    }
}

enum class ExpressionIdentifierType(val symbol: String) {
    ARTICLE("para"),
    PARAGRAPH("abs"),
    POSITION_MARKER("bezeichnung"),
    TTILE("doktitel"),
    SHORT_TITLE("kurztitel"),
    HEADING("überschrift"),
    TEXT("text")
}
