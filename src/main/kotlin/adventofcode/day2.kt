package adventofcode

import java.io.File

fun main(args: Array<String>) {
    println(first(File("./data/day2_1.txt").readLines()))
    println(second(File("./data/day2_2.txt").readLines()))
}

fun first(input: List<String>): Int {
    var a = 0
    var b = 0
    input.forEach { str ->
        val map = mutableMapOf<Char, Int>()
        str.forEach { ch ->
            map[ch] = map.getOrDefault(ch, 0) + 1
        }

        var aDone = false
        var bDone = false
        map.forEach { _, count ->
            if (count == 2 && !aDone) {
                ++a
                aDone = true
            }
            if (count == 3 && !bDone) {
                ++b
                bDone = true
            }
        }
    }

    return a * b
}

fun second(input: List<String>): String {
    (0 until input.size - 1).forEach { i ->
        (i + 1 until input.size).forEach { j ->
            var count = 0
            input[i].forEachIndexed { index, c ->
                if (c != input[j][index]) {
                    count++
                }
            }

            if (count == 1) {
                return input[i].filterIndexed { index, c ->
                    c == input[j][index]
                }
            }
        }
    }

    return ""
}