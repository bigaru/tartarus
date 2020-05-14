import com.examples.*
import org.junit.Assert
import org.junit.Test
import curry.*
import com.examples.curry.*

class CurrySpec {
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

    @Test
    fun insidePackages() {
        val foo = RiceCooker()
        val expected = foo.cook("Jasmin", 200, listOf("pepper","onions"))
        val actual = foo.cook("Jasmin")(200)(listOf("pepper","onions"))

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun lambda() {
        val foo = RiceCooker()
        val expected = foo.steamCook("potato", false)
        val actual = foo.steamCook("potato")(false)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun whenFirstTypeDifferentNoClash() {
        val foo = RiceCooker()
        val expected = foo.noClash("potato", 1)
        val actual = foo.noClash("potato")(1)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun primaryCtor() {
        val expected = PrimarySpice(3, "Herb", 40.0)
        val actual = createPrimarySpice(3)("Herb")(40.0)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun secondaryCtor() {
        val expected = SecondarySpice(3, "Herb", 40.0)
        val actual = createSecondarySpice(3)("Herb")(40.0)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun renameCurriedFn(){
        val hotPepper = HotPepper()
        val expected = hotPepper.burn("Victim's Name", 20000, 0xFF0)
        val actual = hotPepper.ignite("Victim's Name")(20000)(0xFF0)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun reorderParams(){
        val hotPepper = HotPepper()
        val expected = hotPepper.killBacteria("bacteriaXZN", 0.4, 'C')
        val actual = hotPepper.killBacteria('C')(0.4)("bacteriaXZN")

        Assert.assertEquals(expected, actual)
    }
}
