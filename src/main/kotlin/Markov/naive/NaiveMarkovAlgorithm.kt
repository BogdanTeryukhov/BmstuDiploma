package markov.naive

import markov.parser.Rule

/**
 * Наивная реализация нормального алгоритма Маркова
 * Использует стандартные строки и линейный поиск
 */
class NaiveMarkovAlgorithm(private val rules: List<Rule>) {

    /**
     * Запускает алгоритм Маркова на входной строке
     *
     * @param input входная строка
     * @param maxSteps максимальное количество шагов (по умолчанию 10000)
     * @return результат выполнения алгоритма
     */
    fun run(input: String, maxSteps: Int = 10000): String {
        var currentString = input
        var steps = 0

        while (steps < maxSteps) {
            var applied = false

            for (rule in rules) {
                // Линейный поиск подстроки в строке
                val index = currentString.indexOf(rule.left)

                if (index != -1) {
                    // Применяем правило: заменяем левую часть на правую
                    val before = currentString.substring(0, index)
                    val after = currentString.substring(index + rule.left.length)
                    currentString = before + rule.right + after

                    applied = true

                    // Если правило терминальное, завершаем выполнение
                    if (rule.isTerminal) {
                        return currentString
                    }

                    // После применения правила начинаем проверку с начала
                    break
                }
            }

            // Если ни одно правило не было применено, завершаем выполнение
            if (!applied) {
                break
            }

            steps++
        }

        return currentString
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
        var currentString = input
        var steps = 0

        if (verbose) {
            println("Initial: $currentString")
        }

        while (steps < maxSteps) {
            var applied = false

            for ((ruleIndex, rule) in rules.withIndex()) {
                val index = currentString.indexOf(rule.left)

                if (verbose) {
                    println("Checking rule #$ruleIndex '${rule.left}' ->${if (rule.isTerminal) "." else ""} '${rule.right}': index = $index")
                }

                if (index != -1) {
                    if (verbose) {
                        println("Step $steps: Applying rule #$ruleIndex '${rule.left} ->${if (rule.isTerminal) "." else ""} ${rule.right}' at position $index")
                    }

                    val before = currentString.substring(0, index)
                    val after = currentString.substring(index + rule.left.length)
                    currentString = before + rule.right + after

                    if (verbose) {
                        println("Result: $currentString")
                    }

                    applied = true

                    if (rule.isTerminal) {
                        if (verbose) {
                            println("Terminal rule applied. Stopping execution.")
                        }
                        return currentString
                    }

                    break
                }
            }

            if (!applied) {
                if (verbose) {
                    println("No rules applied. Final result: $currentString")
                }
                break
            }

            steps++
        }

        if (verbose) {
            println("Maximum steps reached. Result: $currentString")
        }

        return currentString
    }
}