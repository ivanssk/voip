package com.fingerq.pattern;

import com.fingerq.AVDecoder.VideoData;
import com.fingerq.AVDecoder.AudioData;

public interface AVVisitor {
	public void visit(VideoData videoData);
	public void visit(AudioData audioData);
}
