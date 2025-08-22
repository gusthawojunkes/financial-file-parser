package dev.wo.infrastructure.adapters

import dev.wo.domain.exceptions.WriteFileException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileServiceTest {

    @Test
    fun writesContentToFileSuccessfully() {
        val file = File.createTempFile("testFile", ".txt")
        val content = "Sample content"

        val result = FileService.writeNewFile(file, content)

        assertTrue(result.exists())
        assertEquals(content, result.readText())
    }

    @Test
    fun throwsExceptionWhenFileCannotBeWritten() {
        val file = File("/invalid/path/testFile.txt")
        val content = "Sample content"

        val exception = assertThrows(WriteFileException::class.java) {
            FileService.writeNewFile(file, content)
        }

        assertTrue(exception.message.contains("Error writing file"))
    }

    @Test
    fun writesEmptyContentToFileSuccessfully() {
        val file = File.createTempFile("testFile", ".txt")
        val content = ""

        val result = FileService.writeNewFile(file, content)

        assertTrue(result.exists())
        assertEquals(content, result.readText())
    }
}