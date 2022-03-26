[![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&metadataUrl=https://s01.oss.sonatype.org/service/local/repo_groups/public/content/cafe/adriel/lyricist/lyricist/maven-metadata.xml&style=for-the-badge)](https://repo.maven.apache.org/maven2/cafe/adriel/lyricist/)
[![Android API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![kotlin](https://img.shields.io/github/languages/top/adrielcafe/lyricist.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?style=for-the-badge)](https://ktlint.github.io/)
[![License MIT](https://img.shields.io/github/license/adrielcafe/lyricist.svg?style=for-the-badge&color=orange)](https://opensource.org/licenses/MIT)

# Lyricist 🌎🌍🌏 
> The missing [I18N and L10N](https://en.wikipedia.org/wiki/Internationalization_and_localization) multiplatform library for [Jetpack Compose](https://developer.android.com/jetpack/compose)!

Jetpack Compose greatly improved the way we build UIs on Android, but not how we **interact with strings**. `stringResource()` works well, but doesn't benefit from the idiomatic Kotlin like Compose.

Lyricist tries to make working with strings as powerful as building UIs with Compose, *i.e.*, working with parameterized string is now typesafe, use of `when` expression to work with plurals with more flexibility, and even load/update the strings dynamically via an API!

#### Features
- [x] Multiplatform: Android, Desktop
- [x] [Simple API](#usage) to handle locale changes and provide the current strings
- [x] [Multi module support](#multi-module-settings)
- [x] [Easy migration](#migrating-from-stringsxml) from `strings.xml`
- [x] Code generation with [KSP](https://github.com/google/ksp)
  
#### Roadmap
- iOS support

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
    val list: List<String>
)
```

Next, create instances for each supported language and annotate with `@Strings`. The `languageTag` must be an [IETF BCP47](https://en.wikipedia.org/wiki/IETF_language_tag) compliant language tag ([docs](https://developer.android.com/guide/topics/resources/providing-resources#LocaleQualifier)). You must flag one of them as default.
```kotlin
@Strings(languageTag = Locales.EN, default = true)
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
            1, 2 -> "few"
            in 3..10 -> "bunch of"
            else -> "lot of"
        }
        "I have a $value apples"
    },

    list = listOf("Avocado", "Pineapple", "Plum")
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

---
</details>

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

Use the Lyricist instance provided by `rememberStrings()` to change the current locale. This will trigger a [recomposition](https://developer.android.com/jetpack/compose/mental-model#recomposition) that will update the strings wherever they are being used.
```kotlin
lyricist.languageTag = Locales.PT
```

**Important:** Lyricist uses the System locale as default. It won't persist the current locale on storage, is outside its scope.

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

Similar to the multi module setup, you must provide a few arguments to KSP. Lyricist will search for `strings.xml` files in the resources path. You can also provide a language tag to be used as default value for the `LocalStrings`. 
```gradle
ksp {
    // Required
    arg("lyricist.xml.resourcesPath", android.sourceSets.main.res.srcDirs.first().absolutePath)
    
    // Optional
    arg("lyricist.packageName", "com.my.app")
    arg("lyricist.xml.moduleName", "xml")
    arg("lyricist.xml.defaultLanguageTag", "en")
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
```gradle
buildscript {
    dependencies {
        classpath "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${ksp-latest-version}"
    }
}

apply plugin: "com.google.devtools.ksp"
```

2. Add the desired dependencies to your module's `build.gradle`
```gradle
// Required
implementation "cafe.adriel.lyricist:lyricist:${latest-version}"

// If you want to use @Strings to generate code for you
compileOnly "cafe.adriel.lyricist:lyricist-processor:${latest-version}"
ksp "cafe.adriel.lyricist:lyricist-processor:${latest-version}"

// If you want to migrate from strings.xml
ksp "cafe.adriel.lyricist:lyricist-processor-xml:${latest-version}"
```

Current version: ![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&metadataUrl=https://s01.oss.sonatype.org/service/local/repo_groups/public/content/cafe/adriel/lyricist/lyricist/maven-metadata.xml)