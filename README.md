# Radz App Updater 2024

[![](https://jitpack.io/v/Radzdevteam/Sign_Checker.svg)](https://jitpack.io/#Radzdevteam/Sign_Checker)

Introducing Radz Sign Checker 2024: The ultimate SHA-1 signature verifier for Android apps. Ensure your app's integrity from KitKat 4.4 to Android 14. Simple, fast, and reliable.


## How to Include

### Step 1. Add the repository to your project settings.gradle:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
   ```

### Step 2. Add the dependency
```groovy
dependencies {
     implementation ("com.github.Radzdevteam:Sign_Checker:1.0")
}
   ```

## Usage

In your `MainActivity`, add the following code to check for updates:
```groovy
val cs = Security(this)
cs.setSHA1("0c:55:6b:47:5d:a0:c0:eb:24:b1:a6:ab:fa:e7:1e:b1:c7:90:66:9e")
cs.check()
   ```



