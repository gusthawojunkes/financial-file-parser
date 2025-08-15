package dev.wo.infrastructure.adapters

import dev.wo.domain.exceptions.FileProcessingException
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.xml.bind.JAXBContext
import javax.xml.bind.ValidationEventHandler

object FileService {

    private val logger = LoggerFactory.getLogger(FileService::class.java)

    fun validateFileType(expectedType: String): Boolean {
        val availableFileTypes = avaliableFileTypes()
        return availableFileTypes.contains(expectedType.uppercase())
    }

    fun avaliableFileTypes(): List<String> = listOf("OFX", "CSV")

    fun unmarshalFile(file: File, type: Class<*>): Any? {
        return try {
            val context: JAXBContext = JAXBContext.newInstance(type)
            val unmarshaller = context.createUnmarshaller()
            unmarshaller.eventHandler = ValidationEventHandler { event ->
                logger.debug("Evento de validação: ${event.message}")
                true
            }
            unmarshaller.unmarshal(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun writeNewFile(file: File, cleanedContent: String): File {
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
