
import markov.core.MarkovAlgorithm
import markov.parser.parseRules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
}