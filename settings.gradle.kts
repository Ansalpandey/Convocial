pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // or PREFER_SETTINGS, but not both
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}


rootProject.name = "Convocial"

include(":app")
