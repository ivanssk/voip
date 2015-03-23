#include "AudioDecoderImpl.h"

int AudioDecoderImpl::exec(uint8_t* src, int size, uint8_t* out) {
	_AVPacket.data = src;
	_AVPacket.size = size;

	int got_frame = 0;
	int ret = avcodec_decode_audio4(_AVCodecContext, _AVFrame, &got_frame, &_AVPacket);

	if (got_frame == 1) {
		int data_size = av_samples_get_buffer_size(NULL, _AVCodecContext->channels, _AVFrame->nb_samples, _AVCodecContext->sample_fmt, 1);
		memcpy(out, _AVFrame->data[0], data_size);
		return data_size;
	}

	return -1;
}



