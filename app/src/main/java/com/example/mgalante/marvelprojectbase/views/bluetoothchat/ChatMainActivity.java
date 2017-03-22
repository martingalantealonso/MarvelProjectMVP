package com.example.mgalante.marvelprojectbase.views.bluetoothchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.control.services.BluetoothChatService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    @Bind(R.id.btn_bluetooth)
    ImageView mBluetoothBtn;
    @Bind(R.id.btn_visibility)
    ImageView mVisibilityBtn;
    @Bind(R.id.btn_device_list)
    ImageView mDeviceBtn;
    @Bind(R.id.btn_settings)
    ImageView mSettingBtn;

    private Animatable cursiveAvd;
    private boolean isVisibilityChecked;
    private BluetoothAdapter mBluetoothAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        ButterKnife.bind(this);

        cursiveAvd = ((Animatable) ((ImageView) findViewById(R.id.cursiveIcon)).getDrawable());
        cursiveAvd.start();

        mBluetoothBtn.setOnClickListener(this);
        mDeviceBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);
        mVisibilityBtn.setOnClickListener(this);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothBtn.setImageResource(R.drawable.asl_trimclip_bluetooth_disabled);
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_bluetooth:
                Log.i("TAG", "btn_bluetooth");
                if (mBluetoothAdapter != null) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                        // Otherwise, setup the chat session
                    } else {
                        //setupChat();
                        mBluetoothAdapter.disable();
                        mBluetoothBtn.setImageResource(R.drawable.asl_trimclip_bluetooth_disabled);
                    }
                }
                //Toast.makeText(getApplicationContext(), "btn_bluetooth", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_visibility:
                Log.i("TAG", "btn_visibility");
                //Toast.makeText(getApplicationContext(),"btn_visibility",Toast.LENGTH_LONG).show();
                ensureDiscoverable();
                break;
            case R.id.btn_device_list:
                Log.i("TAG", "btn_device_list");
                Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                //Toast.makeText(getApplicationContext(), "btn_device_list", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_settings:
                Log.i("TAG", "btn_settings");
                Toast.makeText(getApplicationContext(), "btn_settings", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void ensureDiscoverable() {
        isVisibilityChecked = !isVisibilityChecked;
        final int[] stateSet = {android.R.attr.state_checked * (isVisibilityChecked ? 1 : -1)};
        mVisibilityBtn.setImageState(stateSet, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                    intent.putExtra("data",data);
                    intent.putExtra("state",true);
                    startActivityForResult(intent,4);
                    //connectDevice(data, true);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                   mBluetoothBtn.setImageResource(R.drawable.asl_trimclip_bluetooth);
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
}
