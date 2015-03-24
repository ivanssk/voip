package com.fingerq.net;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Class;
import java.lang.reflect.Constructor;

import com.fingerq.AVDecoder.VideoData;
import com.fingerq.AVDecoder.AudioData;

final class DataProvider implements Runnable {
	private LinkedBlockingQueue <Object> [] _queue = new LinkedBlockingQueue[] {new LinkedBlockingQueue <Object>(), new LinkedBlockingQueue <Object>()};

	private boolean _keep_running;
	private String _ip;
	private int _port;
	private Thread _workerThread;

	public DataProvider start(String ip, int port) {
		_ip = ip;
		_port = port;

		_workerThread = new Thread(this);
		_workerThread.start();
		return this;
	}

	public void stop() {
		try {
			_keep_running = false;
			_workerThread.join();
		}
		catch (Exception e) {
		}
	}

	public void run() {
		try {
			Constructor<?> [] clazz = new Constructor <?> [] { Class.forName("com.fingerq.AVDecoder.VideoData").getConstructor(byte [].class),
									   Class.forName("com.fingerq.AVDecoder.AudioData").getConstructor(byte [].class)};

			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress(_ip, _port));

			ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
			byte [] header = new byte[7];

			for (_keep_running = true; _keep_running == true;) {
				socketChannel.read(byteBuffer);

				int position = byteBuffer.position();

				if (position < 7)
					continue;

				byte [] buffer = byteBuffer.array();

				if (buffer[0] != 0x19 || buffer[1] != 0x79)
					continue;

				int size = ((buffer[3] & 0xff) | ((buffer[4] << 8) & 0x0000ff00) | ((buffer[5] << 16) & 0x00ff0000) | ((buffer[6] << 24) & 0xff000000));

				if (position - 7 < size)
					continue;

				byte [] data = new byte [size];

				byteBuffer.flip();
				byteBuffer.get(header);
				byteBuffer.get(data, 0, size);
				byteBuffer.compact();
				_queue[buffer[2]].offer(clazz[buffer[2]].newInstance(data));
			}

			socketChannel.close();
		}
		catch (Exception e) {
		}
	}

	public VideoData getVideoData() {
		return (VideoData) _queue[0].poll();
	}

	public AudioData getAudioData() {
		return (AudioData) _queue[1].poll();
	}
}
