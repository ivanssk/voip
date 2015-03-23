package com.fingerq.pattern.state;

public class StateContext {
	protected State _state;

	public StateContext() {
	}

	public StateContext(State initState) {
		_state = initState;
	}

	public synchronized void setState(State s) {
		_state = s;
	}

	public synchronized boolean handle(Object o) {
		return _state.handle(this, o);
	}
}
