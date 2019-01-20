package adventofcode

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    first17(File("./data/day17.txt").readLines())
}

private val SPRING_X = 500
private val SPRING_Y = 0

private fun first17(data: List<String>) {
    val (clay, yRange) = parse(data)
    val (minY, maxY) = yRange
    val water = mutableSetOf<XY>()
    val lockedWater = mutableSetOf<XY>()

    flowDown(SPRING_X, SPRING_Y, clay, water, lockedWater, maxY)
    //delete tile less then minY
    water.removeIf { it.y < minY }

    //printGroundToFile(clay, water, lockedWater, maxY + 1)

    println(water.size)
    println(lockedWater.size)
}

private fun flowDown(x: Int, fromY: Int, clay: Set<XY>, water: MutableSet<XY>, lockedWater: MutableSet<XY>, maxY: Int) {
    var y = fromY + 1
    //first go down until not meet clay or locked water
    while (y <= maxY && !clay.contains(x, y) && !water.contains(x, y) && !lockedWater.contains(x, y)) {
        water.add(XY(x,y))
        ++y
    }

    --y
    while (y > fromY && (clay.contains(x, y + 1) || lockedWater.contains(x, y + 1))) {
        val left = flowSide(x - 1, -1, y, clay, water, lockedWater, maxY)
        val right = flowSide(x + 1, 1, y, clay, water, lockedWater, maxY)

        if (left && right) {
            lockedWater.add(XY(x, y))
            lockWater(x - 1, -1, y, clay, lockedWater)
            lockWater(x + 1, 1, y, clay, lockedWater)
            --y
        } else {
            break
        }
    }
}

private fun lockWater(fromX: Int, dx: Int, y: Int, clay: Set<XY>, lockedWater: MutableSet<XY>) {
    var x = fromX
    while (!clay.contains(x, y)) {
        lockedWater.add(XY(x, y))
        x += dx
    }
}

private fun flowSide(fromX: Int, dx: Int, y: Int, clay: Set<XY>, water: MutableSet<XY>, lockedWater: MutableSet<XY>, maxY: Int): Boolean {
    var x = fromX
    if (clay.contains(x, y)) {
        return true
    }

    while (true) {
        water.add(XY(x, y))
        if (!clay.contains(x, y + 1) && !lockedWater.contains(x, y + 1)) {
            flowDown(x, y, clay, water, lockedWater, maxY)
        }

        if (clay.contains(x, y + 1) || lockedWater.contains(x, y + 1)) {
            if (clay.contains(x + dx, y)) {
                return true
            } else {
                x += dx
            }
        } else {
            return false
        }
    }
}

private fun parse(data: List<String>): Pair<Set<XY>, Pair<Int, Int>> {
    val regex = Regex("(\\D)=(\\d+), (\\D)=(\\d+)..(\\d+)")
    var maxY = 0
    var minY = Int.MAX_VALUE

    val clay = mutableSetOf<XY>()
    data.forEach { str ->
        regex.find(str)?.let {
            val (axe1, val1, _, valS, valE) = it.destructured
            val a = val1.toInt()
            (valS.toInt()..valE.toInt()).forEach { b ->
                if (axe1 == "x") {
                    clay.add(XY(a, b))
                } else {
                    clay.add(XY(b, a))
                }
            }

            maxY = max(maxY, if (axe1 == "y") a else valE.toInt())
            minY = min(minY, if (axe1 == "y") a else valS.toInt())
        }
    }

    return Pair(clay, Pair(minY, maxY))
}

private fun Set<XY>.contains(x: Int, y: Int) = this.contains(XY(x, y))


private fun printGroundToFile(clay: Set<XY>, water: Set<XY>, lockedWater: Set<XY>, maxY: Int) {
    val bi = BufferedImage(3200, maxY * 4, BufferedImage.TYPE_INT_ARGB)
    val img = bi.createGraphics()

    var count = 0
    (0..maxY).forEach { y ->
        (0..800).forEach { x ->
            if (clay.contains(x,y)) {
                img.paint = Color.black
                img.fillRect(x * 4,y * 4, 4, 4)
            } else if (lockedWater.contains(x,y)) {
                count++
                img.paint = Color.green
                img.fillRect(x * 4,y * 4, 4, 4)
            } else if (water.contains(x,y)) {
                count++
                img.paint = Color.blue
                img.fillRect(x * 4,y * 4, 4, 4)
            }
        }
    }

    ImageIO.write(bi, "PNG", File("ground.PNG"))
}