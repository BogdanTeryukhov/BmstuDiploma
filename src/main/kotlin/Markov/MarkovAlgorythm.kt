package org.example.Markov

data class Rule(
    val left: String,
    val right: String,
    val isTerminal: Boolean
)

class MarkovAlgorithm(private val rules: List<Rule>) {

    fun run(input: String): String {

        var current = input

        while (true) {

            var applied = false

            for (rule in rules) {

                val index = current.indexOf(rule.left)

                if (index != -1) {

                    current = current.replaceFirst(rule.left, rule.right)
                    applied = true

                    if (rule.isTerminal) {
                        return current
                    }

                    break
                }
            }

            if (!applied) {
                return current
            }
        }
    }
}