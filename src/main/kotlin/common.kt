data class Vector(val dx: Int, val dy: Int) {
    operator fun plus(other: Vector) = Vector(dx + other.dx, dy + other.dy)
    operator fun minus(other: Vector) = Vector(dx - other.dx, dy - other.dy)
    operator fun times(scalar: Int) = Vector(dx * scalar, dy * scalar)
}

enum class Direction(val vector: Vector) {
    UP(Vector(0, -1)),
    RIGHT(Vector(1, 0)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0));

    fun turnRight() = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}

data class Position(val x: Int, val y: Int) {
    operator fun plus(vector: Vector) = Position(x + vector.dx, y + vector.dy)
    operator fun minus(vector: Vector) = Position(x - vector.dx, y - vector.dy)

    operator fun minus(position: Position) = Vector(x - position.x, y - position.y)

    operator fun plus(direction: Direction) = this + direction.vector
    operator fun minus(direction: Direction) = this - direction.vector
}

fun Position.isInside(map: List<String>) = y in map.indices && x in map[y].indices
fun Position.isInside(map: Array<CharArray>) = y in map.indices && x in map[y].indices

fun List<String>.toMap(): Array<CharArray> = map { it.toCharArray() }.toTypedArray()

fun Array<CharArray>.findPositionOf(ch: Char): Position = mapIndexed { y, line -> y to line }
    .filter { (_, line) -> line.contains(ch) }
    .map { (y, line) -> y to line.indexOf(ch) }
    .map { (y, x) -> Position(x, y) }
    .single()

operator fun Array<CharArray>.get(position: Position) = this[position.y][position.x]

operator fun Array<CharArray>.set(position: Position, c: Char) {
    this[position.y][position.x] = c
}

fun Array<CharArray>.deepCopy(): Array<CharArray> = map { it.copyOf() }.toTypedArray()
