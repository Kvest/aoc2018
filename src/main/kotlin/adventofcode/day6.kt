package adventofcode

import java.io.File
import kotlin.math.abs
import kotlin.math.max

private const val DIST = 10000

fun main(args: Array<String>) {
    println(first6(File("./data/day6_1.txt").readLines()))
    println(second6(File("./data/day6_2.txt").readLines()))
}

fun first6(lines: List<String>): Int? {
    var w = 0
    var h = 0
    val dots = lines.map {
        val result = IJ(it)

        w = max(w, result.j + 1)
        h = max(h, result.i + 1)

        result
    }

    val field = List(w * h) { index ->
        val i = index / w
        val j = index % w
        var min = Int.MAX_VALUE
        var dot = -1

        dots.forEachIndexed { ix, dt ->
            val dist = dt.dist(i, j)

            //detect double pathes
            if (dist == min) {
                dot = -1
            } else if (dist < min) {
                min = dist
                dot = ix
            }
        }

        dot
    }

    val ignore = mutableSetOf(-1)
    (0 until h).forEach {
        ignore.add(field[it * w])
        ignore.add(field[(it + 1) * w - 1])
    }
    (0 until w).forEach {
        ignore.add(field[it])
        ignore.add(field[(h - 1) * w + it])
    }

    return field
            .filterNot { ignore.contains(it) }
            .groupingBy { it }
            .eachCount()
            .values
            .max()
}

fun second6(lines: List<String>): Int? {
    val dots = lines.map { IJ(it) }

    val base = dots.find {
        val dist = dots.fold(0) { acc, jr -> acc + jr.dist(it.i, it.j) }

        dist < DIST
    } ?: return -1

    var count = 1
    var flag = true
    var delta = 1
    while (flag) {
        flag = false

        (-delta..delta).forEach { dt ->
            if (dots.fold(0) { acc, jr -> acc + jr.dist(base.i - delta, base.j + dt) } < DIST) {
                ++count
                flag = true
            }
            if (dots.fold(0) { acc, jr -> acc + jr.dist(base.i + delta, base.j + dt) } < DIST) {
                ++count
                flag = true
            }
        }

        (-(delta - 1)..(delta - 1)).forEach { dt ->
            if (dots.fold(0) { acc, jr -> acc + jr.dist(base.i + dt, base.j - delta) } < DIST) {
                ++count
                flag = true
            }
            if (dots.fold(0) { acc, jr -> acc + jr.dist(base.i + dt, base.j + delta) } < DIST) {
                ++count
                flag = true
            }
        }

        ++delta
    }

    return count
}

private class IJ(str: String) {
    val i = str.substringAfter(", ").toInt()
    val j = str.substringBefore(", ").toInt()

    fun dist(otherI: Int, otherJ: Int) = abs(i - otherI) + abs(j - otherJ)
}
