import kotlinx.coroutines.generate

/**
 * Created by sd on 29.12.16.
 */


fun main(args: Array<String>) {

    val generator = generate<Number> {
        //(1..10).forEach(this::yield) // not allowed
        (1..10).forEach{yield(it)}
        yieldAll(1..10)
    }

    generator.forEach {
        println(it)
    }


}