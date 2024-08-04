package dev.wo.infrastructure.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.wo.infrastructure.serialization.adapters.LocalDateTimeAdapter
import java.time.LocalDateTime
import java.lang.reflect.Type

object GsonProvider {
    val gson: Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    fun toJson(obj: Any): String = gson.toJson(obj)

    inline fun <reified T> fromJson(json: String): T = gson.fromJson(json, T::class.java)

    inline fun <reified T> fromJson(json: String, type: Type): T = gson.fromJson(json, type)
}