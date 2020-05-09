import `in`.abaddon.tartarus.unholycurry.Curry

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val foo = Foo()
        foo.uncurried("a",2, emptyList(), false, 2.0)
        foo.uncurried("a")(2)(emptyList())(false)( 2.0)
    }
}

class Foo(){
    @Curry
    fun uncurried(a: String, b: Int, c: List<List<String>>, d: Boolean, e: Double){
        println("->>> $a $b $c $d $e <<<-")
    }

}