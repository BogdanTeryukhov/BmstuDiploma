package automata

/**
 * Автомат для поиска подстроки по алгоритму Кнута-Морриса-Пратта
 *
 * @param pattern искомая подстрока
 */
class KmpAutomaton(val pattern: String) : Automaton<Int, Char> {

    // Таблица отказов для алгоритма KMP
    private val failureTable: IntArray = computeFailureFunction(pattern)

    override val initialState: Int = 0

    override fun transition(state: Int, symbol: Char): Int {
        // Если символ совпадает с ожидаемым в текущей позиции
        if (state < pattern.length && pattern[state] == symbol) {
            return state + 1
        }

        // Если мы уже нашли всю подстроку, начинаем заново
        if (state == pattern.length) {
            // Пытаемся продолжить с начала
            if (pattern.isNotEmpty() && pattern[0] == symbol) {
                return 1
            } else {
                return transition(initialState, symbol)
            }
        }

        // Используем таблицу отказов для определения следующего состояния
        var currentState = state
        while (currentState > 0) {
            val failureState = failureTable[currentState]
            if (failureState < pattern.length && pattern[failureState] == symbol) {
                return failureState + 1
            }
            currentState = failureState
        }

        // Начинаем с начала, если символ совпадает с первым символом паттерна
        return if (pattern.isNotEmpty() && pattern[0] == symbol) 1 else 0
    }

    override fun isFinal(state: Int): Boolean {
        return state == pattern.length
    }

    /**
     * Вычисляет таблицу отказов для алгоритма KMP
     */
    private fun computeFailureFunction(pattern: String): IntArray {
        val table = IntArray(pattern.length + 1)
        table[0] = -1
        var i = 0
        var j = -1

        while (i < pattern.length) {
            while (j >= 0 && pattern[i] != pattern[j]) {
                j = table[j]
            }
            i++
            j++
            table[i] = j
        }

        return table
    }

    /**
     * Функция действия a(s) - преобразует состояние после обработки строки s
     */
    fun actionFunction(input: String): (Int) -> Int {
        return fun(initialState: Int): Int {
            var state = initialState
            for (char in input) {
                state = transition(state, char)
            }
            return state
        }
    }

    /**
     * Функция расстояния f(s) - показывает через сколько символов достигается финальное состояние
     * Возвращает -1 если финальное состояние недостижимо
     */
    fun distanceFunction(input: String): (Int) -> Int {
        return fun(initialState: Int): Int {
            var state = initialState
            for ((index, char) in input.withIndex()) {
                state = transition(state, char)
                if (isFinal(state)) {
                    val result = index + 1
                    if (pattern == "c0") {
                        println("DEBUG KMP: Found at distance $result")
                    }
                    return result
                }
            }
            return -1 // Финальное состояние не достигнуто
        }
    }
}