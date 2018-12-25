package adventofcode

import java.io.File

fun main(args: Array<String>) {
    first13(File("./data/day13_1.txt").readLines())
    second13(File("./data/day13_2.txt").readLines())
}

fun first13(data: List<String>) {
    val carts = mutableListOf<Cart>()
    data.forEachIndexed { i, row ->
        row.forEachIndexed { j, ch ->
            when (ch) {
                '>' -> carts.add(Cart(i, j, Direction.RIGHT))
                '<' -> carts.add(Cart(i, j, Direction.LEFT))
                'v' -> carts.add(Cart(i, j, Direction.DOWN))
                '^' -> carts.add(Cart(i, j, Direction.UP))
            }
        }
    }

    var noCrash = true
    while (noCrash) {
        val sorted = carts.sortedWith(compareBy(Cart::i, Cart::j))
        sorted.forEach { cart ->
            cart.move(adjastChar(data[cart.i][cart.j]))

            //find collision
            carts.filter {
                cart != it && cart.i == it.i && cart.j == it.j
            }.forEach {
                noCrash = false
                println("${it.j},${it.i}")
            }
        }

    }
}

private fun second13(data: List<String>) {
    val carts = mutableListOf<Cart>()
    data.forEachIndexed { i, row ->
        row.forEachIndexed { j, ch ->
            when (ch) {
                '>' -> carts.add(Cart(i, j, Direction.RIGHT))
                '<' -> carts.add(Cart(i, j, Direction.LEFT))
                'v' -> carts.add(Cart(i, j, Direction.DOWN))
                '^' -> carts.add(Cart(i, j, Direction.UP))
            }
        }
    }

    while (carts.size > 1) {
        val crashed = mutableSetOf<Cart>()

        val sorted = carts.sortedWith(compareBy(Cart::i, Cart::j))
        sorted.forEach { cart ->
            if (!crashed.contains(cart)) {
                cart.move(adjastChar(data[cart.i][cart.j]))

                //find collision
                carts.filter {
                    cart != it && cart.i == it.i && cart.j == it.j
                }.forEach {
                    crashed.add(it)
                    crashed.add(cart)
                }
            }
        }

        carts.removeAll(crashed)
    }

    with(carts.first()) {
        println("$j,$i")
    }
}

private fun adjastChar(ch: Char) =
        when (ch) {
            '>' -> '-'
            '<' -> '-'
            'v' -> '|'
            '^' -> '|'
            else -> ch
        }

private enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

private class Cart(var i: Int, var j: Int, var dir: Direction, var nextTurn: Direction = Direction.LEFT) {
    fun move(ch: Char) {
        when (ch) {
            '|' -> {
                if (dir == Direction.UP) {
                    --i
                } else {
                    ++i
                }
            }
            '-' -> {
                if (dir == Direction.LEFT) {
                    --j
                } else {
                    ++j
                }
            }
            '/' -> {
                when (dir) {
                    Direction.LEFT -> {
                        ++i
                        dir = Direction.DOWN
                    }
                    Direction.RIGHT -> {
                        --i
                        dir = Direction.UP
                    }
                    Direction.UP -> {
                        ++j
                        dir = Direction.RIGHT
                    }
                    Direction.DOWN -> {
                        --j
                        dir = Direction.LEFT
                    }
                }
            }
            '\\' -> {
                when (dir) {
                    Direction.LEFT -> {
                        --i
                        dir = Direction.UP
                    }
                    Direction.RIGHT -> {
                        ++i
                        dir = Direction.DOWN
                    }
                    Direction.UP -> {
                        --j
                        dir = Direction.LEFT
                    }
                    Direction.DOWN -> {
                        ++j
                        dir = Direction.RIGHT
                    }
                }
            }
            '+' -> turn()
        }
    }

    private fun turn() {

        when (dir) {
            Direction.LEFT -> {
                when (nextTurn) {
                    Direction.LEFT -> {
                        ++i
                        dir = Direction.DOWN
                        nextTurn = Direction.UP
                    }
                    Direction.UP -> {
                        --j
                        dir = Direction.LEFT
                        nextTurn = Direction.RIGHT
                    }
                    Direction.RIGHT -> {
                        --i
                        dir = Direction.UP
                        nextTurn = Direction.LEFT
                    }
                }
            }
            Direction.RIGHT -> {
                when (nextTurn) {
                    Direction.LEFT -> {
                        --i
                        dir = Direction.UP
                        nextTurn = Direction.UP
                    }
                    Direction.UP -> {
                        ++j
                        dir = Direction.RIGHT
                        nextTurn = Direction.RIGHT
                    }
                    Direction.RIGHT -> {
                        ++i
                        dir = Direction.DOWN
                        nextTurn = Direction.LEFT
                    }
                }
            }
            Direction.UP -> {
                when (nextTurn) {
                    Direction.LEFT -> {
                        --j
                        dir = Direction.LEFT
                        nextTurn = Direction.UP
                    }
                    Direction.UP -> {
                        --i
                        dir = Direction.UP
                        nextTurn = Direction.RIGHT
                    }
                    Direction.RIGHT -> {
                        ++j
                        dir = Direction.RIGHT
                        nextTurn = Direction.LEFT
                    }
                }
            }
            Direction.DOWN -> {
                when (nextTurn) {
                    Direction.LEFT -> {
                        ++j
                        dir = Direction.RIGHT
                        nextTurn = Direction.UP
                    }
                    Direction.UP -> {
                        ++i
                        dir = Direction.DOWN
                        nextTurn = Direction.RIGHT
                    }
                    Direction.RIGHT -> {
                        --j
                        dir = Direction.LEFT
                        nextTurn = Direction.LEFT
                    }
                }
            }
        }
    }
}