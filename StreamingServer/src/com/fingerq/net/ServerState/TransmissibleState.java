package com.fingerq.net.ServerState;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;

import com.fingerq.pattern.state.State;
import com.fingerq.pattern.state.StateContext;

final class TransmissibleState implements State {
	private LinkedBlockingQueue <Buffer> _queue;
	private ServerSocketChannel _serverSocketChannel;
	private SocketChannel _socketChannel;
	private StateContext _serverStateContext;

	public TransmissibleState(LinkedBlockingQueue <Buffer> queue, ServerSocketChannel serverSocketChannel, SocketChannel socketChannel, StateContext s) {
		s.setState(new ConnectedState(queue));
		_serverStateContext = s;

		_queue = queue;
		_serverSocketChannel = serverSocketChannel;
		_socketChannel = socketChannel;
	}

	public boolean handle(StateContext stateContext, Object o) {
		try {
			for (Buffer buffer = _queue.poll(10L, TimeUnit.MILLISECONDS); buffer != null; buffer = _queue.poll(10L, TimeUnit.MILLISECONDS))
				_socketChannel.write((ByteBuffer)buffer);

			return _serverSocketChannel.isOpen();
		}
		catch (InterruptedException e) {
			return true;
		}
		catch (Exception e) {
			stateContext.setState(new AcceptableState(_queue, _serverSocketChannel, _serverStateContext));
			return stateContext.handle(o);
		}
	}
}


