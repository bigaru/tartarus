package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

abstract class Recipe<T: Element>(val messager: Messager): WithHelper {
    lateinit var element: T

    open fun prepName(): String = element.name()
    open fun prepReceiver(): TypeName? = element.enclosingElement.asType().makeType()

    abstract fun initElement(newElement: T)
    abstract fun prepFirstParam(): ParameterSpec
    abstract fun prepReturnType(): TypeName
    abstract fun prepBody(): String

    fun cookFunction(newElement: T): FunSpec {
        initElement(newElement)
        checkForSignatureClash(element.enclosingElement as TypeElement, element, prepFirstParam().type)

        val fn = FunSpec.builder(prepName())
                        .addParameter(prepFirstParam())
                        .returns(prepReturnType())
                        .addStatement(prepBody())

        prepReceiver()?.let { fn.receiver(it) }

        return fn.build()
    }

    private fun checkForSignatureClash(classElement: TypeElement, element: Element, firstParamType: TypeName) {
        val existsSignature = classElement.enclosedElements
            .filterIsInstance<ExecutableElement>()
            .filter { it.name() == element.name() }
            .filter { it.parameters.size == 1}
            .any { it.parameters[0].asType().makeType() == firstParamType }

        if (existsSignature) {
            messager.printMessage(Diagnostic.Kind.ERROR, "'$element' cannot be curried because there is a signature clash")
        }
    }
}
