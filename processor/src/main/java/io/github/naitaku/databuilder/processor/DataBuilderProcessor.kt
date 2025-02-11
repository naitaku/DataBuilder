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

    override fun process(resolver: Resolver): List<KSAnnotated> =
        resolver.getSymbolsWithAnnotation(DataBuilder::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .map {
                if (it.validate()) {
                    it.accept(DataBuilderVisitor(codeGenerator, logger), Unit)
                    null
                } else {
                    it
                }
            }.filterNotNull()
            .toList()

    internal class DataBuilderVisitor(
        private val codeGenerator: CodeGenerator,
        private val logger: KSPLogger
    ) : KSVisitorVoid() {
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit
        ) {
            if (!classDeclaration.modifiers.contains(Modifier.DATA)) {
                logger.error("@DataBuilder can only be applied to data classes", classDeclaration)
                return
            }

            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()
            val builderName = "${className}Builder"

            val typeSpec = TypeSpec.classBuilder(builderName)
                .addProperty(
                    PropertySpec.builder("instance", classDeclaration.toClassName())
                        .mutable(true)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("%T()", classDeclaration.toClassName())
                        .build()
                )

            classDeclaration.primaryConstructor?.parameters?.forEach { param ->
                if (!param.hasDefault) {
                    logger.error("All properties has to have its default value", classDeclaration)
                    return@forEach
                }
                val propName = param.name?.asString() ?: return@forEach
                val docString = classDeclaration.getAllProperties()
                    .find { it.simpleName == param.name }?.docString?.trim() ?: ""

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
                    .returns(classDeclaration.toClassName())
                    .addStatement("return instance")
                    .build()
            )

            FileSpec.builder(packageName, builderName)
                .addType(typeSpec.build())
                .build()
                .writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
        }
    }
}

class DataBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        DataBuilderProcessor(environment.codeGenerator, environment.logger)
}
