package com.example.jvf.robot2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class Accelerometer extends AppCompatActivity {

    //bluetooth variables
    public String mstrFrameMotL = "";
    public String mstrFrameMotR = "";
    // display results on layout

    private TextView mTxtAccY;
    private TextView mTxtTest;
    private TextView mTxtTest2;

    // acceleration vector
    float[] accelerometerVector = new float[3];
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    RelativeLayout layout_Gas;
    RelativeLayout layout_Brake;
    ImageButton ImButtGas;
    ImageButton ImButtBrake;
    boolean bTouchGas = false;
    boolean bTouchBrake = false;

    // sensor manager declaration
    SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // define accelerometer
        Sensor mAccelerometer;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        // object mSensorManager od class  SensorManager manage sensors
        mSensorManager = (SensorManager)getSystemService(Accelerometer.SENSOR_SERVICE);
        /*1st parameter : what object will receive informations given by the sensor
        * 2nd parameter : Sensor. TYPE_ACCELEROMETER kind of sensor
        * 3rd : delay between 2 refreshes SensorManager.SENSOR_DELAY_NORMAL
        */
        // We use accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorListener,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);


        //attributes are associated to text fields in the layout

        mTxtAccY = (TextView)findViewById(R.id.textAccY);
        mTxtTest = (TextView)findViewById(R.id.textViewtest);
        mTxtTest2 = (TextView)findViewById(R.id.textViewtest2);

        accelerometerVector[0]=0;
        accelerometerVector[1]=0;
        accelerometerVector[2]=0;

        //link gas and brake layouts
        layout_Gas = (RelativeLayout) findViewById(R.id.layout_accelerator);
        layout_Brake = (RelativeLayout) findViewById(R.id.layout_brake);
        //link gas and brake image buttons
        ImButtGas = (ImageButton) findViewById(R.id.imageGas);
        ImButtBrake = (ImageButton) findViewById(R.id.imageBrake);

        ImButtGas.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mTxtTest2.setText(mstrFrameMotL+mstrFrameMotR);
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Move forward
                        bTouchGas = true;
                        ImButtGas.setBackgroundResource(R.drawable.accelerator_back);
                    break;

                    case MotionEvent.ACTION_UP:
                        //stop
                        bTouchGas = false;
                        MotStop(bTouchGas,bTouchBrake);
                        ImButtGas.setBackgroundResource(R.drawable.accelerator);
                    break;
                }
                return true;
            }
        });

        ImButtBrake.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Move backward
                        bTouchBrake = true;
                        ImButtBrake.setBackgroundResource(R.drawable.brake_down);
                    break;

                    case MotionEvent.ACTION_UP:
                        //stop
                        bTouchBrake = false;
                        MotStop(bTouchGas,bTouchBrake);
                        ImButtBrake.setBackgroundResource(R.drawable.brake);
                    break;

                }
                return true;
            }
        });
    }


    // display data
    public void Position(float iAX, float iAY, float iAZ) {
        //For our application we only use the Y axis but the other axis X or Z are also available

        mTxtAccY.setText(" " + String.format("% 2.1f", iAY));
        //sensors give values between -10 and 10 so we convert them to adapt them for the arduino between : -255 and 255
        iAY = iAY * 25;

        if (iAY > 50) {
            //turn the phone on the right
            // gas at the same time    255 is the maximum speed
            if((bTouchGas == true)&&(bTouchBrake == false)) {
                //acceleration of motors on the left
                mstrFrameMotL = "l255\0";
                //acceleration of motors on the right
                mstrFrameMotR = "r" + Integer.toString(255 - (int)iAY) + "\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
            // brake at the same time
            else if((bTouchGas == false)&&(bTouchBrake == true)){
                //motors on the left move back
                mstrFrameMotL = "l-255\0";
                //motors on the right move back
                mstrFrameMotR = "r-" + Integer.toString(255 - (int)iAY) + "\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
            //brake and gas at the same time the turn faster
            else if((bTouchGas == true)&&(bTouchBrake == true)){
                //acceleration of the motors on the left
                mstrFrameMotL = "l" + Integer.toString((int)iAY) + "\0";
                //motors on the right move back
                mstrFrameMotR = "r-" + Integer.toString((int)iAY) + "\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
        }
        else if(iAY < -50) {
            //turn the phone on the left iAY < -50
            // gas at the same time
            if((bTouchGas == true)&&(bTouchBrake == false)) {
                //acceleration of motors on the left
                mstrFrameMotL = "l" + Integer.toString(255 -(int)Math.abs(iAY)) + "\0";
                //acceleration of motors on the right
                mstrFrameMotR = "r255\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
            // brake at the same time
            else if((bTouchGas == false)&&(bTouchBrake == true)){
                //motors on the left move back
                mstrFrameMotL = "l-" + Integer.toString(255 -(int)Math.abs(iAY)) + "\0";
                //motors on the right move back
                mstrFrameMotR = "r-255\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
            //brake and gas at the same time the turn faster
            else if((bTouchGas == true)&&(bTouchBrake == true)){
                //acceleration of the motors on the left
                mstrFrameMotL = "l-" + Integer.toString((int)Math.abs(iAY)) + "\0";
                //motors on the right move back
                mstrFrameMotR = "r" + Integer.toString((int)Math.abs(iAY)) + "\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }

        }

        else if((iAY >= -50)&&(iAY <= 50)){
            // moving forward without turning
            if((bTouchGas == true)&&(bTouchBrake == false)) {
                //acceleration of motors on the left
                mstrFrameMotL = "l255\0";
                //acceleration of motors on the right
                mstrFrameMotR = "r255\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
            // // moving backward without turning
            else if((bTouchGas == false)&&(bTouchBrake == true)){
                //motors on the left move back
                mstrFrameMotL = "l-255\0";
                //motors on the right move back
                mstrFrameMotR = "r-255\0";
                MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
                MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            }
            //Don't do anything between -50 and 50
            else{
                MotStop(bTouchGas, bTouchBrake);
            }
        }

    }

    public void MotStop(boolean bGas, boolean bBrake){
        //Test if no orders are given by the buttons
        if((bGas==false)&&(bBrake==false)){
            //stop
            mstrFrameMotL = "l0\0";
            mstrFrameMotR = "r0\0";
            MainMenu.mBluetooth.strCommandeL = mstrFrameMotL;
            MainMenu.mBluetooth.strCommandeR = mstrFrameMotR;
            mTxtTest.setText(mstrFrameMotL+mstrFrameMotR);
        }
    }
    /*
     *  create object able to receive information from sensor
     *  final : no derivation is possible (there is only 1 sensor)
     *  this syntax permits to declare an object of class SensorEventListener and at the same time instanciate it
     *  and declare the abstract method onSensorChanged
     */
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        // action if on sensor changes
        public void onSensorChanged(SensorEvent se) {
            if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerVector = se.values;
                Position(accelerometerVector[0], accelerometerVector[1], accelerometerVector[2]);
            }

        }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        } // not used
    };

    // close the applciation
    /* -> create button
	 * -> in properties, inform OnClick what method as to be lunched when the user click on this button
	 * -> create this method
	*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_close:
                mSensorManager.unregisterListener(mSensorListener);
                this.finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Accelerometer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jvf.robot2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Accelerometer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jvf.robot2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    @Override
    protected void onDestroy() {
        this.mSensorManager.unregisterListener(mSensorListener);
        MainMenu.mBluetooth.strCommandeR="d0\0";
        MainMenu.mBluetooth.strCommandeL="g0\0";
        super.onDestroy(); // nothing special


    }

}

//http://stackoverflow.com/questions/4617898/how-can-i-give-imageview-click-effect-like-a-button-in-android
