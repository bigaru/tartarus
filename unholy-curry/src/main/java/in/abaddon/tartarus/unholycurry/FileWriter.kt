package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import java.lang.IllegalArgumentException
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.*

class FileWriter(
    private val filer: Filer,
    private val messager: Messager,
    private val elements: List<Element>
): WithHelper {

    private fun methodRecipe(v: ExecutableElement): FunSpec = MethodRecipe(v, messager).cookFunction()
    private fun lambdaRecipe(v: VariableElement): FunSpec = LambdaRecipe(v, messager).cookFunction()
    private fun constructorRecipe(v: ExecutableElement): FunSpec = ConstructorRecipe(v, messager).cookFunction()

    private fun convertToFunSpec(e: Element): FunSpec {
        return when(e.kind){
            ElementKind.CONSTRUCTOR -> constructorRecipe(e as ExecutableElement)
            ElementKind.METHOD -> methodRecipe(e as ExecutableElement)
            ElementKind.FIELD -> lambdaRecipe(e as VariableElement)
            else -> throw IllegalArgumentException("not handled")
        }
    }

    fun makeCurries(){
        elements.groupBy { getPackageName(it.enclosingElement as TypeElement) }
                .mapValues {it.value.map(this::convertToFunSpec)}
                .map(this::makeFile)
                .forEach { it.writeTo(filer) }
    }

    private fun makeFile(pair: Map.Entry<String, List<FunSpec>>): FileSpec{
        val originalPackageName = if(pair.key.length == 0) "" else pair.key + "."
        val packageName = "${originalPackageName}curry"
        val interfaceName = "CurryFns"

        val fileSpec = FileSpec.builder(packageName, interfaceName)
        pair.value.forEach { fileSpec.addFunction(it) }

        return fileSpec.build()
    }
}
