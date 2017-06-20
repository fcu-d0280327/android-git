package com.example.user.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FrameLayout frameContent;
    private FrameLayout frameWifi;
    private FrameLayout frameWifiOpenData;

    private GoogleMap mMap;
    /**
     * global variable for second tab
     * third tab is about message of wifi from open data
     */
    private WifiArrayAdapter wifi_adapter = null;
    private static final int LIST_PETS = 1;
    private ListView wifiView;

    /**
     * global variables for third tab
     * third tab is about message of wifi around the cellphone
     */
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private WifiManager wifiManager;
    ArrayList<HashMap<String, String>> wifiList = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    private static final Map<String, String> wifichannel = new HashMap<String, String>();
    static{
        wifichannel.put("2412", "2.4G Ch01");wifichannel.put("2417", "2.4G Ch02");
        wifichannel.put("2422", "2.4G Ch03");wifichannel.put("2427", "2.4G Ch04");
        wifichannel.put("2432", "2.4G Ch05");wifichannel.put("2437", "2.4G Ch06");
        wifichannel.put("2442", "2.4G Ch07");wifichannel.put("2447", "2.4G Ch08");
        wifichannel.put("2452", "2.4G Ch09");wifichannel.put("2457", "2.4G Ch10");
        wifichannel.put("2462", "2.4G Ch11");wifichannel.put("2467", "2.4G Ch12");
        wifichannel.put("2472", "2.4G Ch13");wifichannel.put("2484", "2.4G Ch14");
    }


    /**
     * navigation view for below three tab
     * map, wifi list and around wifi
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    frameContent.setVisibility(View.VISIBLE);
                    frameWifi.setVisibility(View.GONE);
                    frameWifiOpenData.setVisibility(View.GONE);
                    return true;

                case R.id.navigation_dashboard:
                    frameContent.setVisibility(View.GONE);
                    frameWifi.setVisibility(View.GONE);
                    frameWifiOpenData.setVisibility(View.VISIBLE);
                    frameWifiOpenData.removeAllViews();
                    listWifi();
                    return true;

                case R.id.navigation_notifications:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                    }
                    frameContent.setVisibility(View.GONE);
                    frameWifi.setVisibility(View.VISIBLE);
                    frameWifiOpenData.setVisibility(View.GONE);
                    frameWifi.removeAllViews();
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        frameContent = (FrameLayout) findViewById(R.id.content);
        frameWifi = (FrameLayout) findViewById(R.id.frameWifi);
        frameWifiOpenData = (FrameLayout) findViewById(R.id.frameWifiOpenData);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * below part is for third tab
     * scan avalible wifi around the cellphone and list it
     */
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
        wifiMessage.setTextSize(12);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(30, 0, 0, 0);
        wifiMessage.setText(message);
        frameWifi.addView(wifiMessage, lp);
    }

    private void setWifiList(){
        ListView listView = new ListView(this);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        fl.setMargins(25, 85, 0, 0);

        this.adapter = new SimpleAdapter(MainActivity.this, wifiList, R.layout.wifi_list, new String[] {"ssid","power","freq"}, new int[] {R.id.ssid, R.id.power, R.id.freq});
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        frameWifi.addView(listView, fl);
    }

    private void scanWifi(){
        wifiManager.startScan();
        List<ScanResult> mWifiScanResultList = wifiManager.getScanResults();
        List<WifiConfiguration> mWifiConfigurationList = wifiManager.getConfiguredNetworks();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        setWifiMessage("周圍Wifi: " + mWifiScanResultList.size() +
                ", 手機內存: " + mWifiConfigurationList.size() +
                "\n目前連線: " + wifiInfo.getSSID());
        wifiList.clear();
        for(int i = 0 ; i < mWifiScanResultList.size() ; i++ )
        {
            Log.d("info", mWifiScanResultList.get(i).SSID);
            HashMap item = new HashMap();
            item.put("ssid", "名稱:" + mWifiScanResultList.get(i).SSID);
            item.put("power","強度:" + new String(mWifiScanResultList.get(i).level+" dBm"));
            String wifichn = wifichannel.containsKey(new String(String.valueOf(mWifiScanResultList.get(i).frequency)))? wifichannel.get(new String(String.valueOf(mWifiScanResultList.get(i).frequency))):"5G";
            item.put("freq", "無線信道:" + wifichn);
            wifiList.add(item);
        }
        Collections.sort(wifiList, new Comparator<HashMap>() {
            @Override
            public int compare(HashMap lhs, HashMap rhs) {
                return ((String) lhs.get("power")).compareTo((String) rhs.get("power"));
            }
        });
        Log.d("power sort", wifiList.toString());
        setWifiList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
    }

    /**
     * below is for second tab
     * get wifi data from firebase where data is from opendata
     */
    private void listWifi(){
        wifiView = new ListView(this);
        wifi_adapter = new WifiArrayAdapter(this, new ArrayList<Wifi>());
        wifiView.setAdapter(wifi_adapter);
        frameWifiOpenData.addView(wifiView);
        getPetsFromFirebase();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_PETS: {
                    List<Wifi> wifis = (List<Wifi>)msg.obj;
                    refreshPetList(wifis);
                    for(Wifi wifi: wifis){
                        LatLng mark = new LatLng (Float.parseFloat(wifi.getLATITUDE()),Float.parseFloat(wifi.getLONGITUDE()));
                        mMap.addMarker(new MarkerOptions().position(mark).title(wifi.getName()));
                    }
                    break;
                }
            }
        }
    };

    private void refreshPetList(List<Wifi> pets) {
        wifi_adapter.clear();
        wifi_adapter.addAll(pets);
    }

    class FirebaseThread extends Thread {

        private DataSnapshot dataSnapshot;

        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public void run() {
            List<Wifi> wifis = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                DataSnapshot dsWifiName = ds.child("NAME");
                DataSnapshot dsWifiAddr = ds.child("ADDR");
                DataSnapshot dsLATITUDE = ds.child("LATITUDE");
                DataSnapshot dsLONGITUDE = ds.child("LONGITUDE");

                String wifiName = (String)dsWifiName.getValue();
                String wifiAddr = (String)dsWifiAddr.getValue();

                String latitude = (String)dsLATITUDE.getValue();
                String longitude = (String)dsLONGITUDE.getValue();

                Wifi aWifi = new Wifi();
                aWifi.setName(wifiName);
                aWifi.setAddr(wifiAddr);
                aWifi.setLATITUDE(latitude);
                aWifi.setLONGITUDE(longitude);
                wifis.add(aWifi);

                Log.v("wifi-data", wifiName + ";" + wifiAddr + ";" + latitude);
            }
            Message msg = new Message();
            msg.what = LIST_PETS;
            msg.obj = wifis;
            handler.sendMessage(msg);
        }
    }

    private void getPetsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new FirebaseThread(dataSnapshot).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase-error", databaseError.getMessage());
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng taiwan = new LatLng(24.1788209, 120.64451117);
        mMap.addMarker(new MarkerOptions().position(taiwan).title("Marker in Taiwan")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))); //second marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(taiwan, 14.0f));
    }

}
