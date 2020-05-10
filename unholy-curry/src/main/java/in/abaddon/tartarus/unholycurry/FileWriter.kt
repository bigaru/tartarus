package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

class FileWriter(
    private val filer: Filer,
    private val messager: Messager,
    private val methods: List<ExecutableElement>,
    private val lambdas: List<VariableElement>,
    private val ctors: List<ExecutableElement>
): WithHelper {

    private fun makeParam(p: VariableElement): ParameterSpec =
        ParameterSpec.builder(p.name(), p.asType().makeType()).build()

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

    private fun cookMethodCurry(element: ExecutableElement): FunSpec {
        val firstParam = element.parameters.first()
        val tailParam = element.parameters.drop(1).reversed()

        val classElement = element.enclosingElement as TypeElement
        val returnType = tailParam.map(this::makeParam).fold(element.returnType.makeType()) {acc, p ->
                LambdaTypeName.get(returnType = acc, parameters = listOf(p))
        }

        val args = element.parameters.map{it.name()}.reduce{acc, a -> "$acc, $a"}
        val body = tailParam.map{it.name()}.fold("this.${element.name()}($args)") { acc, s ->
            "{$s -> $acc}"
        }

        checkForSignatureClash(classElement, element, firstParam.asType().makeType())

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

        checkForSignatureClash(classElement, element, firstParam.second)

        return FunSpec.builder(element.name())
            .receiver(classElement.asType().makeType())
            .addParameter(ParameterSpec.builder("a0", firstParam.second).build())
            .returns(returnType)
            .addStatement("return $body")
            .build()
    }

    private fun cookCtorCurry(element: ExecutableElement): FunSpec {
        val firstParam = element.parameters.first()
        val tailParam = element.parameters.drop(1).reversed()

        val classElement = element.enclosingElement as TypeElement
        val returnType = tailParam.map(this::makeParam).fold(classElement.asType().makeType()) {acc, p ->
            LambdaTypeName.get(returnType = acc, parameters = listOf(p))
        }

        val args = element.parameters.map{it.name()}.reduce{acc, a -> "$acc, $a"}
        val body = tailParam.map{it.name()}.fold("${classElement.name()}($args)") { acc, s ->
            "{$s -> $acc}"
        }

        return FunSpec.builder("create${classElement.name()}")
            .addParameter(makeParam(firstParam))
            .returns(returnType)
            .addStatement("return $body")
            .build()
    }

    private fun getPackageName(classElement: TypeElement): String =
        classElement.qualifiedName.toString().substringBeforeLast('.',"")


    fun makeCurries(){
        val curriedMethods = methods.groupBy { it.enclosingElement }
                                    .mapValues { it.value.map(this::cookMethodCurry) }

        val curriedLambdas = lambdas.groupBy { it.enclosingElement }
                                    .mapValues { it.value.map(this::cookLambdaCurry) }

        val curriedCtors = ctors.groupBy { it.enclosingElement }
                                .mapValues { it.value.map(this::cookCtorCurry) }

        val keys = curriedMethods.keys + curriedLambdas.keys + curriedCtors.keys

        keys.map {
            val m = curriedMethods[it] ?: emptyList()
            val l = curriedLambdas[it] ?: emptyList()
            val c = curriedCtors[it] ?: emptyList()
            it to (m+l+c)
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
