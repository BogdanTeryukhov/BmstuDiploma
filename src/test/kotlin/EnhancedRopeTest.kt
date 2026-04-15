import rope.EnhancedRope
import automata.KmpAutomaton
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EnhancedRopeTest {

    @Test
    fun testBasicRopeCreation() {
        val rope = EnhancedRope.fromString("hello world")

        // Проверяем длину
        assertEquals(11, rope.length)

        // Проверяем преобразование в строку
        assertEquals("hello world", rope.toString())
    }

    @Test
    fun testRopeConcatenation() {
        val left = EnhancedRope.fromString("hello ")
        val right = EnhancedRope.fromString("world")
        val combined = EnhancedRope.concat(left, right)

        // Проверяем длину
        assertEquals(11, combined.length)

        // Проверяем преобразование в строку
        assertEquals("hello world", combined.toString())
    }

    @Test
    fun testDynamicSearch() {
        val rope = EnhancedRope.fromString("test string")

        // Проверяем динамический поиск
        val index = rope.indexOf("str")
        // "str" находится в позиции 5
        assertEquals(5, index)
    }

    @Test
    fun testPatternSearch() {
        val rope = EnhancedRope.fromString("this is a test")

        // Проверяем поиск паттерна
        val index = rope.indexOf("test")
        // "test" начинается с позиции 10
        assertEquals(10, index)
    }

    @Test
    fun testIndexOf() {
        val rope = EnhancedRope.fromString("hello world")

        // Проверяем поиск подстроки
        val index = rope.indexOf("world")
        assertEquals(6, index) // "world" начинается с позиции 6

        // Проверяем несуществующую подстроку
        val notFound = rope.indexOf("xyz")
        assertEquals(-1, notFound)
    }

    @Test
    fun testEmptyRope() {
        val empty = EnhancedRope.fromString("")

        // Проверяем пустую верёвку
        assertEquals(0, empty.length)
        assertEquals("", empty.toString())
    }
}