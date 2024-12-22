// https://adventofcode.com/2024/day/5

fun main() {
    val lines = fileReader("day05/sample.txt").readLines()

    val ordering = lines.filter { it.contains('|') }
        .map { it.split("|").map { it.toInt() } }
        .map { (a,b) -> a to b }
    val pageUpdates = lines.filter { it.contains(',') }
        .map { it.split(",").map { it.toInt() } }

    val correctPages = pageUpdates.filter { pages ->
        ordering.all { (page1, page2) ->
            val index1 = pages.indexOf(page1)
            val index2 = pages.indexOf(page2)
            index1 < 0 || index2 < 0 || index1 < index2
        }
    }
    correctPages.sumOf { it[it.size / 2] }.debug("result1")

    val sortedIncorrectPages = (pageUpdates - correctPages)
        .map { pages ->
            pages.sortedWith { p1, p2 ->
                if (ordering.contains(p1 to p2)) -1 else if (ordering.contains(p2 to p1)) 1 else 0
            }
        }
    sortedIncorrectPages.sumOf { it[it.size / 2] }.debug("result2")
}
