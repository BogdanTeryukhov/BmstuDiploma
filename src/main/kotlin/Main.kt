package org.example

import org.example.Markov.MarkovAlgorithm
import org.example.util.parseRules

fun main() {

    // 1001100001
    val rulesText = listOf(
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
    )

    val rules = parseRules(rulesText)

    val markov = MarkovAlgorithm(rules)

    val input = "1001+100001="

    val result = markov.run(input)

    println("Input: $input")
    println("Result: $result")
}