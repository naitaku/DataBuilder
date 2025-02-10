package io.github.naitaku.databuilder.processor


import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.*
import io.github.naitaku.databuilder.annotation.DataBuilder

class DataBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(DataBuilder::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .map { klass ->
                if (klass.validate()) {
                    if (!klass.modifiers.contains(Modifier.DATA)) {
                        logger.error("@DataBuilder can only be applied to data classes", klass)
                    } else {
                        generateBuilderClass(klass)
                    }
                    null
                } else {
                    klass
                }
            }.filterNotNull()
            .toList()
    }

    private fun generateBuilderClass(klass: KSClassDeclaration) {
        val packageName = klass.packageName.asString()
        val className = klass.simpleName.asString()
        val builderName = "${className}Builder"

        val typeSpec = TypeSpec.classBuilder(builderName)
            .addProperty(
                PropertySpec.builder("instance", klass.asClassName())
                    .mutable(true)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("%T()", klass.asClassName())
                    .build()
            )

        klass.primaryConstructor?.parameters?.forEach { param ->
            if (!param.hasDefault) {
                logger.error("All properties has to have its default value", klass)
                return@forEach
            }
            val propName = param.name?.asString() ?: return@forEach
            val docString = klass.getAllProperties().find { it.simpleName == param.name }?.docString?.trim() ?: ""

            typeSpec.addFunction(
                FunSpec.builder(propName)
                    .addKdoc(docString)
                    .addParameter("value", param.type.toTypeName())
                    .returns(ClassName(packageName, builderName))
                    .addStatement("instance = instance.copy(%N = value)", propName)
                    .addStatement("return this")
                    .build()
            )
        }

        typeSpec.addFunction(
            FunSpec.builder("build")
                .returns(klass.asClassName())
                .addStatement("return instance")
                .build()
        )

        FileSpec.builder(packageName, builderName)
            .addType(typeSpec.build())
            .build()
            .writeTo(codeGenerator, Dependencies(true, klass.containingFile!!))
    }

    private fun KSClassDeclaration.asClassName() = ClassName(
        packageName.asString(),
        simpleName.asString()
    )
}

class DataBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        DataBuilderProcessor(environment.codeGenerator, environment.logger)
}
