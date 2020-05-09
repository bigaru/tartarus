import `in`.abaddon.tartarus.unholycurry.Curry

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val foo = Foo()
        foo.uncurried("a",2, emptyList(), false, 2.0)
        foo.uncurried("a")(2)(emptyList())(false)( 2.0)

        foo.lambda("a","b")
        foo.lambda("a")("b")
    }
}

class Foo(){
    @Curry
    val lambda: (String, String) -> Int = {a: String, b:String -> a.length + b.length}

    //@Curry
    var _s = ""

    var s: String
        get() = _s
        set(value){_s = value}

    @Curry
    fun uncurried(a: String, b: Int, c: List<List<String>>, d: Boolean, e: Double){
        println("->>> $a $b $c $d $e <<<-")
    }

}

// TODO How to handle?
//@Curry
fun no(s: Int, s2: Int, s3: Int){}