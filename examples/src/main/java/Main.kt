import `in`.abaddon.tartarus.unholycurry.Curry
import curry.*

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        var foo = Foo("", 2,4, true)
        foo = Mooo(4)("a")(5)(false)


        foo.uncurried("a",2, emptyList(), false, 2.0)
        foo.superC("a")(2)(emptyList())(2.0)(false)

        foo.lambda("a","b", true)
        foo.lamma("b")(true)("a")
    }
}

class Foo {

    @Curry(name = "Mooo", order = intArrayOf(2,0,1,3) )
    constructor(a: String, b: Int, c: Int, d: Boolean) {}


    @Curry(name = "lamma", order = intArrayOf(1,2,0) )
    val lambda: (String, String, Boolean) -> Int = {a: String, b:String, c: Boolean -> a.length + b.length}

    //@Curry
    var _s = ""

    var s: String
        get() = _s
        set(value){_s = value}

    @Curry(name = "superC", order = intArrayOf(0,1,2,4,3))
    fun uncurried(a: String, b: Int, c: List<List<String>>, d: Boolean, e: Double){
        println("->>> $a $b $c $d $e <<<-")
    }

}

// TODO How to handle?
//@Curry
fun no(s: Int, s2: Int, s3: Int){}
