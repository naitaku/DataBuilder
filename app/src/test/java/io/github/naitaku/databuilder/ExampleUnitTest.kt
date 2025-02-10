package io.github.naitaku.databuilder

import io.github.naitaku.databuilder.annotation.DataBuilder
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun default_value() {
        val data = TestDataBuilder().build()
        assertEquals("Naito", data.name)
        assertEquals(18, data.age)
    }

    @Test
    fun set_value() {
        val data = TestDataBuilder()
            .name("Ito")
            .age(16)
            .build()
        assertEquals("Ito", data.name)
        assertEquals(16, data.age)
    }

}

@DataBuilder
data class TestData(val name:String = "Naito", val age:Int = 18)