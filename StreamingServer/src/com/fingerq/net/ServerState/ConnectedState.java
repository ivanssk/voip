package com.fingerq.net.ServerState;

import java.nio.Buffer;
import java.util.concurrent.LinkedBlockingQueue;

import com.fingerq.pattern.state.State;
import com.fingerq.pattern.state.StateContext;

import junit.framework.Assert;

final public class ConnectedState implements State {
	private LinkedBlockingQueue <Buffer> _queue;

	public ConnectedState(LinkedBlockingQueue <Buffer> queue) {
		_queue = queue;
	}

	public boolean handle(StateContext stateContext, Object o) {
		try {
			_queue.put((Buffer) o);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		return true;
	}
}
