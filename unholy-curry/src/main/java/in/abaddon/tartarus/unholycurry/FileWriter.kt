package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class FileWriter(
    private val filer: Filer,
    private val messager: Messager,
    private val methods: List<ExecutableElement>,
    private val lambdas: List<VariableElement>,
    private val ctors: List<ExecutableElement>
): WithHelper {
    private fun getPackageName(classElement: Element): String =
        (classElement as TypeElement).qualifiedName.toString().substringBeforeLast('.',"")

    private fun methodRecipe(v: ExecutableElement): FunSpec = MethodRecipe(v, messager).cookFunction()
    private fun lambdaRecipe(v: VariableElement): FunSpec = LambdaRecipe(v, messager).cookFunction()
    private fun constructorRecipe(v: ExecutableElement): FunSpec = ConstructorRecipe(v, messager).cookFunction()

    fun makeCurries(){
        val curriedMethods = methods.groupBy { getPackageName(it.enclosingElement) }
                                    .mapValues { it.value.map(this::methodRecipe) }

        val curriedLambdas = lambdas.groupBy { getPackageName(it.enclosingElement) }
                                    .mapValues { it.value.map(this::lambdaRecipe) }

        val curriedCtors = ctors.groupBy { getPackageName(it.enclosingElement) }
                                .mapValues { it.value.map(this::constructorRecipe) }

        val keys = curriedMethods.keys + curriedLambdas.keys + curriedCtors.keys

        keys.map {
            val m = curriedMethods[it] ?: emptyList()
            val l = curriedLambdas[it] ?: emptyList()
            val c = curriedCtors[it] ?: emptyList()
            it to (m+l+c)
        }
        .map(this::makeFile)
        .forEach { it.writeTo(filer) }
    }

    private fun makeFile(pair: Pair<String, List<FunSpec>>): FileSpec{
        val packageName = "${if(pair.first.length == 0) "" else pair.first + "."}curry"
        val interfaceName = "CurryFns"

        val fileSpec = FileSpec.builder(packageName, interfaceName)
        pair.second.forEach { fileSpec.addFunction(it) }

        return fileSpec.build()
    }
}
