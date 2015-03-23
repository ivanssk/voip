package com.fingerq.streaming;

import java.nio.ByteBuffer;
import android.graphics.Bitmap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

import com.fingerq.Codec;

final class StreamingParser {
	private byte [] _buffer = new byte [1024 * 1024];
	private byte [] _header = new byte [7];
	private VideoDecoder _videoDecoder;
	private AudioDecoder _audioDecoder;

	abstract class AVDecoder implements Runnable {
		public LinkedBlockingQueue <byte[]> _queue = new LinkedBlockingQueue <byte[]> ();
		private boolean _keep_running;
		private Thread _workerThread;
		private StreamingClient.AVFrameCallback _avFrameCallback;

		public AVDecoder(StreamingClient.AVFrameCallback avFrameCallback) {
			_avFrameCallback = avFrameCallback;
		}

		public void run() {
			for (_keep_running = true; _keep_running == true;) {
				try {
					byte [] data = _queue.poll(100, TimeUnit.MILLISECONDS);

					if (data != null)
						process(data, _avFrameCallback);
				}
				catch (Exception e) {
				}
			}
		}

		public abstract void process(byte [] data, StreamingClient.AVFrameCallback avFrameCallback);

		public void start() {
			_workerThread = new Thread(this);
			_workerThread.start();
		}

		public void stop() {
			try {
				_keep_running = false;
				_workerThread.join();
			}
			catch (Exception e) {
			}
		}
	}

	final class VideoDecoder extends AVDecoder {
		private int [] _out_video_buffer = new int [640 * 480];

		public VideoDecoder(StreamingClient.AVFrameCallback avFrameCallback) {
			super(avFrameCallback);
		}

		public void process(byte [] data, StreamingClient.AVFrameCallback avFrameCallback) {
			Codec.nativeVideoDecode(data, data.length, _out_video_buffer);
			Bitmap bitmap = Bitmap.createBitmap(_out_video_buffer, 0, 640, 640, 480, Bitmap.Config.ARGB_8888);
			avFrameCallback.onVideoFrame(bitmap);
		}
	}

	final class AudioDecoder extends AVDecoder {
		private byte [] _out_audio_buffer = new byte [2048];

		public AudioDecoder(StreamingClient.AVFrameCallback avFrameCallback) {
			super(avFrameCallback);
		}

		public void process(byte [] data, StreamingClient.AVFrameCallback avFrameCallback) {
			int ret = Codec.nativeAudioDecode(data, data.length, _out_audio_buffer);
			avFrameCallback.onAudioFrame(_out_audio_buffer, ret);
		}
	}

	public StreamingParser() {
	}

	public void start(StreamingClient.AVFrameCallback avFrameCallback) {
		_videoDecoder = new VideoDecoder(avFrameCallback);
		_audioDecoder = new AudioDecoder(avFrameCallback);

		_videoDecoder.start();
		_audioDecoder.start();
	}

	public void stop() {
		_videoDecoder.stop();
		_audioDecoder.stop();
	}

	public void parse(ByteBuffer byteBuffer, int width, int height, int [] out_video_buffer, byte [] out_audio_buffer) {
		int position = byteBuffer.position(); //this is buffer size and zero base
		byte [] buffer = byteBuffer.array();

		if (position >= 7) {
			byte mag1 = buffer[0];
			byte mag2 = buffer[1];
			byte video_or_audio = buffer[2];
			int size = ((buffer[3] & 0xff) | ((buffer[4] << 8) & 0x0000ff00) | ((buffer[5] << 16) & 0x00ff0000) | ((buffer[6] << 24) & 0xff000000));

			if (mag1 == 0x19 && mag2 == 0x79 && video_or_audio == (byte)1) { // video
				if (position - 8 >= size) {
					byteBuffer.flip();
					byteBuffer.get(_header); // discard 8 bytes header
					byteBuffer.get(_buffer, 0, size); // read real video data
					byteBuffer.compact();
					_videoDecoder._queue.offer(Arrays.copyOf(_buffer, size));
				}
			}
			else if (mag1 == (byte)0x19 && mag2 == (byte)0x79 && video_or_audio == (byte)2) { // audio
				if (position - 8 >= size) {
					byteBuffer.flip();
					byteBuffer.get(_header); // discard 8 bytes header
					byteBuffer.get(_buffer, 0, size); // read real audio data
					byteBuffer.compact();
					_audioDecoder._queue.offer(Arrays.copyOf(_buffer, size));
				}
			}
		}
	}
}


