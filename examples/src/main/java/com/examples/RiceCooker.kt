package com.examples

import `in`.abaddon.tartarus.unholycurry.Curry

class RiceCooker {
    @Curry
    val steamCook = {kind: String, salty: Boolean -> "$kind ~ #$salty"}

    @Curry
    fun cook(kindOfRice: String, amount: Int, ingredients: List<String>): Int{
        return kindOfRice.length + amount + ingredients.size
    }

    @Curry
    fun noClash(first: String, second: Int) = "$first $second"
    fun noClash(first: Int){}


    // if uncommented, curried signature will clashes with 2nd fun
    // error will be displayed

    //@Curry
    fun methodClash(first: String, second: Int) = "$first $second"
    fun methodClash(first: String){}

    //@Curry
    val lambdaClash = {kind: String, salty: Boolean -> "$kind ~ #$salty"}
    fun lambdaClash(kind: String){}


}
