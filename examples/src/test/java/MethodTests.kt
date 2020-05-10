import org.junit.Assert
import org.junit.Test

class MethodTests: TestFooCurry {
    @Test
    fun givenMultiType() {
        val foo = TestFoo()
        val bar = Bar()
        val expected = foo.methodMultiTypes(1, 2.5, false, bar)
        val actual = foo.methodMultiTypes(1)(2.5)(false)(bar)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun givenStrings() {
        val foo = TestFoo()
        val expected = foo.methodStrings("cur","ry")
        val actual = foo.methodStrings("cur")("ry")

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun givenGeneric() {
        val foo = TestFoo()
        val expected = foo.methodGeneric(listOf("c"), listOf("u"))
        val actual = foo.methodGeneric(listOf("c"))(listOf("u"))

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun givenNestedGeneric() {
        val foo = TestFoo()
        val expected = foo.methodNestedGeneric( listOf(listOf("c")), listOf(listOf("u")) )
        val actual = foo.methodNestedGeneric( listOf(listOf("c")) )( listOf(listOf("u")) )

        Assert.assertEquals(expected, actual)
    }
}
