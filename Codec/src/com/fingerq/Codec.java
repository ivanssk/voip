package com.fingerq;
import junit.framework.Assert;

public final class Codec {
	static {
		try {
			System.loadLibrary("FingerQCodec");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	static public native boolean nativeVideoEncoderInit(int width, int height);
	static public native int nativeVideoEncode(byte[] yv12, int insize, byte[] out);
	static public native void nativeVideoEncoderRelease();

	static public native boolean nativeVideoDecoderInit(int width, int height);
	static public native int nativeVideoDecode(byte[] in, int insize, int[] out);
	static public native void nativeVideoDecoderRelease();

	static public native boolean nativeAudioEncoderInit();
	static public native int nativeAudioEncode(byte[] in, int insize, byte[] out);
	static public native void nativeAudioEncoderRelease();

	static public native boolean nativeAudioDecoderInit();
	static public native int nativeAudioDecode(byte[] in, int insize, byte[] out);
	static public native void nativeAudioDecoderRelease();
}

