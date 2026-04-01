import automata.KmpAutomaton
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class KmpAutomatonTest {

    @Test
    fun testBasicPatternMatching() {
        val automaton = KmpAutomaton("abc")

        // Проверяем начальное состояние
        assertEquals(0, automaton.initialState)

        // Проверяем переходы
        var state = automaton.initialState

        // Читаем 'a'
        state = automaton.transition(state, 'a')
        assertEquals(1, state)

        // Читаем 'b'
        state = automaton.transition(state, 'b')
        assertEquals(2, state)

        // Читаем 'c'
        state = automaton.transition(state, 'c')
        assertEquals(3, state) // 3 = длина паттерна, финальное состояние

        // Проверяем, что состояние финальное
        assertTrue(automaton.isFinal(state))
    }

    @Test
    fun testActionFunction() {
        val automaton = KmpAutomaton("ab")
        val actionFunc = automaton.actionFunction("xab")

        // Начинаем с начального состояния
        val result = actionFunc(0)

        // После чтения "xab" мы должны быть в состоянии 2 (прочитали "ab")
        assertEquals(2, result)
    }

    @Test
    fun testDistanceFunction() {
        val automaton = KmpAutomaton("test")
        val distanceFunc = automaton.distanceFunction("this is a test")

        // Начинаем с начального состояния
        val distance = distanceFunc(0)

        // "test" начинается с позиции 10, поэтому расстояние до конца "test" = 14
        // Но нам нужно расстояние до достижения финального состояния
        assertEquals(14, distance)
    }

    @Test
    fun testNoMatch() {
        val automaton = KmpAutomaton("xyz")
        val actionFunc = automaton.actionFunction("abcdef")
        val result = actionFunc(0)

        // Не должно быть совпадения, останемся в состоянии 0
        assertEquals(0, result)
    }
}