package com.examples

import `in`.abaddon.tartarus.unholycurry.Curry

class SecondarySpice @Curry constructor(val quantity: Int, val kind: String, val scoville: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SecondarySpice

        if (quantity != other.quantity) return false
        if (kind != other.kind) return false
        if (scoville != other.scoville) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity ?: 0
        result = 31 * result + (kind?.hashCode() ?: 0)
        result = 31 * result + (scoville?.hashCode() ?: 0)
        return result
    }


}
