package adventofcode

fun Array<IntArray>.forEachIndexed(action: (Int, Int, Int) -> Unit) {
    this.forEachIndexed { i, row ->
        row.forEachIndexed { j, v ->
            action(i, j, v)
        }
    }
}

val OPCODES = mapOf<String, Opcode>("addr" to ::addr, "addi" to ::addi, "mulr" to ::mulr, "muli" to ::muli,
        "banr" to ::banr, "bani" to ::bani, "borr" to ::borr, "bori" to ::bori,
        "setr" to ::setr, "seti" to ::seti, "gtir" to ::gtir, "gtri" to ::gtri,
        "gtrr" to ::gtrr, "eqir" to ::eqir, "eqri" to ::eqri, "eqrr" to ::eqrr)

fun addr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] + registers[b]
}

fun addi(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] + b
}

fun mulr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] * registers[b]
}

fun muli(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] * b
}

fun banr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] and registers[b]
}

fun bani(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] and b
}

fun borr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] or registers[b]
}

fun bori(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a] or b
}

fun setr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = registers[a]
}

fun seti(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = a
}

fun gtir(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = if (a > registers[b]) 1 else 0
}

fun gtri(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = if (registers[a] > b) 1 else 0
}

fun gtrr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = if (registers[a] > registers[b]) 1 else 0
}

fun eqir(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = if (a == registers[b]) 1 else 0
}

fun eqri(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = if (registers[a] == b) 1 else 0
}

fun eqrr(a: Int, b: Int, c: Int, registers: IntArray) {
    registers[c] = if (registers[a] == registers[b]) 1 else 0
}