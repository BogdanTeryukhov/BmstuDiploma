import rope.EnhancedRope
import automata.RegexState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EnhancedRopeTest {

    @Test
    fun testBasicRopeCreation() {
        val rope = EnhancedRope.fromString("hello world", "world")

        // Проверяем длину
        assertEquals(11, rope.length)

        // Проверяем преобразование в строку
        assertEquals("hello world", rope.toString())
    }

    @Test
    fun testRopeConcatenation() {
        val left = EnhancedRope.fromString("hello ", "lo")
        val right = EnhancedRope.fromString("world", "wo")
        val combined = EnhancedRope.concat(left, right, "lo")

        // Проверяем длину
        assertEquals(11, combined.length)

        // Проверяем преобразование в строку
        assertEquals("hello world", combined.toString())
    }

    @Test
    fun testActionFunction() {
        val rope = EnhancedRope.fromString("test string", "str")

        // Проверяем функцию действия
        val actionFunc = rope.actionFunction
        val result = actionFunc(RegexState.START)

        // Результат зависит от паттерна, но должен быть каким-то состоянием
        assertNotNull(result)
    }

    @Test
    fun testDistanceFunction() {
        val rope = EnhancedRope.fromString("this is a test", "test")

        // Проверяем функцию расстояния
        val distanceFunc = rope.distanceFunction
        val distance = distanceFunc(RegexState.START)

        // "test" начинается с позиции 10, так что расстояние должно быть 14
        // (позиция после последнего символа "test")
        assertEquals(14, distance)
    }

    @Test
    fun testIndexOf() {
        val rope = EnhancedRope.fromString("hello world", "world")

        // Проверяем поиск подстроки
        val index = rope.indexOf("world")
        assertEquals(6, index) // "world" начинается с позиции 6

        // Проверяем несуществующую подстроку
        val notFound = rope.indexOf("xyz")
        assertEquals(-1, notFound)
    }

    @Test
    fun testEmptyRope() {
        val empty = EnhancedRope.fromString("", "test")

        // Проверяем пустую верёвку
        assertEquals(0, empty.length)
        assertEquals("", empty.toString())
    }
}