// https://adventofcode.com/2024/day/23

fun main() {
    val input = fileReader("day23/sample.txt").readLines()
    val connections = input.map { it.split("-") }
        .flatMap { (c1, c2) -> listOf(c1 to c2, c2 to c1) }
        .groupBy { it.first }
        .mapValues { (_, v) -> v.map { it.second } }

    connections.keys
        .filter { it.startsWith("t") }
        .flatMap { circleOf3(it, connections) }
        .toSet()
        .size.debug("result1")

    biggestGroup(connections)
        .map { it.sorted().joinToString(",") }
        .debug("result2")
}

private fun circleOf3(
    node: String,
    connections: Map<String, List<String>>,
    current: String = node,
    visited: Set<String> = emptySet()
): Set<Set<String>> {
    if (visited.size == 3) {
        return if (node == current) setOf(visited) else emptySet()
    }

    return connections[current].orEmpty().filter { it !in visited }
        .flatMap { circleOf3(node, connections, it, visited + it) }
        .toSet()
}

private fun biggestGroup(connections: Map<String, List<String>>) =
    biggestGroup(
        connections,
        connections.keys.map { setOf(it) to (connections.keys - it) }.toSet()
    )

private fun biggestGroup(
    connections: Map<String, List<String>>,
    currentGroups: Set<Pair<Set<String>, Collection<String>>>
): List<Set<String>> {
    val nextGroups = currentGroups.flatMap { (group, candidates) ->
        val next = candidates
            .filter { node -> group.all { member -> connections[member]?.contains(node) == true } }
        next.map { node -> (group + node) to (next - node) }
    }.toSet()
    return if (nextGroups.isEmpty()) currentGroups.map { it.first } else biggestGroup(connections, nextGroups)
}
