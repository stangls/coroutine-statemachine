package sm1

import java.io.EOFException
import java.io.IOError
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * Created by sd on 29.12.16.
 */
@RestrictsSuspension
class StateMachine<Alphabet> internal constructor() {
    fun getInputStub(): Alphabet = null as Alphabet
}


fun <T> stateMachine(block: suspend StateMachine<T>.() -> Unit): StateMachine<T> {

    val stateMachine = StateMachine<T>()

    Thread(){
        block.startCoroutine(stateMachine, object : Continuation<Unit> {
            override fun resume(value: Unit) {
            }

            override fun resumeWithException(exception: Throwable) {
            }
        })
    }.start()

    return stateMachine

}