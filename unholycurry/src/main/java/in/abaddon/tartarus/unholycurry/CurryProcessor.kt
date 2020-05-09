package `in`.abaddon.tartarus.unholycurry

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("in.abaddon.tartarus.unholycurry.Curry")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions(CurryProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class CurryProcessor: AbstractProcessor(){
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private val fileWriter = FileWriter()

    private fun log(msg: String){
        processingEnv.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, msg)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val methods = annotations?.flatMap { roundEnv?.getElementsAnnotatedWith(it) ?: emptySet() }
                                 ?.filterIsInstance<ExecutableElement>() ?: emptyList()


        if(methods.isNotEmpty()) {
            fileWriter.makeCurries(processingEnv.filer, methods)
        }

        return true
    }

}
