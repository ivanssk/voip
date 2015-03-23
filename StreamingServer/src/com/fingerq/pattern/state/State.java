package com.fingerq.pattern.state;

public interface State {
	public abstract boolean handle(StateContext stateContext, Object o);
}
