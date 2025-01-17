package de.bund.digitalservice.ris.norms.framework.adapter.output.database.repository

import de.bund.digitalservice.ris.norms.framework.adapter.output.database.dto.FileReferenceDto
import java.util.*
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FileReferenceRepository : ReactiveCrudRepository<FileReferenceDto, UUID> {

  fun findByNormGuid(normGuid: UUID): Flux<FileReferenceDto>
}
