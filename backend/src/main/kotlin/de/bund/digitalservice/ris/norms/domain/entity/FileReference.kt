package de.bund.digitalservice.ris.norms.domain.entity

import java.security.MessageDigest
import java.util.HexFormat

data class FileReference(
    val name: String,
    val hash: String,
)

fun getHashFromContent(bytes: ByteArray): String = HexFormat
    .of().formatHex(
        MessageDigest
            .getInstance("SHA-256")
            .digest(bytes),
    )