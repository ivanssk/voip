package com.fingerq.AVDecoder;

import android.graphics.Bitmap;

import com.fingerq.pattern.AVVisitor;
import com.fingerq.Codec;

public class Decoder implements AVVisitor {
	private int [] _out_video_buffer = new int [640 * 480];
	private byte [] _out_audio_buffer = new byte [2048];

	public interface FrameCallback {
		public void onFrame(Bitmap bitmap);
		public void onFrame(byte [] pcm_data, int size);
	}

	private FrameCallback _frameCallback;

	public Decoder(FrameCallback frameCallback) {
		_frameCallback = frameCallback;
	}

	public boolean init(int width, int height) {
		return Codec.nativeVideoDecoderInit(width, height) && Codec.nativeAudioDecoderInit();
	}

	public void release() {
		Codec.nativeVideoDecoderRelease();
		Codec.nativeAudioDecoderRelease();
	}

	public void visit(VideoData videoData) {
		byte [] data = videoData.getData();

		Codec.nativeVideoDecode(data, data.length, _out_video_buffer);
		_frameCallback.onFrame(Bitmap.createBitmap(_out_video_buffer, 0, 640, 640, 480, Bitmap.Config.ARGB_8888));
	}

	public void visit(AudioData audioData) {
		byte [] data = audioData.getData();
		int ret = Codec.nativeAudioDecode(data, data.length, _out_audio_buffer);
		_frameCallback.onFrame(_out_audio_buffer, ret);
	}
}
