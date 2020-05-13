package `in`.abaddon.tartarus.unholycurry

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes(CurryProcessor.ANNOTATION_PACKAGE)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions(CurryProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class CurryProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val ANNOTATION_PACKAGE = "in.abaddon.tartarus.unholycurry.Curry"
    }

    private fun log(msg: String){
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, msg)
    }

    private fun error(msg: String){
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Curry::class.java).toList()

        elements
            .filterIsInstance<VariableElement>()
            .filterNot(this::isLambda)
            .forEach{error("${it.simpleName} is NOT a lambda")}

        if(elements.isNotEmpty()) {
            FilePan(processingEnv.filer, processingEnv.messager, elements).makeCurries()
        }

        return true
    }

    private fun isLambda(e: VariableElement) =
        e.asType().toString().startsWith("kotlin.jvm.functions.")

}
