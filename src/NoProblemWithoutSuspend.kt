/**
 * Created by sd on 30.12.16.
 */
class StateMachine2<Alphabet> internal constructor() {
    fun getInputStub(): Alphabet = null as Alphabet
}

fun <T> stateMachine2(block: StateMachine2<T>.() -> Unit): StateMachine2<T> {
    return StateMachine2<T>()
}

class ProblemWithoutSuspend<Alphabet>(){

    fun getInputStub(): Alphabet = null as Alphabet

    fun createStateMachine(): StateMachine2<Alphabet> = stateMachine2 {
        val letter = getInputStub()
        if (letter is Any)
            println("yes")
    }

}