package com.fingerq.net;

import com.fingerq.AVDecoder.Decoder;
import com.fingerq.AVDecoder.VideoData;
import com.fingerq.AVDecoder.AudioData;

final public class StreamingClient {
	private boolean _keep_running;
	private Thread _workerThread;
	private int _image_width;
	private int _image_height;

	public StreamingClient(int image_width, int image_height) {
		_image_width = image_width;
		_image_height = image_height;
	}

	public boolean start(String ip, int port, Decoder.FrameCallback frameCallback) {
		final DataProvider dataProvider = new DataProvider().start(ip, port);
		final Decoder decoder = new Decoder(frameCallback);

		if (false == decoder.init(_image_width, _image_height))
			return false;

		final Thread videoStreamWorker = new Thread(new Runnable() {
			public void run() {
				for (_keep_running = true; _keep_running == true;) {
					VideoData data = dataProvider.getVideoData();

					if (null != data)
						data.accept(decoder);
				}
			}
		});

		final Thread audioStreamWorker = new Thread(new Runnable() {
			public void run() {
				for (_keep_running = true; _keep_running == true;) {
					AudioData data = dataProvider.getAudioData();

					if (null != data)
						data.accept(decoder);
				}
			}
		});

		_workerThread = new Thread(new Runnable () {
			public void run() {
				try {
					videoStreamWorker.start();
					audioStreamWorker.start();

					videoStreamWorker.join();
					audioStreamWorker.join();

					dataProvider.stop();
					decoder.release();
				}
				catch (Exception e) {
				}
			}
		});

		_workerThread.start();
		return true;
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


