import rope.EnhancedRope
import util.split
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DebugSplitTest {

    @Test
    fun debugSplitAndDelete() {
        val rope = EnhancedRope.fromString("hello beautiful world", "test")
        println("Original: '${rope.toString()}' length: ${rope.length}")

        // Разбиваем на позиции 6
        val (left, rest) = split(rope, 6)
        println("Left: '${left.toString()}' length: ${left.length}")
        println("Rest: '${rest.toString()}' length: ${rest.length}")

        // Разбиваем rest на позиции 9
        val (middle, right) = split(rest, 9)
        println("Middle: '${middle.toString()}' length: ${middle.length}")
        println("Right: '${right.toString()}' length: ${right.length}")

        // Склеиваем left и right
        val result = EnhancedRope.concat(left, right, "")
        println("Result: '${result.toString()}' length: ${result.length}")
    }
}