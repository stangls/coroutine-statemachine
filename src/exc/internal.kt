package exc

/**
 * Created by sd on 30.12.16.
 */
fun main(args: Array<String>) {
    val c = C<Int?>()
    c.myFun(1)
    c.myFun(null)
}

class C<X> {
    fun myFun( x:X ){
        if (x is Any){
            println("$x has class ${x.javaClass}")
        }else{
            println("$x is not Any")
        }
    }
}