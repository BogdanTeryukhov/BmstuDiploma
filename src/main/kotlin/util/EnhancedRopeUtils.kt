package util

import rope.EnhancedRope
import automata.KmpAutomaton

/**
 * Получает функцию действия для подверевки и конкретного автомата
 */
fun getActionFunctionForSubrope(rope: EnhancedRope, automaton: KmpAutomaton): (Int) -> Int {
    return when (rope) {
        is EnhancedRope.Leaf -> {
            automaton.actionFunction(rope.text)
        }

        is EnhancedRope.Node -> {
            fun(initialState: Int): Int {
                // a(s+t) = a(t) * a(s) - сначала применяем действие левой части, потом правой
                val intermediateState = getActionFunctionForSubrope(rope.left, automaton)(initialState)
                return getActionFunctionForSubrope(rope.right, automaton)(intermediateState)
            }
        }
    }
}

/**
 * Ищет паттерн в подверевке с использованием автомата
 */
fun findPatternInSubrope(rope: EnhancedRope, automaton: KmpAutomaton, initialState: Int): Int {
    return when (rope) {
        is EnhancedRope.Leaf -> {
            // Для листа используем стандартный поиск по тексту
            val distanceFunc = automaton.distanceFunction(rope.text)
            val distance = distanceFunc(initialState)
            if (distance == -1) -1 else distance - automaton.pattern.length
        }

        is EnhancedRope.Node -> {
            // Проверяем левое поддерево
            val leftDistance = findPatternInSubrope(rope.left, automaton, initialState)

            if (leftDistance != -1) {
                // Нашли в левом поддереве
                return leftDistance
            }

            // Если не нашли в левом поддереве, проверяем правое
            // Но нужно учесть состояние после обработки левого поддерева
            val stateAfterLeft = getActionFunctionForSubrope(rope.left, automaton)(initialState)
            val rightDistance = findPatternInSubrope(rope.right, automaton, stateAfterLeft)

            if (rightDistance != -1) {
                // Нашли в правом поддереве, добавляем длину левого поддерева
                return rope.left.length + rightDistance
            }

            // Не нашли нигде
            return -1
        }
    }
}

/**
 * Разбивает верёвку на две части по указанному индексу
 */
fun split(rope: EnhancedRope, index: Int): Pair<EnhancedRope, EnhancedRope> {
    // Границы проверки
    if (index <= 0) {
        return EnhancedRope.fromString("") to rope
    }
    if (index >= rope.length) {
        return rope to EnhancedRope.fromString("")
    }

    return when (rope) {
        is EnhancedRope.Leaf -> {
            // Для листа просто разбиваем текст
            val leftText = rope.text.substring(0, index)
            val rightText = rope.text.substring(index)
            EnhancedRope.fromString(leftText) to EnhancedRope.fromString(rightText)
        }

        is EnhancedRope.Node -> {
            if (index < rope.left.length) {
                // Разбиение происходит в левой подверёвке
                val (left, mid) = split(rope.left, index)
                val right = if (mid.length > 0) {
                    EnhancedRope.concat(mid, rope.right)
                } else {
                    rope.right
                }
                left to right
            } else {
                // Разбиение происходит в правой подверёвке
                val (mid, right) = split(rope.right, index - rope.left.length)
                val left = if (mid.length > 0) {
                    EnhancedRope.concat(rope.left, mid)
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
    val inserted = EnhancedRope.fromString(value)
    return EnhancedRope.concat(EnhancedRope.concat(left, inserted), right)
}

/**
 * Удаляет подстроку из верёвки
 */
fun delete(rope: EnhancedRope, start: Int, length: Int): EnhancedRope {
    val (left, rest) = split(rope, start)
    val (_, right) = split(rest, length)
    return EnhancedRope.concat(left, right)
}



fun EnhancedRope.findPattern(pattern: String): Int {
    return this.indexOf(pattern)
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