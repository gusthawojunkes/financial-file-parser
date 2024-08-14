package dev.wo.infrastructure.adapters

import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.models.ofx.OFXFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*
import javax.xml.bind.JAXBContext

object FileService {
    fun validateFileType(__file: File, expectedType: String): Boolean {
        val availableFileTypes = avaliableFileTypes()
        return availableFileTypes.contains(expectedType)
    }

    fun avaliableFileTypes(): List<String> = listOf("OFX")

    public fun <T> unmarshalFile(file: File, type: Class<T>): T? {
        return try {
            val context: JAXBContext = JAXBContext.newInstance(type)
            val unmarshaller = context.createUnmarshaller()
            unmarshaller.unmarshal(file) as T?
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    public fun writeNewFile(file: File, cleanedContent: String): File {
        try {
            BufferedWriter(FileWriter(file)).use { writer ->
                writer.write(cleanedContent)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw FileProcessingException("Error writing file: ${e.message}")
        }

        return file
    }
}
