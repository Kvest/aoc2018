package adventofcode

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    first23(File("./data/day23.txt").readLines())
    second23(File("./data/day23.txt").readLines() )
}

private val nanobotRegex = Regex("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)")
fun first23(data: List<String>) {
    var radius = IntArray(data.size) { 0 }
    val pos = Array(data.size) { i ->
        val (x, y, z, r) = nanobotRegex.find(data[i])!!.destructured
        radius[i] = r.toInt()

        XYZ(x.toInt(), y.toInt(), z.toInt())
    }

    var maxR = radius[0]
    var maxI = 0
    radius.forEachIndexed { i, v ->
        if (v > maxR) {
            maxR = v
            maxI = i
        }
    }

    val count = pos.filter { it.manhattanDistance(pos[maxI]) <= maxR }.count()
    println(count)
}

private class Nanobot(val c: XYZ, val r: Int)

fun second23(data: List<String>) {
    var xB = Int.MAX_VALUE
    var xE = Int.MIN_VALUE
    var yB = Int.MAX_VALUE
    var yE = Int.MIN_VALUE
    var zB = Int.MAX_VALUE
    var zE = Int.MIN_VALUE

    val allNanobots = Array(data.size) { i ->
        val (xS, yS, zS, rS) = nanobotRegex.find(data[i])!!.destructured
        val xC = xS.toInt()
        val yC = yS.toInt()
        val zC = zS.toInt()
        val r = rS.toInt()

        xB = min(xB, xC - r)
        xE = max(xE, xC + r)

        yB = min(yB, yC - r)
        yE = max(yE, yC + r)

        zB = min(zB, zC - r)
        zE = max(zE, zC + r)

        Nanobot(XYZ(xC, yC, zC), r)
    }

    var maxMatches = 0
    var dist = 100000000
    val best = mutableListOf<XYZ>()

    while (true) {
        for (x in xB..xE step dist) {
            for (y in yB..yE step dist) {
                for (z in zB..zE step dist) {
                    val probe = XYZ(x, y, z)
                    var inRange = 0

                    //count nanobots in range of probe
                    allNanobots.forEach { nb ->
                        if (probe.manhattanDistance(nb.c) <= nb.r) {
                            ++inRange
                        }
                    }

                    when {
                        inRange == maxMatches -> best.add(probe)
                        inRange > maxMatches -> {
                            maxMatches = inRange
                            best.clear()
                            best.add(probe)
                        }
                    }
                }
            }
        }

        //get most perspective point(close to 0,0,0)
        val p = best.minBy { abs(it.x) + abs(it.y) + abs(it.z) }!!
        xB = p.x - dist
        xE = p.x + dist
        yB = p.y - dist
        yE = p.y + dist
        zB = p.z - dist
        zE = p.z + dist

        dist /= 10

        if (dist == 0) {
            println(p)
            println(p.x + p.y + p.z)
            break
        }
    }
}