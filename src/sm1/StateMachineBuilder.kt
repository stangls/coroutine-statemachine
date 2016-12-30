package sm1

/**
 * Created by sd on 30.12.16.
 */

@DslMarker
annotation class StateMachineDSL

@StateMachineDSL
open class State<T> internal constructor(
    var label: String? = null,
    protected val body: State<T>.() -> Unit
) {
    var final: Boolean = false
    internal fun visit() {
        this.body()
    }
    internal val simpleTransitions = mutableMapOf<T, State<T>>()
    internal val classBasedTransitions : MutableMap<Class<out T>, State<T>> = mutableMapOf()

    internal var errorCallback: ((T) -> Unit)? = null
    fun onError(function : (T) -> Unit) {
        errorCallback = function
    }/*
    @Deprecated("loops should be defined outside of states")
    fun loop(letter:T){}
    @Deprecated("simpleTransitions should be defined outside of states")
    fun via(letter: T, b:Any?){}*/

    override fun toString(): String = label?:"State ${hashCode()}"
    val noTransitions: Boolean
      get() = simpleTransitions.isEmpty() && classBasedTransitions.isEmpty()
}
/*
class Transition<T> internal constructor(
    val builder : StateMachineBuilder<T>,
    val fromState: State<T>, val letter: T, val nextState: State<T>
) {/*
    fun loop( letter: T ) = builder.transition(nextState,letter,nextState)
    fun via(letter: T, nextState: State<T> ) = builder.transition(this.nextState,letter,nextState)
    fun via(letter: T, nextState: State<T>.() -> Unit ) = builder.transition(this.nextState, letter, nextState)*/
}*/

internal class FakeInitialState<T> : State<T>( "Initial State", {} ) {
    var used = false
}

@StateMachineDSL
class StateMachineBuilder<Alphabet> internal constructor(val block: StateMachineBuilder<Alphabet>.() -> Unit){

    internal val fakeInitialState = FakeInitialState<Alphabet>()
    var initialState : State<Alphabet> = fakeInitialState
    var currentState: State<Alphabet> = fakeInitialState

    protected fun use(state: State<Alphabet>) {
        if (state==fakeInitialState){
            fakeInitialState.used=true
        }
    }

    fun state( label:String?=null, function: State<Alphabet>.() -> Unit = {} ): State<Alphabet> {
        val state = State<Alphabet>(label,function)
        if (initialState===fakeInitialState && !fakeInitialState.used){
            initialState = state
        }
        currentState = state
        return state
    }
    fun state( function: State<Alphabet>.() -> Unit = {} ): State<Alphabet> = state(null,function)

    fun transition(fromState: State<Alphabet>, letter: Alphabet, nextState: State<Alphabet>): State<Alphabet> {
        fromState.simpleTransitions[letter] = nextState
        use(fromState)
        return nextState //Transition(this,fromState,letter,nextState)
    }
    fun transition(fromState: State<Alphabet>.() -> Unit, letter: Alphabet, toState: State<Alphabet>.() -> Unit) =
        transition(state(fromState), letter, state(toState))
    fun transition(fromState: State<Alphabet>, letter: Alphabet, toState: State<Alphabet>.() -> Unit): State<Alphabet> {
        use(fromState)
        return transition(fromState,letter,state(toState))
    }

    fun transition(fromState: State<Alphabet>, clazz: Class<out Alphabet>, nextState: State<Alphabet>): State<Alphabet> {
        fromState.classBasedTransitions[clazz] = nextState
        use(fromState)
        return nextState //Transition(this,fromState,letter,nextState)
    }
    fun transition(fromState: State<Alphabet>.() -> Unit, clazz: Class<out Alphabet>, toState: State<Alphabet>.() -> Unit) =
            transition(state(fromState), clazz, state(toState))
    fun transition(fromState: State<Alphabet>, clazz: Class<out Alphabet>, toState: State<Alphabet>.() -> Unit): State<Alphabet> {
        use(fromState)
        return transition(fromState,clazz,state(toState))
    }

    fun via(letter: Alphabet, nextState: State<Alphabet> ) = transition(currentState, letter, nextState)
    fun via(letter: Alphabet, nextState: State<Alphabet>.() -> Unit ) = transition(currentState, letter, nextState)
    fun via(clazz: Class<out Alphabet>, nextState: State<Alphabet> ) = transition(currentState, clazz, nextState)
    fun via(clazz: Class<out Alphabet>, nextState: State<Alphabet>.() -> Unit ) = transition(currentState, clazz, nextState)

    fun loop(letter: Alphabet) = transition(currentState,letter, currentState)
    fun loop(clazz: Class<out Alphabet>) = transition(currentState,clazz, currentState)

    fun createStateMachine(): StateMachine<Alphabet> = stateMachine {
        this@StateMachineBuilder.block()
        var state: State<Alphabet> = initialState
        state.visit()
        while (!state.final || !state.noTransitions){
            val letter = null as Alphabet //getInput()
            val nextState : State<Alphabet>? =
                state.simpleTransitions[letter] ?:
                if (letter is Object)
                    state.classBasedTransitions[letter.javaClass as Class<out Alphabet>]
                else null
            if (nextState!=null){
                unfinal()
                state = nextState
                state.visit()
                if (state.final){
                    final()
                }
            } else {
                state.errorCallback?.invoke(letter)
                errorCallback?.invoke(state, letter)
            }
        }
    }

    private var errorCallback: ((State<Alphabet>, letter: Alphabet) -> Unit)? = null

    fun onError(function: (State<Alphabet>, letter: Alphabet) -> Unit) {
        errorCallback = function
    }

    var label: String?
        set(value) { currentState.label = value }
        get() = currentState.label

}

fun <T> buildStateMachine(block: StateMachineBuilder<T>.() -> Unit): StateMachine<T> {
    val builder = StateMachineBuilder<T>(block)
    return builder.createStateMachine()
}