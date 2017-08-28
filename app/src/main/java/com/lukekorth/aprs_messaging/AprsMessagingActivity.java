package com.lukekorth.aprs_messaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lukekorth.aprs_messaging.services.AprsAudioRecordingService;

public class AprsMessagingActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startService(new Intent(this, AprsAudioRecordingService.class));
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, AprsAudioRecordingService.class));
    }
}
