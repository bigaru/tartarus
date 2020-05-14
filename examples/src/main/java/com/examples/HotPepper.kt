package com.examples

import `in`.abaddon.tartarus.unholycurry.Curry

class HotPepper {
    @Curry(name = "ignite")
    fun burn(victim: String, hotness: Int, color: Int): Int =
        victim.hashCode() + hotness + color

    @Curry(order = intArrayOf(2,1,0))
    fun killBacteria(kind: String, percent: Double, version: Char) =
        "$kind $percent $version"
}
