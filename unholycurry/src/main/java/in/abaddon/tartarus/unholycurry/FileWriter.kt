package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Filer
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

class FileWriter {


    private fun makeParam(p: VariableElement): ParameterSpec =
        ParameterSpec.builder(p.simpleName.toString(), p.asType().makeType()).build()

    private fun makeReturnType(initialReturnType: TypeMirror, params: List<VariableElement>): TypeName =
        params.map{makeParam(it)}
              .fold(initialReturnType.makeType()) {acc, p ->
                     LambdaTypeName.get(returnType = acc, parameters = listOf(p))
              }

    private fun makeArgs(ps: List<VariableElement>): String =
        ps.map{it.simpleName.toString()}
          .reduce{acc, a -> "$acc, $a"}

    private fun makeBody(ps: List<VariableElement>, originalName: String, args: String): String =
        ps.map{it.simpleName.toString()}
          .fold("this.${originalName}($args)") {acc, s -> "{$s -> $acc}"}

    private fun makeCurriedMethod(element: ExecutableElement): FunSpec {
        val firstParam = element.parameters.first()
        val tailParam = element.parameters.drop(1).reversed()

        val classElement = element.enclosingElement as TypeElement
        val returnType = makeReturnType(element.returnType, tailParam)

        val args = makeArgs(element.parameters)
        val body = makeBody(tailParam, element.simpleName.toString(), args)

        // TODO check if Method Signature is already is used
        return FunSpec.builder(element.simpleName.toString())
            .receiver(classElement.asType().makeType())
            .addParameter(makeParam(firstParam))
            .returns(returnType)
            .addStatement("return $body")
            .build()
    }

    fun makeCurries(filer: Filer, methods: List<ExecutableElement>){
        // TODO produce in separate package
        val fileBuilder = FileSpec.builder("", "CurriedExtensions")

        methods.map { makeCurriedMethod(it) }
               .forEach { fileBuilder.addFunction(it) }

        fileBuilder.build().writeTo(filer)
    }
}

fun TypeMirror.makeType(): TypeName {
    return this.asTypeName().javaToKotlinType()
}

// workaround: kotlin types are inferred as java types
// especially string and generics
// taken from https://github.com/square/kotlinpoet/issues/236
fun TypeName.javaToKotlinType(): TypeName {
    return when (this) {
        is ParameterizedTypeName -> {
            (rawType.javaToKotlinType() as ClassName).parameterizedBy(*(typeArguments.map { it.javaToKotlinType() }.toTypedArray()))
        }
        is WildcardTypeName -> {
            outTypes[0].javaToKotlinType()
        }
        else -> {
            val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
            return if (className == null) {
                this
            } else {
                ClassName.bestGuess(className)
            }
        }
    }
}