package adventofcode

import java.util.*

fun main(args: Array<String>) {
//    val a = Day22.solveA("depth: 11541\n" +
//            "target: 14,778")
//    println(a)
//    val b = Day22.solveB("depth: 11541\n" +
//            "target: 14,778")
//    println(b)

    first22(14, 778, 11541)
    second22(14, 778, 11541) //1068  - yes?????

    //first22(10, 725, 8787)
    //second22(10, 725, 8787)

//     first22(8, 701, 5913)
//    second22(8, 701, 5913)

//    first22(6, 770, 4845)
//    second22(6, 770, 4845)
//
//    first22(14, 785, 4080)
//    second22(14, 785, 4080)
}

private val X_COEFF = 16807
private val Y_COEFF = 48271
private val MODE = 20183
private val CHANGE_EQUIPMENT_TIME = 7
private val MOVE_TIME = 1

private val ROCK = 0
private val WET = 1
private val NARROW = 2

private val TORCH = 0
private val GEAR = 1
private val NOTHING = 2

private fun first22(targetX: Int, targetY: Int, depth: Int) {
    val cave = Cave(targetX, targetY, depth)

    var riskLevel = 0
    (0..targetX).forEach { x ->
        (0..targetY).forEach { y ->
            riskLevel += cave[x, y]
        }
    }

    println(riskLevel)
}

private fun second22(targetX: Int, targetY: Int, depth: Int) {
    val changeEq = mapOf(
            XY(ROCK, TORCH) to GEAR,
            XY(ROCK, GEAR) to TORCH,

            XY(WET, GEAR) to NOTHING,
            XY(WET, NOTHING) to GEAR,

            XY(NARROW, TORCH) to NOTHING,
            XY(NARROW, NOTHING) to TORCH
    )
    val transitions = mapOf(
            XYZ(ROCK, GEAR, WET) to GEAR,
            XYZ(ROCK, TORCH, NARROW) to TORCH,

            XYZ(WET, NOTHING, NARROW) to NOTHING,
            XYZ(WET, GEAR, ROCK) to GEAR,

            XYZ(NARROW, TORCH, ROCK) to TORCH,
            XYZ(NARROW, NOTHING, WET) to NOTHING
    )


    val cave = Cave(targetX, targetY, depth)

    val dist = Dists()

    val comparator = Comparator<XYZ> { first, second -> dist[first.x, first.y, first.z] - dist[second.x, second.y, second.z] }
    val queue = PriorityQueue<XYZ>(comparator)
    dist[0, 0, TORCH] = 0
    queue.add(XYZ(0, 0, TORCH))

    while (queue.isNotEmpty()) {
        val next = queue.poll()
        if (next.x == targetX && next.y == targetY && next.z == TORCH) {
            break
        }

        val curr = cave[next.x, next.y]

        //changed equipment
        val ch = XYZ(next.x, next.y, changeEq[XY(curr, next.z)]!!)
        if (dist[next.x, next.y, next.z] + CHANGE_EQUIPMENT_TIME < dist[ch.x, ch.y, ch.z]) {
            queue.removeAll(ch)

            dist[ch.x, ch.y, ch.z] = dist[next.x, next.y, next.z] + CHANGE_EQUIPMENT_TIME
            queue.add(ch)
        }

        //check neighbors
        neighbors(next.x, next.y) { x, y ->
            //if neighbor is the same type
            if (cave[x, y] == curr && dist[next.x, next.y, next.z] + MOVE_TIME < dist[x, y, next.z]) {
                val opposite = XYZ(x, y, next.z)
                queue.removeAll(opposite)

                dist[opposite.x, opposite.y, opposite.z] = dist[next.x, next.y, next.z] + MOVE_TIME
                queue.add(opposite)
            } else {
                val newZ = transitions[XYZ(curr, next.z, cave[x, y])]
                newZ?.let { newZ ->
                    if (dist[next.x, next.y, next.z] + MOVE_TIME < dist[x, y, newZ]) {
                        val opposite = XYZ(x, y, newZ)
                        queue.removeAll(opposite)

                        dist[opposite.x, opposite.y, opposite.z] = dist[next.x, next.y, next.z] + MOVE_TIME
                        queue.add(opposite)
                    }
                }
            }
        }
    }

    println("finished ${dist[targetX, targetY, TORCH]}")
}

private fun PriorityQueue<XYZ>.removeAll(xyz: XYZ) {
    while (this.remove(xyz)) {
    }
}

private class Dists {
    private val m = mutableMapOf<XYZ, Int>()

    operator fun get(x: Int, y: Int, z: Int) = m.getOrPut(XYZ(x, y, z)) { Int.MAX_VALUE }
    operator fun set(x: Int, y: Int, z: Int, value: Int) {
        m[XYZ(x, y, z)] = value
    }
}

private class Cave(targetX: Int, targetY: Int, val depth: Int) {
    private val el = mutableMapOf<XY, Int>()

    init {
        el[XY(0, 0)] = 0
        el[XY(targetX, targetY)] = 0
    }

    operator fun get(x: Int, y: Int) = geoIndex(x, y) % 3

    private fun geoIndex(x: Int, y: Int): Int = el.getOrPut(XY(x, y)) {
        when {
            y == 0 -> (x * X_COEFF + depth) % MODE
            x == 0 -> (y * Y_COEFF + depth) % MODE
            else -> (geoIndex(x - 1, y) * geoIndex(x, y - 1) + depth) % MODE
        }
    }
}

private inline fun neighbors(x: Int, y: Int, action: (Int, Int) -> Unit) {
    if (x > 0) {
        action(x - 1, y)
    }
    if (y > 0) {
        action(x, y - 1)
    }
    action(x, y + 1)
    action(x + 1, y)
}
