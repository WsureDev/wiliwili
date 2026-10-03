# Android build dependencies

The Android port uses the same SDL2 + libmpv architecture as switchfin:

- SDL2 provides the Android window, touch, keyboard, and controller bridge.
- Borealis uses the Android platform backend and GLES3 renderer.
- FFmpeg and libmpv are built as shared libraries for each ABI.
- Gradle packages the native target and its dependency prefix into ABI-split APKs.

The supported ABIs are `armeabi-v7a` (32-bit ARMv7) and `arm64-v8a` (64-bit ARMv8).
The CI workflow builds one APK for each ABI. For a local build, install Android NDK r27d,
Meson, Ninja, pkg-config, and NASM, then run:

```sh
make -C scripts/android TMPDIR="$TMPDIR/wiliwili" download
for abi in armeabi-v7a arm64-v8a; do
  make -C scripts/android ABI="$abi" ANDROID_NDK="$ANDROID_NDK" build
done
bash library/borealis/build_libromfs_generator.sh
(cd app/platform/android && ./gradlew assembleRelease)
```

The release variant uses the Gradle debug keystore by default so CI artifacts are installable.
Downstream release builds should provide their own signing configuration.
