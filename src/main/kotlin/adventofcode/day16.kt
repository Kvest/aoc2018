package adventofcode

import java.io.File

typealias Opcode = (Int, Int, Int, IntArray) -> Unit
private val OPCODES_LIST = arrayOf<Opcode>(::addr, ::addi, ::mulr, ::muli, ::banr, ::bani, ::borr, ::bori,
        ::setr, ::seti, ::gtir, ::gtri, ::gtrr, ::eqir, ::eqri, ::eqrr)

fun main(args: Array<String>) {
    first16(File("./data/day16_1.txt").readLines())
    second16(File("./data/day16_2.txt").readLines())
}

private fun first16(data: List<String>) {
    var count = 0

    (0 until data.size step 4).forEach { i ->
        val before = data[i].substringAfter("[").substringBefore("]")
        val registers = before.split(",").map { it.trim().toInt() }.toIntArray()

        val (_, a, b, c) = data[i + 1].split(" ").map { it.toInt() }

        val after = data[i + 2].substringAfter("[").substringBefore("]")
        val target = after.split(",").map { it.trim().toInt() }.toIntArray()

        if (sutableOpcodesCount(a, b, c, registers, target) >= 3) {
            count++
        }
    }

    println(count)
}

private fun second16(data: List<String>) {
    val opts = mutableMapOf<Int, MutableSet<Int>>()
    (0 until OPCODES_LIST.size).forEach {
        opts[it] = (0 until OPCODES_LIST.size).toMutableSet()
    }

    var last = 0
    (0 until data.size step 4).forEach { i ->
        if (data[i].startsWith("Before")) {
            val before = data[i].substringAfter("[").substringBefore("]")
            val registers = before.split(",").map { it.trim().toInt() }.toIntArray()

            val (optcode, a, b, c) = data[i + 1].split(" ").map { it.toInt() }

            val after = data[i + 2].substringAfter("[").substringBefore("]")
            val target = after.split(",").map { it.trim().toInt() }.toIntArray()

            OPCODES_LIST.forEachIndexed { num, f ->
                val registersCopy = registers.copyOf()
                f(a, b, c, registersCopy)
                if (!target.contentEquals(registersCopy)) {
                    opts[optcode]?.remove(num)
                }
            }

            last = i + 2
        }
    }

    repeat(OPCODES_LIST.size) {
        val singles = mutableSetOf<Int>()
        opts.forEach { _, v ->
            if (v.size == 1) {
                singles.add(v.first())
            }
        }
        opts.forEach { _, v ->
            if (v.size > 1) {
                v.removeAll(singles)
            }
        }
    }

    val mapper = Array(OPCODES_LIST.size) { OPCODES_LIST[opts[it]!!.first()] }

    val registers = intArrayOf(0, 0, 0, 0)
    ((last + 1) until data.size).forEach {
        if (data[it].isNotEmpty()) {
            val (optcode, a, b, c) = data[it].split(" ").map { it.toInt() }
            mapper[optcode](a, b, c, registers)
        }
    }

    println(registers.joinToString())
}

private fun sutableOpcodesCount(a: Int, b: Int, c: Int, registers: IntArray, target: IntArray): Int {
    return OPCODES_LIST.count {
        val registersCopy = registers.copyOf()
        it(a, b, c, registersCopy)
        target.contentEquals(registersCopy)
    }
}