package com.lukekorth.aprs_messaging;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import net.ab0oo.aprs.parser.Parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PacketDroidActivity extends Activity implements PacketCallback {

	public static String LOG_TAG = "MultimonDroid";

	// TODO this shouldn't be a constant. Use Context.getApplicationInfo().dataDir
	private String PIPE_PATH = "/data/data/com.lukekorth.aprs_messaging/pipe";

	private Button readButton, stopButton;
	private TextView tv;
	private ScrollView sv;

	private AudioBufferProcessor abp = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		readButton = findViewById(R.id.button1);
		readButton.setOnClickListener(onClickReadButtonListener);

		stopButton = findViewById(R.id.button2);
		stopButton.setOnClickListener(onClickStopButtonListener);

		tv = findViewById(R.id.textview);
		sv = findViewById(R.id.scrollView1);
	}

	private OnClickListener onClickReadButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startMonitor();

			//Log.d(LOG_TAG, "START: PipeReader");
			//startPipeRead();

			v.setEnabled(false);
			stopButton.setEnabled(true);
		}
	};

	private OnClickListener onClickStopButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stopMonitor();

			v.setEnabled(false);
			readButton.setEnabled(true);
		}
	};

	private void startMonitor() {
		if (abp == null) {
			abp = new AudioBufferProcessor(this);
			abp.start();
		} else {
			abp.startRecording();
		}
	}

	private void stopMonitor() {
		abp.stopRecording();
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(LOG_TAG, "GOT MESSAGE FROM FILE READER!");
			tv.append(msg.getData().getString("line") + "\n");
			sv.scrollTo(0, tv.getHeight());
		}
	};

	private void startPipeRead() {
		Thread t = new Thread(null, new Runnable() {
			public void run() {
				try {
					BufferedReader in = new BufferedReader(new FileReader(PIPE_PATH));
					String line;
					while (true) {
						line = in.readLine();
						if (line != null) {
							Log.d(LOG_TAG, line);
							Message msg = Message.obtain();
							msg.what = 0;
							Bundle bundle = new Bundle();
							bundle.putString("line", line);
							msg.setData(bundle);
							handler.sendMessage(msg);
						}

					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	// PacketCallback interface
	public void received(byte[] data) {
		Message msg = Message.obtain();
		msg.what = 0;
		Bundle bundle = new Bundle();
		String packet;
		try {
			packet = Parser.parseAX25(data).toString();
		} catch (Exception e) {
			packet = "raw " + new String(data);
		}
		bundle.putString("line", packet);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
}
