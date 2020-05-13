package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.VariableElement

class LambdaRecipe(messager: Messager): Recipe<VariableElement>(messager) {
    lateinit var originallambdaType: ParameterizedTypeName
    lateinit var params: List<Pair<String,TypeName>>

    override fun initElement(newElement: VariableElement) {
        element = newElement
        originallambdaType = element.asType().makeType() as ParameterizedTypeName

        val ps = originallambdaType.typeArguments.dropLast(1)
                                                 .mapIndexed{idx, type -> "a$idx" to type}
        params = reorder(ps)
    }

    override fun prepFirstParam(): ParameterSpec {
      val (idx, t) = params.first()
      return ParameterSpec(idx, t)
    }

    override fun prepReturnType(): TypeName {
        val initialReturnType = originallambdaType.typeArguments.last()
        val tailParam = params.drop(1).reversed()

        return tailParam.map{it.second}.fold(initialReturnType) {acc, p ->
            LambdaTypeName.get(returnType = acc, parameters = *arrayOf(p))
        }
    }

    override fun prepBody(): String {
        val tailParam = params.drop(1).map{it.first}.reversed()
        val args = (0..params.size-1).map{idx -> "a${idx}"}.reduce{acc, a -> "$acc, $a"}

        val body = tailParam.fold("this.${element.name()}($args)"){acc, idx ->
            "{${idx} -> $acc}"
        }

        return "return $body"
    }
}
