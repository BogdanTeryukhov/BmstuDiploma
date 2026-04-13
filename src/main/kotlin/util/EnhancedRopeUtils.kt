package util

import rope.EnhancedRope
import automata.KmpAutomaton

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

/**
 * Получает функцию действия для подверевки и конкретного автомата
 */
private fun getActionFunctionForSubrope(rope: EnhancedRope, automaton: automata.KmpAutomaton): (Int) -> Int {
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
private fun findPatternInSubrope(rope: EnhancedRope, automaton: automata.KmpAutomaton, initialState: Int): Int {
    return when (rope) {
        is EnhancedRope.Leaf -> {
            // Для листа используем стандартный поиск по тексту
            val distanceFunc = automaton.distanceFunction(rope.text)
            val distance = distanceFunc(initialState)
            val result = if (distance == -1) -1 else distance - automaton.pattern.length

            // Отладочный вывод для "c0"
            if (automaton.pattern == "c0") {
                println("DEBUG: Leaf search in '${rope.text}': distance=$distance, result=$result")
            }

            return result
        }

        is EnhancedRope.Node -> {
            // Отладочный вывод для "c0"
            if (automaton.pattern == "c0") {
                println("DEBUG: Node search: left='${rope.left.toString()}' (len=${rope.left.length}), right='${rope.right.toString()}' (len=${rope.right.length})")
            }

            // Проверяем левое поддерево
            val leftDistance = findPatternInSubrope(rope.left, automaton, initialState)

            if (leftDistance != -1) {
                // Нашли в левом поддереве
                if (automaton.pattern == "c0") {
                    println("DEBUG: Found in left subtree at position $leftDistance")
                }
                return leftDistance
            }

            // Если не нашли в левом поддереве, проверяем правое
            // Но нужно учесть состояние после обработки левого поддерева
            val stateAfterLeft = getActionFunctionForSubrope(rope.left, automaton)(initialState)
            if (automaton.pattern == "c0") {
                println("DEBUG: State after left subtree: $stateAfterLeft")
            }

            val rightDistance = findPatternInSubrope(rope.right, automaton, stateAfterLeft)

            if (rightDistance != -1) {
                // Нашли в правом поддереве, добавляем длину левого поддерева
                val result = rope.left.length + rightDistance
                if (automaton.pattern == "c0") {
                    println("DEBUG: Found in right subtree at position $result (right pos: $rightDistance)")
                }
                return result
            }

            // Не нашли нигде
            if (automaton.pattern == "c0") {
                println("DEBUG: Not found in node")
            }
            return -1
        }
    }
}

/**
 * Поиск подстроки с использованием конечного автомата
 */
private fun EnhancedRope.findPatternWithAutomaton(automaton: automata.KmpAutomaton, initialState: Int): Int {
    return when (this) {
        is EnhancedRope.Leaf -> {
            // Для листа используем стандартный поиск по тексту
            val distanceFunc = automaton.distanceFunction(this.text)
            val distance = distanceFunc(initialState)
            if (distance == -1) -1 else distance - automaton.pattern.length
        }

        is EnhancedRope.Node -> {
            // Для узла создаем аннотации динамически
            // Проверяем левое поддерево
            val leftDistance = findPatternInSubrope(this.left, automaton, initialState)

            if (leftDistance != -1) {
                // Нашли в левом поддереве
                return leftDistance
            }

            // Если не нашли в левом поддереве, проверяем правое
            // Но нужно учесть состояние после обработки левого поддерева
            val stateAfterLeft = getActionFunctionForSubrope(this.left, automaton)(initialState)
            val rightDistance = findPatternInSubrope(this.right, automaton, stateAfterLeft)

            if (rightDistance != -1) {
                // Нашли в правом поддереве, добавляем длину левой части
                return this.left.length + rightDistance
            }

            // Не нашли нигде
            return -1
        }
    }
}

fun EnhancedRope.findPattern(pattern: String): Int {
    // Создаем автомат для поиска подстроки
    val automaton = automata.KmpAutomaton(pattern)

    // Используем динамическое создание аннотаций для эффективного поиска
    return this.findPatternWithAutomaton(automaton, automaton.initialState)
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