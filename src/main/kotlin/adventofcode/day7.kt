package adventofcode

import java.io.File
import java.lang.StringBuilder
import java.util.*

fun main(args: Array<String>) {
    println(first7(File("./data/day7_1.txt").readLines()))
    println(second7(File("./data/day7_2.txt").readLines()))
}

fun first7(data: List<String>): String? {
    val dependencies = mutableMapOf<Char, MutableSet<Char>>()
    data.forEach {
        dependencies.getOrPut(it[5]) { mutableSetOf() }
        dependencies.getOrPut(it[36]) { mutableSetOf() }.add(it[5])
    }

    val set = TreeSet<Char>()
    dependencies.filter {
        it.value.isEmpty()
    }.forEach { k, _ ->
        set.add(k)
    }
    set.forEach {
        dependencies.remove(it)
    }

    val builder = StringBuilder()
    while (set.isNotEmpty()) {
        val c = set.first()

        builder.append(c)
        set.remove(c)

        dependencies.forEach { key, value ->
            value.remove(c)
            if (value.isEmpty()) {
                set.add(key)
            }
        }
        set.forEach {
            dependencies.remove(it)
        }
    }

    println(set.joinToString())

    return builder.toString()
}

private const val WORKERS = 5
fun second7(data: List<String>): Int {
    val dependencies = mutableMapOf<Char, MutableSet<Char>>()
    data.forEach {
        dependencies.getOrPut(it[5]) { mutableSetOf() }
        dependencies.getOrPut(it[36]) { mutableSetOf() }.add(it[5])
    }

    val set = TreeSet<Char>()
    dependencies.filter {
        it.value.isEmpty()
    }.forEach { k, _ ->
        set.add(k)
    }
    set.forEach {
        dependencies.remove(it)
    }

    val left = Array(WORKERS) { Int.MAX_VALUE }
    val inProgress = Array(WORKERS) {
        if (set.isNotEmpty()) {
            val k = set.first()
            set.remove(k)

            left[it] = k.steps

            k
        } else {
            null
        }
    }

    var time = 0
    while (inProgress.count { it != null } > 0) {
        val min = left.min() ?: throw IllegalStateException("min not found")

        time += min

        (0 until WORKERS).forEach { i ->
            left[i] -= min

            if (left[i] == 0) {
                val c = inProgress[i]
                inProgress[i] = null
                left[i] = Int.MAX_VALUE

                dependencies.forEach { key, value ->
                    value.remove(c)
                    if (value.isEmpty()) {
                        set.add(key)
                    }
                }
            }
        }
        set.forEach {
            dependencies.remove(it)
        }

        //refill array
        (0 until WORKERS).forEach { i ->
            if (set.isNotEmpty() && inProgress[i] == null) {
                inProgress[i] = set.first()
                left[i] = inProgress[i]!!.steps

                set.remove(inProgress[i])
            }
        }
    }

    return time
}

private val Char.steps
        get() = (this - 'A') + 61