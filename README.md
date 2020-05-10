# Tartarus 

## unholy-curry
[![Release](https://jitpack.io/v/in.abaddon/tartarus.svg)](https://jitpack.io/#in.abaddon/tartarus)

Enables currying for function/lambda/constructor

### Gradle Setup
```groovy
apply plugin: 'kotlin-kapt'

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    kapt 'in.abaddon.tartarus:unholy-curry:0.0.2'
    compileOnly 'in.abaddon.tartarus:unholy-curry:0.0.2'
}
```

### Example
1. annotate function/lambda/constructor with `@Curry`
2. use the generated mixin interface with `-Curry` suffix
3. have fun ;)
```kotlin
class Spice
@Curry constructor(val name: String, val color: Int, hotness: Int) {

    @Curry
    val enhance = {factor: Double, quantity: Int, degree: Int ->  ... }

    @Curry
    fun mitigate(drink: String, quantity: Int, degree: Int): Int { ... }
}

object MainObj: SpiceCurry {
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
