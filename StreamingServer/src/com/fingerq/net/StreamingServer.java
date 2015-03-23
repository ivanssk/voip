package com.fingerq.net;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.Buffer;
import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.Assert;

import com.fingerq.pattern.state.StateContext;
import com.fingerq.net.ServerState.InternalServerContext;

final public class StreamingServer {
	private Thread _workerThread;
	private ServerSocketChannel _serverSocketChannel;
	private LinkedBlockingQueue <Buffer> _queue = new LinkedBlockingQueue <Buffer>();
	private StateContext _communicableStateContext = new StateContext();
	static private StreamingServer _instance = new StreamingServer();

	private StreamingServer() {
	}

	static public StreamingServer getInstance() {
		return _instance;
	}

	public void putPacket(Buffer buffer) {
		_communicableStateContext.handle(buffer);
	}

	public void start(final int port) {
		Assert.assertNull(_workerThread);

		_workerThread = new Thread(new Runnable() {
			public void run() {
				try {
					_serverSocketChannel = ServerSocketChannel.open();
					_serverSocketChannel.socket().bind(new InetSocketAddress(port));

					for (StateContext s = new InternalServerContext(_queue, _serverSocketChannel, _communicableStateContext); true == s.handle(null););
				}
				catch (Exception e) {
				}

				_serverSocketChannel = null;
			}
		});

		_workerThread.start();
	}

	public void stop() {
		if (_workerThread == null)
			return;

		try {
			_serverSocketChannel.close();
			_workerThread.join();
			_workerThread = null;
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}

