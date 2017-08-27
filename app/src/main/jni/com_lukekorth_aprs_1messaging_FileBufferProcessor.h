#include <jni.h>

#ifndef _Included_com_lukekorth_aprs_1messaging_AudioBufferProcessor
#define _Included_com_lukekorth_aprs_1messaging_AudioBufferProcessor
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_lukekorth_aprs_1messaging_AudioBufferProcessor_init
  (JNIEnv *, jobject);

JNIEXPORT void JNICALL Java_com_lukekorth_aprs_1messaging_AudioBufferProcessor_processBuffer
  (JNIEnv *, jobject, jfloatArray, jint);

JNIEXPORT void JNICALL Java_com_lukekorth_aprs_1messaging_AudioBufferProcessor_processBuffer2
  (JNIEnv *, jobject, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
