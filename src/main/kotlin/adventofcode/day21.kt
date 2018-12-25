package adventofcode

import java.io.File

/*
r[5] = 123          						seti 123 0 5
r[5] = r[5] and 456   						bani 5 456 5
r[5] = if (r[5] == 72) 1 else 0					eqri 5 72 5
r[1] = r[5] + r[1]						addr 5 1 1
r[1] = 0	    						seti 0 0 1
r[5] = 0	    						seti 0 3 5
r[4] = r[5] or 65536						bori 5 65536 4
r[5] = 13_284_195	    						seti 13284195 4 5
r[3] = r[4] and 255						bani 4 255 3
r[5] = r[5] + r[3]						addr 5 3 5
r[5] = r[5] and 16_777_215					bani 5 16777215 5
r[5] = r[5] * 65899						muli 5 65899 5
r[5] = r[5] and 16777215					bani 5 16777215 5
r[3] = if (256 > r[4]) 1 else 0					gtir 256 4 3
r[1] = r[3] + r[1]						addr 3 1 1
r[1]++								addi 1 1 1
r[1] = 27	    						seti 27 1 1
r[3] = 0	    						seti 0 5 3
r[2] = r[3] + 1							addi 3 1 2
r[2] = r[2] * 256						muli 2 256 2
r[2] = if (r[2] > r[4]) 1 else 0				gtrr 2 4 2
r[1] = r[2] + r[1]						addr 2 1 1
r[1]++								addi 1 1 1
r[1] = 25	    						seti 25 2 1
r[3]++								addi 3 1 3
r[1] = 17	    						seti 17 1 1
r[4] = r[3]							setr 3 7 4
r[1] = 7	    						seti 7 3 1
r[3] = if (r[5] == r[0]) 1 else 0				eqrr 5 0 3
r[1] = r[3] + r[1]						addr 3 1 1
r[1] = 5	    						seti 5 3 1

 */

fun main(args: Array<String>) {
    first21(File("./data/day21_1.txt").readLines())
    second21()
}

private val IP = 1
private fun first21(data: List<String>) {
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

        if (ip == 28) {
            break
        }
    }

    println(registers.joinToString())
}

private fun second21() {
    val target = 0
    var prev = 0
    var a = 0
    var b = 0
    var c = 0
    b = c or 65536
    c = 1328419

    val set = HashSet<Int>(10300)
    repeat(Int.MAX_VALUE) {
        a = b and 255
        c = c + a
        c = c and 16777215
        c = c * 65899
        c = c and 16777215

        if (256 > b) {
            if (!set.contains(c)) {
                set.add(c)
                prev = c
            }
            //prev = c

            if (c == target) {
                TODO("HALT!!!")
            } else {
                b = c or 65536
                c = 13284195
            }
        } else {
//            r[3] = 0
//            while ((r[3] + 1) * 256 <= r[4]) {
//                r[3]++
//            }
//            r[4] = r[3]

            //converted to:
            b = b / 256
        }
    }

    println(prev)
}