#include "VideoDecoderImpl.h"

int VideoDecoderImpl::exec(uint8_t* src, int size, uint8_t* out) {
	av_frame_unref(_AVFrame);
	_AVPacket.data = src;
	_AVPacket.size = size;

	int got_output = 0;
	int len = avcodec_decode_video2(_AVCodecContext, _AVFrame, &got_output, &_AVPacket);

	if (len < 0)
		return -1;

	if (got_output > 0) {
		SwsContext* swsContext = sws_getContext(_AVCodecContext->width, _AVCodecContext->height, _AVCodecContext->pix_fmt, _AVCodecContext->width, _AVCodecContext->height, AV_PIX_FMT_BGRA, SWS_BILINEAR, NULL, NULL, NULL);
		sws_scale(swsContext, (const uint8_t* const*)_AVFrame->data, _AVFrame->linesize,0, _AVCodecContext->height, _tmpAVFrame->data, _tmpAVFrame->linesize);
		memcpy(out, _tmpAVFrame->data[0], _AVCodecContext->width * _AVCodecContext->height * 4);
		sws_freeContext(swsContext);
		return len;
	}

	return -1;
}
