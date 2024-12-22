// https://adventofcode.com/2024/day/17

private var registerA: Long = 0
private var registerB: Long = 0
private var registerC: Long = 0
private val program = mutableListOf<Int>()
private var pointer = 0
private var output = mutableListOf<Int>()

fun main() {
    // part two doesn't work with the sample input ¯\_(ツ)_/¯
    val input = fileReader("day17/input.txt").readLines()

    val registerRegex = Regex("^Register (.): (\\d+)$")
    val programRegex = Regex("^Program: (.*)$")

    val registers = mutableMapOf<Char, Long>()
    input.forEach {
        if (it.matches(registerRegex)) {
            val (register, value) = registerRegex.find(it)!!.destructured
            registers[register[0]] = value.toLong()
        } else if (it.matches(programRegex)) {
            val (programLine) = programRegex.find(it)!!.destructured
            programLine.split(",").forEach { program.add(it.toInt()) }
        }
    }
    registerA = registers['A']!!
    registerB = registers['B']!!
    registerC = registers['C']!!

    runProgram()
    output.joinToString(",").debug("result1")

    var solution2 = 0L
    var x = 0L
    do {
        do {
            x++
            registerA = x
            registerB = 0
            registerC = 0
            runProgram()
        } while (!program.joinToString().endsWith(output.joinToString()))
        solution2 = x
        x = x * 8 - 1
    } while (program.joinToString() != output.joinToString())

    solution2.debug("result2")
}

private fun runProgram() {
    output.clear()
    pointer = 0
    while (pointer < program.size) {
        when (program[pointer]) {
            0 -> adv()
            1 -> bxl()
            2 -> bst()
            3 -> jnz()
            4 -> bxc()
            5 -> out()
            6 -> bdv()
            7 -> cdv()
            else -> throw IllegalStateException("Invalid opcode ${program[pointer]}")
        }
        ++pointer
    }
}

private fun getNextComboOperand(): Long {
    val value = program[++pointer]
    return when (value) {
        in 0..3 -> value.toLong()
        4 -> registerA
        5 -> registerB
        6 -> registerC
        else -> throw IllegalStateException("Invalid operand $value")
    }
}

private fun getNextLiteralOperand() = program[++pointer]

private fun Long.pow(exp: Long): Long {
    var result = 1L
    var e = exp
    while (e-- > 0) {
        result *= this
    }
    return result
}

private fun adv() {
    val op = getNextComboOperand()
    val den = 2L.pow(op)
    registerA = registerA / den
}

private fun bxl() {
    val op = getNextLiteralOperand().toLong()
    registerB = registerB.xor(op)
}

private fun bst() {
    val op = getNextComboOperand()
    registerB = op.mod(8).toLong()
}

private fun jnz() {
    pointer = if (registerA != 0L) getNextLiteralOperand() - 1 else pointer + 1
}

private fun bxc() {
    pointer++
    registerB = registerB.xor(registerC)
}

private fun out() {
    output.add(getNextComboOperand().mod(8))
}

private fun bdv() {
    val op = getNextComboOperand()
    val den = 2L.pow(op)
    registerB = registerA / den
}

private fun cdv() {
    val op = getNextComboOperand()
    val den = 2L.pow(op)
    registerC = registerA / den
}
