package adventofcode

import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val time = measureTimeMillis {
        first20(File("./data/day20.txt").readText())
    }
    println("For $time ms\n")

    val time2 = measureTimeMillis {
        first20Alternative(File("./data/day20.txt").readText())
    }
    println("For $time2 ms")
}

private val START = 0
private val ROOM = 1
private val DOOR = 2

private fun first20(data: String) {
    val field = Array(data.length) { IntArray(data.length) { -1 } }
    val startI = data.length / 2
    val startJ = data.length / 2

    field[startI][startJ] = START
    parsePath(field, data, startI, startJ, 1)

    //find bounds
    var top = Int.MAX_VALUE
    var bottom = Int.MIN_VALUE
    var left = Int.MAX_VALUE
    var right = Int.MIN_VALUE
    field.forEachIndexed { i, j, v ->
        if (v != -1) {
            top = min(top, i)
            bottom = max(bottom, i)
            left = min(left, j)
            right = max(right, j)
        }
    }

    val nodesMap = mutableMapOf<Int, MutableMap<Int, Node20>>()
    field.forEachIndexed { i, j, v ->
        if (v == ROOM || v == START) {
            val a = nodesMap.getOrPut(i) { mutableMapOf() }
            a[j] = Node20()
        }
    }
    field.forEachIndexed { i, j, v ->
        if (v == DOOR) {
            if (nodesMap[i - 1]?.get(j) != null) {
                //vertical
                nodesMap[i - 1]?.get(j)?.childs?.add(nodesMap[i + 1]?.get(j)!!)
                nodesMap[i + 1]?.get(j)?.childs?.add(nodesMap[i - 1]?.get(j)!!)
            } else {
                //horizontal
                nodesMap[i]?.get(j - 1)?.childs?.add(nodesMap[i]?.get(j + 1)!!)
                nodesMap[i]?.get(j + 1)?.childs?.add(nodesMap[i]?.get(j - 1)!!)
            }
        }
    }

    val root = nodesMap.get(startI)?.get(startJ)!!
    root.countDepth(0)

    //Find max depth
    var max = Int.MIN_VALUE
    nodesMap.forEach { key, v ->
        v.forEach { key, v ->
            max = max(max, v.depth)
        }
    }
    println(max)

    //How many rooms have a shortest path from your current location that pass through at least 1000 doors?
    var count = 0
    nodesMap.forEach { _, v ->
        v.forEach { _, v ->
            if (v.depth >= 1000) {
                ++count
            }
        }
    }
    println(count)
}

private fun parsePath(field: Array<IntArray>, path: String, si: Int, sj: Int, sp: Int): ParsePathResult {
    var i = si
    var j = sj
    var p = sp

    while (path[p] != '$') {
        when (path[p++]) {
            'E' -> {
                field[i][++j] = DOOR
                field[i][++j] = ROOM
            }
            'S' -> {
                field[++i][j] = DOOR
                field[++i][j] = ROOM
            }
            'W' -> {
                field[i][--j] = DOOR
                field[i][--j] = ROOM
            }
            'N' -> {
                field[--i][j] = DOOR
                field[--i][j] = ROOM
            }
            '|' -> {
                i = si
                j = sj
            }
            '(' -> {
                val result = parsePath(field, path, i, j, p)
                i = result.i
                j = result.j
                p = result.p
            }
            ')' -> {
                return ParsePathResult(i, j, p)
            }
        }
    }

    return ParsePathResult(i, j, p)
}

private fun first20Alternative(data: String) {
    val nodesMap = mutableMapOf<Int, MutableMap<Int, Node20>>()
    val root = Node20()
    nodesMap[0] = mutableMapOf(0 to root)

    parsePathToMap(nodesMap, data, si = 0, sj = 0, sp = 1)

    root.countDepth(0)

    //Find max depth
    var max = Int.MIN_VALUE
    nodesMap.forEach { key, v ->
        v.forEach { key, v ->
            max = max(max, v.depth)
        }
    }
    println(max)

    //How many rooms have a shortest path from your current location that pass through at least 1000 doors?
    var count = 0
    nodesMap.forEach { _, v ->
        v.forEach { _, v ->
            if (v.depth >= 1000) {
                ++count
            }
        }
    }
    println(count)
}

private fun parsePathToMap(
        nodes: MutableMap<Int, MutableMap<Int, Node20>>,
        path: String,
        si: Int,
        sj: Int,
        sp: Int
): ParsePathResult {
    var i = si
    var j = sj
    var p = sp

    while (path[p] != '$') {
        when (path[p++]) {
            'E' -> {
                val oldNode = nodes.get(i)!!.get(j)!!
                ++j
                val newNode = nodes.get(i)!!.getOrPut(j) { Node20() }
                oldNode.childs.add(newNode)
                newNode.childs.add(oldNode)
            }
            'S' -> {
                val oldNode = nodes.get(i)!!.get(j)!!
                ++i
                val newNode = nodes.getOrPut(i) { mutableMapOf() }.getOrPut(j) { Node20() }
                oldNode.childs.add(newNode)
                newNode.childs.add(oldNode)
            }
            'W' -> {
                val oldNode = nodes.get(i)!!.get(j)!!
                --j
                val newNode = nodes.get(i)!!.getOrPut(j) { Node20() }
                oldNode.childs.add(newNode)
                newNode.childs.add(oldNode)
            }
            'N' -> {
                val oldNode = nodes.get(i)!!.get(j)!!
                --i
                val newNode = nodes.getOrPut(i) { mutableMapOf() }.getOrPut(j) { Node20() }
                oldNode.childs.add(newNode)
                newNode.childs.add(oldNode)
            }
            '|' -> {
                i = si
                j = sj
            }
            '(' -> {
                val result = parsePathToMap(nodes, path, i, j, p)
                i = result.i
                j = result.j
                p = result.p
            }
            ')' -> {
                return ParsePathResult(i, j, p)
            }
        }
    }

    return ParsePathResult(i, j, p)
}

private class ParsePathResult(val i: Int, val j: Int, val p: Int)

private class Node20(
        val childs: MutableList<Node20> = mutableListOf()
) {
    var depth = Int.MAX_VALUE
        private set

    fun countDepth(d: Int) {
        if (d < depth) {
            depth = d

            childs.forEach {
                it.countDepth(d + 1)
            }
        }
    }
}
