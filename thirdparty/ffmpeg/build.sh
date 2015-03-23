#!/bin/bash
NDK=/Users/ivanshih/android/android-ndk-r9
SYSROOT=$NDK/platforms/android-9/arch-arm/
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.8/prebuilt/darwin-x86_64
function build_one
{
	./configure \
		--prefix=$PREFIX \
		--disable-debug \
		--disable-shared \
		--disable-doc \
		--disable-ffmpeg \
		--disable-ffplay \
		--disable-ffprobe \
		--disable-ffserver \
		--disable-avdevice \
		--disable-filters \
		--disable-symver \
		--disable-doc \
		--disable-indevs \
		--disable-outdevs \
		--disable-decoders \
		--disable-network \
		--disable-protocols \
		--disable-bsfs \
		--disable-muxers \
		--disable-asm \
		--disable-yasm \
		--enable-gpl \
		--enable-nonfree \
		--disable-encoders \
		--disable-demuxers \
		--disable-demuxer=dv \
		--disable-parsers \
		--disable-decoder=h263 \
		--enable-decoder=h264 \
		--enable-decoder=libfdk_aac \
		--enable-encoder=libx264 \
		--enable-encoder=libfdk_aac \
		--enable-libfdk-aac \
		--enable-libx264 \
		--enable-static \
		--enable-pic  \
		--enable-neon \
		--enable-armv5te \
		--enable-cross-compile \
		--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
		--target-os=linux \
		--arch=arm \
		--sysroot=$SYSROOT \
		--extra-cflags="-Os -I../../Codec/jni/x264/include -I../../Codec/jni/fdkaac/include -Ivideokit -fpic -DANDROID -DNDEBUG -marm" \
		--extra-ldflags="-L../x264 -L../../Codec/jni/fdkaac/lib"
	make clean
	make -j4 install
}
CPU=arm
PREFIX=$(pwd)/../../Codec/jni/ffmpeg
build_one
