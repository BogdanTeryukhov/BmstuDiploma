package rope

import automata.RegexAutomaton
import automata.RegexState

/**
 * Улучшенная версия верёвки с автоматными функциями для эффективного поиска
 */
sealed class EnhancedRope {

    abstract val length: Int

    /**
     * Функция действия a(s) - преобразует состояние автомата после обработки содержимого верёвки
     */
    abstract val actionFunction: (RegexState) -> RegexState

    /**
     * Функция расстояния f(s) - показывает через сколько символов достигается финальное состояние
     */
    abstract val distanceFunction: (RegexState) -> Int

    /**
     * Лист верёвки - содержит текст
     */
    data class Leaf(
        val text: String,
        override val actionFunction: (RegexState) -> RegexState,
        override val distanceFunction: (RegexState) -> Int
    ) : EnhancedRope() {
        override val length: Int = text.length

        constructor(text: String, pattern: String) : this(
            text,
            createActionFunction(text, pattern),
            createDistanceFunction(text, pattern)
        )

        override fun toString(): String = this.text
    }

    /**
     * Узел верёвки - конкатенация двух подверёвок
     */
    data class Node(
        val left: EnhancedRope,
        val right: EnhancedRope,
        override val actionFunction: (RegexState) -> RegexState,
        override val distanceFunction: (RegexState) -> Int
    ) : EnhancedRope() {
        override val length: Int = left.length + right.length

        constructor(left: EnhancedRope, right: EnhancedRope, pattern: String) : this(
            left,
            right,
            createCombinedActionFunction(left, right),
            createCombinedDistanceFunction(left, right, pattern)
        )

        override fun toString(): String = this.left.toString() + this.right.toString()
    }

    companion object {
        private const val BALANCE_THRESHOLD = 4
        private const val MAX_LEAF_SIZE = 1024

        /**
         * Создает верёвку из строки
         */
        fun fromString(s: String, pattern: String = ""): EnhancedRope {
            if (s.length <= MAX_LEAF_SIZE) {
                return Leaf(s, pattern)
            }

            // Для больших строк разбиваем на части для балансировки
            val mid = s.length / 2
            val left = fromString(s.substring(0, mid), pattern)
            val right = fromString(s.substring(mid), pattern)
            return Node(left, right, pattern).balance()
        }

        /**
         * Конкатенация двух верёвок с автоматической балансировкой
         */
        fun concat(a: EnhancedRope, b: EnhancedRope, pattern: String = ""): EnhancedRope {
            if (a.length == 0) return b
            if (b.length == 0) return a

            val newNode = Node(a, b, pattern)
            return newNode.balance()
        }

        /**
         * Создает функцию действия для текста и паттерна
         */
        private fun createActionFunction(text: String, pattern: String): (RegexState) -> RegexState {
            if (pattern.isEmpty()) {
                return fun(state: RegexState): RegexState = state // Пустой паттерн ничего не меняет
            }

            val automaton = RegexAutomaton(pattern)
            return automaton.actionFunction(text)
        }

        private fun createDistanceFunction(text: String, pattern: String): (RegexState) -> Int {
            if (pattern.isEmpty()) {
                return fun(_: RegexState): Int = 0 // Пустой паттерн найден сразу
            }

            val automaton = RegexAutomaton(pattern)
            return automaton.distanceFunction(text)
        }

        private fun createCombinedActionFunction(left: EnhancedRope, right: EnhancedRope): (RegexState) -> RegexState {
            return fun(initialState: RegexState): RegexState {
                // a(s+t) = a(t) * a(s) - сначала применяем действие левой части, потом правой
                val intermediateState = left.actionFunction(initialState)
                return right.actionFunction(intermediateState)
            }
        }

        private fun createCombinedDistanceFunction(
            left: EnhancedRope,
            right: EnhancedRope,
            pattern: String
        ): (RegexState) -> Int {
            return fun(initialState: RegexState): Int {
                // Сначала проверяем левую часть
                val leftDistance = left.distanceFunction(initialState)

                if (leftDistance != -1) {
                    // Нашли в левой части
                    return leftDistance
                }

                // Если не нашли в левой части, проверяем правую часть
                // Но нужно учесть состояние после обработки левой части
                val stateAfterLeft = left.actionFunction(initialState)
                val rightDistance = right.distanceFunction(stateAfterLeft)

                return if (rightDistance != -1) {
                    // Нашли в правой части, добавляем длину левой части
                    left.length + rightDistance
                } else {
                    -1 // Не нашли нигде
                }
            }
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
        // Для простоты используем автомат для поиска
        val automaton = RegexAutomaton(pattern)
        return automaton.findPatternStart(this.toString())
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
                    return Node(balancedLeft, balancedRight, this.actionFunction, this.distanceFunction)
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
        return Companion.fromString(this.toString(), getPatternFromNode())
    }

    /**
     * Получает паттерн из узла для создания новых автоматов
     */
    private fun getPatternFromNode(): String {
        // В реальной реализации можно анализировать содержимое для выбора оптимального паттерна
        // Пока используем пустой паттерн
        return ""
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