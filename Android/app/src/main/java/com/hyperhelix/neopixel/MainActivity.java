//package com.hyperhelix.neopixel;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}
package com.hyperhelix.neopixel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    public final String TAG = "Main";

    private int iR = 0x6e;
    private int iG = 0x00;
    private int iB = 0x7e;
    private SeekBar sbR;
    private SeekBar sbG;
    private SeekBar sbB;
    private Button bColor;
    private SeekBar sbI;
    private TextView status;
    private Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.textStatus);

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectService();
            }
        });
        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                disconnectService();
            }
        });

        bColor = (Button) findViewById(R.id.color);

        sbR = (SeekBar) findViewById(R.id.seekBarR);
        sbR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBarR) {
                Log.d("SeekbarR","onStopTrackingTouch ");
                int progress = seekBarR.getProgress();
                String p = String.valueOf(progress);

                TextView debug = (TextView) findViewById(R.id.textR);
                debug.setText("R: "+p);
                bt.sendMessage("r"+p);

                iR = progress;
                bColor.setBackgroundColor(Color.rgb(iR, iG, iB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarR) {
                Log.d("SeekbarR","onStartTrackingTouch ");
            }

            @Override
            public void onProgressChanged(SeekBar seekBarR, int progress, boolean fromUser) {
                //Log.d("SeekbarR", "onProgressChanged " + progress);
            }
        });

        sbG = (SeekBar) findViewById(R.id.seekBarG);
        sbG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBarG) {
                Log.d("SeekbarG","onStopTrackingTouch ");
                int progress = seekBarG.getProgress();
                String p = String.valueOf(progress);

                TextView debug = (TextView) findViewById(R.id.textG);
                debug.setText("G: "+p);
                bt.sendMessage("g"+p);

                iG = progress;
                bColor.setBackgroundColor(Color.rgb(iR, iG, iB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarG) {
                Log.d("SeekbarG","onStartTrackingTouch ");
            }

            @Override
            public void onProgressChanged(SeekBar seekBarG, int progress, boolean fromUser) {
                //Log.d("SeekbarG", "onProgressChanged " + progress);
            }
        });

        sbB = (SeekBar) findViewById(R.id.seekBarB);
        sbB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBarB) {
                Log.d("SeekbarB","onStopTrackingTouch ");
                int progress = seekBarB.getProgress();
                String p = String.valueOf(progress);

                TextView debug = (TextView) findViewById(R.id.textB);
                debug.setText("B: "+p);
                bt.sendMessage("b" + p);

                iB = progress;
                bColor.setBackgroundColor(Color.rgb(iR, iG, iB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarB) {
                Log.d("SeekbarB","onStartTrackingTouch ");
            }

            @Override
            public void onProgressChanged(SeekBar seekBarB, int progress, boolean fromUser) {
                //Log.d("SeekbarB", "onProgressChanged " + progress);
            }
        });

        sbI = (SeekBar) findViewById(R.id.seekBarI);
        sbI.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBarI) {
                Log.d("SeekbarI", "onStopTrackingTouch ");
                int progress = seekBarI.getProgress();
                String p = String.valueOf(progress);

                bt.sendMessage("i" + p);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarB) {
                Log.d("SeekbarB", "onStartTrackingTouch ");
            }

            @Override
            public void onProgressChanged(SeekBar seekBarB, int progress, boolean fromUser) {
                //Log.d("SeekbarB", "onProgressChanged " + progress);
            }
        });

        sbR.setProgress(iR);
        sbG.setProgress(iG);
        sbB.setProgress(iB);

        sbI.setProgress(0x1f);

        bColor.setBackgroundColor(Color.rgb(iR, iG, iB));

        findViewById(R.id.animation).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.sendMessage("a");
            }
        });

        findViewById(R.id.low).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.sendMessage("l");
                sbI.setProgress(0x1f);
            }
        });

        findViewById(R.id.high).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.sendMessage("h");
                sbI.setProgress(0xff);
            }
        });

        bt = new Bluetooth(this, mHandler);
        connectService();

    }

    public void connectService() {
        try {
            status.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-06");
                Log.d(TAG, "Btservice started - listening");
                status.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                status.setText("Bluetooth is not enabled");
            }
        } catch(Exception e) {
            Log.e(TAG, "Unable to start bt ",e);
            status.setText("Unable to connect " +e);
        }
    }
    public void disconnectService() {
        try {
            status.setText("Disconnecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.stop();
                status.setText("Disconnected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                status.setText("Bluetooth is not enabled");
            }
        } catch(Exception e) {
            Log.e(TAG, "Unable to stop bt ",e);
            status.setText("Unable to disconnect " +e);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
            }
        }
    };
}
