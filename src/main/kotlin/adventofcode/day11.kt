package adventofcode


fun main(args: Array<String>) {
    first11(7803)
    second11(7803)
}

private const val SIZE = 300
fun first11(gsn: Int) {
    val f = calcField(gsn)

    maxSqr(f, 3).printXYK()
}
fun second11(gsn: Int) {
    val f = calcField(gsn)

    val max = (1..SIZE).map { maxSqr(f, it) }.maxBy { it.max }
    max?.printXYK()
}

fun maxSqr(f: Array<IntArray>, k: Int): MaxInf {
    var max = Int.MIN_VALUE
    var maxI = -1
    var maxJ = -1

    (1..(f.size - k)).forEach { i ->
        (1..(f[i].size - k)).forEach { j ->
            val sum = f[i + k - 1][j + k - 1] + f[i - 1][j - 1] - f[i - 1][j + k - 1] - f[i + k - 1][j - 1]

            if (sum > max) {
                max = sum
                maxI = i
                maxJ = j
            }
        }
    }

    return MaxInf(maxI - 1, maxJ - 1, max, k)
}

private fun calcField(gsn: Int): Array<IntArray> {
    val f = Array(SIZE + 1) { y ->
        IntArray(SIZE + 1) { x ->
            if (x == 0 || y == 0) {
                0
            } else {
                calc(x, y, gsn)
            }
        }
    }

    (1 until f.size).forEach { i ->
        (1 until f[i].size).forEach { j ->
            f[i][j] = f[i][j] + f[i - 1][j] + f[i][j - 1] - f[i - 1][j - 1]
        }
    }

    return f
}

private fun calc(x: Int, y: Int, gsn: Int): Int {

    val rackID = x + 10
    val tmp = (rackID * y + gsn) * rackID

    return ((tmp % 1000) / 100) - 5
}

data class MaxInf(val i: Int, val j: Int, val max: Int, val k: Int) {
    fun printXYK() {
        println("${j + 1},${i+1},${k}")
    }
}
