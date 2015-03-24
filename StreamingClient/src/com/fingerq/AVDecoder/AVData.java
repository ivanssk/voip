package com.fingerq.AVDecoder;

import com.fingerq.pattern.AVVisitor;

public abstract class AVData {
	private byte [] _encoded_data;

	public AVData(byte [] data) {
		_encoded_data = data;
	}

	public abstract void accept(AVVisitor visitor);

	byte [] getData() {
		return _encoded_data;
	}
}

