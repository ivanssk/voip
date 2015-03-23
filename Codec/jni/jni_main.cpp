#include "VideoEncoderImpl.h"
#include "VideoDecoderImpl.h"
#include "AudioEncoderImpl.h"
#include "AudioDecoderImpl.h"

extern "C" {
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"

	JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeVideoEncoderInit(JNIEnv* env, jobject thiz, jint width, jint height);
	JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeVideoEncode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jbyteArray out);
	JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeVideoEncoderRelease(JNIEnv* env, jobject thiz);

	JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeVideoDecoderInit(JNIEnv* env, jobject thiz, jint width, jint height);
	JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeVideoDecode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jintArray out);
	JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeVideoDecoderRelease(JNIEnv* env, jobject thiz);

	JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeAudioEncoderInit(JNIEnv* env, jobject thiz);
	JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeAudioEncode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jbyteArray out);
	JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeAudioEncoderRelease(JNIEnv* env, jobject thiz);

	JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeAudioDecoderInit(JNIEnv* env, jobject thiz);
	JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeAudioDecode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jbyteArray out);
	JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeAudioDecoderRelease(JNIEnv* env, jobject thiz);
}

VideoCodec* g_video_encoder = NULL;
VideoCodec* g_video_decoder = NULL;
AudioCodec* g_audio_encoder = NULL;
AudioCodec* g_audio_decoder = NULL;

JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeVideoEncoderInit(JNIEnv* env, jobject thiz, jint width, jint height) {
	g_video_encoder = new VideoEncoderImpl();
	return g_video_encoder->initalize(width, height);
}

JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeVideoEncoderRelease(JNIEnv* env, jobject thiz) {
	if (g_video_encoder == NULL)
		return;

	g_video_encoder->release();
	delete g_video_encoder;
	g_video_encoder = NULL;
}

JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeVideoEncode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jbyteArray out) {
	jbyte* src = (jbyte*)env->GetByteArrayElements(in, 0);
	jbyte* dst = (jbyte*)env->GetByteArrayElements(out, 0);

	int ret = g_video_encoder->exec((uint8_t*)src, inbuf_size, (uint8_t*)dst);

	env->ReleaseByteArrayElements(in, src, 0);
	env->ReleaseByteArrayElements(out, dst, 0);
	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeVideoDecoderInit(JNIEnv* env, jobject thiz, jint width, jint height) {
	g_video_decoder = new VideoDecoderImpl();
	return g_video_decoder->initalize(width, height);
}

JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeVideoDecoderRelease(JNIEnv* env, jobject thiz) {
	if (g_video_decoder == NULL)
		return;

	g_video_decoder->release();
	delete g_video_decoder;
	g_video_decoder = NULL;
}

JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeVideoDecode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jintArray out) {
	jbyte* src = (jbyte*)env->GetByteArrayElements(in, 0);
	jint* dst = (jint*)env->GetIntArrayElements(out, 0);

	bool ret = g_video_decoder->exec((uint8_t*)src, inbuf_size, (uint8_t*)dst);

	env->ReleaseByteArrayElements(in, src, 0);
	env->ReleaseIntArrayElements(out, dst, 0);
	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeAudioEncoderInit(JNIEnv* env, jobject thiz) {
	g_audio_encoder = new AudioEncoderImpl();
	return g_audio_encoder->initalize();
}

JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeAudioEncode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jbyteArray out) {
	jbyte* src = (jbyte*)env->GetByteArrayElements(in, 0);
	jbyte* dst = (jbyte*)env->GetByteArrayElements(out, 0);

	int ret = g_audio_encoder->exec((uint8_t*)src, inbuf_size, (uint8_t*)dst);

	env->ReleaseByteArrayElements(in, src, 0);
	env->ReleaseByteArrayElements(out, dst, 0);
	return ret;
}

JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeAudioEncoderRelease(JNIEnv* env, jobject thiz) {
	if (g_audio_encoder == NULL)
		return;

	g_audio_encoder->release();
	delete g_audio_encoder;
	g_audio_encoder = NULL;
}

JNIEXPORT jboolean JNICALL Java_com_fingerq_Codec_nativeAudioDecoderInit(JNIEnv* env, jobject thiz) {
	g_audio_decoder = new AudioDecoderImpl();
	return g_audio_decoder->initalize();
}

JNIEXPORT jint JNICALL Java_com_fingerq_Codec_nativeAudioDecode(JNIEnv* env, jobject thiz, jbyteArray in, jint inbuf_size, jbyteArray out) {
	jbyte* src = (jbyte*)env->GetByteArrayElements(in, 0);
	jbyte* dst = (jbyte*)env->GetByteArrayElements(out, 0);

	int ret = g_audio_decoder->exec((uint8_t*)src, inbuf_size, (uint8_t*)dst);

	env->ReleaseByteArrayElements(in, src, 0);
	env->ReleaseByteArrayElements(out, dst, 0);
	return ret;
}

JNIEXPORT void JNICALL Java_com_fingerq_Codec_nativeAudioDecoderRelease(JNIEnv* env, jobject thiz) {
	if (g_audio_decoder == NULL)
		return;

	g_audio_decoder->release();
	delete g_audio_decoder;
	g_audio_decoder = NULL;
}
