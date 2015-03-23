package com.fingerq.Provider;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import junit.framework.Assert;

final public class AudioProvider extends AVProvider {
	final static private int SAMPLE_RATE = 8000;
	final static public int SAMPLE_SIZE = 1024 * 2;
	private boolean _running;
	private Thread _worker;

	public interface AudioFrameListener {
		public abstract void onFrame(byte [] frame_data, int data_size);
	}

	public void start() {
		_worker = new Thread(new Runnable() {
			public void run() {
				try {
					AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, SAMPLE_SIZE);
					audioRecord.startRecording();

					byte [] pcm_buffer = new byte [SAMPLE_SIZE];

					for (_running = true; _running == true;) {
						int ret = audioRecord.read(pcm_buffer, 0, SAMPLE_SIZE);
						AudioProvider.this.notify(pcm_buffer, ret);
					}

					audioRecord.stop();
					audioRecord.release();

				} catch (Exception e) {
					Assert.fail(e.getMessage());
				}
			}
		});

		_worker.start();
	}

	public void stop() {
		try {
			_running = false;
			_worker.join();
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
