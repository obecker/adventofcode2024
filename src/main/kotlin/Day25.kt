fun main() {
    val input = fileReader("day25/sample.txt").readLines()

    val (locks, keys) = input.chunked(8).map {
        (it.first().contains('#')) to
                it.filter { it.isNotBlank() }
                    .drop(1).dropLast(1)
                    .let { lines ->
                        List(lines[0].length) { index ->
                            lines.count { it[index] == '#' }
                        }
                    }
    }.partition { it.first }.let { (trueList, falseList) ->
        trueList.map { it.second } to falseList.map { it.second }
    }

    locks.sumOf { lock ->
        keys.count { key ->
            lock.zip(key).all { (l, k) -> l + k < 6 }
        }
    }.debug("result")
}
