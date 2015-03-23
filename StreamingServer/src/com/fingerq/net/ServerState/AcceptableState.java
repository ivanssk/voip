package com.fingerq.net.ServerState;

import java.nio.channels.ServerSocketChannel;
import java.nio.Buffer;
import java.util.concurrent.LinkedBlockingQueue;

import com.fingerq.pattern.state.State;
import com.fingerq.pattern.state.StateContext;

final class AcceptableState implements State {
	private ServerSocketChannel _serverSocketChannel;
	private LinkedBlockingQueue <Buffer> _queue;
	private StateContext _serverStateContext;

	public AcceptableState(LinkedBlockingQueue <Buffer> queue, ServerSocketChannel serverSocketChannel, StateContext s) {
		s.setState(new DisconnectedState());

		_serverStateContext = s;
		_serverSocketChannel = serverSocketChannel;
		_queue = queue;
	}

	public boolean handle(StateContext stateContext, Object o) {
		try {
			stateContext.setState(new TransmissibleState(_queue, _serverSocketChannel, _serverSocketChannel.accept(), _serverStateContext));
			return stateContext.handle(o);
		}
		catch (Exception e) {
			return false;
		}
	}
}
