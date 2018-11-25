package com.emre.campusassistant;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class ScanFragment extends Fragment {

    private static final String TAG = "ScanFragment";

    //vars
    private ArrayList mDeviceNames = new ArrayList<>();
    boolean pressed = false;

    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    public RecyclerViewAdapter recyclerViewAdapter;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    /**Note About Fragments
     * In this method, you can assign variables, get Intent extras,
     and anything else that doesn't involve the View hierarchy*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: created.");
        mDeviceNames.add("Teest");

    }
    /**You can assign your View variables and do any graphical initialisations.*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_scan, container, false);


        btManager = (BluetoothManager) getActivity().getSystemService(v.getContext().BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(v.getContext(), mDeviceNames);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        Button scanButton = v.findViewById(R.id.scanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mDeviceNames.add("Test");
                Log.d(TAG, "onClick: "+ mDeviceNames);
                recyclerViewAdapter.notifyDataSetChanged();*/
                if(pressed==false){
                    Log.d(TAG, "onClick: pressed false.");
                    startScanning();
                    pressed=true;
                }else{
                    Log.d(TAG, "onClick: pressed true.");
                    stopScanning();
                    pressed=false;
                }
            }
        });

        return v;

    }
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult: Device found : "+result.getDevice().getAddress());
            mDeviceNames.add(result.getDevice().getAddress());
            recyclerViewAdapter.notifyDataSetChanged();
        }
    };

    public void startScanning(){
        Log.d(TAG, "startScanning: Started...");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning(){
        Log.d(TAG, "stopScanning: Stopping...");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }
}
