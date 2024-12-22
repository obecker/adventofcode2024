// https://adventofcode.com/2024/day/16

fun main() {
    val input = fileReader("day16/sample.txt").readLines()
    val map = input.filter { it.startsWith("#") }.toMap()

    var position = map.findPositionOf('S')
    var direction = Direction.RIGHT

    val paths = move(map, listOf(ScoredPosition(position, direction)))
    paths.first().score.debug("result1")

    (paths.flatMap { it.visited }.toSet().size + 1).debug("result2") // +1 for the end position
}

private tailrec fun move(
    map: Array<CharArray>,
    positions: List<ScoredPosition>,
    visited: Map<Pair<Direction, Position>, Int> = emptyMap()
): List<ScoredPosition> {
    val (solved, unsolved) = positions.partition { map[it.position] == 'E' }

    val bestSolved = solved.groupBy { it.score }.minByOrNull { it.key }?.value.orEmpty()
    val bestCandidates = unsolved.groupBy { it.score }.minByOrNull { it.key }?.value.orEmpty()
    val otherCandidates = unsolved - bestCandidates

    if (bestSolved.isNotEmpty() && bestSolved.first().score.let { bestScore -> bestCandidates.all { it.score > bestScore } }) {
        return bestSolved
    }

    val next = bestCandidates.flatMap { sp ->
        Direction.entries.filter { it != sp.direction.opposite() }
            .map { d -> Triple(d, sp.position + d, sp.score + score(sp.direction, d) + 1) }
            .filter { (d, p, s) ->
                map[p] != '#' && visited[d to p]?.let { it >= s } != false && !sp.visited.contains(p)
            }
            .map { (d, p, s) -> ScoredPosition(p, d, s, sp.visited + sp.position) }
    }

    return move(
        map,
        bestSolved + next + otherCandidates,
        visited + next.map { it.direction to it.position to it.score })
}

private fun score(d1: Direction, d2: Direction): Int = when (d1) {
    d2 -> 0
//    d2.opposite() -> 2000 // not needed
    else -> 1000
}

private data class ScoredPosition(
    val position: Position,
    val direction: Direction,
    val score: Int = 0,
    val visited: List<Position> = emptyList()
)
