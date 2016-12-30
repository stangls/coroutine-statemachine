import kotlinx.coroutines.generate
import sm1.stateMachine
import java.util.concurrent.*
import kotlin.coroutines.*

fun main(args: Array<String>) {

    /**
     * A -[1]->  B  -[2]-> C
     *           u
     *           1
     */
    val sm = stateMachine<Int> {
        println("state A")
        while(getInput()!=1){
            throw Exception("invalid input")
        }
        fun bodyB(){
            println("state B")
        }
        bodyB()
        loop@do {
            when(getInput()){
                1 -> bodyB()
                2 -> break@loop
            }
        }while(true)
        println("final state C")
        final()
    }

    sm.input(1)
    sm.input(1)
    sm.input(1)
    sm.input(2)
    sm.endInput()
    sm.waitForFinish()

}