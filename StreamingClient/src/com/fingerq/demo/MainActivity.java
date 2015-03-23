package com.fingerq.demo;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ImageView;
import android.graphics.Bitmap;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioFormat;

import com.fingerq.streaming.StreamingClient;

public class MainActivity extends Activity implements StreamingClient.AVFrameCallback {
	private StreamingClient _streaming_client;
	private ImageView _videoImageView;
	private AudioTrack _audioTrack;
	static final private String SERVER_IP = "192.168.0.209";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		_videoImageView = (ImageView) findViewById(R.id.VideoImage);

		_streaming_client = new StreamingClient(640, 480);

		_audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 2048, AudioTrack.MODE_STREAM);
		_audioTrack.play();

		_streaming_client.start(SERVER_IP, 5557, this);
	}

	protected void onPause() {
		super.onPause();

		_audioTrack.stop();
		_streaming_client.stop();
	}

	public void onVideoFrame(final Bitmap bitmap) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				_videoImageView.setImageBitmap(bitmap);
			}
		});
	}

	public void onAudioFrame(final byte [] pcm_data, int size) {
		_audioTrack.write(pcm_data, 0, size);
	}
}

