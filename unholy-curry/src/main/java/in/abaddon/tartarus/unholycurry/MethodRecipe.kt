package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

class MethodRecipe(val element: ExecutableElement, messager: Messager): Recipe(element, messager) {
    val params: List<VariableElement> by lazy { element.parameters }

    override fun prepFirstParam(): ParameterSpec =
        makeParam(params.first())

    override fun prepReturnType(): TypeName {
        val tailParam = params.drop(1).reversed()

        return tailParam.map{it.asType().makeType()}.fold(element.returnType.makeType()) {acc, pType ->
            LambdaTypeName.get(returnType = acc, parameters = *arrayOf(pType))
        }
    }

    override fun prepBody(): String {
        val tailParam = params.drop(1).reversed()
        val args = params.map{it.name()}.reduce{acc, a -> "$acc, $a"}

        val body = tailParam.map{it.name()}.fold("this.${element.name()}($args)") { acc, s ->
            "{$s -> $acc}"
        }

        return "return $body"
    }
}