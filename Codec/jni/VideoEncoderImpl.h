#ifndef __FINGERQ_VIDEO_ENCODER_H__
#define __FINGERQ_VIDEO_ENCODER_H__

#include "Codec.h"

class VideoEncoderImpl : public VideoCodec {
public:
	bool initalize(int width, int height);
	int exec(uint8_t* src, int size, uint8_t* out);

protected:
	AVCodec* getVideoCodec() {
		return avcodec_find_encoder(AV_CODEC_ID_H264);
	}

	AVPixelFormat getPixelFormat() {
		return AV_PIX_FMT_NV21;
	}
};

#endif


