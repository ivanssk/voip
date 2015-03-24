package com.fingerq.net;

import java.nio.Buffer;
import java.nio.ByteBuffer;

final public class Packet {
	static protected byte MAG1 = (byte)0x19;
	static protected byte MAG2 = (byte)0x79;
	static public byte VIDEO = 0x0;
	static public byte AUDIO = 0x1;

	private Packet() {
	}

	static public Buffer wrap(byte [] src, int src_size, byte video_or_audio) {
		return ByteBuffer.allocate(7 + src_size).put(new byte [] {MAG1, MAG2,
									  video_or_audio,
							       		  (byte) (src_size & 0xff),
							       		  (byte) ((src_size >> 8) & 0xff),
							       		  (byte) ((src_size >> 16) & 0xff),
							       		  (byte) ((src_size >> 24) & 0xff)},
							       		  0, 7).put(src, 0, src_size).flip();
	}
}
