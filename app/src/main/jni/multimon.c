#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <jni.h>
#include "multimon.h"

static const struct demod_param *dem[] = { &demod_afsk1200 };

#define NUMDEMOD (sizeof(dem)/sizeof(dem[0]))

static struct demod_state dem_st[NUMDEMOD];

void verbprintf(int verb_level, const char *fmt, ...)
{
    va_list args;
}

static void process_buffer(float *buf, unsigned int len)
{
  dem[0]->demod(dem_st+0, buf, len);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
  LOGD("called JNI_OnLoad");
  return JNI_VERSION_1_6;
}

void Java_com_lukekorth_aprs_1messaging_services_AprsAudioRecordingService_init(JNIEnv *env, jobject object) {
  static int sample_rate = -1;
  unsigned int i;
  unsigned int overlap = 0;

  LOGD("NUMDEMOD: %d", NUMDEMOD);

  for (i = 0; i < NUMDEMOD; i++) {
    LOGD(" DEM: %s", dem[i]->name);
    memset(dem_st+i, 0, sizeof(dem_st[i]));
    dem_st[i].dem_par = dem[i];
    if (dem[i]->init)
      dem[i]->init(dem_st+i);
    if (sample_rate == -1)
      sample_rate = dem[i]->samplerate;
    else if (sample_rate != dem[i]->samplerate) {
      /* fprintf(stdout, "\n"); */
      /* fprintf(stderr, "Error: Current sampling rate %d, " */
      /*         " demodulator \"%s\" requires %d\n", */
      /*         sample_rate, dem[i]->name, dem[i]->samplerate); */
      exit(3);
    }
    if (dem[i]->overlap > overlap)
      overlap = dem[i]->overlap;
  }
}

JNIEnv *env_global;
jobject *abp_global;

void Java_com_lukekorth_aprs_1messaging_services_AprsAudioRecordingService_processBuffer(JNIEnv *env, jobject object, jfloatArray fbuf, jint length) {
  env_global = env;
  abp_global = object;
  jfloat *jfbuf = (*env)->GetFloatArrayElements(env, fbuf, 0);
  process_buffer(jfbuf, length);
  (*env)->ReleaseFloatArrayElements(env, fbuf, jfbuf, 0);
}

void send_frame_to_java(unsigned char *bp, unsigned int len) {
  // prepare data array to pass to callback
  jbyteArray data = (*env_global)->NewByteArray(env_global, len);
  if (data == NULL) {
    LOGD("OOM on allocating data buffer");
    return;
  }
  (*env_global)->SetByteArrayRegion(env_global, data, 0, len, (jbyte*)bp);

  // get callback function
  jclass cls = (*env_global)->GetObjectClass(env_global, abp_global);
  jmethodID callback = (*env_global)->GetMethodID(env_global, cls, "callback", "([B)V");
  if (callback == 0)
    return;
  (*env_global)->CallVoidMethod(env_global, abp_global, callback, data);
  //(*env_global)->ReleaseByteArrayElements(env_global, data);
}
