package adventofcode

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    first23(File("./data/day23.txt").readLines())
   second23_1(File("./data/day23.txt").readLines()
//            listOf(
//                    "pos=<10,12,12>, r=2",
//                    "pos=<12,14,12>, r=2",
//                    "pos=<16,12,12>, r=4",
//                    "pos=<14,14,14>, r=6",
//                    "pos=<50,50,50>, r=200",
//                    "pos=<10,10,10>, r=5"
//            )
    )
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

fun second23_1(data: List<String>) {
    var xB = Int.MAX_VALUE
    var xE = Int.MIN_VALUE
    var yB = Int.MAX_VALUE
    var yE = Int.MIN_VALUE
    var zB = Int.MAX_VALUE
    var zE = Int.MIN_VALUE

    val r = IntArray(data.size) { 0 }
    val points = Array(data.size) { i ->
        val (xS, yS, zS, rS) = nanobotRegex.find(data[i])!!.destructured
        val xC = xS.toInt()
        val yC = yS.toInt()
        val zC = zS.toInt()
        r[i] = rS.toInt()

        xB = min(xB, xC - r[i])
        xE = max(xE, xC + r[i])

        yB = min(yB, yC - r[i])
        yE = max(yE, yC + r[i])

        zB = min(zB, zC - r[i])
        zE = max(zE, zC + r[i])

        XYZ(xC, yC, zC)
    }

    val maxSize = max(xE - xB, max(yE - yB, zE - zB))
    var p1 = XYZ(xB, yB, zB)
    var p2 = XYZ(xB + maxSize, yB + maxSize, zB + maxSize)
    println("c1=$p1")
    println("c2=$p2")

    while(abs(p1.x - p2.x) > 100 || abs(p1.z - p2.z) > 100 || abs(p1.z - p2.z) > 100) {
        val pC = XYZ(p1.x + (p2.x - p1.x) / 2, p1.y + (p2.y - p1.y) / 2, p1.z + (p2.z - p1.z) / 2)
        val pics = arrayOf(
                XYZ(p1.x, p1.y, p1.z),
                XYZ(p1.x, p2.y, p1.z),
                XYZ(p2.x, p2.y, p1.z),
                XYZ(p2.x, p1.y, p1.z),
                XYZ(p1.x, p1.y, p2.z),
                XYZ(p1.x, p2.y, p2.z),
                XYZ(p2.x, p2.y, p2.z),
                XYZ(p2.x, p1.y, p2.z)
        )
        val counts = IntArray(pics.size) { 0 }
        pics.forEachIndexed { i, corner1 ->
            val diagonal = corner1.manhattanDistance(pC)

            points.forEachIndexed { j, p ->
                val d = (p.manhattanDistance(corner1) + p.manhattanDistance(pC) - diagonal) / 2
                if (d <= r[j]) {
                    ++counts[i]
                }
            }
        }

        p1 = pC
        var maxI = 0
        counts.forEachIndexed { i, v ->
            if (v > counts[maxI]) {
                maxI = i
            }
        }
        p2 = pics[maxI]

        println("$p1...$p2  " + counts.joinToString())
    }

    var maxCount = 0
    var maxXYZ = XYZ(0, 0, 0)
    (min(p1.x, p2.x)..max(p1.x, p2.x)).forEach { x ->
        (min(p1.y, p2.y)..max(p1.y, p2.y)).forEach { y ->
            (min(p1.z, p2.z)..max(p1.z, p2.z)).forEach { z ->
                var count = 0
                points.forEachIndexed { i, p ->
                    if (p.manhattanDistance(XYZ(x, y, z)) <= r[i]) {
                        ++count
                    }
                }

                if (count > maxCount || (count == maxCount && (x + y + z) < (maxXYZ.x + maxXYZ.y + maxXYZ.z))) {
                    maxCount = count
                    maxXYZ = XYZ(x, y, z)
                }
            }
        }
    }

    println("$maxCount in $maxXYZ")
    println(maxXYZ.x + maxXYZ.y + maxXYZ.z)

    //---------------------------------
//    val delta = (c2.x - c1.x) / 2
//    val xC = XYZ(c1.x + delta, c1.y + delta, c1.z + delta)
//    val eighths = IntArray(8) { 0 } //-x, +x, -y, +y, -z, +z
    //---------------------------------
}

fun second23(data: List<String>) {
    //10,10,10, 150
//    var count = 0
//    val testR = 5000
//    println(
//        measureNanoTime {
//            ((-testR)..(testR)).forEach { x ->
//                ((-testR)..(testR)).forEach { y ->
//                    ((-testR)..(testR)).forEach { z ->
//                        if (abs(x) + abs(y) + abs(z) <= testR) {
//                            //println("$x, $y, $z")
//                            count++
//                        }
//                    }
//                }
//            }
//        }
//    )
//    println("count=$count")
//    count = 0

//    println(
//        measureNanoTime {
//            //-r + r
//            (-testR..testR).forEach { rX ->
//                //println("$rX")
//                (-(testR - abs(rX))..(testR - abs(rX))).forEach { rY ->
////                    println("$rX, $rY")
//                    (-(testR - abs(rX) - abs(rY))..(testR - abs(rX) - abs(rY))).forEach { rZ ->
//                       // println("$rX, $rY, $rZ")
//                        count++
//                    }
//                }
//            }
//        }
//    )
//    println("count=$count")


    val map = mutableMapOf<XYZ, Int>()

    var xB = Int.MAX_VALUE
    var xE = Int.MIN_VALUE
    var yB = Int.MAX_VALUE
    var yE = Int.MIN_VALUE
    var zB = Int.MAX_VALUE
    var zE = Int.MIN_VALUE

//    var radius = IntArray(data.size) { 0 }
//    val pos = Array(data.size) { i ->
//        val (x, y, z, r) = nanobotRegex.find(data[i])!!.destructured
//        radius[i] = r.toInt()
//
//        XYZ(x.toInt(), y.toInt(), z.toInt())
//    }

    data.forEach {
        val (xS, yS, zS, rS) = nanobotRegex.find(it)!!.destructured
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

//        ((xC - r)..(xC + r)).forEach { x ->
//            ((yC - r)..(yC + r)).forEach { y ->
//                ((zC - r)..(zC + r)).forEach { z ->
//                    if (abs(xC - x) + abs(yC - y) + abs(zC - z) <= r) {
//                        map[XYZ(x, y, z)] = map.getOrDefault(XYZ(x, y, z), 0) + 1
//                    }
//                }
//            }
//        }
    }

    println("$xB..$xE, $yB..$yE, $zB..$zE")
    println("${xE - xB}, ${yE - yB}, ${zE - zB}")

    val maxSize = max(xE - xB, max(yE - yB, zE - zB))
    var c1 = XYZ(xB, yB, zB)
    var c2 = XYZ(xB + maxSize, yB + maxSize, zB + maxSize)
    println("c1=$c1")
    println("c2=$c2")

//    val xs = IntArray(xE - xB + 1) { 0 }
//    val ys = IntArray(yE - yB + 1) { 0 }
//    val zs = IntArray(zE - zB + 1) { 0 }
//
//    radius.forEachIndexed { i, r ->
//        ((pos[i].x - r)..(pos[i].x + r)).forEach {x ->
//            xs[x - xB] += 1
//        }
//        ((pos[i].y - r)..(pos[i].y + r)).forEach {y ->
//            ys[y - yB] += 1
//        }
//        ((pos[i].z - r)..(pos[i].z + r)).forEach {z ->
//            zs[z - zB] += 1
//        }
//    }
//    println(xs.indexOf(xs.max()!!) + xB)
//    println(ys.indexOf(ys.max()!!) + yB)
//    println(zs.indexOf(zs.max()!!) + zB)

//    (xB..xE).forEach { x ->
//        (yB..yE).forEach { y ->
//            (zB..zE).forEach { z ->
//                radius.forEachIndexed { i, r ->
//                    if (abs(pos[i].x - x) + abs(pos[i].y - y) + abs(pos[i].z - z) <= r) {
//                        map[XYZ(x, y, z)] = map.getOrDefault(XYZ(x, y, z), 0) + 1
//                    }
//                }
//            }
//        }
//    }

//    val arr = Array(xE - xB) {
//        Array(yE - yB) {
//            IntArray(zE - zB) { 0 }
//        }
//    }

//    println(map.size)
}