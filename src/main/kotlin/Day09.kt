// https://adventofcode.com/2024/day/9

private const val empty = -1

fun main() {
    val line = fileReader("day09/sample.txt").readText().trim()

    var currentId = 0
    var isBlock = true
    val segments = mutableListOf<List<Int>>()
    for (char in line) {
        segments += List(char.toString().toInt()) { if (isBlock) currentId else empty }
        isBlock = !isBlock
        if (isBlock) {
            currentId++
        }
    }

    val compactBlocks1 = method1(segments)
    checksum(compactBlocks1).debug("result1")

    val compactBlocks2 = method2(segments)
    checksum(compactBlocks2).debug("result2")

}

private fun method1(segments: List<List<Int>>): List<Int> {
    val blocks = segments.flatten().toMutableList()
    for (i in blocks.indices) {
        if (blocks[i] == empty) {
            for (j in blocks.size - 1 downTo i) {
                if (blocks[j] != empty) {
                    blocks[i] = blocks[j]
                    blocks[j] = empty
                    break
                }
            }
        }
    }
    return blocks
}

private fun method2(segments: List<List<Int>>): List<Int> {
    val copy = segments.map { it.toMutableList() }
    for (i in copy.indices.reversed()) {
        if (copy[i].firstOrNull() != empty) {
            for (j in 0 until i) {
                if (copy[j].count { it == empty } >= copy[i].size) {
                    val emptyIndex = copy[j].indexOf(empty)
                    for (k in copy[i].indices) {
                        copy[j][k + emptyIndex] = copy[i][k]
                        copy[i][k] = empty
                    }
                    break
                }
            }
        }
    }
    return copy.flatten()
}

private fun checksum(compactBlocks: List<Int>): Long {
    var result = 0L
    for (i in 0 until compactBlocks.size) {
        if (compactBlocks[i] != empty) {
            val id = compactBlocks[i]
            result += i * id
        }
    }
    return result
}
