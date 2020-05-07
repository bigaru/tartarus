import `in`.abaddon.tartarus.unholycurry.Curry

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Meow ...")
        noe()
    }

    @Curry
    fun noe(){}
}