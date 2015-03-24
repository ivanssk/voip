#ifndef __FINGERQ_AUDIO_DECODER_IMPL_H__
#define __FINGERQ_AUDIO_DECODER_IMPL_H__

#include "Codec.h"

class AudioDecoderImpl : public AudioCodec {
public:
	int exec(uint8_t* src, int size, uint8_t* out);

protected:
	AVCodec* getAudioCodec() {
		return avcodec_find_decoder_by_name("libfdk_aac");
	}
};

#endif

