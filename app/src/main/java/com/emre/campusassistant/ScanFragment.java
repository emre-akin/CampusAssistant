package com.emre.campusassistant;
// TODO: 29.11.2018 11.43 
import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class ScanFragment extends Fragment implements RecyclerViewAdapter.OnItemClicked {

    private static final String TAG = "ScanFragment";

    private static final String DEVICE_NAME = "CampusAssistant";
    // TODO: 29.11.2018 Find specific UUID and insert here

    //vars
    private ArrayList mDeviceNames = new ArrayList<>();
//    private ArrayList mDevices = new ArrayList<ScanResult>();
    private SparseArray<BluetoothDevice> mDevices;
    private BluetoothGatt mConnectedGatt;
    boolean pressed = false;
    /**
     * If there is a random error make Context static!
     */
    private Context context2 = null;
    UUID HEART_RATE_SERVICE_UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    UUID HEART_RATE_MEASUREMENT_CHAR_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    UUID HEART_RATE_CONTROL_POINT_CHAR_UUID;
    UUID CLIENT_CHARACTERISTIC_CONFIG_UUID;

    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    public RecyclerViewAdapter recyclerViewAdapter;

    Button scanButton;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    /**Note About Fragments
     * In this method, you can assign variables, get Intent extras,
     and anything else that doesn't involve the View hierarchy*/
    public UUID convertFromInteger(int i) {

        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: created.");
        mDeviceNames.add("Teest");
        context2=getActivity();

        //HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D);
        //HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37);
        HEART_RATE_CONTROL_POINT_CHAR_UUID = convertFromInteger(0x2A39);
        CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902);


    }
    /**You can assign your View variables and do any graphical initialisations.*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_scan, container, false);


        btManager = (BluetoothManager) getActivity().getSystemService(v.getContext().BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        mDevices = new SparseArray<BluetoothDevice>();

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(v.getContext(), mDeviceNames);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        //Binding itemClick to adapter
        recyclerViewAdapter.setOnClick(ScanFragment.this);

        scanButton = v.findViewById(R.id.scanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pressed==false){
                    //Check for bluetooth
                    if (btAdapter != null && !btAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                        return;
                    }
                    //Clearing previous results
                    mDeviceNames.clear();
                    mDevices.clear();
                    recyclerViewAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onClick: pressed false.");
                    startScanning();
                    scanButton.setText("Scanning...");
                    pressed=true;
                }else{
                    Log.d(TAG, "onClick: pressed true.");
                    stopScanning();
                    scanButton.setText("Scan");
                    pressed=false;
                }
            }
        });

        return v;

    }
    // The onClick implementation of the RecyclerView item click
    @Override
    public void onItemClick(int position) {

        BluetoothDevice mDevice = mDevices.get(mDevices.keyAt(position));
        Toast.makeText(context2, "IT WORKS"+mDevices.keyAt(position), Toast.LENGTH_SHORT).show();
        mConnectedGatt = mDevice.connectGatt(context2,true,gattCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Bluetooth must be enabled.
         */
        if(btAdapter == null || !btAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            getActivity().onBackPressed();
            return;
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        stopScanning();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Disconnect from any device
        if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "addDevice:mDevices: "+mDevices);
        Log.d(TAG, "addDevice: Result Device: " + bluetoothDevice.getAddress());
        if(!mDevices.toString().contains(bluetoothDevice.getAddress())) {
            mDevices.put(bluetoothDevice.hashCode(), bluetoothDevice);
            mDeviceNames.add(bluetoothDevice.getAddress());
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult: Device found : "+result.getDevice().getAddress() + result.getDevice().getName());
            addDevice(result.getDevice());
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

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        

        //State-Machine
        private int mState = 0;

        private void reset() { mState = 0; }
        private void advance() { mState++; }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange: Initialized");
           //Log.d(TAG, "onConnectionStateChange: "+status+"->"+connectionState(newstate));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "onConnectionStateChange: Discovering services");
                /**
                 * Once successfully connected, discover all the services
                 */
                //gatt.getServices();
                gatt.discoverServices();
               // mHandler.sendMessage(Message.obtain(null, MSG_PROCESS,"Discovering Services"));
            }   else if(status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "onConnectionStateChange: BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED");
                /**
                 * If disconnect, send a massage to clear values
                 */
                //mHandler.sendEmptyMessage(MSG_CLEAR);
            }   else if (status != BluetoothGatt.GATT_SUCCESS) {
                //If there is a error, disconnect
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: " +status);
            //mHandler.sendMessage(Message.obtain(null, MSG_PROCESS, "Enabling "));
            reset();
            //enableNextSensor(gatt);

            BluetoothGattCharacteristic characteristic = gatt.getService(HEART_RATE_SERVICE_UUID)
                    .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID);
            /*BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            gatt.writeDescriptor(descriptor);*/
            characteristic.setValue(new byte[]{65, 66});
            gatt.writeCharacteristic(characteristic);
            gatt.disconnect();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            BluetoothGattCharacteristic characteristic = gatt.getService(HEART_RATE_SERVICE_UUID)
                    .getCharacteristic(HEART_RATE_CONTROL_POINT_CHAR_UUID);

            characteristic.setValue(new byte[]{1, 1});
            gatt.writeCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            //processData(characteristic.getValue());
        }
    };
}
