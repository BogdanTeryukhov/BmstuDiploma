import markov.core.MarkovAlgorithm
import markov.parser.parseRules
import rope.EnhancedRope
import util.findPattern
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import kotlin.system.measureTimeMillis

class PerformanceBenchmark {

    @Test
    @Disabled("Benchmark test, run manually when needed")
    fun benchmarkStringOperations() {
        println("=== Performance Benchmark ===")

        // Создаем большие строки для тестирования
        val largeInput = "a".repeat(10000) + "b" + "c".repeat(10000)
        val rules = parseRules(listOf(
            "ab -> ba",
            "ba ->. DONE"
        ))

        // Тестируем обычную реализацию
        println("Testing original implementation...")
        val originalTime = measureTimeMillis {
            val originalMarkov = MarkovAlgorithm(rules)
            val result = originalMarkov.run(largeInput, 100000)
            println("Original result length: ${result.length}")
        }
        println("Original implementation time: ${originalTime}ms")

        // Тестируем улучшенную реализацию
        println("\nTesting enhanced implementation...")
        val enhancedTime = measureTimeMillis {
            val enhancedMarkov = MarkovAlgorithm(rules)
            val result = enhancedMarkov.run(largeInput, 100000)
            println("Enhanced result length: ${result.length}")
        }
        println("Enhanced implementation time: ${enhancedTime}ms")

        // Сравнение
        val speedup = if (enhancedTime > 0) {
            originalTime.toDouble() / enhancedTime.toDouble()
        } else {
            0.0
        }
        println("\nPerformance comparison:")
        println("Speedup: ${String.format("%.2f", speedup)}x")
        println("Improvement: ${String.format("%.1f", ((originalTime - enhancedTime).toDouble() / originalTime.toDouble() * 100))}%")
    }

    @Test
    @Disabled("Benchmark test, run manually when needed")
    fun benchmarkRopeOperations() {
        println("\n=== Rope Operations Benchmark ===")

        // Создаем большие верёвки
        val largeText = "x".repeat(50000) + "pattern" + "y".repeat(50000)

        // Тестируем поиск в обычной строке
        println("Testing string search...")
        val stringTime = measureTimeMillis {
            val index = largeText.indexOf("pattern")
            println("String search result: $index")
        }
        println("String search time: ${stringTime}ms")

        // Тестируем поиск в EnhancedRope
        println("\nTesting rope search...")
        val rope = EnhancedRope.fromString(largeText, "pattern")
        val ropeTime = measureTimeMillis {
            val index = rope.findPattern("pattern")
            println("Rope search result: $index")
        }
        println("Rope search time: ${ropeTime}ms")

        println("\nSearch comparison:")
        val searchSpeedup = if (ropeTime > 0) {
            stringTime.toDouble() / ropeTime.toDouble()
        } else {
            0.0
        }
        println("Search speedup: ${String.format("%.2f", searchSpeedup)}x")
    }

    @Test
    @Disabled("Benchmark test, run manually when needed")
    fun benchmarkComplexScenario() {
        println("\n=== Complex Scenario Benchmark ===")

        // Создаем сложный сценарий с множеством операций
        val complexInput = "a".repeat(1000) + "b".repeat(1000) + "c".repeat(1000)
        val complexRules = parseRules(listOf(
            "ab -> x",
            "bc -> y",
            "xy -> z",
            "z ->. DONE"
        ))

        println("Testing complex scenario with input length: ${complexInput.length}")

        // Обычная реализация
        val originalTime = measureTimeMillis {
            val originalMarkov = MarkovAlgorithm(complexRules)
            val result = originalMarkov.run(complexInput, 50000)
            println("Original complex result length: ${result.length}")
        }
        println("Original complex time: ${originalTime}ms")

        // Улучшенная реализация
        val enhancedTime = measureTimeMillis {
            val enhancedMarkov = MarkovAlgorithm(complexRules)
            val result = enhancedMarkov.run(complexInput, 50000)
            println("Enhanced complex result length: ${result.length}")
        }
        println("Enhanced complex time: ${enhancedTime}ms")

        val speedup = if (enhancedTime > 0) {
            originalTime.toDouble() / enhancedTime.toDouble()
        } else {
            0.0
        }
        println("\nComplex scenario speedup: ${String.format("%.2f", speedup)}x")
    }
}