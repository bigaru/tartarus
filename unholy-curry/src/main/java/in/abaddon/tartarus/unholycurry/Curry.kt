package `in`.abaddon.tartarus.unholycurry

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.BINARY)
annotation class Curry(
    val name: String = "",
    vararg val order: Int = intArrayOf()
)
