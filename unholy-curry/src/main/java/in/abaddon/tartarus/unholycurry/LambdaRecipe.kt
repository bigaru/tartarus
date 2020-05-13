package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.VariableElement

class LambdaRecipe(messager: Messager): Recipe<VariableElement>(messager) {
    lateinit var originallambdaType: ParameterizedTypeName
    lateinit var params: List<TypeName>

    override fun initElement(newElement: VariableElement) {
        element = newElement
        originallambdaType = element.asType().makeType() as ParameterizedTypeName
        params = originallambdaType.typeArguments.dropLast(1)
    }

    override fun prepFirstParam(): ParameterSpec =
        ParameterSpec("a0", params.first())

    override fun prepReturnType(): TypeName {
        val initialReturnType = originallambdaType.typeArguments.last()
        val tailParam = params.drop(1).reversed()

        return tailParam.fold(initialReturnType) {acc, p ->
            LambdaTypeName.get(returnType = acc, parameters = *arrayOf(p))
        }
    }

    override fun prepBody(): String {
        val tailParam = (0..params.size-1).drop(1).reversed()
        val args = (0..params.size-1).map{idx -> "a${idx}"}.reduce{acc, a -> "$acc, $a"}

        val body = tailParam.fold("this.${element.name()}($args)"){acc, idx ->
            "{a${idx} -> $acc}"
        }

        return "return $body"
    }
}
