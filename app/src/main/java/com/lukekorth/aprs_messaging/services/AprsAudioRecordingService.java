package com.lukekorth.aprs_messaging.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.Parser;

public class AprsAudioRecordingService extends Service {

    static {
        System.loadLibrary("multimon");
    }

    native void init();
    native void processBuffer(float[] buf, int length);

    private AudioRecorderThread mAudioRecorder;

    @Override
    public void onCreate() {
        super.onCreate();

        init();

        mAudioRecorder = new AudioRecorderThread();

        mAudioRecorder.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAudioRecorder.interrupt();
    }

    public void callback(byte[] data) {
        try {
            APRSPacket packet = Parser.parseAX25(data);
            Log.d("APRS", "Message: " + packet.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AudioRecorderThread extends Thread {

        // overlap for AFSK DEMOD (FREQSAMP / BAUDRATE)
        private static final int OVERLAP = 18;

        private AudioRecord mRecorder;
        private short[][] mBuffers = new short[256][8192];
        private float[] mDecodeBuffer = new float[16384];
        private int mDecodeBufferIndex = 0;

        AudioRecorderThread() {
            super("AudioRecorderThread");

            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 16384);
        }

        @Override
        public void run() {
            int i = 0;

            mRecorder.startRecording();

            while (!isInterrupted()) {
                short[] buffer = mBuffers[i++ % mBuffers.length];

                mRecorder.read(buffer, 0, buffer.length);

                decode(buffer);
            }

            mRecorder.stop();
        }

        private void decode(short[] s) {
            for (int i = 0; i < s.length; i++) {
                mDecodeBuffer[mDecodeBufferIndex++] = s[i] * (1.0f/32768.0f);
            }

            if (mDecodeBufferIndex > OVERLAP) {
                processBuffer(mDecodeBuffer, mDecodeBufferIndex - OVERLAP);
                System.arraycopy(mDecodeBuffer, mDecodeBufferIndex - OVERLAP, mDecodeBuffer, 0,
                        OVERLAP);
                mDecodeBufferIndex = OVERLAP;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
