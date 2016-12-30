package sm1

/**
 * Created by sd on 30.12.16.
 */

class StateMachineBuilder<Alphabet> internal constructor(){

    fun createStateMachine(): StateMachine<Alphabet> = stateMachine {
        val letter = getInputStub()
        val nextState : Int =
            if (letter is Object)
                1
            else
                2
    }

}