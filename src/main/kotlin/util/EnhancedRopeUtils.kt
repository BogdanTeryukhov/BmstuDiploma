package util

import rope.EnhancedRope

/**
 * Разбивает верёвку на две части по указанному индексу
 */
fun split(rope: EnhancedRope, index: Int): Pair<EnhancedRope, EnhancedRope> {
    // Границы проверки
    if (index <= 0) {
        return EnhancedRope.fromString("", "") to rope
    }
    if (index >= rope.length) {
        return rope to EnhancedRope.fromString("", "")
    }

    return when (rope) {
        is EnhancedRope.Leaf -> {
            // Для листа просто разбиваем текст
            val leftText = rope.text.substring(0, index)
            val rightText = rope.text.substring(index)
            EnhancedRope.fromString(leftText, "") to EnhancedRope.fromString(rightText, "")
        }

        is EnhancedRope.Node -> {
            if (index < rope.left.length) {
                // Разбиение происходит в левой подверёвке
                val (left, mid) = split(rope.left, index)
                val right = if (mid.length > 0) {
                    EnhancedRope.concat(mid, rope.right, "")
                } else {
                    rope.right
                }
                left to right
            } else {
                // Разбиение происходит в правой подверёвке
                val (mid, right) = split(rope.right, index - rope.left.length)
                val left = if (mid.length > 0) {
                    EnhancedRope.concat(rope.left, mid, "")
                } else {
                    rope.left
                }
                left to right
            }
        }
    }
}

/**
 * Вставляет строку в верёвку по указанному индексу
 */
fun insert(rope: EnhancedRope, index: Int, value: String): EnhancedRope {
    val (left, right) = split(rope, index)
    val inserted = EnhancedRope.fromString(value, "")
    return EnhancedRope.concat(EnhancedRope.concat(left, inserted, ""), right, "")
}

/**
 * Удаляет подстроку из верёвки
 */
fun delete(rope: EnhancedRope, start: Int, length: Int): EnhancedRope {
    val (left, rest) = split(rope, start)
    val (_, right) = split(rest, length)
    return EnhancedRope.concat(left, right, "")
}

/**
 * Поиск подстроки в верёвке
 */
fun EnhancedRope.findPattern(pattern: String): Int {
    // Для поиска произвольного паттерна используем стандартный поиск
    // В реальной реализации можно было бы создавать временный автомат
    return this.toString().indexOf(pattern)
}

/**
 * Печать структуры верёвки
 */
fun EnhancedRope.print(prefix: String = "", isTail: Boolean = true) {
    when (this) {
        is EnhancedRope.Leaf -> {
            println(prefix + (if (isTail) "└── " else "├── ") + "\"${this.text}\" (len=${this.length})")
        }

        is EnhancedRope.Node -> {
            println(prefix + (if (isTail) "└── " else "├── ") + "Node[len=${this.length}]")

            this.left.print(prefix + (if (isTail) "    " else "│   "), false)
            this.right.print(prefix + (if (isTail) "    " else "│   "), true)
        }
    }
}