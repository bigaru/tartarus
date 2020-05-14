# Tartarus 

## unholy-curry
[![Release](https://jitpack.io/v/in.abaddon/tartarus.svg)](https://jitpack.io/#in.abaddon/tartarus)
[![Build Status](https://travis-ci.org/bigaru/tartarus.svg?branch=master)](https://travis-ci.org/bigaru/tartarus)

Enables currying for function/lambda/constructor
### Table of contents
1. [Why Currying](#why-currying)
1. [Gradle Setup](#gradle-setup)
1. [Examples](#examples)
1. [Advanced Examples](#advanced-examples)
### Why Currying
```kotlin
@Curry
fun testHotness(person: String, chili: String) {}

val chilis = listOf("Cayenne","Habanero", "Carolina Reaper")

// normally
chilis.map{chili -> testHotness("Peter", chili)}

// with currying, you can preload the function
val testerPeter = testHotness("Peter")
chilis.map(testerPeter)
```

### Gradle Setup
```groovy
apply plugin: 'kotlin-kapt'

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    kapt 'in.abaddon.tartarus:unholy-curry:0.0.4'
    compileOnly 'in.abaddon.tartarus:unholy-curry:0.0.4'
}
```

### Examples
1. annotate function/lambda/constructor with `@Curry`
2. import subpackage `curry`
3. have fun ;)
```kotlin
package com.example.foo

import com.example.foo.curry.*

class Spice
@Curry constructor(val name: String, val color: Int, hotness: Int) {

    @Curry
    val enhance = {factor: Double, quantity: Int, degree: Int ->  ... }

    @Curry
    fun mitigate(drink: String, quantity: Int, degree: Int): Int { ... }
}

object MainObj {
    fun doSomething() {
        val s1 = Spice("GhostPepper",0xF00,1000000)
        s1.enhance(2.5, 50, 90)
        s1.mitigate("Milk",100,90)
        
        // with currying
        val s2 = createSpice("GhostPepper")(0xF00)(1000000)
        s2.enhance(2.5)(50)(90)
        s2.mitigate("Milk")(100)(90)
    }
}
```

### Advanced Examples
>Recap: 
>* Method signature consists of method name and ordered parameter list 
>* Only types are significant for the parameter list and the name of parameter is ignored
>* The return type is not included

#### Rename 
It could happen that generated curried function clashes with the existing method signature. In such a case, you can simply override the name.
```kotlin
class Pan {   
    fun cook(ingredients: String) { ... }

    // Requires custom name because the curried function will clash with method signature above.
    @Curry(name = cookSmoothly)
    fun cook(ingredients: String, heat: Int, duration: Double){ ... }
}

pan.cook("chicken", 90, 5.0)
pan.cookSmoothly("chicken")(90)(5.0)
```

#### Rearrange parameters
We'd like to preload the function `cook` and apply it to the list of meats. Unfortunately the parameter `meat: String`, which is needed for the map function, is at the beginning. We can rearrange the order of the parameters for curried function by passing a list of indexes of parameters in the desired order.
 
```kotlin
class Pan {
    @Curry(order = intArrayOf(1,2,0)) // (heat, duration, meat)
    fun cook(meat: String, heat: Int, duration: Double){ ... }
}

val cookWellDone = pan.cook(/*heat*/ 95)(/*duration*/ 10.0)
listOf("Chicken","Beef","Pork").map(cookWellDone)

```

