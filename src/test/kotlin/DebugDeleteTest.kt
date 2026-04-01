import rope.EnhancedRope
import util.delete
import org.junit.jupiter.api.Test

class DebugDeleteTest {

    @Test
    fun debugDelete() {
        val rope = EnhancedRope.fromString("hello beautiful world", "")
        println("Original: '${rope.toString()}'")
        println("Length: ${rope.length}")

        // Посимвольный вывод для проверки
        val chars = rope.toString().toList()
        for ((i, c) in chars.withIndex()) {
            println("$i: '$c'")
        }

        val result = delete(rope, 6, 9)
        println("After delete(6, 9): '${result.toString()}'")
        println("Result length: ${result.length}")
    }
}