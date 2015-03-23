#ifndef __FINGERQ_AUDIO_ENCODER_IMPL_H__
#define __FINGERQ_AUDIO_ENCODER_IMPL_H__

#include "Codec.h"

class AudioEncoderImpl : public AudioCodec {
public:
	int exec(uint8_t* src, int size, uint8_t* out);

protected:
	AVCodec* getAudioCodec() {
		return avcodec_find_encoder(AV_CODEC_ID_AAC);
		//return avcodec_find_encoder_by_name("libfdk_aac");
		//return avcodec_find_encoder_by_name("aac");
	}
};

#endif
