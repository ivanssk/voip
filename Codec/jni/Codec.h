#ifndef __FINGERQ_CODEC_H__
#define __FINGERQ_CODEC_H__

#ifdef ANDROID

#include <jni.h>
extern "C" {
JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_Init(JNIEnv* env, jobject thiz, jint width, jint height);
JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_NALDecode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jintArray out);
JNIEXPORT jint JNICALL Java_com_fingerq_Codec_NALEncode(JNIEnv* env, jobject thiz, jbyteArray in, jint width, jint height, jbyteArray out);
JNIEXPORT void JNICALL Java_com_fingerq_Codec_Destroy(JNIEnv* env, jobject thiz);
}

#else
#define LOGD(...) 0;

#endif

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"
}

class VideoCodec {
public:
	VideoCodec() : _AVCodecContext(NULL), _AVCodec(NULL), _AVFrame(NULL), _tmpAVFrame(NULL) {
	}

	virtual ~VideoCodec() {
	}

	bool initalize(int width, int height);

	virtual void release() {
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
	}

	virtual int exec(uint8_t* src, int size, uint8_t* out) = 0;

protected:
	virtual AVCodec* getVideoCodec() = 0;
	virtual AVPixelFormat getPixelFormat() = 0;

	struct AVCodecContext* _AVCodecContext;
	struct AVCodec* _AVCodec;
	struct AVFrame* _AVFrame;
	struct AVFrame* _tmpAVFrame;
	struct AVPacket _AVPacket;
};

class AudioCodec {
public:
	AudioCodec() : _AVCodecContext(NULL), _AVCodec(NULL), _AVFrame(NULL), _samples(NULL) {
	}

	virtual ~AudioCodec() {
	}

	bool initalize();

	virtual void release() {
		if (_AVCodecContext != NULL) {
			avcodec_close(_AVCodecContext);
			_AVCodecContext = NULL;
		}

		if (_AVFrame != NULL) {
			av_frame_free(&_AVFrame);
			_AVFrame = NULL;
		}
	}

	virtual int exec(uint8_t* src, int size, uint8_t* out) = 0;

protected:
	virtual AVCodec* getAudioCodec() = 0;

	struct AVCodecContext* _AVCodecContext;
	struct AVCodec* _AVCodec;
	struct AVFrame* _AVFrame;
	struct AVPacket _AVPacket;

	void* _samples;
};
#endif
