package com.examples

import `in`.abaddon.tartarus.unholycurry.Curry

class RiceCooker {
    @Curry
    val steamCook = {kind: String, salty: Boolean -> "$kind ~ #$salty"}

    @Curry
    fun cook(kindOfRice: String, amount: Int, ingredients: List<String>): Int{
        return kindOfRice.length + amount + ingredients.size
    }
}