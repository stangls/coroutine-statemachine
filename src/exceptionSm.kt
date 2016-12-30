import kotlinx.coroutines.generate
import sm1.buildStateMachine
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
        val input = getInput()
        while(input !=1){
            throw Exception("invalid input $input")
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
    }

    sm.slowFeeder = 100.0

    sm.input(5) // exception
    sm.input(1) // is rethrown here
    sm.input(1) // or here
    sm.input(2) // or here
    sm.waitForFinish() // or here

}