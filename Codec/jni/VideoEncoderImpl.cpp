#include "VideoEncoderImpl.h"

int VideoEncoderImpl::exec(uint8_t* src, int size, uint8_t* out) {
	av_init_packet(&_AVPacket);
	_AVPacket.data = NULL;
	_AVPacket.size = 0;

	SwsContext* swsContext = sws_getContext(_AVCodecContext->width, _AVCodecContext->height, AV_PIX_FMT_NV21, _AVCodecContext->width, _AVCodecContext->height, _AVCodecContext->pix_fmt, SWS_BILINEAR, NULL, NULL, NULL);
	avpicture_fill((AVPicture*)_tmpAVFrame, (const unsigned char*) src, AV_PIX_FMT_NV21, _AVCodecContext->width, _AVCodecContext->height);

	sws_scale(swsContext, (const uint8_t* const*)_tmpAVFrame->data, _tmpAVFrame->linesize, 0, _AVCodecContext->height, _AVFrame->data, _AVFrame->linesize);
	sws_freeContext(swsContext);

	int got_output = 0;
	int ret = avcodec_encode_video2(_AVCodecContext, &_AVPacket, _AVFrame, &got_output);

	if (ret < 0)
		return -1;

	if (got_output > 0) {
		int packet_size = _AVPacket.size;

		memcpy(out, _AVPacket.data, packet_size);
		av_free_packet(&_AVPacket);
		return packet_size;
	}

	return _AVPacket.size;
}

