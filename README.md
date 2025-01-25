https://github.com/user-attachments/assets/100ee026-550f-4d84-b180-58874ae2b395

### How to run the Jetpack Compose WearOS example

1) Clone the WearOS Jetpack Compose [example](https://github.com/fabOnReact/wearos-communication-with-rn)

```
git clone https://github.com/fabOnReact/wearos-communication-with-rn
```

2) Open the project with android studio, build and run it on an Android WearOS emulator.

### How to run the React Native Mobile example

You need to clone the `react-native-wear-connectivity` project, build and run the mobile app example.

```
git clone https://github.com/fabOnReact/react-native-wear-connectivity
cd react-native-wear-connectivity/example
yarn android
```

Now you can pair the WearOS emulator with the Android Mobile Emulator as explained in these [instructions](https://developer.android.com/training/wearables/get-started/connect-phone).


### How to implement the Jetpack Compose WearOS app

The implementation of the sendMessage function is here:

https://github.com/fabOnReact/wearos-communication-with-rn/blob/371e6c5862d49ccbff08ab951a26284a216daf97/app/src/main/java/com/wearconnectivityexample/presentation/MainActivity.kt#L75-L87

The implementation of the onMessageReceived function is here:

https://github.com/fabOnReact/wearos-communication-with-rn/blob/371e6c5862d49ccbff08ab951a26284a216daf97/app/src/main/java/com/wearconnectivityexample/presentation/MainActivity.kt#L89-L95

Make sure you respect this requirements:

### Both apps share the same package name and applicationId

Generate the app using the same package name and applicationId of the React Native Android App otherwise follow [these instructions](https://stackoverflow.com/a/29092698/7295772) to rename package name (in AndroidManifest, build.gradle, the project files) and applicationId in build.gradle.

### Both apps are signed with the same key

Make sure both apps use the same signing key. You can verify it as follows:

**Jetpack Compose App WearOS app** (no react-native)
- Verify that your build.gradle.kts on WearOS uses the same certificate from the Mobile App. The WearOS example configurations are [here](https://github.com/fabOnReact/wearos-communication-with-rn/blob/371e6c5862d49ccbff08ab951a26284a216daf97/app/build.gradle.kts#L21-L38) for our WearOS Jetpack Compose example.
- Make sure the two projects use the same keystore. The WearOS project uses the same  [debug.keystore](https://github.com/fabOnReact/wearos-communication-with-rn/blob/main/app/debug.keystore) of the Mobile App.

**Android Mobile React Native app**
- Make sure both apps are using the same key, in our example the singingConfigs are configured [here](https://github.com/fabOnReact/react-native-wear-connectivity/blob/2f936622422e197c22bef228b44eb24b46c878ae/example/android/app/build.gradle#L78-L104) and the [debug.keystore](https://github.com/fabOnReact/wearos-communication-with-rn/blob/371e6c5862d49ccbff08ab951a26284a216daf97/app/debug.keystore) is the same from the WearOS app.
