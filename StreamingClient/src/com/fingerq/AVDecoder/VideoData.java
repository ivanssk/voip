package com.fingerq.AVDecoder;

import com.fingerq.pattern.AVVisitor;

public class VideoData extends AVData {
	public VideoData(byte [] encoded_data) {
		super(encoded_data);
	}

	public void accept(AVVisitor visitor) {
		visitor.visit(this);
	}
}
