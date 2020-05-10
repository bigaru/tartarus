package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
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

    private fun cookMethodCurry(element: ExecutableElement): FunSpec {
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

    private fun cookLambdaCurry(element: VariableElement): FunSpec {
        val type = element.asType().makeType() as ParameterizedTypeName

        val params = type.typeArguments.dropLast(1).mapIndexed{idx, t -> idx to t}
        val firstParam = params.first()
        val tailParam = params.drop(1).reversed()
        val initialReturnType = type.typeArguments.last()

        val returnType = tailParam.map { t ->  ParameterSpec.builder("a${t.first}", t.second).build() }
            .fold(initialReturnType) {acc, p ->
            LambdaTypeName.get(returnType = acc, parameters = listOf(p))
        }

        val args = params.map{t -> "a${t.first}"}.reduce{acc, a -> "$acc, $a"}
        val body = tailParam.fold("this.${element.simpleName}($args)"){acc, s ->
            "{a${s.first} -> $acc}"
        }

        val classElement = element.enclosingElement as TypeElement

        return FunSpec.builder(element.name())
            .receiver(classElement.asType().makeType())
            .addParameter(ParameterSpec.builder("a0", firstParam.second).build())
            .returns(returnType)
            .addStatement("return $body")
            .build()
    }

    private fun getPackageName(classElement: TypeElement): String =
        classElement.qualifiedName.toString().substringBeforeLast('.',"")


    fun makeCurries(filer: Filer, methods: List<ExecutableElement>, lambdas: List<VariableElement>, logger: Messager){
        val curriedMethods = methods.groupBy { it.enclosingElement }
                                    .mapValues { it.value.map(this::cookMethodCurry) }

        val curriedLambdas = lambdas.groupBy { it.enclosingElement }
                                    .mapValues { it.value.map(this::cookLambdaCurry) }

        val keys = curriedMethods.keys + curriedLambdas.keys

        keys.map {
            val m = curriedMethods[it] ?: emptyList()
            val l = curriedLambdas[it] ?: emptyList()
            it to (m+l)
        }
        .map(this::makeInterface)
        .forEach { it.writeTo(filer) }
    }

    private fun makeInterface(pair: Pair<Element, List<FunSpec>>): FileSpec{
        val packageName = getPackageName(pair.first as TypeElement)
        val interfaceName = "${pair.first.name()}Curry"
        val interfaceSpec = TypeSpec.interfaceBuilder(interfaceName)

        pair.second.forEach { interfaceSpec.addFunction(it) }

        return FileSpec.builder(packageName, interfaceName)
            .addType(interfaceSpec.build())
            .build()
    }
}
