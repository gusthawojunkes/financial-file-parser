package dev.wo.infrastructure.serialization.adapters

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.time.LocalDateTime

class LocalDateTimeAdapterTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @Test
    fun `should write a LocalDateTime`() {
        val localDateTime = LocalDateTime.of(2023, 10, 5, 14, 30, 0)
        val json = gson.toJson(localDateTime)
        val expectedJson = "\"05/10/2023 14:30:00\""
        assertEquals(expectedJson, json)
    }

    @Test
    fun `should write a null LocalDateTime`() {
        val json = gson.toJson(null as LocalDateTime?)
        val expectedJson = "null"
        assertEquals(expectedJson, json)
    }

    @Test
    fun `should read a LocalDateTime`() {
        val json = "\"05/10/2023 14:30:00\""
        val localDateTime = gson.fromJson(json, LocalDateTime::class.java)
        val expectedLocalDateTime = LocalDateTime.of(2023, 10, 5, 14, 30, 0)
        assertEquals(expectedLocalDateTime, localDateTime)
    }

    @Test
    fun `should read a null LocalDateTime`() {
        val json = "null"
        val localDateTime = gson.fromJson(json, LocalDateTime::class.java)
        assertNull(localDateTime)
    }
}