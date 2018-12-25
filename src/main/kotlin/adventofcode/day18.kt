package adventofcode

import java.io.File

fun main(args: Array<String>) {
    first18(File("./data/day18_1.txt").readLines(), 10)
    second18(1000000000)
}

private const val TREE = 1
private const val LUMBERYARD = 2
private const val GROUND = 3

private fun first18(data: List<String>, repeats: Int) {
    val field = Array(data.size) { i ->
        IntArray(data[i].length) { j ->
            when (data[i][j]) {
                '|' -> TREE
                '#' -> LUMBERYARD
                '.' -> GROUND
                else -> throw IllegalArgumentException("???")
            }
        }
    }

    val adjacentTrees = Array(field.size) { IntArray(field[it].size) { 0 } }
    val adjacentLumberyards = Array(field.size) { IntArray(field[it].size) { 0 } }
    val adjacentGrounds = Array(field.size) { IntArray(field[it].size) { 0 } }

    repeat(repeats) {
        field.forEachIndexed { i, j, _ ->
            adjacentTrees[i][j] = 0
            adjacentLumberyards[i][j] = 0
            adjacentGrounds[i][j] = 0

            field.forEachAdjacent(i, j) {
                when (it) {
                    TREE -> adjacentTrees[i][j]++
                    LUMBERYARD -> adjacentLumberyards[i][j]++
                    GROUND -> adjacentGrounds[i][j]++
                }
            }
        }

        field.forEachIndexed { i, j, v ->
            when (v) {
                GROUND -> {
                    if (adjacentTrees[i][j] >= 3) {
                        field[i][j] = TREE
                    }
                }
                TREE -> {
                    if (adjacentLumberyards[i][j] >= 3) {
                        field[i][j] = LUMBERYARD
                    }
                }
                LUMBERYARD -> {
                    if (adjacentLumberyards[i][j] >= 1 && adjacentTrees[i][j] >= 1) {
                        field[i][j] = LUMBERYARD
                    } else {
                        field[i][j] = GROUND
                    }
                }
            }
        }
    }

    var treesCount = 0
    var lumberyardsCount = 0
    field.forEachIndexed { _, _, v ->
        when (v) {
            LUMBERYARD -> ++lumberyardsCount
            TREE -> ++treesCount
        }
    }

    println(treesCount * lumberyardsCount)
}

private fun second18(repeats: Int) {
    //found dependency:
    /*
    10000) 189945
    10001) 183464
    10002) 181930
    10003) 176080
    10004) 177660
    10005) 173240
    10006) 175150
    10007) 173545
    10008) 176280
    10009) 173545
    10010) 176648
    10011) 177057
    10012) 181068
    10013) 181853
    10014) 187726
    10015) 190836
    10016) 196392
    10017) 198830
    10018) 202410
    10019) 205686
    10020) 205674
    10021) 201718
    10022) 200208
    10023) 195640
    10024) 195026
    10025) 190740
    10026) 193336
    10027) 188760 */
    val results = arrayOf(189945, 183464, 181930, 176080, 177660, 173240, 175150, 173545, 176280, 173545, 176648, 177057, 181068, 181853, 187726, 190836, 196392, 198830, 202410, 205686, 205674, 201718, 200208, 195640, 195026, 190740, 193336, 188760)

    println(results[(repeats - 10001) % results.size])
}

private fun Array<IntArray>.forEachAdjacent(i: Int, j: Int, action: (Int) -> Unit) {
    if (i > 0 && j > 0) {
        action(this[i - 1][j - 1])
    }
    if (i > 0) {
        action(this[i - 1][j])
    }
    if (i > 0 && j < (this[i].size - 1)) {
        action(this[i - 1][j + 1])
    }
    if (j < (this[i].size - 1)) {
        action(this[i][j + 1])
    }
    if (i < (this.size - 1) && j < (this[i].size - 1)) {
        action(this[i + 1][j + 1])
    }
    if (i < (this.size - 1)) {
        action(this[i + 1][j])
    }
    if (i < (this.size - 1) && j > 0) {
        action(this[i + 1][j - 1])
    }
    if (j > 0) {
        action(this[i][j - 1])
    }
}