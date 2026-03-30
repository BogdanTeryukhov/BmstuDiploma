package util

import rope.Rope

fun split(rope: Rope, index: Int): Pair<Rope, Rope> {
    val str = rope.toString()
    if (index <= 0) {
        return Rope.Leaf("") to rope
    }
    if (index >= str.length) {
        return rope to Rope.Leaf("")
    }

    val leftStr = str.substring(0, index)
    val rightStr = str.substring(index)
    return Rope.Leaf(leftStr) to Rope.Leaf(rightStr)
}

fun insert(rope: Rope, index: Int, value: String): Rope {
    val (left, right) = split(rope, index)
    val resultStr = left.toString() + value + right.toString()
    return Rope.Leaf(resultStr)
}

fun delete(rope: Rope, start: Int, length: Int): Rope {
    val (left, rest) = split(rope, start)
    val safeLength = kotlin.math.min(length, rest.length)
    val (_, right) = split(rest, safeLength)
    val resultStr = left.toString() + right.toString()
    return Rope.Leaf(resultStr)
}

fun Rope.indexOf(pattern: String): Int {
    return this.toString().indexOf(pattern)
}

fun Rope.print(prefix: String = "", isTail: Boolean = true) {
    when (this) {
        is Rope.Leaf -> {
            println(prefix + (if (isTail) "└── " else "├── ") + "\"$text\"")
        }
    }
}