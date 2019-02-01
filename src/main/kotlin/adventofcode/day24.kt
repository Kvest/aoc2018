package adventofcode

import kotlin.math.min

//private val IMMUNE_SYSTEM = arrayOf(
//        "17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2",
//        "989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3"
//)
//
//private val INFECTION = arrayOf(
//        "801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1",
//        "4485 units each with 2961 hit points (weak to fire, cold; immune to radiation) with an attack that does 12 slashing damage at initiative 4"
//)

private val IMMUNE_SYSTEM = arrayOf(
        "7079 units each with 12296 hit points (weak to fire) with an attack that does 13 bludgeoning damage at initiative 14",
        "385 units each with 9749 hit points (weak to cold) with an attack that does 196 bludgeoning damage at initiative 16",
        "2232 units each with 1178 hit points (weak to cold, slashing) with an attack that does 4 fire damage at initiative 20",
        "917 units each with 2449 hit points (weak to bludgeoning; immune to fire, cold) with an attack that does 25 cold damage at initiative 15",
        "2657 units each with 2606 hit points (weak to slashing) with an attack that does 9 cold damage at initiative 13",
        "2460 units each with 7566 hit points ( ) with an attack that does 29 cold damage at initiative 8",
        "2106 units each with 6223 hit points ( ) with an attack that does 29 bludgeoning damage at initiative 2",
        "110 units each with 7687 hit points (weak to slashing; immune to radiation, fire) with an attack that does 506 slashing damage at initiative 19",
        "7451 units each with 9193 hit points (immune to cold) with an attack that does 12 radiation damage at initiative 6",
        "1167 units each with 3162 hit points (immune to bludgeoning; weak to fire) with an attack that does 23 fire damage at initiative 9"
)

private val INFECTION = arrayOf(
        "2907 units each with 11244 hit points (immune to slashing) with an attack that does 7 fire damage at initiative 7",
        "7338 units each with 12201 hit points (immune to bludgeoning, slashing, cold) with an attack that does 3 radiation damage at initiative 4",
        "7905 units each with 59276 hit points (immune to fire) with an attack that does 12 cold damage at initiative 17",
        "1899 units each with 50061 hit points (weak to fire) with an attack that does 51 radiation damage at initiative 10",
        "2711 units each with 27602 hit points ( ) with an attack that does 17 cold damage at initiative 12",
        "935 units each with 38240 hit points (immune to slashing) with an attack that does 78 bludgeoning damage at initiative 1",
        "2783 units each with 17937 hit points (immune to cold, bludgeoning) with an attack that does 12 fire damage at initiative 11",
        "8046 units each with 13608 hit points (weak to fire, bludgeoning) with an attack that does 2 slashing damage at initiative 5",
        "2112 units each with 37597 hit points (immune to cold, slashing) with an attack that does 31 slashing damage at initiative 18",
        "109 units each with 50867 hit points (immune to slashing; weak to radiation) with an attack that does 886 cold damage at initiative 3"
)

private val TYPE_IMMUNE_SYSTEM = 0
private val TYPE_INFECTION = 1
private val armyGroupReg = Regex("(\\d+) units each with (\\d+) hit points \\Q(\\E(\\D+)\\Q)\\E with an attack that does (\\d+) (\\D+) damage at initiative (\\d+)")

fun main(args: Array<String>) {
    first24(IMMUNE_SYSTEM, INFECTION)
    second24(IMMUNE_SYSTEM, INFECTION)
}

private fun first24(immuneSystemData: Array<String>, infectionData: Array<String>) {
    var groups = solve24(
            (immuneSystemData.map { ArmyGroup(TYPE_IMMUNE_SYSTEM, it) } + infectionData.map { ArmyGroup(TYPE_INFECTION, it) })
    )

    println(groups?.sumBy { it.count })
}

private fun second24(immuneSystemData: Array<String>, infectionData: Array<String>) {
    var boost = 0
    while (true) {

        var groups = solve24(
                (immuneSystemData.map { ArmyGroup(TYPE_IMMUNE_SYSTEM, it, boost) } + infectionData.map { ArmyGroup(TYPE_INFECTION, it) })
        )

        // println("$boost -> ${groups?.first().type}")

        if (groups?.first()?.type == TYPE_IMMUNE_SYSTEM) {
            println("at boost $boost")
            println(groups.sumBy { it.count })

            break
        }

        ++boost
    }
}

private fun solve24(src: List<ArmyGroup>): List<ArmyGroup>? {
    var groups = src.sortedByDescending { it.initiative }
    var prev = groups.sumBy { it.count }

    while (groups.count { it.type == TYPE_IMMUNE_SYSTEM } > 0 && groups.count { it.type == TYPE_INFECTION } > 0) {
        //build attack map
        val selected = mutableSetOf<ArmyGroup>()
        groups.sortedByDescending { it.effectivePower }.forEach { attacker ->
            var mostDamage = -1

            groups.forEach { target ->
                if (attacker.type != target.type && !selected.contains(target)) {
                    val dmg = target.countDamage(attacker)

                    if ((dmg >0 && dmg > mostDamage) || (dmg == mostDamage && target.effectivePower > attacker.target!!.effectivePower)) {
                        mostDamage = dmg
                        //remove old selected
                        attacker.target?.let { selected.remove(it) }

                        //add new selected
                        selected.add(target)

                        //mark target
                        attacker.target = target
                    }
                }
            }
        }

        //attack
        groups.forEach { attacker ->
            if (attacker.count > 0 && attacker.target != null) {
                val target = attacker.target!!

                target.attack(attacker)
            }
        }

        //clean up
        groups = groups.filter { it.count > 0 }
        groups.forEach { it.target = null }

        val newCount = groups.sumBy { it.count }
        if (newCount == prev) {
            //this is dead loop
            return null
        } else {
            prev = newCount
        }
    }

    return groups
}

private class ArmyGroup(val type: Int, src: String, boost: Int = 0) {
    var count: Int
    val hitPoints: Int
    val damage: Int
    val attackType: String
    val initiative: Int
    val weak: Set<String>
    val immune: Set<String>
    var target: ArmyGroup? = null
    val effectivePower: Int
        get() = count * damage

    init {
        val (_count, _hitPoints, _props, _damage, _attackType, _initiative) = armyGroupReg.find(src)!!.destructured
        count = _count.toInt()
        hitPoints = _hitPoints.toInt()
        damage = _damage.toInt() + boost
        attackType = _attackType
        initiative = _initiative.toInt()

        weak = if (_props.contains("weak to ")) {
            _props.substringAfter("weak to ").substringBefore(";").split(",").map { it.trim() }.toSet()
        } else {
            emptySet()
        }
        immune = if (_props.contains("immune to ")) {
            _props.substringAfter("immune to ").substringBefore(";").split(",").map { it.trim() }.toSet()
        } else {
            emptySet()
        }
    }

    fun countDamage(attaker: ArmyGroup) =
            when {
                immune.contains(attaker.attackType) -> 0
                weak.contains(attaker.attackType) -> attaker.effectivePower * 2
                else -> attaker.effectivePower
            }

    fun attack(attaker: ArmyGroup) {
        val damage = countDamage(attaker)

        count -= min(damage / hitPoints, count)
    }
}