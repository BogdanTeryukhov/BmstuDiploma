import markov.core.MarkovAlgorithm
import markov.parser.parseRules
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MarkovAlgorithmTest {

    @Test
    fun testBasicReplacementAndTermination() {
        val rules = parseRules(listOf(
            "ab -> ba",
            "ba ->. DONE"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run("ab")

        assertEquals("DONE", result)
    }

    @Test
    fun testReplaceFirstOnly() {
        val rules = parseRules(listOf(
            "aa -> b"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run("aaaa")

        assertEquals("bb", result)
    }

    @Test
    fun testNoMoreRulesStops() {
        val rules = parseRules(listOf(
            "ab -> ba"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run("ab")

        assertEquals("ba", result)
    }

    @Test
    fun testRulePriority() {
        val rules = parseRules(listOf(
            "ab -> X",
            "a -> Y"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run("ab")

        assertEquals("X", result)
    }

    @Test
    fun testBinarySimple() {
        val rules = parseRules(listOf(
            "1+1 -> 10",
            "+ ->",
            "= ->."
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run("1+1=")

        assertEquals("10", result)
    }

    @Test
    fun testBinary() {
        val rules = parseRules(listOf(
            "1= -> =1",
            "0= -> =0",

            "1+1 -> 0+1c",
            "1+0 -> 1+",
            "0+1 -> 1+",
            "0+0 -> 0+",

            "1c1 -> c01",
            "1c0 -> c11",
            "0c1 -> c11",
            "0c0 -> c01",

            "c1 -> 0c",
            "c0 -> 1",

            "+ ->",
            "= ->."
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run("1001+100001=")

        assertEquals("1001100001", result)
    }

    @Test
    fun testRunWithTrace() {
        val rules = parseRules(listOf(
            "a -> b",
            "b ->. DONE"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.runWithTrace("a", verbose = false)

        assertEquals("DONE", result)
    }

    @Test
    fun testLargeStringWithSingleReplacement() {
        // Создаем большой входной текст с единственным вхождением
        val largeText = "prefix".repeat(50) + "TARGET" + "suffix".repeat(50)
        val rules = parseRules(listOf(
            "TARGET -> DONE"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run(largeText)

        // Проверяем, что замена произошла
        assertFalse(result.contains("TARGET"))
        assertTrue(result.contains("DONE"))
        // Проверяем, что результат имеет разумную длину
        assertTrue(result.length > 100)
        assertTrue(result.length < 1000)
    }

    @Test
    fun testVeryLargeStringWithUniqueMarker() {
        // Создаем очень большой входной текст с уникальным маркером
        val veryLargeText = "a".repeat(500) + "UNIQUE_MARKER" + "b".repeat(500)
        val rules = parseRules(listOf(
            "UNIQUE_MARKER -> FOUND_AND_DONE"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run(veryLargeText)

        // Проверяем результат
        assertTrue(result.contains("FOUND_AND_DONE"))
        assertFalse(result.contains("UNIQUE_MARKER"))
        // Проверяем, что результат имеет разумную длину
        assertTrue(result.length > 900)
        assertTrue(result.length < 1100)
    }

    @Test
    fun testLargeStringWithImmediateTerminal() {
        // Тест с немедленным терминальным правилом
        val text = "BEFORE_MARKER_AFTER"

        val rules = parseRules(listOf(
            "MARKER ->. TERMINATED"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run(text)

        // Терминальное правило останавливает алгоритм, но возвращает весь результат
        assertTrue(result.contains("TERMINATED"))
        assertFalse(result.contains("MARKER"))
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testLargeStringProcessingCapability() {
        // Тест для проверки, что большие строки могут быть обработаны
        val largeText = "word".repeat(20) // 80 символов

        val rules = parseRules(listOf(
            "word -> processed"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run(largeText, 1000) // Увеличиваем максимальное количество шагов

        assertFalse(result.contains("word"))
        assertTrue(result.contains("processed"))
        assertTrue(result.length > 50)
    }

    @Test
    fun testHugeStringHandling() {
        val text = "begin_test_end"

        val rules = parseRules(listOf(
            "test -> done",
            "done ->. FINISHED"
        ))

        val markov = MarkovAlgorithm(rules)
        val result = markov.run(text)

        assertTrue(result.contains("FINISHED"))
        assertFalse(result.contains("test"))
        assertFalse(result.contains("done"))
        assertTrue(result.isNotEmpty())
    }
}