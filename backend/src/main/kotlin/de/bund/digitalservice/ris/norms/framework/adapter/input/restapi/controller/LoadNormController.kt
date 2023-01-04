package de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.bund.digitalservice.ris.norms.application.port.input.LoadNormUseCase
import de.bund.digitalservice.ris.norms.domain.entity.Article
import de.bund.digitalservice.ris.norms.domain.entity.Norm
import de.bund.digitalservice.ris.norms.domain.entity.Paragraph
import de.bund.digitalservice.ris.norms.domain.value.UndefinedDate
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.ApiConfiguration
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.encodeGuid
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.encodeLocalDate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping(ApiConfiguration.API_BASE_PATH)
class LoadNormController(private val loadNormService: LoadNormUseCase) {

    @GetMapping(path = ["/{guid}"])
    fun getNormByGuid(@PathVariable guid: String): Mono<ResponseEntity<NormResponseSchema>> {
        val query = LoadNormUseCase.Query(UUID.fromString(guid))

        return loadNormService
            .loadNorm(query)
            .map({ norm -> NormResponseSchema.fromUseCaseData(norm) })
            .map({ normResponseSchema -> ResponseEntity.ok(normResponseSchema) })
            .defaultIfEmpty(ResponseEntity.notFound().build<NormResponseSchema>())
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

    data class NormResponseSchema
    private constructor(
        val guid: String,
        val articles: List<ArticleResponseSchema>,
        val officialLongTitle: String,
        var risAbbreviation: String?,
        var risAbbreviationInternationalLaw: String?,
        var documentNumber: String?,
        var divergentDocumentNumber: String?,
        var documentCategory: String?,
        var frameKeywords: String?,
        var documentTypeName: String?,
        var documentNormCategory: String?,
        var documentTemplateName: String?,
        var providerEntity: String?,
        var providerDecidingBody: String?,
        var providerIsResolutionMajority: Boolean?,
        var participationType: String?,
        var participationInstitution: String?,
        var leadJurisdiction: String?,
        var leadUnit: String?,
        var subjectFna: String?,
        var subjectPreviousFna: String?,
        var subjectGesta: String?,
        var subjectBgb3: String?,
        var officialShortTitle: String?,
        var officialAbbreviation: String?,
        var unofficialLongTitle: String?,
        var unofficialShortTitle: String?,
        var unofficialAbbreviation: String?,
        var entryIntoForceDate: String?,
        var entryIntoForceDateState: UndefinedDate?,
        var principleEntryIntoForceDate: String?,
        var principleEntryIntoForceDateState: UndefinedDate?,
        var divergentEntryIntoForceDate: String?,
        var divergentEntryIntoForceDateState: UndefinedDate?,
        var expirationDate: String?,
        var expirationDateState: UndefinedDate?,
        @get:JsonProperty("isExpirationDateTemp") var isExpirationDateTemp: Boolean?,
        var principleExpirationDate: String?,
        var principleExpirationDateState: UndefinedDate?,
        var divergentExpirationDate: String?,
        var divergentExpirationDateState: UndefinedDate?,
        var expirationNormCategory: String?,
        var announcementDate: String?,
        var publicationDate: String?,
        var citationDate: String?,
        var printAnnouncementGazette: String?,
        var printAnnouncementYear: String?,
        var printAnnouncementNumber: String?,
        var printAnnouncementPage: String?,
        var printAnnouncementInfo: String?,
        var printAnnouncementExplanations: String?,
        var digitalAnnouncementMedium: String?,
        var digitalAnnouncementDate: String?,
        var digitalAnnouncementEdition: String?,
        var digitalAnnouncementYear: String?,
        var digitalAnnouncementPage: String?,
        var digitalAnnouncementArea: String?,
        var digitalAnnouncementAreaNumber: String?,
        var digitalAnnouncementInfo: String?,
        var digitalAnnouncementExplanations: String?,
        var euAnnouncementGazette: String?,
        var euAnnouncementYear: String?,
        var euAnnouncementSeries: String?,
        var euAnnouncementNumber: String?,
        var euAnnouncementPage: String?,
        var euAnnouncementInfo: String?,
        var euAnnouncementExplanations: String?,
        var otherOfficialAnnouncement: String?,
        var unofficialReference: String?,
        var completeCitation: String?,
        var statusNote: String?,
        var statusDescription: String?,
        var statusDate: String?,
        var statusReference: String?,
        var repealNote: String?,
        var repealArticle: String?,
        var repealDate: String?,
        var repealReferences: String?,
        var reissueNote: String?,
        var reissueArticle: String?,
        var reissueDate: String?,
        var reissueReference: String?,
        var otherStatusNote: String?,
        var documentStatusWorkNote: String?,
        var documentStatusDescription: String?,
        var documentStatusDate: String?,
        var documentStatusReference: String?,
        var documentStatusEntryIntoForceDate: String?,
        var documentStatusProof: String?,
        var documentTextProof: String?,
        var otherDocumentNote: String?,
        var applicationScopeArea: String?,
        var applicationScopeStartDate: String?,
        var applicationScopeEndDate: String?,
        var categorizedReference: String?,
        var otherFootnote: String?,
        var validityRule: String?,
        var digitalEvidenceLink: String?,
        var digitalEvidenceRelatedData: String?,
        var digitalEvidenceExternalDataNote: String?,
        var digitalEvidenceAppendix: String?,
        var referenceNumber: String?,
        var europeanLegalIdentifier: String?,
        var celexNumber: String?,
        var ageIndicationStart: String?,
        var ageIndicationEnd: String?,
        var definition: String?,
        var ageOfMajorityIndication: String?,
        var text: String?
    ) {
        companion object {
            fun fromUseCaseData(data: Norm): NormResponseSchema {
                val articles = data.articles.map { ArticleResponseSchema.fromUseCaseData(it) }
                return NormResponseSchema(
                    encodeGuid(data.guid),
                    articles,
                    data.officialLongTitle,
                    data.risAbbreviation,
                    data.risAbbreviationInternationalLaw,
                    data.documentNumber,
                    data.divergentDocumentNumber,
                    data.documentCategory,
                    data.frameKeywords,
                    data.documentTypeName,
                    data.documentNormCategory,
                    data.documentTemplateName,
                    data.providerEntity,
                    data.providerDecidingBody,
                    data.providerIsResolutionMajority,
                    data.participationType,
                    data.participationInstitution,
                    data.leadJurisdiction,
                    data.leadUnit,
                    data.subjectFna,
                    data.subjectPreviousFna,
                    data.subjectGesta,
                    data.subjectBgb3,
                    data.officialShortTitle,
                    data.officialAbbreviation,
                    data.unofficialLongTitle,
                    data.unofficialShortTitle,
                    data.unofficialAbbreviation,
                    encodeLocalDate(data.entryIntoForceDate),
                    data.entryIntoForceDateState,
                    encodeLocalDate(data.principleEntryIntoForceDate),
                    data.principleEntryIntoForceDateState,
                    encodeLocalDate(data.divergentEntryIntoForceDate),
                    data.divergentEntryIntoForceDateState,
                    encodeLocalDate(data.expirationDate),
                    data.expirationDateState,
                    data.isExpirationDateTemp,
                    encodeLocalDate(data.principleExpirationDate),
                    data.principleExpirationDateState,
                    encodeLocalDate(data.divergentExpirationDate),
                    data.divergentExpirationDateState,
                    data.expirationNormCategory,
                    encodeLocalDate(data.announcementDate),
                    encodeLocalDate(data.publicationDate),
                    encodeLocalDate(data.citationDate),
                    data.printAnnouncementGazette,
                    data.printAnnouncementYear,
                    data.printAnnouncementNumber,
                    data.printAnnouncementPage,
                    data.printAnnouncementInfo,
                    data.printAnnouncementExplanations,
                    data.digitalAnnouncementMedium,
                    encodeLocalDate(data.digitalAnnouncementDate),
                    data.digitalAnnouncementEdition,
                    data.digitalAnnouncementYear,
                    data.digitalAnnouncementPage,
                    data.digitalAnnouncementArea,
                    data.digitalAnnouncementAreaNumber,
                    data.digitalAnnouncementInfo,
                    data.digitalAnnouncementExplanations,
                    data.euAnnouncementGazette,
                    data.euAnnouncementYear,
                    data.euAnnouncementSeries,
                    data.euAnnouncementNumber,
                    data.euAnnouncementPage,
                    data.euAnnouncementInfo,
                    data.euAnnouncementExplanations,
                    data.otherOfficialAnnouncement,
                    data.unofficialReference,
                    data.completeCitation,
                    data.statusNote,
                    data.statusDescription,
                    encodeLocalDate(data.statusDate),
                    data.statusReference,
                    data.repealNote,
                    data.repealArticle,
                    encodeLocalDate(data.repealDate),
                    data.repealReferences,
                    data.reissueNote,
                    data.reissueArticle,
                    encodeLocalDate(data.reissueDate),
                    data.reissueReference,
                    data.otherStatusNote,
                    data.documentStatusWorkNote,
                    data.documentStatusDescription,
                    encodeLocalDate(data.documentStatusDate),
                    data.documentStatusReference,
                    encodeLocalDate(data.documentStatusEntryIntoForceDate),
                    data.documentStatusProof,
                    data.documentTextProof,
                    data.otherDocumentNote,
                    data.applicationScopeArea,
                    encodeLocalDate(data.applicationScopeStartDate),
                    encodeLocalDate(data.applicationScopeEndDate),
                    data.categorizedReference,
                    data.otherFootnote,
                    data.validityRule,
                    data.digitalEvidenceLink,
                    data.digitalEvidenceRelatedData,
                    data.digitalEvidenceExternalDataNote,
                    data.digitalEvidenceAppendix,
                    data.referenceNumber,
                    data.europeanLegalIdentifier,
                    data.celexNumber,
                    data.ageIndicationStart,
                    data.ageIndicationEnd,
                    data.definition,
                    data.ageOfMajorityIndication,
                    data.text
                )
            }
        }
    }

    data class ArticleResponseSchema
    private constructor(
        val guid: String,
        var title: String? = null,
        val marker: String,
        val paragraphs: List<ParagraphResponseSchema>
    ) {
        companion object {
            fun fromUseCaseData(data: Article): ArticleResponseSchema {
                val paragraphs = data.paragraphs.map { ParagraphResponseSchema.fromUseCaseData(it) }
                return ArticleResponseSchema(
                    encodeGuid(data.guid),
                    data.title,
                    data.marker,
                    paragraphs
                )
            }
        }
    }

    data class ParagraphResponseSchema
    private constructor(val guid: String, val marker: String? = null, val text: String) {
        companion object {
            fun fromUseCaseData(data: Paragraph): ParagraphResponseSchema {
                return ParagraphResponseSchema(encodeGuid(data.guid), data.marker, data.text)
            }
        }
    }
}