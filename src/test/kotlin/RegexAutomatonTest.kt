import automata.RegexAutomaton
import automata.RegexState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RegexAutomatonTest {

    @Test
    fun testBasicPatternMatching() {
        val automaton = RegexAutomaton("abc")

        // Проверяем начальное состояние
        assertEquals(RegexState.START, automaton.initialState)

        // Проверяем переходы
        var state = automaton.initialState

        // Читаем символы до начала паттерна
        state = automaton.transition(state, 'x')
        assertEquals(RegexState.START, state)

        state = automaton.transition(state, 'y')
        assertEquals(RegexState.START, state)

        // Начинаем сопоставлять паттерн 'abc'
        state = automaton.transition(state, 'a')
        assertTrue(state is RegexState.MATCHING)
        assertEquals(1, (state as RegexState.MATCHING).position)

        state = automaton.transition(state, 'b')
        assertTrue(state is RegexState.MATCHING)
        assertEquals(2, (state as RegexState.MATCHING).position)

        state = automaton.transition(state, 'c')
        assertEquals(RegexState.FOUND, state)

        // После нахождения паттерна остаемся в FOUND
        state = automaton.transition(state, 'z')
        assertEquals(RegexState.FOUND, state)

        // Проверяем, что состояние финальное
        assertTrue(automaton.isFinal(state))
    }

    @Test
    fun testActionFunction() {
        val automaton = RegexAutomaton("test")
        val actionFunc = automaton.actionFunction("this is a test")

        // Начинаем с начального состояния
        val result = actionFunc(RegexState.START)

        // После чтения всей строки мы должны найти паттерн "test"
        assertEquals(RegexState.FOUND, result)
    }

    @Test
    fun testDistanceFunction() {
        val automaton = RegexAutomaton("ab")
        val distanceFunc = automaton.distanceFunction("xxabyy")

        // Начинаем с начального состояния
        val distance = distanceFunc(RegexState.START)

        // "ab" начинается с позиции 2, заканчивается в позиции 4, так что расстояние = 4
        assertEquals(4, distance)
    }

    @Test
    fun testNoMatch() {
        val automaton = RegexAutomaton("xyz")
        val actionFunc = automaton.actionFunction("abcdef")
        val result = actionFunc(RegexState.START)

        // Не должно быть совпадения, останемся в состоянии START
        assertEquals(RegexState.START, result)
    }

    @Test
    fun testFindPatternPositions() {
        val automaton = RegexAutomaton("test")

        // Находим позицию начала
        val startPos = automaton.findPatternStart("this is a test string")
        assertEquals(10, startPos) // "test" начинается с позиции 10

        // Находим позицию конца
        val endPos = automaton.findPatternEnd("this is a test string")
        assertEquals(14, endPos) // "test" заканчивается в позиции 14 (после последнего символа)

        // Проверяем случай, когда паттерн не найден
        val notFoundStart = automaton.findPatternStart("no match here")
        assertEquals(-1, notFoundStart)

        val notFoundEnd = automaton.findPatternEnd("no match here")
        assertEquals(-1, notFoundEnd)
    }
}