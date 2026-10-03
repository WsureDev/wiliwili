cmake_minimum_required(VERSION 3.18)

project(wiliwili_android_jni LANGUAGES C CXX)

# The Android Gradle plugin configures this project once for each ABI. The
# native dependencies built by scripts/android are kept in the repository root
# so the same CMake project can be reused for armeabi-v7a and arm64-v8a.
set(WILIWILI_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../../..")
set(MPV_PREFIX "${WILIWILI_ROOT}/jniLibs/${ANDROID_ABI}")
set(ENV{PKG_CONFIG_SYSROOT_DIR} "${MPV_PREFIX}")
set(ENV{PKG_CONFIG_LIBDIR} "${MPV_PREFIX}/lib/pkgconfig")
set(CMAKE_PREFIX_PATH "${MPV_PREFIX}")
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)

if (NOT EXISTS "${MPV_PREFIX}")
    message(FATAL_ERROR "Missing Android dependencies for ${ANDROID_ABI}: ${MPV_PREFIX}. Run scripts/android/Makefile first.")
endif ()

# libmpv's shared object keeps FFmpeg as separate shared dependencies. Listing
# them explicitly makes the link step deterministic and lets Gradle package the
# full runtime set into the selected ABI split.
set(APP_PLATFORM_LIB
    "${MPV_PREFIX}/lib/libmpv.so"
    "${MPV_PREFIX}/lib/libavcodec.so"
    "${MPV_PREFIX}/lib/libavdevice.so"
    "${MPV_PREFIX}/lib/libavfilter.so"
    "${MPV_PREFIX}/lib/libavformat.so"
    "${MPV_PREFIX}/lib/libswresample.so"
    "${MPV_PREFIX}/lib/libswscale.so"
    "${MPV_PREFIX}/lib/libavutil.so"
)

foreach (library IN LISTS APP_PLATFORM_LIB)
    if (NOT EXISTS "${library}")
        message(FATAL_ERROR "Missing Android runtime library: ${library}")
    endif ()
endforeach ()

set(LIBROMFS_PREBUILT_GENERATOR
    "${WILIWILI_ROOT}/library/borealis/libromfs-generator"
    CACHE STRING "Host libromfs generator")
if (NOT EXISTS "${LIBROMFS_PREBUILT_GENERATOR}")
    message(FATAL_ERROR "libromfs-generator has not been built; run library/borealis/build_libromfs_generator.sh")
endif ()

set(PLATFORM_ANDROID ON CACHE BOOL "")
set(USE_SYSTEM_CURL ON CACHE BOOL "")
set(USE_SYSTEM_SDL2 ON CACHE BOOL "")
set(USE_GLES3 ON CACHE BOOL "")
set(SIMPLE_HIGHLIGHT ON CACHE BOOL "")
set(USE_LIBROMFS ON CACHE BOOL "")

# src is a symlink to the repository root. Keeping the same layout as
# switchfin/borealis means all project and submodule paths remain unchanged.
add_subdirectory(src "${CMAKE_CURRENT_BINARY_DIR}/wiliwili")

target_link_directories(wiliwili PRIVATE "${MPV_PREFIX}/lib")
target_link_libraries(wiliwili PRIVATE "${MPV_PREFIX}/lib/libcurl.so" android log EGL GLESv3)
