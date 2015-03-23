package com.fingerq.Provider;

public abstract class AVProvider {
	public interface FrameListener {
		public void onFrame(byte [] frame_data, int frame_size);
	}

	public abstract void start();
	public abstract void stop();

	public void addListener(FrameListener listener) {
		_listener = listener;
	}

	protected void notify(byte [] frame_data, int frame_size) {
		_listener.onFrame(frame_data, frame_size);
	}

	private FrameListener _listener;
}
