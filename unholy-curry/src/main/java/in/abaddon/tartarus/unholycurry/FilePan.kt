package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import java.lang.IllegalArgumentException
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.*

class FilePan(
    private val filer: Filer,
    private val messager: Messager,
    private val elements: List<Element>
): WithHelper {

    private val methodRecipe = MethodRecipe(messager)
    private val lambdaRecipe = LambdaRecipe(messager)
    private val constructorRecipe = ConstructorRecipe(messager)

    private fun convertToFunSpec(e: Element): FunSpec {
        return when(e.kind){
            ElementKind.CONSTRUCTOR -> constructorRecipe.cookFunction(e as ExecutableElement)
            ElementKind.METHOD -> methodRecipe.cookFunction(e as ExecutableElement)
            ElementKind.FIELD -> lambdaRecipe.cookFunction(e as VariableElement)
            else -> throw IllegalArgumentException("ElementKind not handled")
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

        val fileSpec = FileSpec.builder("${originalPackageName}curry", "CurryFns")
        pair.value.forEach { fileSpec.addFunction(it) }

        return fileSpec.build()
    }
}
