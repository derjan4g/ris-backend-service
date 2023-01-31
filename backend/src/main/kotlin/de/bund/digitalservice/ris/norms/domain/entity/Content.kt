package de.bund.digitalservice.ris.norms.domain.entity

data class Content(
    val identifier: Identifier = Identifier(),
    val additionalIdentifiers: List<Identifier>,
    val text: String? = null,
    val list: ContentList? = null,
) {
  init {
    require(text.isNullOrEmpty().xor(list == null)) { "Content can have either a text or a list" }
  }
}
