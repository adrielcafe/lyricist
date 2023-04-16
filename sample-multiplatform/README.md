# multiplatform sample

### Running iOS
- IPhone: `./gradlew :sample-multiplatform:iosDeployIPhone8Debug`
- IPad: `./gradlew :sample-multiplatform:iosDeployIPadDebug`

### Running MacOS Native app (Desktop using Kotlin Native)
```shell
./gradlew :sample-multiplatform:runNativeDebug
```

### Running JVM Native app (Desktop)
```shell
./gradlew :sample-multiplatform:run
```

### Running Web Compose Canvas
```shell
./gradlew :sample-multiplatform:jsBrowserDevelopmentRun
```

### Building Android App
```shell
./gradlew :sample-multiplatform:assembleDebug
```

If you want to run Android sample in the emulator, you can open the project and run the application configuration `sample-multiplatform` on Android Studio.
