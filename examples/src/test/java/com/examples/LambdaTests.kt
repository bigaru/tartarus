package com.examples

import org.junit.Assert
import org.junit.Test

class LambdaTests: RiceCookerCurry {
    @Test
    fun lambda() {
        val foo = RiceCooker()
        val expected = foo.steamCook("potato", false)
        val actual = foo.steamCook("potato")(false)

        Assert.assertEquals(expected, actual)
    }
}
