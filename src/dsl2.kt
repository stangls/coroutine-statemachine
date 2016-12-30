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
    with(
        buildStateMachine<Int> {

            // initial state
            state {
                label="A"
                println("A")
            }

            via(1){
                label = "B"
                println("B")
                onError { println("expected input 1 or 2, got $it") }
            }
            loop(1)
            via(2){
                label = "C"
                final = true
                println("C")
            }

            onError { state, letter ->
                println("error in state $state")
            }
        }
    ){
        input(5) // error
        input(1) // A -> B
        input(1) // B -> B
        input(3) // error
        input(1) // B -> B
        input(2) // B -> C
        input(1) // error
        input(2) // error
        input(3) // error
        endInput()
        waitForFinish()
    }

}