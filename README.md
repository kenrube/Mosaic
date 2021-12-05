# Mosaic
Simplified replica of the first two screens of [Prisma Android app](https://play.google.com/store/apps/details?id=com.neuralprisma).
Without neural networks, just simple shader-based filters.

## Preview
![](files/preview.gif)

## Pre-requisites
- [OpenGL ES 2.0](https://developer.android.com/guide/topics/graphics/opengl)-compatible device/emulator

## Tech stack
Architecture pattern is MVI (over ViewModel).

### Dependencies
- [Jetpack](https://developer.android.com/jetpack) ([Android KTX](https://developer.android.com/kotlin/ktx)):
    - [Fragment](https://developer.android.com/jetpack/androidx/releases/fragment) - for convenient 
      [Fragment Result API](https://developer.android.com/guide/fragments/communicate#fragment-result)
    - [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - for ViewModel
    - [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation) - for declarative 
      navigation between fragments
- [Material](https://github.com/material-components/material-components-android) - for Material themes
- [Coroutines](https://developer.android.com/kotlin/coroutines) - for convenient concurrency
- [Hilt](https://dagger.dev/hilt/) - for Android-specific DI with compile-time graph checks
- [Glide](https://github.com/bumptech/glide) - for image loading

### Test dependencies
- [JUnit 4](https://github.com/junit-team/junit4)
- [Espresso](https://developer.android.com/training/testing/espresso)

### Plugins
- [SafeArgs](https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args) - for 
  type-safe argument passing between navigation destinations
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - for more 
  convenient Hilt API

## 3rd party content
Filters thumbnails are created from the [Sunflower](https://unsplash.com/photos/Kh5uDUoXXfU) by
[Rehan Shaik](https://unsplash.com/@rehanshaik_17), licensed under the [Unsplash License](https://unsplash.com/license).
Cropped version of this photo (720x720px) can be found [here](https://github.com/kenrube/Mosaic/blob/master/files/sunflower.jpg).
