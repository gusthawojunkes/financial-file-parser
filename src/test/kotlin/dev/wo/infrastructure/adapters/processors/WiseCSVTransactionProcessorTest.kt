package dev.wo.infrastructure.adapters.processors

import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WiseCSVTransactionProcessorTest {

    @Test
    fun `when processFile is called with a non empty comma separated file then it should return a list of transactions`() {
        val processor = WiseCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/wise_comma.csv")
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isNotEmpty())
    }

}