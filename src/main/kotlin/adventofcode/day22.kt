package adventofcode

import java.util.*

fun main(args: Array<String>) {
    first22(14, 778, 11541)
    second22(14 , 778, 11541)

//    8090
//    finished in 992
//    first22(10, 725, 8787)
//    second22(10, 725, 8787)

//    6256
//    finished in 973
//     first22(8, 701, 5913)
//    second22(8, 701, 5913)

//    5400
//    finished in 1048
//    first22(6, 770, 4845)
//    second22(6, 770, 4845)

//    11843
//    finished in 1078
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

private val changeEq = mapOf(
        XY(ROCK, TORCH) to GEAR,
        XY(ROCK, GEAR) to TORCH,

        XY(WET, GEAR) to NOTHING,
        XY(WET, NOTHING) to GEAR,

        XY(NARROW, TORCH) to NOTHING,
        XY(NARROW, NOTHING) to TORCH
)
private val transitions = mapOf(
        XYZ(ROCK, GEAR, WET) to GEAR,
        XYZ(ROCK, TORCH, NARROW) to TORCH,

        XYZ(WET, NOTHING, NARROW) to NOTHING,
        XYZ(WET, GEAR, ROCK) to GEAR,

        XYZ(NARROW, TORCH, ROCK) to TORCH,
        XYZ(NARROW, NOTHING, WET) to NOTHING
)

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

//alternative solution for part 2 which is in average 4 time faster
private fun second22_alternative(targetX: Int, targetY: Int, depth: Int) {
    val cave = Cave(targetX, targetY, depth)
    val map = mutableMapOf<Int, MutableSet<XYZ>>()
    val dist = Dists()
    map.getOrPut(0) { mutableSetOf() }.add(XYZ(0, 0, TORCH))
    dist[0, 0, TORCH] = 0

    var t = 0
    while (true) {
        map.remove(t)?.forEach { xyz ->
            if (xyz.x == targetX && xyz.y == targetY && xyz.z == TORCH) {
                println("finished in $t")
                return
            }

            val curr = cave[xyz.x, xyz.y]

            //changed equipment
            val ch = XYZ(xyz.x, xyz.y, changeEq[XY(curr, xyz.z)]!!)
            if (t + CHANGE_EQUIPMENT_TIME < dist[ch.x, ch.y, ch.z]) {
                dist[ch.x, ch.y, ch.z] = t + CHANGE_EQUIPMENT_TIME
                map.getOrPut(t + CHANGE_EQUIPMENT_TIME) { mutableSetOf() }.add(ch)
            }

            //move
            val moveSet = map.getOrPut(t + MOVE_TIME) { mutableSetOf() }
            neighbors(xyz.x, xyz.y) { x, y ->
                //if neighbor is the same type
                if (cave[x, y] == curr) {
                    if (t + MOVE_TIME < dist[x, y, xyz.z]) {
                        dist[x, y, xyz.z] = t + MOVE_TIME
                        moveSet.add(XYZ(x, y, xyz.z))
                    }
                } else {
                    val newZ = transitions[XYZ(curr, xyz.z, cave[x, y])]
                    newZ?.let { newZ ->
                        if (t + MOVE_TIME < dist[x, y, newZ]) {
                            dist[x, y, newZ] = t + MOVE_TIME
                            moveSet.add(XYZ(x, y, newZ))
                        }
                    }
                }
            }
        }

        ++t
    }
}

private fun second22(targetX: Int, targetY: Int, depth: Int) {
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
            if (cave[x, y] == curr) {
                if (dist[next.x, next.y, next.z] + MOVE_TIME < dist[x, y, next.z]) {
                    val opposite = XYZ(x, y, next.z)
                    queue.removeAll(opposite)

                    dist[opposite.x, opposite.y, opposite.z] = dist[next.x, next.y, next.z] + MOVE_TIME
                    queue.add(opposite)
                }
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

    println("finished in ${dist[targetX, targetY, TORCH]}")
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

private class Cave(val targetX: Int, val targetY: Int, val depth: Int) {
    private val el = mutableMapOf<XY, Int>()

    operator fun get(x: Int, y: Int) = erosionLevel(x, y) % 3

    private fun erosionLevel(x: Int, y: Int) = el.getOrPut(XY(x, y)) { (geoIndex(x, y) + depth) % MODE }

    private fun geoIndex(x: Int, y: Int): Int {
        return when {
            x == 0 && y == 0 -> 0
            x == targetX && y == targetY -> 0
            y == 0 -> x * X_COEFF
            x == 0 -> y * Y_COEFF
            else -> erosionLevel(x - 1, y) * erosionLevel(x, y - 1)
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
