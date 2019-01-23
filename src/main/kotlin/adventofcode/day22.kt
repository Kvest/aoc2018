package adventofcode

import java.util.*
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    first22(14, 778, 11541)
    second22(14, 778, 11541)
    //second22(10, 10, 510)
}

private val X_COEFF = 16807
private val Y_COEFF = 48271
private val MODE = 20183
private val CHANGE_EQUIPMENT_TIME = 7
private val ROCK = 0
private val WET = 1
private val NARROW = 2

private fun first22(targetX: Int, targetY: Int, depth: Int) {
    val cave = buildCave(targetX, targetY, depth)

    var riskLevel = 0
    cave.forEachIndexed { _, _, v ->
        riskLevel += v

    }
    println(riskLevel)

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

private fun second22(targetX: Int, targetY: Int, depth: Int) {
    val transitions = mapOf(
            XYZ(ROCK, 1, WET) to 0,
            XYZ(ROCK, 0, NARROW) to 0,
            XYZ(WET, 1, NARROW) to 1,
            XYZ(WET, 0, ROCK) to 1,
            XYZ(NARROW, 0, ROCK) to 0,
            XYZ(NARROW, 1, WET) to 1
    )

    val w = (targetX + 1) * 100
    val h = (targetY + 1) * 10
    val cave = buildCave(w, h, depth)

    val dist = Array(h) {
        Array(w) {
            IntArray(2) { Int.MAX_VALUE }
        }
    }

    val comparator = Comparator<XYZ> { first, second -> dist[first.y][first.x][first.z] - dist[second.y][second.x][second.z] }
    val queue = PriorityQueue<XYZ>(comparator)
    dist[0][0][0] = 0
    queue.add(XYZ(0, 0, 0))

    while (queue.isNotEmpty()) {
        val next = queue.poll()
        if (next.x == targetX && next.y == targetY && next.z == 0) {
            break
        }

        //fill sell for changed equipment
        val opposite = XYZ(next.x, next.y, if (next.z == 0) 1 else 0)
        if (dist[next.y][next.x][next.z] + CHANGE_EQUIPMENT_TIME < dist[opposite.y][opposite.x][opposite.z]) {
            queue.removeAll(opposite)

            dist[opposite.y][opposite.x][opposite.z] = dist[next.y][next.x][next.z] + CHANGE_EQUIPMENT_TIME
            queue.add(opposite)
        }

        //check neighbors
        val curr = cave[next.y][next.x]
        neighbors(next.y, next.x) { i, j ->
            //         println("$next  = $curr [$i,$j] comp ${cave[i][j]}")
            if (cave[i][j] == curr && dist[next.y][next.x][next.z] + 1 < dist[i][j][next.z]) {
                val opposite = XYZ(j, i, next.z)
                queue.removeAll(opposite)

                dist[opposite.y][opposite.x][opposite.z] = dist[next.y][next.x][next.z] + 1
                queue.add(opposite)
            } else {
                val newZ = transitions[XYZ(curr, next.z, cave[i][j])]
                newZ?.let { newZ ->
                    if (dist[next.y][next.x][next.z] + 1 < dist[i][j][newZ]) {
                        val opposite = XYZ(j, i, newZ)
                        queue.removeAll(opposite)

                        dist[opposite.y][opposite.x][opposite.z] = dist[next.y][next.x][next.z] + 1
                        queue.add(opposite)
                    }
                }
            }
        }
    }

    println("finished ${dist[targetY][targetX][0]} ${dist[targetY][targetX][1]}")
}

private fun PriorityQueue<*>.removeAll(item: Any) {
    while (this.remove(item)) {
    }
}

private fun buildCave(targetX: Int, targetY: Int, depth: Int): Array<IntArray> {
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

    el.forEachIndexed { y, x, _ ->
        el[y][x] = el[y][x] % 3
    }

    return el
}

inline fun neighbors(i: Int, j: Int, action: (Int, Int) -> Unit) {
    if (i > 0) {
        action(i - 1, j)
    }
    if (j > 0) {
        action(i, j - 1)
    }
    action(i, j + 1)
    action(i + 1, j)
}
