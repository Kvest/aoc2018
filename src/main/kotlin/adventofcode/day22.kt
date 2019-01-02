package adventofcode

fun main(args: Array<String>) {
    first22(14,778, 11541)
}


private val X_COEFF = 16807
private val Y_COEFF = 48271
private val MODE = 20183
private fun first22(targetX: Int, targetY: Int, depth: Int) {
    val el = Array(targetY + 1) { IntArray(targetX + 1) { 0 } }
    el.forEachIndexed { y, x, _ ->
        if ((y == 0 && x == 0) || (y == targetY && x == targetX)) {
            el[y][x] = 0
        } else if (y == 0) {
            el[y][x] = (x * X_COEFF + depth) % MODE
        } else if (x == 0) {
            el[y][x] = (y * Y_COEFF + depth) % MODE
        } else {
            el[y][x] = (el[y][x - 1] * el[y - 1][x] + depth) % MODE
        }
    }

    var riskLevel = 0
    el.forEachIndexed { _, _, v ->
        riskLevel += (v % 3)

    }
    println(riskLevel)

    // 0 for rocky regions, 1 for wet regions, and 2 for narrow regions.
//    println(el.joinToString(separator = "\n") {
//        it.joinToString(separator = "") {
//            when (it % 3) {
//                0 -> "."
//                1 -> "="
//                2 -> "|"
//                else -> "?"
//            }
//        }
//    })
}
