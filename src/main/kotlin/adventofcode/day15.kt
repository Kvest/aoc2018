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
    println(second15(File("./data/day15_1.txt").readLines()))
}

private const val DEFAULT_ELF_ATTACK = 3

fun first15(data: List<String>): Int {
    val field = Field(data, DEFAULT_ELF_ATTACK)

    return field.play()
}

fun second15(data: List<String>): Int {
    var elfAttack = DEFAULT_ELF_ATTACK + 1
    while (true) {
        val field = Field(data, elfAttack)
        var result = field.playWhileAlvesAlive()

        if (result > 0) {
            println("elfAttack=$elfAttack")
            return result
        } else {
            ++elfAttack
        }
    }
}

private val ORDER = arrayOf(arrayOf(-1, 0), arrayOf(0, -1), arrayOf(0, 1), arrayOf(1, 0))

private class Field(data: List<String>, elfAttack: Int) {
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
                        val unit = Creature(CreatureType.ELF, i, j, attack = elfAttack)

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

            //print()
        }

        println("$roundsCount * " + units.sumBy { it.hp })

        return units.sumBy { it.hp } * roundsCount
    }

    fun playWhileAlvesAlive(): Int {
        var roundsCount = 0
        val elvesCount = getCreaturesCount(CreatureType.ELF)

        while (isNotFinished()) {
            //check if any elf died
            if (getCreaturesCount(CreatureType.ELF) < elvesCount) {
                return -1
            }

            if (round()) {
                ++roundsCount
            }
        }

        return units.sumBy { it.hp } * roundsCount
    }

    private fun isNotFinished() = getCreaturesCount(CreatureType.GOBLIN) > 0 && getCreaturesCount(CreatureType.ELF) > 0

    private fun getCreaturesCount(type: CreatureType) = units.count { it.type == type }

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

            unitsField[i][j]?.let { cr ->
                if (cr.type == creature.enemyType && cr.hp < minHP) {
                    minHP = cr.hp
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
        var value = 0
        while (hasChanges) {
            hasChanges = false

            iterateWithoutBorders { i, j ->
                if (result[i][j] == Int.MAX_VALUE && freeCell(i, j) &&
                        (result[i - 1][j] == value || result[i][j - 1] == value || result[i][j + 1] == value || result[i + 1][j] == value)) {

                    result[i][j] = value + 1
                    hasChanges = true
                }
            }

            ++value
        }

        return result
    }

    private inline fun iterateWithoutBorders(action: (Int, Int) -> Unit) {
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

        unitsField.forEachIndexed { _, row ->
            var newLine = false
            row.forEachIndexed { _, u ->
                u?.let {
                    print(it.toString())
                    newLine = true
                }
            }
            if (newLine) {
                println()
            }
        }
    }
}

private class Creature(val type: CreatureType, var i: Int, var j: Int, val attack: Int = 3, var hp: Int = 200) {
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