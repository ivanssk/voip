package com.fingerq.pattern.state;

public interface State {
	public boolean handle(StateContext stateContext, Object o);
}
