package adventofcode

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    first25(File("./data/day25.txt").readLines())
}

private val MAX_DIST = 3
fun first25(data: List<String>) {
    val points = data.map {
        val items = it.split(",")
        ABCD(items[0].toInt(), items[1].toInt(), items[2].toInt(), items[3].toInt())
    }

    //build relations
    points.forEach { src ->
        points.forEach { p ->
            if (p != src && src.dist(p) <= MAX_DIST) {
                src.relations.add(p)
            }
        }
    }

    var group = 1
    points.forEach {
        if (it.group == 0) {
            it.group = group++
        }
    }


    println(points.maxBy(ABCD::group)?.group)
}

class ABCD(val a: Int, val b: Int, val c: Int, val d: Int) {
    val relations = mutableSetOf<ABCD>()
    var group = 0
        set(value) {
            if (field != value) {
                field = value

                relations.forEach {
                    it.group = value
                }
            }
        }

    fun dist(other: ABCD) = abs(a - other.a) + abs(b - other.b) + abs(c - other.c) + abs(d - other.d)
}