This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for
  your project.

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

[Compose Multiplatform Sucks!!! Just go fucking flutter, this Mother Fucker don't even comes with Navigation Apis like flutter's Navigator]

come on! i just want a simple screen navigation tools like Navigator.push() and Navigator.pop(), how
it this shit so HARD RIGHT FUCKING HERE????

[MotherFucker Regex will even crash on ios devices damn it, Kmm is fucking trash RIGHT FUCKING NOW]
Regex("access_token=((?:[a-zA-Z]|\\d|\\.|\\-|_)*).*id_token=((?:[a-zA-Z]|\\d|\\.|\\-|_)*).
*expires_in=(\\d*)").find() just fucking crash on ios devices,
And NOT FUCKING ONE CAN FUCKING FIX IT
https://youtrack.jetbrains.com/issue/KT-35508/EXCBADACCESScode2-address0x16d8dbff0-crashes-on-iOS-when-using-a-sequence-from-map-etc

[Just GO flutter instead, this shits is not ready to use RIGHT FUCKING NOW!!!]

valorant api:

https://valapidocs.techchrism.me/endpoint/cookie-reauth