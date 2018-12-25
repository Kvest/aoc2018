package first

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

private const val START = 'S'
private const val END = 'X'
private const val OBSTACLE = 'B'
private const val PATH = '*'
private const val STRAIGHT_STEP = 1f
private const val DIAGONAL_STEP = 1.5f

fun addPath(mapString: String): String {
    val maze = Maze(mapString)

    val frontier = Frontier()
    frontier.add(maze.start, 0f)

    val cameFrom = mutableMapOf<Cell, Cell>()
    val costSoFar = mutableMapOf(maze.start to 0f)

    while (frontier.isNotEmpty()) {
        //next cell to consider
        val current = frontier.poll()

        if (current == maze.end) {
            //path already found
            break
        }

        val currentCost = costSoFar[current] ?: 0f
        //examine all available neighbors
        maze.forEachAvailableNeighbor(current) { neighbor, weight ->
            val newCost = currentCost + weight
            //add neighbor for the further consideration if it is new or the new cost is lower than the previous one
            if (neighbor !in costSoFar || newCost < costSoFar.getOrDefault(neighbor, 0f)) {
                costSoFar[neighbor] = newCost
                val priority = newCost + heuristic(maze.end, neighbor)

                frontier.add(neighbor, priority)

                cameFrom[neighbor] = current
            }
        }
    }

    //build set of the all cells in the path
    val path = mutableSetOf(maze.end)
    var step = maze.end
    while (step != maze.start) {
        step = cameFrom[step]!!
        path.add(step)
    }

    //mark path on the original string
    return mapString.split("\n").mapIndexed { i, row ->
        row.mapIndexed { j, ch ->
            if (Cell(i, j) in path) {
                PATH
            } else {
                ch
            }
        }.joinToString(separator = "")
    }.joinToString(separator = "\n")
}

fun heuristic(first: Cell, second: Cell) = first.distanceTo(second)

fun Int.pow2() = this.toFloat().pow(2)

data class Cell private constructor(val i: Int, val j: Int) {
    companion object {
        private val cash = mutableMapOf<Int, MutableMap<Int, Cell>>()

        //Avoid allocations of the huge amount of the Cell items
        operator fun invoke(i: Int, j: Int): Cell {
            val rowsCash = cash.getOrPut(i) { mutableMapOf() }
            return rowsCash.getOrPut(j) { Cell(i, j) }
        }
    }

    fun distanceTo(other: Cell) = sqrt((other.i - i).pow2() + (other.j - j).pow2())
}

class Frontier {
    private val queue = PriorityQueue<Container>()

    fun add(cell: Cell, priority: Float) {
        queue.add(Container(cell, priority))
    }

    fun poll() = queue.poll().cell

    fun isNotEmpty() = queue.isNotEmpty()

    private class Container(val cell: Cell, val priority: Float) : Comparable<Container> {
        override fun compareTo(other: Container): Int {
            val delta = priority - other.priority
            return when {
                delta > 0 -> 1
                delta < 0 -> -1
                else -> 0
            }
        }
    }
}

class Maze(mapString: String) {
    companion object {
        private class NeighborInfo(val dI: Int, val dJ: Int, val stepWeight: Float)

        private val neighbors = arrayOf(
                NeighborInfo(-1, -1, DIAGONAL_STEP),
                NeighborInfo(-1, 0, STRAIGHT_STEP),
                NeighborInfo(-1, 1, DIAGONAL_STEP),
                NeighborInfo(0, 1, STRAIGHT_STEP),
                NeighborInfo(1, 1, DIAGONAL_STEP),
                NeighborInfo(1, 0, STRAIGHT_STEP),
                NeighborInfo(1, -1, DIAGONAL_STEP),
                NeighborInfo(0, -1, STRAIGHT_STEP))
    }

    private val rows = mapString.split("\n")
    private val heightRange = 0 until rows.size
    private val widthRange = 0 until rows[0].length
    val start = findCell(START)
    val end = findCell(END)

    fun forEachAvailableNeighbor(cell: Cell, action: (neighbor: Cell, stepWeight: Float) -> Unit) {
        neighbors.forEach {
            val i = cell.i + it.dI
            val j = cell.j + it.dJ
            if (i in heightRange && j in widthRange && isCellAvailable(i, j)) {
                action(Cell(i, j), it.stepWeight)
            }
        }
    }

    private fun isCellAvailable(i: Int, j: Int) = rows[i][j] != OBSTACLE

    private fun findCell(target: Char): Cell {
        rows.forEachIndexed { i, row ->
            row.forEachIndexed { j, ch ->
                if (ch == target) {
                    return Cell(i, j)
                }
            }
        }

        throw IllegalStateException("$target not found")
    }
}
