package util

import rope.Rope

fun Rope.toStr(): String = when (this) {
    is Rope.Leaf -> text
    is Rope.Node -> left.toStr() + right.toStr()
}

fun split(rope: Rope, index: Int): Pair<Rope, Rope> {
    return when (rope) {

        is Rope.Leaf -> {
            val left = rope.text.substring(0, index)
            val right = rope.text.substring(index)
            Rope.Leaf(left) to Rope.Leaf(right)
        }

        is Rope.Node -> {
            if (index < rope.left.length) {
                val (l, r) = split(rope.left, index)
                l to Rope.concat(r, rope.right)
            } else {
                val (l, r) = split(rope.right, index - rope.left.length)
                Rope.concat(rope.left, l) to r
            }
        }
    }
}

fun insert(rope: Rope, index: Int, value: String): Rope {
    val (left, right) = split(rope, index)
    return Rope.concat(Rope.concat(left, Rope.Leaf(value)), right)
}

fun delete(rope: Rope, start: Int, length: Int): Rope {
    val (left, rest) = split(rope, start)
    val (_, right) = split(rest, length)
    return Rope.concat(left, right)
}

fun Rope.indexOf(pattern: String): Int {
    // Упрощённый вариант (можно заменить на KMP при необходимости)
    return this.toStr().indexOf(pattern)
}

fun Rope.print(prefix: String = "", isTail: Boolean = true) {
    when (this) {

        is Rope.Leaf -> {
            println(prefix + (if (isTail) "└── " else "├── ") + "\"$text\"")
        }

        is Rope.Node -> {
            println(prefix + (if (isTail) "└── " else "├── ") + "[len=$length]")

            left.print(prefix + (if (isTail) "    " else "│   "), false)
            right.print(prefix + (if (isTail) "    " else "│   "), true)
        }
    }
}