package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("in.abaddon.tartarus.unholycurry.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(CurryProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class CurryProcessor: AbstractProcessor(){
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private var messager: Messager? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        messager = processingEnv?.messager
    }

    private fun log(msg: String){
        processingEnv.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, msg)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val methodBuffer = mutableListOf<ExecutableElement>()

        annotations?.forEach { a -> run{
            roundEnv?.getElementsAnnotatedWith(a)?.forEach { e -> run {
                if(e is ExecutableElement) {
                    methodBuffer.add(e)
                }
            }}
        }}

        if(methodBuffer.isNotEmpty()) printFiles(methodBuffer)
        return true
    }

    // workaround kotlin string
    fun TypeName.correctStringType() =
        if (this.toString() == "java.lang.String") ClassName("kotlin", "String") else this


    private fun makeParam(p: VariableElement): ParameterSpec{
        return ParameterSpec.builder(p.simpleName.toString(), p.asType().asTypeName().correctStringType()).build()
    }

    private fun printFiles(methodBuffer: List<ExecutableElement>){
        val method = methodBuffer.first()
        val classElement = method.enclosingElement as TypeElement
        log(""+ classElement.kind)

        val firstParam = method.parameters.first()
        val tailParam = method.parameters.drop(1).reversed()

        val finalType = tailParam.map{makeParam(it)}
                                 .fold(method.returnType.asTypeName()) {acc, p -> LambdaTypeName.get(returnType = acc, parameters = listOf(p))}

        val args = method.parameters.map{it.simpleName.toString()}
                                    .reduce{acc, a -> "$acc, $a"}

        val body = tailParam.map{it.simpleName.toString()}
                            .fold("this.${method.simpleName}($args)") {acc, s -> "{$s -> $acc}"}

        val curriedFn = FunSpec.builder(method.simpleName.toString())
                .receiver(classElement.asType().asTypeName())
                .addParameter(makeParam(firstParam))
                .returns(finalType)
                .addStatement("return $body")
                .build()

        val file = FileSpec.builder("", "CurriedExtensions")
                           .addFunction(curriedFn)
                           .build()

        file.writeTo(processingEnv.filer)
    }

}