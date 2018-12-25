package adventofcode

import java.io.File
import java.text.SimpleDateFormat

private val dateFormat = SimpleDateFormat("yyyyy-MM-dd hh:mm")

fun main(args: Array<String>) {
    println(first4(File("./data/day4_1.txt").readLines()))
    println(second4(File("./data/day4_2.txt").readLines()))
}

fun first4(data: List<String>): Int {
    val total = mutableMapOf<Int, Int>()
    val sorted = data.sortedBy { dateFormat.parse(it.substring(1, 17)).time }

    var currentId = -1
    var startedAt = 0
    sorted.forEach {
        when {
            it.contains("begins shift") -> currentId = it.substring(26, it.indexOf(" ", 26)).toInt()
            it.contains("falls asleep") -> startedAt = it.substring(15, 17).toInt()
            else -> {
                val end = it.substring(15, 17).toInt()
                val duration = end - startedAt
                total[currentId] = total.getOrDefault(currentId, 0) + duration
            }
        }
    }

    val guardId = total.maxBy { it.value }?.key ?: -1

    val timeline = IntArray(60) { 0 }

    sorted.forEach {
        when {
            it.contains("begins shift") -> currentId = it.substring(26, it.indexOf(" ", 26)).toInt()
            it.contains("falls asleep") -> startedAt = it.substring(15, 17).toInt()
            currentId == guardId -> {
                val end = it.substring(15, 17).toInt()
                (startedAt until end).forEach {
                    ++timeline[it]
                }
            }
        }
    }

    var max = timeline[0]
    var index = 0
    timeline.forEachIndexed { i, v ->
        if (max < v) {
            max = v
            index = i
        }
    }

    return guardId * index
}

fun second4(data: List<String>): Int {
    val sorted = data.sortedBy { dateFormat.parse(it.substring(1, 17)).time }

    val timelines = mutableMapOf<Int, IntArray>()
    var max = 0
    var maxId = 0
    var maxMinute = 0

    var currentId = -1
    var startedAt = 0
    sorted.forEach {
        when {
            it.contains("begins shift") -> currentId = it.substring(26, it.indexOf(" ", 26)).toInt()
            it.contains("falls asleep") -> startedAt = it.substring(15, 17).toInt()
            else -> {
                val end = it.substring(15, 17).toInt()

                timelines.getOrPut(currentId) { IntArray(60) { 0 } }.let { timeline ->
                    (startedAt until end).forEach {
                        ++timeline[it]

                        if (max < timeline[it]) {
                            max = timeline[it]

                            maxMinute = it
                            maxId = currentId
                        }
                    }
                }
            }
        }
    }

    return maxId * maxMinute
}
