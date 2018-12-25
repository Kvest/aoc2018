package adventofcode

import java.io.File

/*
#######
#.G...#
#...EG#
#.#.#G#
#..G#E#
#.....#
#######


#######
#G..#E#
#E#E.E#
#G.##.#
#...#E#
#...E.#
#######

#######
#E..EG#
#.#G.E#
#E.##E#
#G..#.#
#..E#.#
#######

#######
#E.G#.#
#.#G..#
#G.#.G#
#G..#.#
#...E.#
#######

#######
#.E...#
#.#..G#
#.###.#
#E#G#G#
#...#G#
#######

#########
#G......#
#.E.#...#
#..##..G#
#...##..#
#...#...#
#.G...G.#
#.....G.#
#########

 */

fun main(args: Array<String>) {
    println(first15(File("./data/day15_1.txt").readLines()))
}

fun first15(data: List<String>): Int {
    val field = Field(data)

    return field.play()
}

private val ORDER = arrayOf(arrayOf(-1, 0), arrayOf(0, -1), arrayOf(0, 1), arrayOf(1, 0))

private class Field(data: List<String>) {
    private val field: Array<BooleanArray>
    private val units = mutableListOf<Creature>()
    private val unitsField = Array<Array<Creature?>>(data.size) { Array(data[it].length) { null } }

    init {
        field = Array(data.size) { i ->
            BooleanArray(data[i].length) { j ->
                when (data[i][j]) {
                    'G' -> {
                        val unit = Creature(CreatureType.GOBLIN, i, j)

                        units.add(unit)
                        unitsField[i][j] = unit
                    }
                    'E' -> {
                        val unit = Creature(CreatureType.ELF, i, j)

                        units.add(unit)
                        unitsField[i][j] = unit
                    }
                }
                data[i][j] != '#'
            }
        }
    }

    fun play(): Int {
        var roundsCount = 0

        while (isNotFinished()) {
            if (round()) {
                ++roundsCount
            }

            print()
        }

        println("$roundsCount * " + units.sumBy { it.hp })

        return units.sumBy { it.hp } * roundsCount
    }

    private fun isNotFinished(): Boolean {
        return (units.count { it.type == CreatureType.GOBLIN } > 0) && (units.count { it.type == CreatureType.ELF } > 0)
    }

    private fun round(): Boolean {
        val done = mutableSetOf<Creature>()

        iterateWithoutBorders { i, j ->
            unitsField[i][j]?.let {
                if (!done.contains(it)) {
                    if (!isNotFinished()) {
                        return false
                    }

                    turn(it)
                    done.add(it)
                }
            }
        }

        return true
    }

    private fun turn(creature: Creature) {
        move(creature)

        attack(creature)
    }

    private fun attack(creature: Creature) {
        var minHP = Int.MAX_VALUE
        var minI = 0
        var minJ = 0

        ORDER.forEach {
            val i = creature.i + it[0]
            val j = creature.j + it[1]

            unitsField[i][j]?.let {
                if (it.type == creature.enemyType && it.hp < minHP) {
                    minHP = it.hp
                    minI = i
                    minJ = j
                }
            }

        }

        if (minHP != Int.MAX_VALUE) {
            val target = unitsField[minI][minJ]!!
            target.hp -= creature.attack
            if (target.isDead) {
                units.remove(target)
                unitsField[minI][minJ] = null
            }
        }
    }

    private fun move(creature: Creature) {
        if (hasAdjacentEnemy(creature.enemyType, creature.i, creature.j)) {
            return
        }

        //build possible moves map
        val moveMap = buildMoveMap(creature)

        //find where to go
        var min = Int.MAX_VALUE
        var nextI = 0
        var nextJ = 0
        ORDER.forEach {
            val i = creature.i + it[0]
            val j = creature.j + it[1]
            if (moveMap[i][j] != Int.MAX_VALUE && moveMap[i][j] < min) {
                min = moveMap[i][j]
                nextI = i
                nextJ = j
            }
        }

        if (min != Int.MAX_VALUE) {
            unitsField[creature.i][creature.j] = null
            unitsField[nextI][nextJ] = creature
            creature.i = nextI
            creature.j = nextJ
        }
    }

    private fun buildMoveMap(creature: Creature): Array<IntArray> {
        val result = Array(field.size) { i -> IntArray(field[i].size) { Int.MAX_VALUE } }
        units.filter { it.type == creature.enemyType }
                .forEach {
                    result[it.i][it.j] = 0
                }

        var hasChanges = true
        while (hasChanges) {
            hasChanges = false

            iterateWithoutBorders { i, j ->
                if (result[i][j] == Int.MAX_VALUE && freeCell(i, j)) {
                    val newVal = minFrom(result[i - 1][j], result[i][j - 1], result[i][j + 1], result[i + 1][j])

                    if (newVal != Int.MAX_VALUE) {
                        result[i][j] = newVal + 1
                        hasChanges = true
                    }
                }
            }
        }

        return result
    }

    inline private fun iterateWithoutBorders(action: (Int, Int) -> Unit) {
        (1 until (field.size - 1)).forEach { i ->
            (1 until (field[i].size - 1)).forEach { j ->
                action(i, j)
            }
        }
    }

    private fun freeCell(i: Int, j: Int) = field[i][j] && unitsField[i][j] == null

    private fun hasAdjacentEnemy(enemy: CreatureType, i: Int, j: Int): Boolean {
        return unitsField[i - 1][j]?.type == enemy || unitsField[i][j - 1]?.type == enemy ||
                unitsField[i][j + 1]?.type == enemy || unitsField[i + 1][j]?.type == enemy
    }

    fun print() {
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, v ->
                when {
                    !v -> print('#')
                    unitsField[i][j]?.type == CreatureType.GOBLIN -> print('G')
                    unitsField[i][j]?.type == CreatureType.ELF -> print('E')
                    else -> print('.')
                }
            }
            println()
        }

        unitsField.forEachIndexed { i, row ->
            row.forEachIndexed { j, u ->
                u?.let {
                    print(it.toString())
                }
            }
            println()
        }
    }
}

private class Creature(val type: CreatureType, var i: Int, var j: Int, var hp: Int = 200, val attack: Int = 3) {
    val isDead: Boolean
        get() = hp <= 0
    val enemyType: CreatureType
        get() = if (type == CreatureType.GOBLIN) CreatureType.ELF else CreatureType.GOBLIN

    override fun toString(): String {
        return "$type($hp)"
    }
}

private enum class CreatureType {
    GOBLIN, ELF
}

private fun minFrom(vararg items: Int) = items.min()!!