# DataBuilder

DataBuilder is a Kotlin Symbol Processing (KSP) based code generator that automatically generates builder pattern implementations for your Kotlin data classes.

## Overview

DataBuilder bridges the gap between Java and Kotlin by generating builder pattern implementations for Kotlin data classes. While Kotlin data classes offer convenient features like optional parameters with default values, these features are not directly accessible from Java code. DataBuilder solves this problem by automatically generating Java-friendly builder pattern implementations using KSP (Kotlin Symbol Processing) for efficient compile-time code generation.

## Limitation

DataBuilder currently only supports Kotlin data classes with all parameters having default values.

## Example

In Kotlin, you can define a data class with default values:

```kotlin
@DataBuilder
data class Person(
    val name: String = "",
    val age: Int = 0,
    val email: String? = null,
    val address: String = "",
    val phone: String? = null
)
```

From Kotlin, you can create instances using named parameters:

```kotlin
val person = Person(
    name = "John",
    age = 30,
    email = "john@example.com"
    // address and phone will use default values
)
```

However, in Java, you can't use named or optional parameters. With DataBuilder, you can use the generated builder instead:

```java
Person person = new PersonBuilder()
    .name("John")
    .age(30)
    .email("john@example.com")
    // address and phone are optional, using default values
    .build();
```

## Installation

Add the following dependencies to your project:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

dependencies {
    implementation("io.github.naitaku:databuilder-annotation:$version")
    ksp("io.github.naitaku:databuilder-processor:$version")
}
```

## Usage

1. Add the `@DataBuilder` annotation to your data class:

```kotlin
import io.github.naitaku.databuilder.annotation.DataBuilder

@DataBuilder
data class Person(
    val name: String = "",
    val age: Int = 0,
    val email: String? = null,
    val address: String = "",
    val phone: String? = null
)
```

2. Build your project. PersonBuilder class will automatically generate a builder class for your data class.

3. Use the generated builder:

```java
Person person = new PersonBuilder()
    .name("John")
    .age(30)
    .email("john@example.com")
    // address and phone are optional, using default values
    .build();
```

## Project Structure

- `:annotation` - Contains the DataBuilder annotation
- `:processor` - Contains the KSP processor for code generation
- `:app` - Sample application demonstrating usage

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
