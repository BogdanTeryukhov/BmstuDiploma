package automata

/**
 * Автомат для поиска подстрок по регулярному выражению вида .*s.*
 * где s - искомая подстрока
 */
class RegexAutomaton(private val pattern: String) : Automaton<RegexState, Char> {

    override val initialState: RegexState = RegexState.START

    override fun transition(state: RegexState, symbol: Char): RegexState {
        return when (state) {
            RegexState.START -> {
                // В начальном состоянии мы либо остаемся в START, либо переходим к поиску паттерна
                if (pattern.isNotEmpty() && pattern[0] == symbol) {
                    if (pattern.length == 1) {
                        RegexState.FOUND // Нашли односимвольный паттерн
                    } else {
                        RegexState.MATCHING(1) // Начинаем сопоставлять со второго символа
                    }
                } else {
                    RegexState.START // Продолжаем искать начало паттерна
                }
            }

            is RegexState.MATCHING -> {
                // Продолжаем сопоставлять паттерн
                if (state.position < pattern.length && pattern[state.position] == symbol) {
                    if (state.position + 1 == pattern.length) {
                        RegexState.FOUND // Закончили сопоставление
                    } else {
                        RegexState.MATCHING(state.position + 1) // Продолжаем сопоставлять
                    }
                } else {
                    // Сопоставление прервано, возвращаемся к началу
                    transition(RegexState.START, symbol)
                }
            }

            RegexState.FOUND -> {
                // После нахождения паттерна мы можем продолжать читать любые символы
                RegexState.FOUND
            }
        }
    }

    override fun isFinal(state: RegexState): Boolean {
        return state == RegexState.FOUND
    }

    /**
     * Функция действия a(s) - преобразует состояние после обработки строки s
     */
    fun actionFunction(input: String): (RegexState) -> RegexState {
        return fun(initialState: RegexState): RegexState {
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
     * Возвращает 0 если начальное состояние уже финальное
     */
    fun distanceFunction(input: String): (RegexState) -> Int {
        return fun(initialState: RegexState): Int {
            // Если начальное состояние уже финальное
            if (isFinal(initialState)) {
                return 0
            }

            var state = initialState
            for ((index, char) in input.withIndex()) {
                state = transition(state, char)
                if (isFinal(state)) {
                    return index + 1
                }
            }
            return -1 // Финальное состояние не достигнуто
        }
    }

    /**
     * Находит позицию начала найденного паттерна в строке
     * Возвращает -1 если паттерн не найден
     */
    fun findPatternStart(input: String): Int {
        val kmp = KmpAutomaton(pattern)
        val actionFunc = kmp.actionFunction(input)
        val distanceFunc = kmp.distanceFunction(input)

        val distance = distanceFunc(0)
        if (distance == -1) {
            return -1
        }

        // Позиция начала = позиция конца - длина паттерна
        return distance - pattern.length
    }

    /**
     * Находит позицию конца найденного паттерна в строке
     * Возвращает -1 если паттерн не найден
     */
    fun findPatternEnd(input: String): Int {
        val kmp = KmpAutomaton(pattern)
        val distanceFunc = kmp.distanceFunction(input)

        val distance = distanceFunc(0)
        return if (distance == -1) -1 else distance
    }
}

/**
 * Состояния автомата для регулярного выражения .*s.*
 */
sealed class RegexState {
    object START : RegexState()          // Начальное состояние, ищем начало паттерна
    data class MATCHING(val position: Int) : RegexState()  // Сопоставляем паттерн
    object FOUND : RegexState()          // Паттерн найден
}