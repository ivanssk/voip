#include "Codec.h"

bool VideoCodec::initalize(int width, int height) {
	if (_AVCodecContext != NULL) {
		avcodec_close(_AVCodecContext);
		_AVCodecContext = NULL;
	}

	if (_AVFrame != NULL) {
		av_frame_free(&_AVFrame);
		_AVFrame = NULL;
	}

	if (_tmpAVFrame != NULL) {
		av_frame_free(&_tmpAVFrame);
		_tmpAVFrame = NULL;
	}

	av_register_all();

	_AVCodec = this->getVideoCodec();

	if (NULL == _AVCodec)
		return false;

	_AVCodecContext = avcodec_alloc_context3(_AVCodec);
	if (NULL == _AVCodecContext)
		return false;

	if (_AVCodec->capabilities & CODEC_CAP_TRUNCATED)
		_AVCodecContext->flags |= CODEC_FLAG_TRUNCATED;

	_AVCodecContext->codec_type = AVMEDIA_TYPE_VIDEO;
	_AVCodecContext->width = width;
	_AVCodecContext->height = height;
	_AVCodecContext->time_base = (AVRational){1, 25};
	_AVCodecContext->gop_size = 10;
	_AVCodecContext->max_b_frames = 1;
	_AVCodecContext->pix_fmt = AV_PIX_FMT_YUV420P;

	av_opt_set(_AVCodecContext->priv_data, "preset", "superfast", 0);
	av_opt_set(_AVCodecContext->priv_data, "tune", "zerolatency", 0);

	if (avcodec_open2(_AVCodecContext, _AVCodec, NULL) < 0)
		return false;

	av_init_packet(&_AVPacket);

	_AVFrame = av_frame_alloc();
	_AVFrame->width = width;
	_AVFrame->height = height;
	_AVFrame->format = _AVCodecContext->pix_fmt;

	if (NULL == _AVFrame)
		return false;

	if (av_image_alloc(_AVFrame->data, _AVFrame->linesize, _AVCodecContext->width, _AVCodecContext->height, AV_PIX_FMT_YUV420P, 32) < 0)
		return false;

	_tmpAVFrame = av_frame_alloc();

	if (_tmpAVFrame == NULL)
		return false;

	AVPixelFormat pixel_format = this->getPixelFormat();

	return av_image_alloc(_tmpAVFrame->data, _tmpAVFrame->linesize, _AVCodecContext->width, _AVCodecContext->height, pixel_format, 32) >= 0;
}

bool AudioCodec::initalize() {
	if (_AVCodecContext != NULL) {
		avcodec_close(_AVCodecContext);
		_AVCodecContext = NULL;
	}

	if (_AVFrame != NULL) {
		av_frame_free(&_AVFrame);
		_AVFrame = NULL;
	}

	av_register_all();

	_AVCodec = this->getAudioCodec();

	if (NULL == _AVCodec)
		return false;

	_AVCodecContext = avcodec_alloc_context3(_AVCodec);
	if (_AVCodecContext == NULL)
		return false;

	_AVCodecContext->codec_id = AV_CODEC_ID_AAC;
	_AVCodecContext->bit_rate = 8000;
	_AVCodecContext->sample_fmt = AV_SAMPLE_FMT_S16;
	_AVCodecContext->profile = FF_PROFILE_AAC_LOW;
	_AVCodecContext->sample_rate = 8000;
	_AVCodecContext->channel_layout = AV_CH_LAYOUT_MONO;
	_AVCodecContext->channels = 1;

	int ret = 0;

	if ((ret = avcodec_open2(_AVCodecContext, _AVCodec, NULL)) < 0)
		return false;

	av_init_packet(&_AVPacket);

	_AVFrame = av_frame_alloc();

	if (NULL == _AVFrame)
		return false;

	if (_AVCodecContext->frame_size == 0)
		return true;

	_AVFrame->nb_samples = _AVCodecContext->frame_size;
	_AVFrame->format = _AVCodecContext->sample_fmt;
	_AVFrame->channel_layout = _AVCodecContext->channel_layout;

	int buffer_size = av_samples_get_buffer_size(NULL, _AVCodecContext->channels, _AVCodecContext->frame_size, _AVCodecContext->sample_fmt, 1);

	_samples = av_malloc(buffer_size);

	if (NULL == _samples)
		return false;

	return avcodec_fill_audio_frame(_AVFrame, _AVCodecContext->channels, _AVCodecContext->sample_fmt, (const uint8_t*)_samples, buffer_size, 0) >= 0;
}
