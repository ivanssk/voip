package com.fingerq.net.ServerState;

import com.fingerq.pattern.state.State;
import com.fingerq.pattern.state.StateContext;

final class DisconnectedState implements State {
	public boolean handle(StateContext stateContext, Object o) {
		return true;
	}
}

