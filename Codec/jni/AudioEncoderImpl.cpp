#include "AudioEncoderImpl.h"

int AudioEncoderImpl::exec(uint8_t* src, int size, uint8_t* out) {
	av_init_packet(&_AVPacket);
	_AVPacket.data = NULL;
	_AVPacket.size = 0;

	memcpy(_samples, src, size);

	int got_output = 0;
	int ret = avcodec_encode_audio2(_AVCodecContext, &_AVPacket, _AVFrame, &got_output);

	if (got_output == 1) {
		int packet_size = _AVPacket.size;
		memcpy(out, _AVPacket.data, _AVPacket.size);
		av_free_packet(&_AVPacket);
		return packet_size;
	}

	return -1;
}
