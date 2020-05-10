import org.junit.Assert
import org.junit.Test

class MethodTests: TestFooCurry {
    @Test
    fun parametersWithMultiType() {
        val foo = TestFoo()
        val bar = Bar()
        val expected = foo.methodMultiTypes(1, 2.5, false, bar)
        val actual = foo.methodMultiTypes(1)(2.5)(false)(bar)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun parametersWithStrings() {
        val foo = TestFoo()
        val expected = foo.methodStrings("cur","ry")
        val actual = foo.methodStrings("cur")("ry")

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun parametersWithGeneric() {
        val foo = TestFoo()
        val expected = foo.methodGeneric(listOf("c"), listOf("u"))
        val actual = foo.methodGeneric(listOf("c"))(listOf("u"))

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun parametersWithNestedGeneric() {
        val foo = TestFoo()
        val expected = foo.methodNestedGeneric( listOf(listOf("c")), listOf(listOf("u")) )
        val actual = foo.methodNestedGeneric( listOf(listOf("c")) )( listOf(listOf("u")) )

        Assert.assertEquals(expected, actual)
    }
}
