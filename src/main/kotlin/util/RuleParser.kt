package org.example.util

import org.example.Markov.Rule

fun parseRules(lines: List<String>): List<Rule> {

    return lines.map { line ->

        val trimmed = line.trim()

        if ("->." in trimmed) {

            val parts = trimmed.split("->.")
            Rule(
                left = parts[0].trim(),
                right = parts[1].trim(),
                isTerminal = true
            )

        } else {

            val parts = trimmed.split("->")
            Rule(
                left = parts[0].trim(),
                right = parts[1].trim(),
                isTerminal = false
            )
        }
    }
}