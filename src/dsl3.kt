import kotlinx.coroutines.generate
import sm1.buildStateMachine
import sm1.stateMachine
import java.util.concurrent.*
import kotlin.coroutines.*

fun main(args: Array<String>) {

    /**
     * A -[1]->  ((B))  -[2]->  C
     *           u      <-[1]-
     *           1
     */
    val sm = buildStateMachine<Int>{

        fun log(str:String){
            println("[ ${Thread.currentThread().name} ] $str")
        }

        // initial state (uses fake initial state)
        label = "A"
        log("A")

        // simpleTransitions for initial state
        val b = via(1){
            label = "B"
            final = true
            log("B")
        }
        loop(1)

        via(2){
            label = "C"
            log("C")
        }
        via(1,b)

        onError { state, letter ->
            log("error in state $state : letter $letter not accepted")
        }
    }
    
    with(sm) {
        input(5) // error
        input(1) // A -> B
        input(1) // B -> B
        input(3) // error
        input(1) // B -> B
        input(2) // B -> C
        input(1) // C -> B
        input(2) // B -> C
        input(3) // error
        endInput()
        println("finished: " + waitForFinish(1000))
    }

}