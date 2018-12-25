package adventofcode

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    first10(File("./data/day10_1.txt").readLines())
}

val regex = Regex("\\D*<\\s*(-?\\d+),\\s*(-?\\d+)>\\D*<\\s*(-?\\d+),\\s*(-?\\d+)>")

fun first10(data: List<String>) {
    val items = data.mapNotNull {
        regex.find(it)?.let {
            val (x, y, dx, dy) = it.destructured
            return@mapNotNull Point(x.toInt(), y.toInt(), dx.toInt(), dy.toInt())
        }

        null
    }

    repeat(25000) {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE

        //move + count bounds
        items.forEach {
            it.move()

            minX = min(minX, it.x)
            minY = min(minY, it.y)

            maxX = max(maxX, it.x)
            maxY = max(maxY, it.y)
        }

        //println("${maxY - minY + 1} to ${maxY - minY + 1}")
        if ((maxY - minY + 1) == 10 && (maxY - minY + 1) == 10) {
            println("${it + 1} seconds")
            val field = Array(maxY - minY + 1) { BooleanArray(maxX - minX + 1) { false } }

            items.forEach {
                field[it.y - minY][it.x - minX] = true
            }

            println(
                field.joinToString(separator = "\n") {
                    it.joinToString(separator = "") { if (it) "#" else "." }
                }
            )
        }
    }
}

fun second10(data: List<String>) {

}

private class Point(var x: Int, var y: Int, val dx: Int, val dy: Int) {
    fun move() {
        x += dx
        y += dy
    }
}