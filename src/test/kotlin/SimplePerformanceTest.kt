package benchmark

import markov.core.MarkovAlgorithm
import markov.naive.NaiveMarkovAlgorithm
import markov.parser.parseRules
import rope.EnhancedRope
import util.findPattern
import kotlin.system.measureTimeMillis

fun main() {
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
        val originalMarkov = NaiveMarkovAlgorithm(rules)
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
    if (speedup > 1.0) {
        println("Enhanced implementation is faster!")
        println("Improvement: ${String.format("%.1f", ((originalTime - enhancedTime).toDouble() / originalTime.toDouble() * 100))}%")
    } else {
        println("Original implementation is faster!")
        println("Degradation: ${String.format("%.1f", ((enhancedTime - originalTime).toDouble() / originalTime.toDouble() * 100))}%")
    }
}