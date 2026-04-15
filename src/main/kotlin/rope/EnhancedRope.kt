package rope

import automata.KmpAutomaton

/**
 * Улучшенная версия верёвки с автоматными функциями для эффективного поиска
 */
sealed class EnhancedRope {

    abstract val length: Int

    /**
     * Лист верёвки - содержит текст
     */
    data class Leaf(val text: String) : EnhancedRope() {
        override val length: Int = text.length
        override fun toString(): String = this.text
    }

    /**
     * Узел верёвки - конкатенация двух подверёвок
     */
    data class Node(
        val left: EnhancedRope,
        val right: EnhancedRope
    ) : EnhancedRope() {
        override val length: Int = left.length + right.length
        override fun toString(): String = this.left.toString() + this.right.toString()
    }

    companion object {
        private const val BALANCE_THRESHOLD = 4
        private const val MAX_LEAF_SIZE = 1024

        /**
         * Создает верёвку из строки
         */
        fun fromString(s: String): EnhancedRope {
            if (s.length <= MAX_LEAF_SIZE) {
                return Leaf(s)
            }

            // Для больших строк разбиваем на части для балансировки
            val mid = s.length / 2
            val left = fromString(s.substring(0, mid))
            val right = fromString(s.substring(mid))
            return Node(left, right).balance()
        }

        /**
         * Конкатенация двух верёвок с автоматической балансировкой
         */
        fun concat(a: EnhancedRope, b: EnhancedRope): EnhancedRope {
            if (a.length == 0) return b
            if (b.length == 0) return a

            val newNode = Node(a, b)
            return newNode.balance()
        }
    }

    override fun toString(): String {
        return when (this) {
            is Leaf -> this.text
            is Node -> this.left.toString() + this.right.toString()
        }
    }

    /**
     * Поиск подстроки в верёвке
     * Возвращает позицию начала найденной подстроки или -1 если не найдено
     */
    fun indexOf(pattern: String): Int {
        if (pattern.isEmpty()) return 0
        if (pattern.length > this.length) return -1

        // Создаем автомат для поиска подстроки
        val automaton = KmpAutomaton(pattern)

        // Используем динамическое создание аннотаций для эффективного поиска
        return this.findPatternWithAutomaton(automaton, automaton.initialState)
    }

    /**
     * Поиск подстроки с использованием конечного автомата
     */
    private fun findPatternWithAutomaton(automaton: KmpAutomaton, initialState: Int): Int {
        return util.findPatternInSubrope(this, automaton, initialState)
    }

    /**
     * Балансирует верёвку для обеспечения O(log N) сложности операций
     */
    protected fun balance(): EnhancedRope {
        return when (this) {
            is Leaf -> this
            is Node -> {
                val leftHeight = this.left.height()
                val rightHeight = this.right.height()

                // Проверяем необходимость балансировки
                if (kotlin.math.abs(leftHeight - rightHeight) > BALANCE_THRESHOLD) {
                    // Выполняем балансировку
                    return this.rebalance()
                }

                // Рекурсивно балансируем поддеревья
                val balancedLeft = this.left.balance()
                val balancedRight = this.right.balance()

                // Если балансировка изменила структуру, создаем новый узел
                if (balancedLeft !== this.left || balancedRight !== this.right) {
                    return Node(balancedLeft, balancedRight)
                }

                this
            }
        }
    }

    /**
     * Выполняет полную ребалансировку путем преобразования в строку и обратно
     */
    private fun rebalance(): EnhancedRope {
        // Преобразуем в строку и создаем сбалансированное дерево
        return Companion.fromString(this.toString())
    }


    /**
     * Вычисляет высоту дерева
     */
    protected fun height(): Int {
        return when (this) {
            is Leaf -> 1
            is Node -> 1 + kotlin.math.max(this.left.height(), this.right.height())
        }
    }
}