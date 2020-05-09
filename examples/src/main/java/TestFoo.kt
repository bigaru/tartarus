import `in`.abaddon.tartarus.unholycurry.Curry

class Bar

class TestFoo {
    @Curry
    fun methodMultiTypes(a1: Int, a2: Double, a3: Boolean, a4: Bar): String {
        return "$a1 | $a2 | $a3 | $a4 "
    }

    @Curry
    fun methodStrings(a1: String, a2: String): String {
        return "$a1 | $a2 "
    }

    @Curry
    fun methodGeneric(a1: List<String>, a2: List<String>): String {
        return "$a1 | $a2 "
    }
}