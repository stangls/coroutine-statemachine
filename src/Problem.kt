/**
 * Created by sd on 30.12.16.
 */
@kotlin.coroutines.RestrictsSuspension
class StateMachine<Alphabet> internal constructor() {
    fun getInputStub(): Alphabet = null as Alphabet
}

fun <T> stateMachine(block: suspend StateMachine<T>.() -> Unit): StateMachine<T> {
    return StateMachine<T>()
}

class Problem<Alphabet>(){

    fun createStateMachine(): StateMachine<Alphabet> = stateMachine {
        val letter = getInputStub()
        if (letter is Any)
            println("yes")
    }

}