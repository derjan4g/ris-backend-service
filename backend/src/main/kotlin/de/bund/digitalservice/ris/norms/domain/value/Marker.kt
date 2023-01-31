package de.bund.digitalservice.ris.norms.domain.value

import de.bund.digitalservice.ris.norms.domain.entity.Identifier

data class Marker(
    val identifier: Identifier = Identifier(),
    val value: String?
) {
    val number: String = value!!.replace("/[()\\s§]/g", "")
}
