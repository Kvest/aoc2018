package adventofcode

fun main(args: Array<String>) {
    println(first9(459, 71790))
    println(first9(459, 7179000))
}

fun first9(playersCount: Int, steps: Int): Long {
    val scores = LongArray(playersCount) { 0 }

    var curr = Node(0)

    (1..steps).forEach { i ->
        if (i % 23 == 0) {
            scores[(i - 1) % playersCount] += i.toLong()

            repeat(7) {
                curr = curr.prev
            }

            scores[(i - 1) % playersCount] += curr.i

            curr.next.prev = curr.prev
            curr.prev.next = curr.next
            curr = curr.next
        } else {
            curr = curr.next

            val newNode = Node(i.toLong())
            newNode.next = curr.next
            newNode.prev = curr
            curr.next = newNode
            newNode.next.prev = newNode

            curr = newNode
        }
    }

    return scores.max()!!
}

class Node(val i: Long) {
    var next: Node = this
    var prev: Node = this
}