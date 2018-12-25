package adventofcode

import kotlin.math.min

fun main(args: Array<String>) {
    first14(540561)
    second14(540561)
}

fun first14(count: Int) {
    val receipts = mutableListOf(3, 7)
    var p1 = 0
    var p2 = 1

    while(receipts.size < count + 10) {
        val newReceipts = (receipts[p1] + receipts[p2]).toDigits()
        receipts.addAll(newReceipts)
        p1 = (p1 + receipts[p1] + 1) % receipts.size
        p2 = (p2 + receipts[p2] + 1) % receipts.size
    }

    (count..(count + 9)).forEach { i ->
        print(receipts[i])
    }
    println()
}

fun second14(targetNum: Int) {
    val target = targetNum.toDigits()

    val receipts = mutableListOf(3, 7)
    var p1 = 0
    var p2 = 1
    var shift = 0

    while(!endsWith(receipts, target, shift)) {
        val newReceipts = (receipts[p1] + receipts[p2]).toDigits()
        receipts.addAll(newReceipts)

        p1 = (p1 + receipts[p1] + 1) % receipts.size
        p2 = (p2 + receipts[p2] + 1) % receipts.size

        shift = newReceipts.size
    }
}

private fun endsWith(src: List<Int>, target: Array<Int>, shiftFromEnd: Int): Boolean {
    if (src.size < target.size) {
        return false
    }

    repeat(min(shiftFromEnd, src.size - target.size)) { shift ->
        var mismatch = 0
        (0 until target.size).forEach {
            if (target[it] != src[src.size - shift - target.size + it]) {
                ++mismatch
            }
        }

        if (mismatch == 0) {
            println((src.size - shift - target.size))

            return true
        }
    }

    return false
}

private fun Int.toDigits(): Array<Int> {
    if (this == 0) {
        return arrayOf(0)
    }

    var max = 1
    var count = 0
    while (max <= this) {
        max *= 10
        ++count
    }

    return Array(count) {
        max /= 10
        (this % (max * 10)) / (max)

    }
}
