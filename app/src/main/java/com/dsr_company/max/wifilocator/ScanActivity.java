package com.dsr_company.max.wifilocator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScanActivity extends Activity {
    private static final String TAG = "ScanActivity";
    WifiManager wifi;
    Button scanButton;
    Button saveButton;

    Map<String, List<Integer>> wifiMap = new HashMap<String, List<Integer>>();
    Intent wifiScanIntent;
    BroadcastReceiver wifiScanBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> results = wifi.getScanResults();

            Log.i(TAG, "WiFi result:" + results.size());
            for (ScanResult res : results) {
                List<Integer> history = wifiMap.get(res.BSSID);
                if (history == null) {
                    history = new ArrayList<Integer>();
                    wifiMap.put(res.BSSID, history);
                }
                history.add(res.level);
                Log.i(TAG, "WiFi:" + res.BSSID + ":" + res.level);
            }
            try {
                Thread.sleep(500);
                wifi.startScan();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    };

    private View.OnClickListener scanButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!wifi.isWifiEnabled()) {
                Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
                wifi.setWifiEnabled(true);
            }
            Log.i(TAG, "Start scanning");
            wifiMap.clear();
            wifiScanIntent = registerReceiver(wifiScanBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        }
    };

    private String listToString(List<Integer> integerList) {
        return Joiner.on(',').join(integerList);
    }

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.i(TAG, "Stop scanning");
            unregisterReceiver(wifiScanBroadcastReceiver);
            for (Map.Entry<String, List<Integer>> entry : wifiMap.entrySet()) {
                Log.i(TAG, entry.getKey() + " " + listToString(entry.getValue()));
                WiFiScanInfo info = new WiFiScanInfo(entry.getKey(), entry.getValue());
                Log.i(TAG, info.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vector_data);

        scanButton = (Button)findViewById(R.id.scanButton);
        saveButton = (Button)findViewById(R.id.saveButton);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        scanButton.setOnClickListener(scanButtonListener);
        saveButton.setOnClickListener(saveButtonListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
