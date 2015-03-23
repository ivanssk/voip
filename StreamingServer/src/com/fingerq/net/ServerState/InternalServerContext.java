package com.fingerq.net.ServerState;

import java.nio.channels.ServerSocketChannel;
import java.nio.Buffer;
import java.util.concurrent.LinkedBlockingQueue;

import com.fingerq.pattern.state.State;
import com.fingerq.pattern.state.StateContext;

public class InternalServerContext extends StateContext {
	public InternalServerContext(LinkedBlockingQueue <Buffer> queue, ServerSocketChannel serverSocketChannel, StateContext s) {
		super(new AcceptableState(queue, serverSocketChannel, s));
	}
}

