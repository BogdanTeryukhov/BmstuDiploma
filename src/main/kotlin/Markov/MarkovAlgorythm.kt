package markov.core

import markov.parser.Rule
import rope.EnhancedRope
import util.delete
import util.findPattern
import util.insert

/**
 * Алгоритм Маркова с использованием EnhancedRope для эффективной работы со строками
 */
class MarkovAlgorithm(private val rules: List<Rule>) {

    /**
     * Запускает алгоритм Маркова на входной строке
     *
     * @param input входная строка
     * @param maxSteps максимальное количество шагов (по умолчанию 10000)
     * @return результат выполнения алгоритма
     */
    fun run(input: String, maxSteps: Int = 10000): String {
        // Создаем верёвку из входной строки
        var rope: EnhancedRope = EnhancedRope.fromString(input)
        var steps = 0

        while (steps < maxSteps) {
            var applied = false
            var shouldTerminate = false

            for (rule in rules) {
                // Используем эффективный поиск с помощью автоматных функций
                val index = rope.indexOf(rule.left)

                if (index != -1) {
                    // Применяем правило: заменяем левую часть на правую
                    rope = delete(rope, index, rule.left.length)
                    rope = insert(rope, index, rule.right)

                    applied = true

                    // Если правило терминальное, запоминаем, что нужно остановиться
                    // но продолжаем выполнение в текущем шаге
                    if (rule.isTerminal) {
                        shouldTerminate = true
                    }

                    // После применения правила начинаем проверку с начала
                    break
                }
            }

            // Если было применено терминальное правило, останавливаем выполнение
            // но сначала даем возможность применить другие правила в текущем шаге
            if (shouldTerminate) {
                // Продолжаем выполнение в текущем шаге для применения других правил
                // которые могут быть найдены после изменений
                for (rule in rules) {
                    val index = rope.indexOf(rule.left)
                    if (index != -1) {
                        // Применяем правило: заменяем левую часть на правую
                        rope = delete(rope, index, rule.left.length)
                        rope = insert(rope, index, rule.right)

                        // После применения правила начинаем проверку с начала
                        break
                    }
                }
                break
            }

            // Если ни одно правило не было применено, завершаем выполнение
            if (!applied) {
                break
            }

            steps++
        }

        return rope.toString()
    }

    /**
     * Запускает алгоритм Маркова с выводом промежуточных шагов
     *
     * @param input входная строка
     * @param maxSteps максимальное количество шагов
     * @param verbose если true, выводит промежуточные результаты
     * @return результат выполнения алгоритма
     */
    fun runWithTrace(input: String, maxSteps: Int = 10000, verbose: Boolean = false): String {
        var rope: EnhancedRope = EnhancedRope.fromString(input)
        var steps = 0

        if (verbose) {
            println("Initial: ${rope.toString()}")
        }

        while (steps < maxSteps) {
            var applied = false
            var shouldTerminate = false

            for ((ruleIndex, rule) in rules.withIndex()) {
                val index = rope.indexOf(rule.left)

                if (verbose) {
                    println("Checking rule #$ruleIndex '${rule.left}' ->${if (rule.isTerminal) "." else ""} '${rule.right}': index = $index")
                }

                if (index != -1) {
                    if (verbose) {
                        println("Step $steps: Applying rule #$ruleIndex '${rule.left} ->${if (rule.isTerminal) "." else ""} ${rule.right}' at position $index")
                    }

                    rope = delete(rope, index, rule.left.length)
                    rope = insert(rope, index, rule.right)

                    if (verbose) {
                        println("Result: ${rope.toString()}")
                    }

                    applied = true

                    // Если правило терминальное, запоминаем, что нужно остановиться
                    // но продолжаем выполнение в текущем шаге
                    if (rule.isTerminal) {
                        shouldTerminate = true
                        if (verbose) {
                            println("Terminal rule applied. Will stop after processing current step.")
                        }
                    }

                    break
                }
            }

            // Если было применено терминальное правило, останавливаем выполнение
            // но сначала даем возможность применить другие правила в текущем шаге
            if (shouldTerminate) {
                if (verbose) {
                    println("Terminal rule applied. Processing remaining rules in current step...")
                }

                // Продолжаем выполнение в текущем шаге для применения других правил
                // которые могут быть найдены после изменений
                for ((ruleIndex, rule) in rules.withIndex()) {
                    val index = rope.indexOf(rule.left)
                    if (index != -1) {
                        if (verbose) {
                            println("Post-terminal: Applying rule #$ruleIndex '${rule.left} ->${if (rule.isTerminal) "." else ""} ${rule.right}' at position $index")
                        }

                        rope = delete(rope, index, rule.left.length)
                        rope = insert(rope, index, rule.right)

                        if (verbose) {
                            println("Post-terminal result: ${rope.toString()}")
                        }

                        // После применения правила начинаем проверку с начала
                        break
                    }
                }

                if (verbose) {
                    println("Stopping execution after terminal rule processing.")
                }
                break
            }

            if (!applied) {
                if (verbose) {
                    println("No rules applied. Final result: ${rope.toString()}")
                }
                break
            }

            steps++
        }

        if (verbose) {
            println("Maximum steps reached. Result: ${rope.toString()}")
        }

        return rope.toString()
    }
}