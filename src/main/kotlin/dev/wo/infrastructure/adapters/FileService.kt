package dev.wo.infrastructure.adapters

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.util.*

class FileService {
    companion object {
        fun validateFileType(__file: File, expectedType: String): Boolean {
            val availableFileTypes = avaliableFileTypes()
            return availableFileTypes.contains(expectedType)
        }

        fun avaliableFileTypes(): List<String> = listOf("OFX")
    }
}
