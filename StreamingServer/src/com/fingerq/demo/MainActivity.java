package com.fingerq.demo;

import android.app.Activity;
import android.os.Bundle;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fingerq.Provider.AVProvider;
import com.fingerq.Provider.VideoProvider;
import com.fingerq.Provider.AudioProvider;
import com.fingerq.Codec;
import com.fingerq.net.StreamingServer;
import com.fingerq.net.Packet;

/*
 * This app will capture image from camera devoce and audio from mic device.
 * Then they will be encoded and transnited over the Internet.
 * Threfore, below permission are necessary,
 *
      <uses-permission android:name="android.permission.CAMERA" />
      <uses-permission android:name="android.permission.INTERNET" />
      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
      <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
      <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */

public class MainActivity extends Activity {
	static private final int PREVIEW_WIDTH = 640;
	static private final int PREVIEW_HEIGHT = 480;
	static private final int SERVER_PORT = 5557;

	private AVProvider _videoProvider;
	private AVProvider _audioProvider;

	static private byte [] _video_encoded_data = new byte [PREVIEW_WIDTH * PREVIEW_HEIGHT * 3 / 2];
	static private byte [] _audio_encoded_data = new byte [AudioProvider.SAMPLE_SIZE];
	static final StreamingServer _streamingServer = StreamingServer.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 1. Video and audio codec should be initated before start encoding
		Codec.nativeVideoEncoderInit(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		Codec.nativeAudioEncoderInit();

		// 2. Start server to handle a socket
		_streamingServer.start(SERVER_PORT);

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.CameraPreview);

		// 3. Once SurfaceView is ready, we expect to start camera's preview and capture it.
		surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			public void surfaceCreated(SurfaceHolder holder) {
				// 4. Once video or audio come, we should encode and put them into a queue that will be sent out over the Internet.
				_videoProvider = new VideoProvider(PREVIEW_WIDTH, PREVIEW_HEIGHT, holder);

				_videoProvider.addListener(new AVProvider.FrameListener() {
					public void onFrame(byte [] frame_data, int frame_size) {
						int encoded_size = Codec.nativeVideoEncode(frame_data, frame_size, _video_encoded_data);
						_streamingServer.putPacket(Packet.wrap(_video_encoded_data, encoded_size, Packet.VIDEO));
					}
				});

				_audioProvider = new AudioProvider();

				_audioProvider.addListener(new AVProvider.FrameListener() {
					public void onFrame(byte [] frame_data, int frame_size) {
						int encoded_size = Codec.nativeAudioEncode(frame_data, frame_size, _audio_encoded_data);
						_streamingServer.putPacket(Packet.wrap(_audio_encoded_data, encoded_size, Packet.AUDIO));
					}
				});

				// 5. Once everything is ready, just start to caprure video and audio
				_videoProvider.start();
				_audioProvider.start();
			}

			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// 6. When this app is terminated, we should release all resource immediately
				_videoProvider.stop();
				_audioProvider.stop();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 7. Release codec resource and stop server before this app is terminated.
		Codec.nativeVideoEncoderRelease();
		Codec.nativeAudioEncoderRelease();
		_streamingServer.stop();
	}
}
