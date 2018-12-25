package adventofcode

import java.io.File

fun main(args: Array<String>) {
    first19(File("./data/day19_1.txt").readLines())
    second19()
}

private val IP = 1
private fun first19(data: List<String>) {
    var ip = 0
    val registers = intArrayOf(0, 0, 0, 0, 0, 0)

    while (ip >=0 && ip < data.size) {
        val tmp = data[ip].split(" ")
        val a = tmp[1].toInt()
        val b = tmp[2].toInt()
        val c = tmp[3].toInt()

        registers[IP] = ip

        OPCODES[tmp[0]]?.invoke(a, b, c, registers)

        ip = registers[IP] + 1
    }

    println(registers.joinToString())
}

private fun second19() {
    var sum = 0
    (1..10551320).forEach {
        if (10551320 % it == 0) {
            sum += it
        }
    }
    println(sum)
}