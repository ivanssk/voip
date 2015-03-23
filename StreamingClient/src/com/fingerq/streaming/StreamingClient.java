package com.fingerq.streaming;

import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import android.util.Log;
import android.graphics.Bitmap;

import com.fingerq.Codec;

final public class StreamingClient implements Runnable {
	public interface AVFrameCallback {
		public void onVideoFrame(Bitmap bitmap);
		public void onAudioFrame(byte [] pcm_data, int size);
	}

	private AVFrameCallback _avFrameCallback;
	private boolean _keep_running;
	private Thread _workerThread;
	private String _server_ip;
	private int _server_port;
	private int _image_width;
	private int _image_height;

	public StreamingClient(int image_width, int image_height) {
		_image_width = image_width;
		_image_height = image_height;
	}

	public void run() {
		try {
			int [] out_video_buffer = new int [_image_width * _image_height];
			byte [] out_audio_buffer = new byte [1024 * 1024];

			if (false == Codec.nativeVideoDecoderInit(_image_width, _image_height))
				return;

			if (false == Codec.nativeAudioDecoderInit())
				return;

			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress(_server_ip, _server_port));

			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

			StreamingParser streamingParser = new StreamingParser();
			streamingParser.start(_avFrameCallback);

			for (_keep_running = true; _keep_running == true;) {
				socketChannel.read(buffer);
				streamingParser.parse(buffer, _image_width, _image_height, out_video_buffer, out_audio_buffer);
			}

			streamingParser.stop();
			socketChannel.close();
			out_video_buffer = null;
			out_audio_buffer = null;
		}
		catch (Exception e) {
		}
		finally {
			Codec.nativeVideoDecoderRelease();
			Codec.nativeAudioDecoderRelease();
		}
	}

	public void start(String server_ip, int server_port, AVFrameCallback avFrameCallback) {
		_server_ip = server_ip;
		_server_port = server_port;
		_avFrameCallback = avFrameCallback;

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


