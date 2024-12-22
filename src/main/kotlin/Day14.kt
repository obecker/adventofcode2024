// https://adventofcode.com/2024/day/14

fun main() {
    val input = fileReader("day14/sample.txt").readLines()
    val robots = parseRobots(input)

    val area = Dimension(11, 7)
//    val area = Dimension(101, 103)

    robots
        .map { it.move(100, area) }
        .groupBy { it.toQuadrant(area) }
        .filter { it.key != 0 }
        .map { it.value.size }
        .fold(1) { product, i -> product * i }
        .debug("result1")

    var seconds = 0
    var movedRobots = robots
    while (!movedRobots.isDistinct()) {
        movedRobots = movedRobots.map { it.move(1, area) }
        seconds++
    }
    movedRobots.toMap(area).debugList("Christmas easter egg")
    seconds.debug("result2")
}

private typealias Dimension = Position

private data class Robot(
    val position: Position,
    val velocity: Vector
) {
    fun move(seconds: Int, area: Dimension): Robot {
        return copy(position = (position + velocity * seconds).mod(area))
    }

    fun toQuadrant(area: Position): Int {
        val (xHalf, yHalf) = (area.x / 2) to (area.y / 2)
        return when {
            position.x < xHalf && position.y < yHalf -> 1
            position.x > xHalf && position.y < yHalf -> 2
            position.x < xHalf && position.y > yHalf -> 3
            position.x > xHalf && position.y > yHalf -> 4
            else -> 0
        }
    }
}

private fun parseRobots(lines: List<String>): List<Robot> {
    return lines.map { it.split(" ") }.map { specs ->
        specs.flatMap { it.substringAfter("=").split(",").map { it.toInt() } }
    }.map { (px, py, vx, vy) -> Robot(Position(px, py), Vector(vx, vy)) }
}

private fun Position.mod(area: Dimension): Position {
    return Position(x.mod(area.x), y.mod(area.y))
}

private fun List<Robot>.toMap(area: Position): List<String> {
    val map = Array<Array<Char>>(area.y) { Array(area.x) { '.' } }
    forEach { map[it.position.y][it.position.x] = '#' }
    return map.map { it.joinToString("") }
}

private fun List<Robot>.isDistinct(): Boolean {
    return distinctBy { it.position }.size == size
}
