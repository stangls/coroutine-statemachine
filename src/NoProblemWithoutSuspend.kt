/**
 * Created by sd on 30.12.16.
 */
class StateMachina<Alphabet> internal constructor() {
    fun getInputStub(): Alphabet = null as Alphabet
}

fun <T> stateMachina(block: StateMachina<T>.() -> Unit): StateMachina<T> {
    return StateMachina<T>()
}

class ProblemWithoutSuspend<Alphabet>(){

    fun getInputStub(): Alphabet = null as Alphabet

    fun createStateMachine(): StateMachina<Alphabet> = stateMachina {
        val letter = getInputStub()
        if (letter is Any)
            println("yes")
    }

}