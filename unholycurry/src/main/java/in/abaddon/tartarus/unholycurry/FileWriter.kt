package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

class FileWriter: WithHelper {

    private fun makeParam(p: VariableElement): ParameterSpec =
        ParameterSpec.builder(p.name(), p.asType().makeType()).build()

    private fun makeReturnType(initialReturnType: TypeMirror, params: List<VariableElement>): TypeName =
        params.map(this::makeParam)
              .fold(initialReturnType.makeType()) {acc, p ->
                     LambdaTypeName.get(returnType = acc, parameters = listOf(p))
              }

    private fun makeArgs(ps: List<VariableElement>): String =
        ps.map{it.name()}
          .reduce{acc, a -> "$acc, $a"}

    private fun makeBody(ps: List<VariableElement>, originalName: String, args: String): String =
        ps.map{it.name()}
          .fold("this.${originalName}($args)") {acc, s -> "{$s -> $acc}"}

    private fun makeCurriedMethod(element: ExecutableElement): FunSpec {
        val firstParam = element.parameters.first()
        val tailParam = element.parameters.drop(1).reversed()

        val classElement = element.enclosingElement as TypeElement
        val returnType = makeReturnType(element.returnType, tailParam)

        val args = makeArgs(element.parameters)
        val body = makeBody(tailParam, element.name(), args)

        // TODO check if Method Signature is already is used
        return FunSpec.builder(element.name())
            .receiver(classElement.asType().makeType())
            .addParameter(makeParam(firstParam))
            .returns(returnType)
            .addStatement("return $body")
            .build()
    }

    fun makeCurries(filer: Filer, methods: List<ExecutableElement>){
        // TODO produce in separate package / interface aka mixin
        val fileBuilder = FileSpec.builder("", "CurriedExtensions")

        methods.map { makeCurriedMethod(it) }
               .forEach { fileBuilder.addFunction(it) }

        fileBuilder.build().writeTo(filer)
    }
}
