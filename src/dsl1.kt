import kotlinx.coroutines.generate
import sm1.buildStateMachine
import java.util.concurrent.*
import kotlin.coroutines.*

fun main(args: Array<String>) {

    /**
     * A -[1]->  B  -[2]-> ((C))
     *           u
     *           1
     */
    val sm = buildStateMachine<Int> {

        // first state is always initial state
        val a = state{
            println("state A")
        }
        val b = state{
            println("state B")
        }
        // last state is final state
        val c = state{
            final=true
            println("state C")
        }

        transition(a,1,b)
        transition(b,1,b)
        transition(b,2,c)

        // TODO: allow multiple final states
    }

    sm.input(1) // A -> B
    sm.input(1) // B -> B
    sm.input(1) // B -> B
    sm.input(2) // B -> C
    sm.endInput()
    sm.waitForFinish()

}