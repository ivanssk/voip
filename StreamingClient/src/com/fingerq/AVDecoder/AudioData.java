package com.fingerq.AVDecoder;

import com.fingerq.pattern.AVVisitor;

public class AudioData extends AVData {
	public AudioData(byte [] encoded_data) {
		super(encoded_data);
	}

	public void accept(AVVisitor visitor) {
		visitor.visit(this);
	}

}
