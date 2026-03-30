import rope.EnhancedRope
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import util.split
import util.insert

class RopeBalanceTest {

    @Test
    fun testBalanceMechanism() {
        // Создаем верёвку, которая должна быть автоматически сбалансирована
        val rope = EnhancedRope.fromString("a".repeat(3000), "")

        // Проверяем, что результат корректный
        assertEquals(3000, rope.length)

        // Проверяем, что высота разумная (логарифмическая)
        val height = getHeight(rope)
        assertTrue(height > 0)
        assertTrue(height < 30, "Height $height should be reasonable for 3000 characters")
    }

    @Test
    fun testAutomaticBalancing() {
        // Создаем верёвку, которая должна быть сбалансирована
        val rope = EnhancedRope.fromString("a".repeat(2000), "")

        val height = getHeight(rope)
        val expectedMaxHeight = kotlin.math.log2(2000.0).toInt() + 10 // Приблизительная оценка

        // Высота должна быть логарифмической, а не линейной
        assertTrue(height < expectedMaxHeight, "Height $height should be less than expected max $expectedMaxHeight")
        assertTrue(height > 0, "Height should be positive")
    }

    @Test
    fun testBalanceAfterOperations() {
        // Создаем большую верёвку
        var rope: EnhancedRope = EnhancedRope.fromString("test".repeat(500), "")

        // Выполняем множество операций вставки
        for (i in 0 until 100) {
            rope = insert(rope, 10, "insert")
        }

        // Проверяем, что верёвка остаётся сбалансированной
        val height = getHeight(rope)
        val expectedMaxHeight = kotlin.math.log2(rope.length.toDouble()).toInt() + 10

        assertTrue(height < expectedMaxHeight, "Height should remain logarithmic after many operations")
    }

    @Test
    fun testRebalanceOperation() {
        // Создаем большую строку, которая должна быть автоматически сбалансирована
        val largeString = "x".repeat(3000)
        val rope = EnhancedRope.fromString(largeString, "")

        // Проверяем, что результат корректный
        assertEquals(largeString, rope.toString())
        assertTrue(rope.length == largeString.length)

        // Проверяем, что структура разумна
        val height = getHeight(rope)
        assertTrue(height > 0)
        assertTrue(height < 30) // Должно быть гораздо меньше, чем линейная высота
    }

    /**
     * Вспомогательная функция для получения высоты верёвки
     */
    private fun getHeight(rope: EnhancedRope): Int {
        return when (rope) {
            is EnhancedRope.Leaf -> 1
            is EnhancedRope.Node -> 1 + kotlin.math.max(getHeight(rope.left), getHeight(rope.right))
        }
    }
}