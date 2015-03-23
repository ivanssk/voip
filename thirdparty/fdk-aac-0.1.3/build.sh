#!/bin/sh

NDK_HOME=/Users/ivanshih/android/android-ndk-r9
SYSROOT=$NDK_HOME/platforms/android-9/arch-arm
ANDROID_BIN=$NDK_HOME/toolchains/arm-linux-androideabi-4.8/prebuilt/darwin-x86_64/bin
WORKSPACE=`pwd`

export CFLAGS="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -fomit-frame-pointer -fstrict-aliasing -funswitch-loops -finline-limit=300"
export CXXFLAGS="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -fomit-frame-pointer -fstrict-aliasing -funswitch-loops -finline-limit=300"
export LDFLAGS="-Wl,--fix-cortex-a8"
export CC="arm-linux-androideabi-gcc --sysroot=$SYSROOT"
export CXX="arm-linux-androideabi-g++ --sysroot=$SYSROOT"
export PATH=$ANDROID_BIN:$PATH

./configure --host=arm-linux-androideabi \
	--with-sysroot="$SYSROOT" \
	--enable-static \
	--disable-shared \
	--prefix="$WORKSPACE/../../Codec/jni/fdkaac"

make clean
make -j4 install
