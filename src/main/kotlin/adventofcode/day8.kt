package adventofcode

import java.io.File

fun main(args: Array<String>) {
    println(first8(File("./data/day8_1.txt").readText()))
    println(second8(File("./data/day8_2.txt").readText()))
}

fun first8(data: String): Int {
    val items = data.split(" ").map { it.toInt() }
    val results = IntArray(2) { 0 }
    f1(items, results)

    return results[1]
}

fun f1(items: List<Int>, results: IntArray) {
    val childrenCount = items[results[0]]
    val metaCount = items[results[0] + 1]

    results[0] += 2

    repeat(childrenCount) {
        f1(items, results)
    }

    repeat(metaCount) {
        results[1] += items[results[0]++]
    }
}

fun second8(data: String): Int {
    val items = data.split(" ").map { it.toInt() }
    val index = IntArray(1) { 0 }

    return f2(items, index)
}

fun f2(items: List<Int>, index: IntArray): Int {
    val childrenCount = items[index[0]]
    val metaCount = items[index[0] + 1]

    index[0] += 2

    if (childrenCount == 0) {
        var sum = 0
        repeat(metaCount) {
            sum += items[index[0]++]
        }

        return sum
    }

    val childrenMeta = IntArray(childrenCount) {
        f2(items, index)
    }

    var sum = 0
    repeat(metaCount) {
        println("take ${items[index[0]]}")
        if (items[index[0]] <= childrenMeta.size) {
            sum += childrenMeta[items[index[0]] - 1]
        }

        index[0]++
    }

    return sum
}
