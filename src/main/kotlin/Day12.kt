// https://adventofcode.com/2024/day/12

fun main() {
    val input = fileReader("day12/sample.txt").readLines()

    // identify fences
    val hFences = Array(input.size + 1) { Array(input[0].length) { false } }
    val vFences = Array(input.size) { Array(input[0].length + 1) { false } }
    for (y in input.indices) {
        for (x in input[y].indices) {
            val position = Position(x, y)
            if (x == 0) {
                vFences[y][x] = true
            }
            if (y == 0) {
                hFences[y][x] = true
            }
            val right = position + Direction.RIGHT
            if ((right.isInside(input) && input.at(position) != input.at(right)) || !right.isInside(input)) {
                vFences[y][x + 1] = true
            }
            val down = position + Direction.DOWN
            if ((down.isInside(input) && input.at(position) != input.at(down)) || !down.isInside(input)) {
                hFences[y + 1][x] = true
            }
        }
    }

    val regions = findRegions(input)

    regions.map { region ->
        region.size to region.flatMap { p ->
            listOf(p, p + Direction.RIGHT).filter { vFences[it.y][it.x] } +
                    listOf(p, p + Direction.DOWN).filter { hFences[it.y][it.x] }
        }
    }.sumOf { (size, fences) -> size * fences.size }.debug("result1")

    regions.map { region ->
        region.size to region.flatMap { p ->
            listOfNotNull(
                if (vFences[p.y][p.x]) Fence(p.x, p.y, FenceType.VERTICAL, -1) else null,
                if (vFences[p.y][p.x + 1]) Fence(p.x + 1, p.y, FenceType.VERTICAL, 1) else null,
                if (hFences[p.y][p.x]) Fence(p.x, p.y, FenceType.HORIZONTAL, -1) else null,
                if (hFences[p.y + 1][p.x]) Fence(p.x, p.y + 1, FenceType.HORIZONTAL, 1) else null
            )
        }
    }.map { (size, fences) ->
        size to fences.sorted().fold(mutableListOf<Fence>()) { newFences, next ->
            if (newFences.isNotEmpty()) {
                val last = newFences.last()
                if ((sameHorizontalLine(last, next) && last.x + 1 == next.x) ||
                    (sameVerticalLine(last, next) && last.y + 1 == next.y)
                ) {
                    newFences.removeLast()
                }
            }
            newFences.apply { add(next) }
        }
    }.sumOf { (size, fences) -> size * fences.size }.debug("result2")
}

private fun findRegions(input: List<String>): Set<Set<Position>> {
    val regions = mutableSetOf<Set<Position>>()
    val visited = mutableSetOf<Position>()
    fun findRegion(plant: Char, currentPosition: Position): Set<Position> {
        if (currentPosition !in visited && currentPosition.isInside(input) && input.at(currentPosition) == plant) {
            visited += currentPosition
            return setOf(currentPosition) +
                    findRegion(plant, currentPosition + Direction.UP) +
                    findRegion(plant, currentPosition + Direction.DOWN) +
                    findRegion(plant, currentPosition + Direction.LEFT) +
                    findRegion(plant, currentPosition + Direction.RIGHT)
        }
        return emptySet()
    }

    for (y in input.indices) {
        for (x in input[y].indices) {
            val position = Position(x, y)
            if (position in visited) {
                continue
            }
            val plant = input.at(position)
            val region = findRegion(plant, position)
            if (region.isNotEmpty()) {
                regions += region
            }
        }
    }
    return regions.toSet()
}

private fun List<String>.at(position: Position): Char = this[position.y][position.x]

private enum class FenceType {
    HORIZONTAL, VERTICAL
}

private data class Fence(
    val x: Int,
    val y: Int,
    val type: FenceType,
    val spin: Int
) : Comparable<Fence> {
    override fun compareTo(other: Fence): Int {
        return when {
            type != other.type -> type.compareTo(other.type)
            spin != other.spin -> spin - other.spin
            else -> if (type == FenceType.HORIZONTAL) {
                if (y != other.y) y - other.y else x - other.x
            } else {
                if (x != other.x) x - other.x else y - other.y
            }
        }
    }
}

private fun sameHorizontalLine(fence1: Fence, fence2: Fence): Boolean =
    fence1.type == FenceType.HORIZONTAL && fence2.type == FenceType.HORIZONTAL && fence1.spin == fence2.spin && fence1.y == fence2.y

private fun sameVerticalLine(fence1: Fence, fence2: Fence): Boolean =
    fence1.type == FenceType.VERTICAL && fence2.type == FenceType.VERTICAL && fence1.spin == fence2.spin && fence1.x == fence2.x
