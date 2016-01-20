package com.example.jvf.robot2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/*
    to use of BT needs to add :
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    in AndroidManifest.xml
    The phone must :
    -> enable bluetooth
    -> pair the phone and the robot with the phone : code 1234
 */

public class BlueT {
    public static final int DEMANDE_AUTH_ACT_BT = 1;
    public static final int N_DEMANDE_AUTH_ACT_BT = 0;
    private static final String TAG = "BTT";

    BluetoothAdapter mbtAdapt; //BT adapter of the phone
    Activity mActivity; //main activity who instantiate blueT -> association
    boolean mbtActif = false;	//state of the association

    private Set<BluetoothDevice> mDevices; //liste of mDevices
    private BluetoothDevice[]mPairedDevices;// table of known devices

    int mDeviceSelected = -1; //the device choosen by the phone
    String[] mstrDeviceName;
    int miBlc = 0;				//used by connection
    boolean mbtConnected = false;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  // dummy UUID
    private BluetoothSocket mSocket;
    private OutputStream mOutStream;	//mSocket for communication
    private InputStream mInStream;		//mSocket for communication


    private Thread mThreadEnvoi =null;	//thread that receives data from device
    public String strCommandeL="";
    public String strCommandeR="";

    public BlueT(Activity Activity)
    {
        this.mActivity = Activity;
        this.Verif();
        mThreadEnvoi = new Thread(new Runnable() { //create Thread for reception
            @Override
            public void run() {

                while(true)
                {
                    if(mbtConnected == true) // reception of data when connected
                    {
                        envoi(strCommandeL); // WHAT WE SEND
                        envoi(strCommandeR);
                    }
                    try {
                        Thread.sleep(50, 0);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        //Log.i("IT", "mstrRecu");
                    }
                }
            }
        });
        mThreadEnvoi.start(); //start thread
    }

    public void Verif() // Verification of BT adapter
    {
        mbtAdapt = BluetoothAdapter.getDefaultAdapter(); // recover BT informations on adapter
        if(mbtAdapt == null) {
            Log.i(TAG, "Not presentt");
        }
        else {
            Log.i(TAG, "Present");
        }
    }

    public void connexion() // connection to device
    {
        this.Device_Connu(); //recover informations for each connected devices
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mActivity);//pop up off knoxn devices
        //        adBuilder.setTitle("device");
        //miDeviceDelected = mDeviceSelected;
        adBuilder.setSingleChoiceItems(mstrDeviceName, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeviceSelected = which;
                dialog.dismiss();
                tryconnect(); //connection to the chosen device
            }
        });

        AlertDialog adb = adBuilder.create();
        adb.show();
    }

    public void Device_Connu() // recover all known devices
    {
        this.mDevices = mbtAdapt.getBondedDevices(); //recover the devices in a tab
        this.miBlc = mDevices.size(); // number of known devices
        this.mstrDeviceName = new String[this.miBlc]; //table will be given to pop up menu
        this.miBlc = 0;
        for(BluetoothDevice dev : this.mDevices) {
            this.mstrDeviceName[this.miBlc] = dev.getName();
            this.miBlc = this.miBlc + 1;
        }
        this.mPairedDevices = (BluetoothDevice[]) this.mDevices.toArray(new BluetoothDevice[this.mDevices.size()]); //cast of set in array.
    }

    public void tryconnect()
    {
        try {
            this.mSocket =this.mPairedDevices[this.mDeviceSelected].createRfcommSocketToServiceRecord(MY_UUID); //connection to vhchoosen device via Socket, mUUID: id of BT on device of the target
            this.mSocket.connect();
            Toast.makeText(this.mActivity, "Connected", Toast.LENGTH_SHORT).show();
            this.mbtConnected = true;
            MainMenu.BlueTooth.setImageResource(R.drawable.bluetooth_active);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.mActivity, "Try again", Toast.LENGTH_SHORT).show();
            this.mbtConnected = false;
            MainMenu.BlueTooth.setImageResource(R.drawable.bluetooth_inactive);
            try {
                mSocket.close();
            }
            catch(Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public Boolean envoi(String strOrdre) // false -> error; true -> ok
    {
        try	{
            this.mOutStream = this.mSocket.getOutputStream(); //open output stream

            byte[] trame = strOrdre.getBytes();

            this.mOutStream.write(trame); //send frame via output stream
            this.mOutStream.flush();
            Log.i(TAG, "Send");
        }
        catch(Exception e2) {
            Log.i(TAG, "Error");
            tryconnect();
            try {
                this.mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mbtConnected = false;
        }
        return this.mbtConnected;
    }


    public void resetConnection()
    {
        if (mOutStream != null) {
            try {mOutStream.close();} catch (Exception e) {}
            mInStream = null;
        }

        if (mInStream != null) {
            try {

                mInStream.close();} catch (Exception e) {}
            mOutStream = null;
        }

        if (mSocket != null) {
            try {
                mSocket.close();
                Toast.makeText(this.mActivity, "Disconnected", Toast.LENGTH_SHORT).show();
                MainMenu.BlueTooth.setImageResource(R.drawable.bluetooth_inactive);
            }
            catch (Exception e) {}
            mSocket = null;
        }
        this.mbtConnected = false;

    }
}