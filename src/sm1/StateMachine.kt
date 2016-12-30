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
class StateMachine<T> internal constructor() {

    protected val queue = LinkedBlockingQueue<T>()
    protected var inputClosed = false
    protected var inFinalState = false

    @Throws(InputClosedException::class)
    suspend fun getInput(): T {
        return suspendCoroutine<T> { c: Continuation<T> ->
            fun checkException() : Boolean {
                synchronized(this) {
                    val exc = exception
                    if (exc == null) {
                        return true
                    } else {
                        c.resumeWithException(exc)
                        //notifAll()
                        return false
                    }
                }
            }
            // blocks until next input is available
            if (checkException()) {
                do {
                    if (queue.isEmpty() && inputClosed){
                        throw InputClosedException()
                    }
                    val taken = queue.poll(1, TimeUnit.SECONDS)
                    if (checkException()) {
                        if (taken!=null) {
                            c.resume(taken)
                            notifIfFinished()
                        }else{
                            if (inputClosed){
                                throw InputClosedException()
                            }
                        }
                    }
                }while(taken==null)
            }
        }
    }

    var exception: Throwable? = null
        private set

    internal fun final( exc: Throwable? = null ) {
        synchronized(this){
            if (exc==null) {
                inFinalState = true
            }else {
                exception = exc
            }
            notifIfFinished()
        }
    }
    internal fun unfinal() {
        synchronized(this) {
            inFinalState = false
        }
    }

    fun notifIfFinished() {
        synchronized(this) {
            if (hasFinished())
                (this as Object).notifyAll()
        }
    }

    /** could potentially block, but should not **/
    @Throws(InputClosedException::class)
    fun input(i:T) {
        if (exception!=null)
            throw Exception(exception)
        if (inputClosed){
            throw InputClosedException()
        }
        queue.put(i)
        if (slowFeeder>0){
            val slow = (slowFeeder*(queue.size-1)).toLong()
            if (slow>0) {
                println("slowing feeder by $slow")
                Thread.sleep(slow)
            }
        }
    }

    fun endInput() {
        inputClosed = true
        notifIfFinished()
    }

    /**
     * returns true if final state was reached.
     * returns false if timeout was reached.
     * throws an exception if an error occurred.
     */
    fun waitForFinish(timeout: Long? = null) : Boolean {
        if (exception!=null)
            throw Exception(exception)
        synchronized(this){
            if (timeout!=null) {
                (this as Object).wait(timeout)
            }else{
                while (!hasFinished()) {
                    (this as Object).wait()
                }
            }
        }
        if (exception!=null)
            throw Exception(exception)
        return inFinalState
    }

    private fun hasFinished() = exception!=null || ( inFinalState && inputClosed && queue.isEmpty() )

    /** slow down feeding thread if queue becomes big by pausing his thread **/
    var slowFeeder: Double = 0.0

}

class InputClosedException : RuntimeException()

fun <T> stateMachine(block: suspend StateMachine<T>.() -> Unit): StateMachine<T> {

    val stateMachine = StateMachine<T>()

    Thread(){
        block.startCoroutine(stateMachine, object : Continuation<Unit> {
            override fun resume(value: Unit) {
                //stateMachine.final()
            }

            override fun resumeWithException(exception: Throwable) {
                if (exception is InputClosedException) {
                    println("Input was closed in non-final state!")
                } else {
                    stateMachine.final(exception)
                }
            }
        })
    }.start()

    return stateMachine

}