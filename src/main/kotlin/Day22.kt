// https://adventofcode.com/2024/day/22

fun main() {
    // note: part 1 and part 2 use different samples -> sample.txt is the sample for the part 1
    val input = fileReader("day22/sample.txt").readLines().map { it.toLong() }

    input.sumOf {
        generateSequence(it) { pseudoRandom(it) }.drop(2000).first()
    }.debug("result1")

    val seqLength = 4
    val pricesPerBuyer = input.map {
        generateSequence(it) { pseudoRandom(it) }
            .map { it.mod(10) }
            .zipWithNext { a, b -> b to (b - a) }
            .take(2000)
            .toList()
    }.map { priceAndChange ->
        priceAndChange.map { it.second }
            .windowed(seqLength)
            .mapIndexed { idx, seq -> seq to priceAndChange[idx + seqLength - 1].first }
            .fold(mutableMapOf<List<Int>, Int>()) { priceMap, (seq, price) ->
                priceMap.apply { putIfAbsent(seq, price) }
            }
    }

    pricesPerBuyer.flatMap { it.keys }.toSet().maxOf { changeSeq ->
        pricesPerBuyer.sumOf { change ->
            change[changeSeq] ?: 0
        }
    }.debug("result2")
}

private fun pseudoRandom(value: Long): Long {
    var newSecret = value
    newSecret = mixAndPrune(newSecret, newSecret * 64)
    newSecret = mixAndPrune(newSecret, newSecret / 32)
    newSecret = mixAndPrune(newSecret, newSecret * 2048)
    return newSecret
}

private fun mixAndPrune(secret: Long, newSecret: Long) = (secret xor newSecret) % 16777216
