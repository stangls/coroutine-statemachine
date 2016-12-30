import kotlinx.coroutines.generate
import sm1.buildStateMachine
import java.util.concurrent.*
import kotlin.coroutines.*

interface MyInput
open class StringInput(val text: String) : MyInput
open class IntInput(val value: Int) : MyInput
object VoidInput : MyInput

fun main(args: Array<String>) {

    /**
     * A -->  B  -Int-> ((C))
     *        u
     *      String
     */
    val sm = buildStateMachine<MyInput> {

        val a = state{
            println("initial state")
        }
        val b = state{
            println("string reading state")
        }
        val c = state{
            final=true
            println("int reading state")
        }

        transition(a,VoidInput,b)
        transition(b,StringInput::class.java,b)
        transition(b,IntInput::class.java,c)

        // TODO: allow multiple final states
    }

    sm.input(VoidInput) // A -> B
    sm.input(StringInput("hello")) // B -> B
    sm.input(StringInput("world")) // B -> B
    sm.input(IntInput(5)) // B -> C
    sm.endInput()
    println("finished : "+sm.waitForFinish(5000))

}