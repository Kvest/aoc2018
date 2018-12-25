package adventofcode

import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
    println(first12("##.#..#.#..#.####.#########.#...#.#.#......##.#.#...##.....#...#...#.##.#...##...#.####.##..#.#..#.", File("./data/day12_1.txt").readLines(), 20))
    println(second12())
}

fun second12(): BigInteger {
    //Note: Solution was found in an analytical way
    val target = BigInteger("50000000000")
    return (target - BigInteger("500")) * BigInteger("87") + BigInteger("44457")
}

fun first12(initial: String, data: List<String>, generationsCount: Int): Int {
    val more = (generationsCount * 1.5).toInt()

    val arr = CharArray(more * 2 + initial.length) {
        if (it < more || it >= (more + initial.length)) {
            '.'
        } else {
            initial[it - more]
        }
    }

    val rules = data.map { Rule(it) }

    repeat(generationsCount) {
        val old = arr.copyOf()
        (2 until (arr.size - 2)).forEach { i ->
            arr[i] = rules.fold('.') { acc, rule ->
                if (rule.match(old, i)) rule.result else acc
            }
        }
    }

    var sum = 0
    arr.forEachIndexed { i, ch ->
        if (ch == '#') {
            sum += (i - more)
        }
    }

    return sum
}

private class Rule(str: String) {
    private val arr: CharArray = charArrayOf(str[0], str[1], str[2], str[3], str[4])
    val result: Char = str[9]

    fun match(src: CharArray, i: Int): Boolean {
        return src[i - 2] == arr[0] &&
                src[i - 1] == arr[1] &&
                src[i] == arr[2] &&
                src[i + 1] == arr[3] &&
                src[i + 2] == arr[4]
    }
}