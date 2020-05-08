import `in`.abaddon.tartarus.unholycurry.Curry

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val foo = Foo()
        foo.uncurried("a",2,"c", false, 4.2)
        foo.uncurried("a")(2)("c")(false)(4.2)
    }
}

class Foo(){
    @Curry
    fun uncurried(a: String, b: Int, c: String, d: Boolean, e: Double){
        println("->>> $a $b $c $d $e <<<-")
    }

}