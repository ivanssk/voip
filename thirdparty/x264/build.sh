#!/bin/sh

NDK_HOME=/Users/ivanshih/android/android-ndk-r9
ANDROID_NDK_ROOT=$NDK_HOME
PREBUILT=$ANDROID_NDK_ROOT/toolchains/arm-linux-androideabi-4.8/prebuilt/darwin-x86_64
PLATFORM=$ANDROID_NDK_ROOT/platforms/android-9/arch-arm
ARM_INC=$PLATFORM/usr/include
ARM_LIB=$PLATFORM/usr/lib
ARM_LIBO=$PREBUILT/lib/gcc/arm-linux-androideabi/4.8

./configure --disable-cli --disable-gpac --enable-pic --enable-strip \
--extra-cflags=" -I$ARM_INC -fPIC -DANDROID -fpic -mthumb-interwork -ffunction-sections -funwind-tables -fstack-protector -fno-short-enums -march=armv7-a -mtune=cortex-a9 -mfloat-abi=softfp -mfpu=neon -D__ARM_ARCH_7__ -D__ARM_ARCH_7A__  -Wno-psabi -msoft-float -mthumb -Os -fomit-frame-pointer -fno-strict-aliasing -finline-limit=64 -DANDROID  -Wa,--noexecstack -MMD -MP " \
--extra-ldflags="-Wl,--fix-cortex-a8 -nostdlib -Bdynamic -Wl,--no-undefined -Wl,-z,noexecstack  -Wl,-z,nocopyreloc -Wl,-soname,/system/lib/libz.so -Wl,-rpath-link=$ARM_LIB,-dynamic-linker=/system/bin/linker -L$ARM_LIB -nostdlib $ARM_LIB/crtbegin_dynamic.o $ARM_LIB/crtend_android.o -lc -lm -ldl -lgcc" \
--cross-prefix=$PREBUILT/bin/arm-linux-androideabi- \
--host=arm-linux \
--enable-static \
--prefix='../../Codec/jni/x264'

make clean
make -j4 install
