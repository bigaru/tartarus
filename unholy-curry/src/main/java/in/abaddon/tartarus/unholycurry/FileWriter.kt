package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

class FileWriter(
    private val filer: Filer,
    private val messager: Messager,
    private val methods: List<ExecutableElement>,
    private val lambdas: List<VariableElement>,
    private val ctors: List<ExecutableElement>
): WithHelper {
    private fun getPackageName(classElement: TypeElement): String =
        classElement.qualifiedName.toString().substringBeforeLast('.',"")

    private fun methodRecipe(v: ExecutableElement): FunSpec = MethodRecipe(v, messager).cookFunction()
    private fun lambdaRecipe(v: VariableElement): FunSpec = LambdaRecipe(v, messager).cookFunction()
    private fun constructorRecipe(v: ExecutableElement): FunSpec = ConstructorRecipe(v, messager).cookFunction()

    fun makeCurries(){
        val curriedMethods = methods.groupBy { it.enclosingElement }
                                    .mapValues { it.value.map(this::methodRecipe) }

        val curriedLambdas = lambdas.groupBy { it.enclosingElement }
                                    .mapValues { it.value.map(this::lambdaRecipe) }

        val curriedCtors = ctors.groupBy { it.enclosingElement }
                                .mapValues { it.value.map(this::constructorRecipe) }

        val keys = curriedMethods.keys + curriedLambdas.keys + curriedCtors.keys

        keys.map {
            val m = curriedMethods[it] ?: emptyList()
            val l = curriedLambdas[it] ?: emptyList()
            val c = curriedCtors[it] ?: emptyList()
            it to (m+l+c)
        }
        .map(this::makeInterface)
        .forEach { it.writeTo(filer) }
    }

    private fun makeInterface(pair: Pair<Element, List<FunSpec>>): FileSpec{
        val packageName = getPackageName(pair.first as TypeElement)
        val interfaceName = "${pair.first.name()}Curry"
        val interfaceSpec = TypeSpec.interfaceBuilder(interfaceName)

        pair.second.forEach { interfaceSpec.addFunction(it) }

        return FileSpec.builder(packageName, interfaceName)
            .addType(interfaceSpec.build())
            .build()
    }
}
