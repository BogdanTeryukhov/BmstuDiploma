import rope.EnhancedRope
import util.split
import util.insert
import util.delete
import util.findPattern
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EnhancedRopeUtilsTest {

    @Test
    fun testSplit() {
        val rope = EnhancedRope.fromString("hello world", "")
        val (left, right) = split(rope, 6)

        assertEquals("hello ", left.toString())
        assertEquals("world", right.toString())
        assertEquals(11, left.length + right.length)
    }

    @Test
    fun testInsert() {
        val rope = EnhancedRope.fromString("hello world", "")
        val result = insert(rope, 6, "beautiful ")

        assertEquals("hello beautiful world", result.toString())
        assertEquals(21, result.length) // "hello " (6) + "beautiful " (10) + "world" (5) = 21
    }

    @Test
    fun testDelete() {
        val rope = EnhancedRope.fromString("hello beautiful world", "")
        val result = delete(rope, 6, 10) // Удаляем "beautiful " (9 букв + 1 пробел = 10 символов)

        assertEquals("hello world", result.toString())
        assertEquals(11, result.length)
    }

    @Test
    fun testFindPattern() {
        val rope = EnhancedRope.fromString("hello world", "")
        val index = rope.findPattern("world")

        assertEquals(6, index) // "world" начинается с позиции 6

        // Проверяем несуществующий паттерн
        val notFound = rope.findPattern("xyz")
        assertEquals(-1, notFound)
    }

    @Test
    fun testEdgeCases() {
        // Тест с пустой верёвкой
        val empty = EnhancedRope.fromString("", "")
        val (left, right) = split(empty, 0)

        assertEquals("", left.toString())
        assertEquals("", right.toString())

        // Тест вставки в начало
        val startInsert = insert(empty, 0, "hello")
        assertEquals("hello", startInsert.toString())

        // Тест удаления из пустой верёвки
        val emptyDelete = delete(empty, 0, 5)
        assertEquals("", emptyDelete.toString())
    }
}