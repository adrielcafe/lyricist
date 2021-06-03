[![Maven Central](https://img.shields.io/maven-central/v/cafe.adriel.lyricist/lyricist?style=for-the-badge&color=blue)](https://repo.maven.apache.org/maven2/cafe/adriel/lyricist/)
[![Android API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![kotlin](https://img.shields.io/github/languages/top/adrielcafe/lyricist.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?style=for-the-badge)](https://ktlint.github.io/)
[![License MIT](https://img.shields.io/github/license/adrielcafe/lyricist.svg?style=for-the-badge&color=orange)](https://opensource.org/licenses/MIT)

# Lyricist 🌎🌍🌏 
> The missing [I18N and I10N](https://en.wikipedia.org/wiki/Internationalization_and_localization) library for [Jetpack Compose](https://developer.android.com/jetpack/compose)!

Jetpack Compose revolutionized the way we build UIs on Android, but not how we **interact with strings**. `stringResource()` works well, but don't benefit from the idiomatic Kotlin like Compose does.

Lyricist tries to make working with strings as powerful as building UIs with Compose, *i.e.*, working with parameterized string is now typesafe, use of `when` expression to work with plurals with more flexibility, and even load/update the strings dynamically via an API!

#### Roadmap
- [x] [Simple API](#user-content-usage) to handle locale changes and provide the current strings
- [x] [Multi module support](#user-content-multi-module-projects)
- [x] Basic code generation with [KSP](https://github.com/google/ksp) to reduce boilerplate code
- [x] Code generation via existing `strings.xml` files

#### Limitations
* The XML processor doesn't handle `few` and `many` [plural values](https://developer.android.com/guide/topics/resources/string-resource#Plurals) (PRs are welcome) 

#### Why _Lyricist_?
Inspired by [accompanist](https://github.com/google/accompanist#why-the-name) library: music composing is done by a composer, and since this library is about writing ~~lyrics~~ strings, the role of a [lyricist](https://en.wikipedia.org/wiki/Lyricist) felt like a good name.

## Usage
Take a look at the [sample app](https://github.com/adrielcafe/lyricist/tree/main/sample/src/main/java/cafe/adriel/lyricist/sample) and [sample-multi-module](https://github.com/adrielcafe/lyricist/tree/main/sample-multi-module/src/main/java/cafe/adriel/lyricist/sample/multimodule) for working examples.

Start by declaring your strings on a `data class`, `class` or `interface` (pick one). The strings can be anything (really, it's up to you): `Char`, `String`, `AnnotatedString`, `List<String>`, `Set<String>` or even lambdas!
```kotlin
data class Strings(
    val simpleString: String,
    val annotatedString: AnnotatedString,
    val parameterString: (locale: String) -> String,
    val pluralString: (count: Int) -> String,
    val listStrings: List<String>
)
```

Next, create instances for each supported language and annotate with `@Strings`. The `languageTag` must be an [IETF BCP47](https://en.wikipedia.org/wiki/IETF_language_tag) compliant language tag ([docs](https://developer.android.com/guide/topics/resources/providing-resources#LocaleQualifier)). You must flag one of them as default.
```kotlin
@Strings(languageTag = Locales.EN, default = true)
val EnStrings = Strings(
    simpleString = "Hello Compose!",

    annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { append("Hello ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append("Compose!") }
    },

    parameterString = { locale ->
        "Current locale: $locale"
    },

    pluralString = { count ->
        val value = when (count) {
            1 -> "a single apple"
            2 -> "two apples"
            in 3..10 -> "a bunch of apples"
            else -> "a lot of apples"
        }
        "I have $value"
    },

    listStrings = listOf("Avocado", "Pineapple", "Plum")
)

@Strings(languageTag = Locales.PT)
val PtStrings = Strings(/* pt strings */)

@Strings(languageTag = Locales.ES)
val EsStrings = Strings(/* es strings */)

@Strings(languageTag = Locales.RU)
val RuStrings = Strings(/* ru strings */)
```

Lyricist will generate the `LocalStrings` property, a [CompositionLocal](https://developer.android.com/reference/kotlin/androidx/compose/runtime/CompositionLocal) that provides the strings of the current locale. It will also generate `rememberStrings()` and `ProvideStrings()`, call them to make `LocalStrings` accessible down the tree.
```kotlin
val lyricist = rememberStrings()

ProvideStrings(lyricist) {
    // Content
}
```

<details><summary>Writing the code for yourself</summary>

Don't want to enable KSP to generate the code for you? No problem! Follow the steps below to integrate with Lyricist manually.

First, map each supported language tag to their corresponding instances.
```kotlin
val strings = mapOf(
    Locales.EN to EnStrings,
    Locales.PT to PtStrings,
    Locales.ES to EsStrings,
    Locales.RU to RuStrings
)
```

Next, create your `LocalStrings` and choose one translation as default.
```kotlin
val LocalStrings = staticCompositionLocalOf { EnStrings }
```

Finally, use the same functions, `rememberStrings()` and `ProvideStrings()`, to make your `LocalStrings` accessible down the tree. But this time you need to provide your `strings` and `LocalStrings` manually.
```kotlin
val lyricist = rememberStrings(strings)

ProvideStrings(lyricist, LocalStrings) {
    // Content
}
```
</details>

Now you can use `LocalStrings` to retrieve the current strings.
```kotlin
val strings = LocalStrings.current

Text(text = strings.simpleString)
// > Hello Compose!

Text(text = strings.annotatedString)
// > Hello Compose!

Text(text = strings.parameterString(lyricist.languageTag))
// > Current locale: en

Text(text = strings.pluralString(2))
Text(text = strings.pluralString(1))
// > You have 2 wishes remaining
// > You have 1 wish remaining

Text(text = strings.listStrings.joinToString())
// > Avocado, Pineapple, Plum, Coconut
```

Use the Lyricist instance provided by `rememberStrings()` to change the current locale. This will trigger a [recomposition](https://developer.android.com/jetpack/compose/mental-model#recomposition) that will update the strings wherever they are being used.
```kotlin
lyricist.languageTag = Locales.PT
```

**Important:** Lyricist uses the System locale as default. It won't persist the current locale on storage, is outside its scope.

### Multi module projects

If you are using Lyricist on a multi module project and the generated declarations (`LocalStrings`, `rememberStrings()`, `ProvideStrings()`) are too generic for you, provide the following arguments to KSP in the module `build.gradle`.
```gradle
ksp {
    arg("lyricist.packageName", "com.my.app")
    arg("lyricist.moduleName", project.name)
}
```

Let's say you have a "dashboard" module, the generated declarations will be `LocalDashboardStrings`, `rememberDashboardStrings()` and `ProvideDashboardStrings()`.

## Processing XML Strings
TODO

## Import to your project

Add the following dependency to your module's `build.gradle`:
```gradle
implementation "cafe.adriel.lyricist:lyricist:${latest-version}"
```

(Optional) Enabling [KSP](https://github.com/google/ksp/blob/main/docs/quickstart.md):
1. Import the plugin in the root `build.gradle` then apply to your modules
```gradle
buildscript {
    dependencies {
        classpath "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${ksp-latest-version}"
    }
}

apply plugin: "com.google.devtools.ksp"
```

2. Add the following dependencies
```gradle
implementation "cafe.adriel.lyricist:lyricist-processor:${latest-version}"
ksp "cafe.adriel.lyricist:lyricist-processor:${latest-version}"
```

Current version: ![Maven Central](https://img.shields.io/maven-central/v/cafe.adriel.lyricist/lyricist?color=blue)