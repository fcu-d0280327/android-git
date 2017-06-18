package com.example.user.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//Wifi list
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FrameLayout frameContent;


    //wifi-part
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private WifiManager wifiManager;
    private List wifiSearch;
    private int wifiCount = 0;
    private static final Map wifichannel = new HashMap();
    static{
        wifichannel.put("2412", "2.4G Ch01");wifichannel.put("2417", "2.4G Ch02");
        wifichannel.put("2422", "2.4G Ch03");wifichannel.put("2427", "2.4G Ch04");
        wifichannel.put("2432", "2.4G Ch05");wifichannel.put("2437", "2.4G Ch06");
        wifichannel.put("2442", "2.4G Ch07");wifichannel.put("2447", "2.4G Ch08");
        wifichannel.put("2452", "2.4G Ch09");wifichannel.put("2457", "2.4G Ch10");
        wifichannel.put("2462", "2.4G Ch11");wifichannel.put("2467", "2.4G Ch12");
        wifichannel.put("2472", "2.4G Ch13");wifichannel.put("2484", "2.4G Ch14");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_map);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_list);
                    return true;
                case R.id.navigation_notifications:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                    }
                    frameContent.removeAllViews();
                    checkWifi();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        frameContent = (FrameLayout) findViewById(R.id.content);
    }

    private void checkWifi(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled() == false) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Remind");
            dialog.setMessage("Your Wi-Fi is not enabled, enable?");
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
                    frameContent.removeAllViews();
                    scanWifi();
                }
            });
            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    frameContent.removeAllViews();
                    setRefreshImage();
                }
            });
            dialog.show();
        }else{
            scanWifi();
        }
    }

    private void setRefreshImage(){
        ImageButton ib = new ImageButton(this);
        ib.setImageResource(R.drawable.icons8_refresh_48);
        ib.setBackground(null);
        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWifi();
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        frameContent.addView(ib, lp);
    }

    private void setWifiMessage(String message){
        TextView wifiMessage = new TextView(this);
        wifiMessage.setText(message);
        frameContent.addView(wifiMessage);
    }

    private void scanWifi(){
        wifiManager.startScan();
        List<ScanResult> mWifiScanResultList = wifiManager.getScanResults();
        List<WifiConfiguration> mWifiConfigurationList = wifiManager.getConfiguredNetworks();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        setWifiMessage("周圍Wifi: " + mWifiScanResultList.size() +
                ", 手機內存: " + mWifiConfigurationList.size() +
                ", 目前連線: " + wifiInfo.getSSID());
        for(int i = 0 ; i < mWifiScanResultList.size() ; i++ )
        {
            Log.d("info", mWifiScanResultList.get(i).SSID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
    }
}
