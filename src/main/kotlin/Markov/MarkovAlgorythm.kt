package markov.core

import markov.parser.Rule
import rope.Rope
import util.delete
import util.indexOf
import util.insert
import util.print
import util.toStr


class MarkovAlgorithm(private val rules: List<Rule>) {

    fun run(input: String, maxSteps: Int = 10000): String {

        var rope: Rope = Rope.fromString(input)
        var steps = 0

        while (steps < maxSteps) {

            var applied = false

            for (rule in rules) {

                val index = rope.indexOf(rule.left)

                if (index != -1) {

                    rope = delete(rope, index, rule.left.length)
                    rope = insert(rope, index, rule.right)
                    rope.print()

                    applied = true

                    if (rule.isTerminal) {
                        return rope.toStr()
                    }

                    break
                }
            }

            if (!applied) break
            steps++
        }

        return rope.toStr()
    }
}