// https://adventofcode.com/2024/day/4

fun main() {
    val input = fileReader("day04/sample.txt").readLines()
    val linelength = input[0].length

    val horizontal = input.joinToString(" ")
    val vertical = input.toVertical().joinToString(" ")

    val diagonal1 = input.mapIndexed { row, line ->
        ("".padStart(row, ' ') + line).padEnd(linelength + input.size - 1, ' ')
    }.toVertical().joinToString(" ")

    val diagonal2 = input.mapIndexed { row, line ->
        (line + "".padStart(row, ' ')).padStart(linelength + input.size- 1, ' ')
    }.toVertical().joinToString(" ")

    val all = listOf(horizontal, vertical, diagonal1, diagonal2).joinToString(" ")

    (Regex("XMAS").findAll(all).count() + Regex("SAMX").findAll(all).count()).debug("result1")

    var result2 = 0
    for (i in 1 until input.size - 1) {
        for (j in 1 until linelength - 1) {
            if (input[i][j] == 'A') {
                val tl = input[i - 1][j - 1]
                val tr = input[i - 1][j + 1]
                val bl = input[i + 1][j - 1]
                val br = input[i + 1][j + 1]
                val chars = listOf(tl, tr, bl, br).joinToString("")
                if (chars in listOf("MMSS", "SSMM", "MSMS", "SMSM")) {
                    result2++
                }
            }
        }
    }
    result2.debug("result2")
}

private fun List<String>.toVertical(): List<String> {
    val linelength = this[0].length
    return (0 until linelength).map { index ->
        (0 until size).map { row -> this[row][index] }.joinToString("")
    }
}
