package com.examples

import org.junit.Assert
import org.junit.Test

class PackageTests: RiceCookerCurry {
    @Test
    fun insidePackages() {
        val foo = RiceCooker()
        val expected = foo.cook("Jasmin", 200, listOf("pepper","onions"))
        val actual = foo.cook("Jasmin")(200)(listOf("pepper","onions"))

        Assert.assertEquals(expected, actual)
    }
}
