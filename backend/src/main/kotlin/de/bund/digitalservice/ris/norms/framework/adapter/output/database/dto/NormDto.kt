package de.bund.digitalservice.ris.norms.framework.adapter.output.database.dto

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*

@Table(name = "norms")
data class NormDto(
    @Id
    val guid: UUID,
    @Column("official_long_title")
    val officialLongTitle: String,
    @Column("ris_abbreviation")
    var risAbbreviation: String? = null,
    @Column("document_number")
    var documentNumber: String? = null,
    @Column("document_category")
    var documentCategory: String? = null,

    @Column("official_short_title")
    var officialShortTitle: String? = null,
    @Column("official_abbreviation")
    var officialAbbreviation: String? = null,

    @Column("announcement_date")
    var announcementDate: LocalDate? = null,
    @Column("publication_date")
    var publicationDate: LocalDate? = null,

    @Column("complete_citation")
    var completeCitation: String? = null,

    @Column("status_note")
    var statusNote: String? = null,
    @Column("status_description")
    var statusDescription: String? = null,
    @Column("status_date")
    var statusDate: LocalDate? = null,
    @Column("status_reference")
    var statusReference: String? = null,
    @Column("repeal_note")
    var repealNote: String? = null,
    @Column("repeal_article")
    var repealArticle: String? = null,
    @Column("repeal_date")
    var repealDate: LocalDate? = null,
    @Column("repeal_references")
    var repealReferences: String? = null,
    @Column("reissue_note")
    var reissueNote: String? = null,
    @Column("reissue_article")
    var reissueArticle: String? = null,
    @Column("reissue_date")
    var reissueDate: LocalDate? = null,
    @Column("reissue_reference")
    var reissueReference: String? = null,
    @Column("other_status_note")
    var otherStatusNote: String? = null,

    @Column("celex_number")
    var celexNumber: String? = null,

    @Column("text")
    var text: String? = null,

) : Persistable<UUID> {

    @Transient
    var newEntry: Boolean = true

    override fun getId(): UUID = guid

    override fun isNew(): Boolean = newEntry
}
