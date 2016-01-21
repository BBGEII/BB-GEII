package com.example.jvf.robot2;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
public class MainMenu extends AppCompatActivity {

    static public BlueT mBluetooth;
    static public ImageButton BlueTooth;
    android.support.v7.app.ActionBar menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_menu);
        this.mBluetooth= new BlueT(this);
        BlueTooth = (ImageButton) findViewById(R.id.imageButtonBluetooth);

        //Display the logo on the ActionBar
        menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(false);
        menu.setDisplayHomeAsUpEnabled(false);
    }

    public void onClickJoystick(View v) {
        Intent myIntent = new Intent(this, Joystick.class);
        startActivity(myIntent);
    }
    public void onClickAccelerometer(View v) {
        Intent myIntent = new Intent(this, Accelerometer.class);
        startActivity(myIntent);
    }
    public void onClickBDD(View v) {
        Intent myIntent = new Intent(this, Interface.class);
        startActivity(myIntent);
    }

    public void onClickConnect(View v){

        if(this.mBluetooth.mbtConnected == false) {
            this.mBluetooth.connexion();
            MainMenu.mBluetooth.strCommandeR="r0\0";
            MainMenu.mBluetooth.strCommandeL="l0\0";
        }
        else{
            MainMenu.mBluetooth.strCommandeR="r0\0";
            MainMenu.mBluetooth.strCommandeL="l0\0";
            this.mBluetooth.resetConnection();
        }
    }


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
                MainMenu.mBluetooth.resetConnection();
                this.finish();
                return true;
            case R.id.action_credits:
                Intent myIntent = new Intent(this,Credits.class);
                startActivityForResult(myIntent, 0);
                return true;

        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
