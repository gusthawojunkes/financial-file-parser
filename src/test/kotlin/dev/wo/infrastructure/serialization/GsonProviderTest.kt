package dev.wo.infrastructure.serialization

import com.google.gson.reflect.TypeToken
import kotlin.test.Test

class GsonProviderTest {

    data class TestObject(val name: String, val age: Int)

    @Test
    fun `should serialize object to json`() {
        val obj = TestObject("test", 1)
        val json = GsonProvider.toJson(obj)
        assert(json.isNotEmpty())
    }

    @Test
    fun `should deserialize json to object`() {
        val json = """{"name":"test","age":1}"""
        val obj = GsonProvider.fromJson<TestObject>(json)
        assert(obj.name == "test")
        assert(obj.age == 1)
    }

    @Test
    fun `should deserialize json to object with type`() {
        val json = """{"name":"test","age":1}"""
        val type = object : TypeToken<TestObject>() {}.type
        val obj = GsonProvider.fromJson<TestObject>(json, type)
        assert(obj.name == "test")
        assert(obj.age == 1)
    }
}