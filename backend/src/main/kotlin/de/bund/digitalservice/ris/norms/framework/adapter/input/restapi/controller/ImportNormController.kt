package de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.controller

import de.bund.digitalservice.ris.norms.application.port.input.ImportNormUseCase
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.ApiConfiguration
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.encodeGuid
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RestController
@RequestMapping(ApiConfiguration.API_NORMS_PATH)
class ImportNormController(private val importNormService: ImportNormUseCase) {

    @PostMapping
    fun createNorm(@RequestBody zipFile: ByteArray, @RequestHeader headers: HttpHeaders): Mono<ResponseEntity<ResponseSchema>> {
        val filename = headers.getFirst("X-Filename") ?: "norm.zip"
        val command = ImportNormUseCase.Command(zipFile, filename, headers.contentLength)

        return importNormService
            .importNorm(command)
            .map { data -> ResponseSchema.fromUseCaseData(data) }
            .map { body -> ResponseEntity.status(201).body(body) }
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

    @PostMapping(path = ["/migrate"])
    fun migrateNorm(@RequestPart files: Flux<FilePart>): Mono<ResponseEntity<ResponseSchema>> {
        return files
            .flatMap { filepart ->
                filepart.content().flatMap { dataBuffer: DataBuffer ->
                    val bytes = ByteArray(dataBuffer.readableByteCount())
                    dataBuffer.read(bytes)
                    DataBufferUtils.release(dataBuffer)
                    Mono.just(filepart.filename() to bytes)
                }
            }
            .collectList()
            .flatMap {
                val zip = zipBytes(it)
                // TODO how to name the zip file?
                val command = ImportNormUseCase.Command(zip, "norm.zip", zip.size.toLong())
                importNormService.importNorm(command)
            }
            .map { data -> ResponseSchema.fromUseCaseData(data) }
            .map { body -> ResponseEntity.status(201).body(body) }
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

    @Throws(IOException::class)
    fun zipBytes(files: MutableList<Pair<String, ByteArray>>): ByteArray {
        val baos = ByteArrayOutputStream()
        val zos = ZipOutputStream(baos)
        files.forEach {
            val entry = ZipEntry(it.first)
            entry.size = it.second.size.toLong()
            zos.putNextEntry(entry)
            zos.write(it.second)
        }
        zos.closeEntry()
        zos.close()
        return baos.toByteArray()
    }

    data class ResponseSchema private constructor(val guid: String) {
        companion object {
            fun fromUseCaseData(data: UUID) = ResponseSchema(encodeGuid(data))
        }
    }
}
