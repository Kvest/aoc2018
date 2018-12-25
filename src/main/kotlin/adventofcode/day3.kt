package adventofcode

import java.io.File
import kotlin.math.max

fun main(args: Array<String>) {
    first3(File("./data/day3_1.txt").readLines())
    second3(File("./data/day3_2.txt").readLines())
}

fun first3(data: List<String>) {
    val regex = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)")
    var w = 0
    var h = 0
    val items = data.mapNotNull {
        regex.find(it)?.let {
            val (id, left, top, width, height) = it.destructured

            val item = Item(id.toInt(), left.toInt(), top.toInt(), width.toInt(), height.toInt())

            w = max(w, item.left + item.width)
            h = max(h, item.top + item.height)

            return@mapNotNull item
        }

        null
    }

    val field = IntArray(w * h) { 0 }

    items.forEach {
        (it.top until (it.top + it.height)).forEach { i ->
            (it.left until (it.left + it.width)).forEach { j ->
                ++field[i * w + j]
            }
        }
    }

    val count = field.count { it > 1 }
    println(count)
}


fun second3(data: List<String>) {
    var regex = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)")
    val items = data.mapNotNull {
        regex.find(it)?.let {
            val (id, left, top, width, height) = it.destructured

            return@mapNotNull ItemRange(
                    id.toInt(),
                    left.toInt() until left.toInt() + width.toInt(),
                    top.toInt() until top.toInt() + height.toInt())
        }

        null as? ItemRange
    }

    items.forEach { i ->
        var intersects = false
        items.forEach { j ->
            if (i.id != j.id && i.intersect(j)) {
                intersects = true
            }
        }

        if (!intersects) {
            println(i.id)
        }
    }
}

data class Item(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int)
data class ItemRange(val id: Int, val width: IntRange, val height: IntRange) {
    fun intersect(other: ItemRange): Boolean {
        return width.intersect(other.width).isNotEmpty() && height.intersect(other.height).isNotEmpty()
    }
}
