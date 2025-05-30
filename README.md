[![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&metadataUrl=https://s01.oss.sonatype.org/service/local/repo_groups/public/content/cafe/adriel/lyricist/lyricist/maven-metadata.xml&style=for-the-badge)](https://central.sonatype.com/search?q=g%3Acafe.adriel.lyricist)
[![Android API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![kotlin](https://img.shields.io/github/languages/top/adrielcafe/lyricist.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?style=for-the-badge)](https://ktlint.github.io/)
[![License MIT](https://img.shields.io/github/license/adrielcafe/lyricist.svg?style=for-the-badge&color=orange)](https://opensource.org/licenses/MIT)

# Lyricist 🌎🌍🌏 
> The missing [I18N and L10N](https://en.wikipedia.org/wiki/Internationalization_and_localization) multiplatform library for [Jetpack Compose](https://developer.android.com/jetpack/compose)!

Jetpack Compose greatly improved the way we build UIs on Android, but not how we **interact with strings**. `stringResource()` works well, but doesn't benefit from the idiomatic Kotlin like Compose.

Lyricist tries to make working with strings as powerful as building UIs with Compose, *i.e.*, working with parameterized string is now typesafe, use of `when` expression to work with plurals with more flexibility, and even load/update the strings dynamically via an API!

#### Features
- [x] Multiplatform: Android, Desktop, iOS, Web (JsCanvas)
- [x] [Simple API](#usage) to handle locale changes and provide the current strings
- [x] [Multi module support](#multi-module-settings)
- [x] [Easy migration](#migrating-from-stringsxml) from `strings.xml`
- [x] [Extensible](#extending-lyricist): supports Compose Multiplatform out of the box but can be integrated on any UI Toolkit
- [x] Code generation with [KSP](https://github.com/google/ksp)

#### Limitations
* The XML processor doesn't handle `few` and `many` [plural values](https://developer.android.com/guide/topics/resources/string-resource#Plurals) (PRs are welcome) 

#### Why _Lyricist_?
Inspired by [accompanist](https://github.com/google/accompanist#why-the-name) library: music composing is done by a composer, and since this library is about writing ~~lyrics~~ strings, the role of a [lyricist](https://en.wikipedia.org/wiki/Lyricist) felt like a good name.

## Usage
Take a look at the [sample app](https://github.com/adrielcafe/lyricist/tree/main/sample/src/main/java/cafe/adriel/lyricist/sample) and [sample-multi-module](https://github.com/adrielcafe/lyricist/tree/main/sample-multi-module/src/main/java/cafe/adriel/lyricist/sample/multimodule) for working examples.

Start by declaring your strings on a `data class`, `class` or `interface` (pick one). The strings can be anything (really, it's up to you): `Char`, `String`, `AnnotatedString`, `List<String>`, `Set<String>` or even lambdas!
```kotlin
data class Strings(
    val simple: String,
    val annotated: AnnotatedString,
    val parameter: (locale: String) -> String,
    val plural: (count: Int) -> String,
    val list: List<String>,
    val nestedStrings: NestedStrings(),
)

data class NestedStrings(
    ...
)
```

Next, create instances for each supported language and annotate with `@LyricistStrings`. The `languageTag` must be an [IETF BCP47](https://en.wikipedia.org/wiki/IETF_language_tag) compliant language tag ([docs](https://developer.android.com/guide/topics/resources/providing-resources#LocaleQualifier)). You must flag one of them as default.
```kotlin
@LyricistStrings(languageTag = Locales.EN, default = true)
val EnStrings = Strings(
    simple = "Hello Compose!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { 
            append("Hello ") 
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { 
            append("Compose!") 
        }
    },

    parameter = { locale ->
        "Current locale: $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "no"
            1, 2 -> "a few"
            in 3..10 -> "a bunch of"
            else -> "a lot of"
        }
        "I have $value apples"
    },

    list = listOf("Avocado", "Pineapple", "Plum")
)

@LyricistStrings(languageTag = Locales.PT)
val PtStrings = Strings(/* pt strings */)

@LyricistStrings(languageTag = Locales.ES)
val EsStrings = Strings(/* es strings */)

@LyricistStrings(languageTag = Locales.RU)
val RuStrings = Strings(/* ru strings */)
```

Lyricist will generate the `LocalStrings` property, a [CompositionLocal](https://developer.android.com/reference/kotlin/androidx/compose/runtime/CompositionLocal) that provides the strings of the current locale. It will also generate `rememberStrings()` and `ProvideStrings()`, call them to make `LocalStrings` accessible down the tree.
```kotlin
val lyricist = rememberStrings()

ProvideStrings(lyricist) {
    // Content
}

// Or just 
ProvideStrings {
    // Content
}
```

Optionally, you can specify the current and default (used as fallback) languages.
```kotlin
val lyricist = rememberStrings(
    defaultLanguageTag = "es-US", // Default value is the one annotated with @LyricistStrings(default = true)
    currentLanguageTag = getCurrentLanguageTagFromLocalStorage(),
)
```

Now you can use `LocalStrings` to retrieve the current strings.
```kotlin
val strings = LocalStrings.current

Text(text = strings.simple)
// > Hello Compose!

Text(text = strings.annotated)
// > Hello Compose!

Text(text = strings.parameter(lyricist.languageTag))
// > Current locale: en

Text(text = strings.plural(1))
Text(text = strings.plural(5))
Text(text = strings.plural(20))
// > I have a few apples
// > I have a bunch of apples
// > I have a lot of apples

Text(text = strings.list.joinToString())
// > Avocado, Pineapple, Plum
```

Use the Lyricist instance provided by `rememberStrings()` to change the current locale. This will trigger a [recomposition](https://developer.android.com/jetpack/compose/mental-model#recomposition) that will update the entire content.
```kotlin
lyricist.languageTag = Locales.PT
```

**Important**

Lyricist uses the System locale as current language (on Compose it uses `Locale.current`). If your app has a mechanism to change the language in-app please set this value on `rememberStrings(currentLanguageTag = CURRENT_VALUE_HERE)`.

If you change the current language at runtime Lyricist won't persist the value on a local storage by itself, this should be done by you. You can save the current language tag on shared preferences, a local database or even through a remote API.

### Controlling the visibility
To control the visibility (`public` or `internal`) of the generated code, provide the following (optional) argument to KSP in the module's `build.gradle`.
```gradle
ksp {
    arg("lyricist.internalVisibility", "true")
}
```

### Generating a `strings` helper property
Instead of use `LocalStrings.current` to access your strings, you can simply call `strings`. Just provide the following (optional) argument to KSP in the module's `build.gradle`.
```gradle
ksp {
    arg("lyricist.generateStringsProperty", "true")
}
```
After a successfully build you can refactor your code as below. 
```kotlin
// Before
Text(text = LocalStrings.current.hello)

// After
Text(text = strings.hello)
```

## Multi module settings

If you are using Lyricist on a multi module project and the generated declarations (`LocalStrings`, `rememberStrings()`, `ProvideStrings()`) are too generic for you, provide the following (optional) arguments to KSP in the module's `build.gradle`.
```gradle
ksp {
    arg("lyricist.packageName", "com.my.app")
    arg("lyricist.moduleName", project.name)
}
```

Let's say you have a "dashboard" module, the generated declarations will be `LocalDashboardStrings`, `rememberDashboardStrings()` and `ProvideDashboardStrings()`.

## Migrating from `strings.xml`
So you liked Lyricist, but already have a project with thousands of strings spread over multiples files? I have good news for you: Lyricist can extract these existing strings and generate all the code you just saw above.
If you don't want to have the Compose code generated by KSP, you can set the `lyricist.xml.generateComposeAccessors` arg to `"false"`, and you can write the code manually by following the instructions [below](#extending-lyricist).

Similar to the multi module setup, you must provide a few arguments to KSP. Lyricist will search for `strings.xml` files in the resources path. You can also provide a language tag to be used as default value for the `LocalStrings`. 
```gradle
ksp {
    // Required
    arg("lyricist.xml.resourcesPath", android.sourceSets.main.res.srcDirs.first().absolutePath)
    
    // Optional
    arg("lyricist.packageName", "com.my.app")
    arg("lyricist.xml.moduleName", "xml")
    arg("lyricist.xml.defaultLanguageTag", "en")
    arg("lyricist.xml.generateComposeAccessors", "false")
}
```

After the first build, the well-known `rememberStrings()` and `ProvideStrings()` (naming can vary depending on your KSP settings) will be available for use. Lyricist will also generated a `Locales` object containing all language tags currently in use in your project. 
```kotlin
val lyricist = rememberStrings(strings)

ProvideStrings(lyricist, LocalStrings) {
    // Content
}

lyricist.languageTag = Locales.PT
```

You can easily migrate from `strings.xml` to Lyricist just by copying the generated files to your project. That way, you can finally say goodbye to `strings.xml`. 

## Extending Lyricist

<details><summary>Writing the generated code from KSP manually</summary>

Don't want to enable KSP to generate the code for you? No problem! Follow the steps below to integrate with Lyricist manually.

1. Map each supported language tag to their corresponding instances.
```kotlin
val strings = mapOf(
    Locales.EN to EnStrings,
    Locales.PT to PtStrings,
    Locales.ES to EsStrings,
    Locales.RU to RuStrings
)
```

2. Create your `LocalStrings` and choose one translation as default.
```kotlin
val LocalStrings = staticCompositionLocalOf { EnStrings }
```

3. Use the same functions, `rememberStrings()` and `ProvideStrings()`, to make your `LocalStrings` accessible down the tree. But this time you need to provide your `strings` and `LocalStrings` manually.
```kotlin
val lyricist = rememberStrings(strings)

ProvideStrings(lyricist, LocalStrings) {
    // Content
}
```
</details>

<details><summary>Supporting other UI Toolkits</summary>

At the moment Lyricist only supports Jetpack Compose and Compose Multiplatform out of the box. If you need to use Lyricist with other UI Toolkit (Android Views, SwiftUI, Swing, GTK...) follow the instructions bellow.

1. Map each supported language tag to their corresponding instances
```kotlin
val translations = mapOf(
    Locales.EN to EnStrings,
    Locales.PT to PtStrings,
    Locales.ES to EsStrings,
    Locales.RU to RuStrings
)
```

2. Create an instance of Lyricist, can be a project-wide singleton or a local instance per module
```kotlin
val lyricist = Lyricist(defaultLanguageTag, translations)
```

3. Collect Lyricist state and notify the UI to update whenever it changes
```kotlin
lyricist.state.collect { (languageTag, strings) ->
    refreshUi(strings)
}

// Example for Compose
val state by lyricist.state.collectAsState()

CompositionLocalProvider(
    LocalStrings provides state.strings
) {
    // Content
}
```
</details>

## Troubleshooting

<details><summary>Can't use the generated code on my IDE</summary>

You should set manually the source sets of the generated files, like described [here](https://github.com/google/ksp/issues/37).
```gradle
buildTypes {
    debug {
        sourceSets {
            main.java.srcDirs += 'build/generated/ksp/debug/kotlin/'
        }
    }
    release {
        sourceSets {
            main.java.srcDirs += 'build/generated/ksp/release/kotlin/'
        }
    }
}
```
</details>

## Import to your project

1. Importing the [KSP plugin](https://github.com/google/ksp/blob/main/docs/quickstart.md#use-your-own-processor-in-a-project) in the project's `build.gradle` then apply to your module's `build.gradle`
```kotlin
plugins {
    id("com.google.devtools.ksp") version "${ksp-latest-version}"
}
```

2. Add the desired dependencies to your module's `build.gradle`
```kotlin
// Required
implementation("cafe.adriel.lyricist:lyricist:${latest-version}")

// If you want to use @LyricistStrings to generate code for you
ksp("cafe.adriel.lyricist:lyricist-processor:${latest-version}")

// If you want to migrate from strings.xml
ksp("cafe.adriel.lyricist:lyricist-processor-xml:${latest-version}")
```

#### Version Catalog
```toml
[versions]
lyricist = {latest-version}

[libraries]
lyricist = { module = "cafe.adriel.lyricist:lyricist", version.ref = "lyricist" }
lyricist-processor = { module = "cafe.adriel.lyricist:lyricist-processor", version.ref = "lyricist" }
lyricist-processorXml = { module = "cafe.adriel.lyricist:lyricist-processor-xml", version.ref = "lyricist" }
```

#### Multiplatform setup

Doing code generation only at `commonMain`. Currently workaround, for more information see [KSP Issue 567](https://github.com/google/ksp/issues/567)
```kotlin
dependencies {
    add("kspCommonMainMetadata", "cafe.adriel.lyricist:lyricist-processor:${latest-version}")
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}
```

Current version: ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&metadataUrl=https://s01.oss.sonatype.org/service/local/repo_groups/public/content/cafe/adriel/lyricist/lyricist/maven-metadata.xml)