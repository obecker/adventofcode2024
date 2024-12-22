// https://adventofcode.com/2024/day/8

fun main() {
    val lines = fileReader("day08/sample.txt").readLines()

    val uniqueAntennas = mutableMapOf<Char, List<Pair<Int, Int>>>()
    for (i in lines.indices) {
        for (j in lines[i].indices) {
            val antenna = lines[i][j]
            if (antenna != '.') {
                uniqueAntennas[antenna] = uniqueAntennas.getOrDefault(antenna, emptyList()) + Pair(i, j)
            }
        }
    }

    val uniqueLocations1 = mutableSetOf<Pair<Int, Int>>()
    val uniqueLocations2 = mutableSetOf<Pair<Int, Int>>()
    for (locations in uniqueAntennas.values) {
        for (i in locations.indices) {
            for (j in locations.indices) {
                if (i < j) {
                    val vector = locations[j] - locations[i]

                    val a = locations[i] - vector
                    if (a.isInside(lines)) {
                        uniqueLocations1.add(a)
                    }
                    val b = locations[j] + vector
                    if (b.isInside(lines)) {
                        uniqueLocations1.add(b)
                    }

                    var loc = locations[i]
                    do {
                        loc -= vector
                    } while (loc.isInside(lines))
                    do {
                        loc += vector
                        if (loc.isInside(lines)) {
                            uniqueLocations2.add(loc)
                        }
                    } while (loc.isInside(lines))
                }
            }
        }
    }
    uniqueLocations1.size.debug("result1")
    uniqueLocations2.size.debug("result2")
}

private fun Pair<Int, Int>.isInside(map: List<String>): Boolean = first in map.indices && second in map[first].indices
private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(first + other.first, second + other.second)
private operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>) = Pair(first - other.first, second - other.second)
