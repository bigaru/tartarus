package `in`.abaddon.tartarus.unholycurry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

interface WithHelper {

    fun getAttributeValue(element: Element, attributeName: String): AnnotationValue? {
        val annotations = element.annotationMirrors
                                 .filter{ it.annotationType.toString() == CurryProcessor.ANNOTATION_PACKAGE }

        val values = annotations.flatMap{it.elementValues.entries}
                               .filter{it.key.simpleName.contentEquals(attributeName)}
                               .map{it.value}

        return if(values.isEmpty()) null else values.first()
    }

    fun getPackageName(classElement: TypeElement): String =
        classElement.qualifiedName.toString().substringBeforeLast('.',"")

    fun makeParam(p: VariableElement): ParameterSpec =
        ParameterSpec(p.name(), p.asType().makeType())

    fun Element.name(): String {
        return this.simpleName.toString()
    }

    fun TypeMirror.makeType(): TypeName {
        return this.asTypeName().javaToKotlinType()
    }

    // workaround: kotlin types are inferred as java types
    // especially string and generics
    // taken @ywwynm's solution from https://github.com/square/kotlinpoet/issues/236
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
}
