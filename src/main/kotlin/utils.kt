import java.io.FileNotFoundException
import kotlin.collections.joinToString

fun fileReader(name: String) =
    ClassLoader.getSystemResourceAsStream(name)?.bufferedReader() ?: throw FileNotFoundException(name)

fun <T> T.debug(value: Any) = also { println("$value: $it") }

fun <T> Collection<T>.debugList(value: Any) = also {
    println("$value:")
    forEach { println(it) }
    println()
}

fun Array<CharArray>.debug(value: Any) = also {
    println("$value:")
    forEachIndexed { i,a -> println(i.toString().padStart(3, ' ') + " " + a.joinToString("")) }
    println()
}

fun List<Char>.debug(value: Any) = also {
    println("$value: ${it.joinToString("")}")
}
