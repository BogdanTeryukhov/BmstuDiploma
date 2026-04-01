package automata

/**
 * Базовый интерфейс для конечного автомата
 */
interface Automaton<State, Symbol> {
    /**
     * Функция переходов автомата
     * @param state текущее состояние
     * @param symbol входной символ
     * @return следующее состояние
     */
    fun transition(state: State, symbol: Symbol): State

    /**
     * Проверяет, является ли состояние финальным
     */
    fun isFinal(state: State): Boolean

    /**
     * Начальное состояние автомата
     */
    val initialState: State
}