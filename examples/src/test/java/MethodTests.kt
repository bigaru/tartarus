import org.junit.Assert
import org.junit.Test

class MethodTests {
    @Test
    fun givenMultiType() {
        val foo = TestFoo()
        val bar = Bar()
        val expected = foo.methodMultiTypes(1, 2.5, false, bar)
        val actual = foo.methodMultiTypes(1)(2.5)(false)(bar)

        Assert.assertEquals(expected, actual)
    }
}
