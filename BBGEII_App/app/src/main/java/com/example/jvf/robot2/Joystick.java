package com.example.jvf.robot2;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Typeface;



public class Joystick extends AppCompatActivity {

    RelativeLayout layout_joystick;
    ImageView UP,LEFT,RIGHT,DOWN;
    TextView textView1, textView2,textView3, textView4,textView5,speedText;
    Typeface fontspeed;

    public int iY=0;
    public int iX=0;
    public int iDist=0;
    public int iAngle=0;
    public int iDir=0;
    private static final double Pi = 3.14159;

    JoyStickClass js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_joystick);

        /* Textview for debugging the joystick values

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);*/

        UP        =(ImageView)findViewById(R.id.speed_up);
        RIGHT     =(ImageView)findViewById(R.id.speed_right);
        LEFT      =(ImageView)findViewById(R.id.speed_left);
        DOWN      =(ImageView)findViewById(R.id.speed_down);
        speedText =(TextView)findViewById(R.id.speed);

        //We change the font of Speed TextView
        String fontPath ="digital.ttf";
        fontspeed = Typeface.createFromAsset(getAssets(),fontPath);
        speedText.setTypeface(fontspeed);

        //link joystick layout
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        //instantiation of the joystick class
        js = new JoyStickClass(getApplicationContext(),layout_joystick, R.drawable.stick);
        //Now we set our objects
        js.setStickSize(180,180);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(200);
        js.setStickAlpha(200);
        js.setOffset(90);
        js.setMinimumDistance(50);

        //setOnTouchListener: we detect an event on our layout with the onTouch function

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mevent) {
                js.DrawStick(mevent);


                //ACTION-DOWN: when a touch on the screen is detected
                //ACTION-MOVE: moving action
                //ACTION-UP: when a touch action end
                if ((mevent.getAction() == MotionEvent.ACTION_DOWN) || (mevent.getAction() == MotionEvent.ACTION_MOVE)) {

                    /* TextView are used for debugging
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));*/

                    iDist= (int) js.getDistance();
                    iY=js.getY();
                    iX=js.getX();
                    iAngle= (int) js.getAngle();
                    iDir = js.get8Direction();

                    if (iDist>255){
                        speedText.setText("255 kmh");
                    }
                    else{
                    speedText.setText(String.valueOf(iDist) + " kmh");}
                    //Function that draw arrows around the joystick
                    DrawSpeed(iDir);

                    //Calculate the direction in function of the stick angle
                    CalcDir(iX,iY,iAngle,iDist);

                } else if (mevent.getAction() == MotionEvent.ACTION_UP) {
                  /*  textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");*/

                    MainMenu.mBluetooth.strCommandeL = "l0\0";
                    MainMenu.mBluetooth.strCommandeR = "r0\0";
                    SetArrowInvisible();
                    speedText.setText(String.valueOf(0) + " kmh");
                }
                return true;
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); // nothing special

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
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //DrawSpeed: Function that draw arrows around the joystick in function of joystick zones
    //STICK_UP = 1
    //STICK_RIGHT = 3
    //STICK_DOWN = 5
    //STICK_LEFT = 7

    public void DrawSpeed(int iDirection){

        SetArrowInvisible();

        switch (iDirection){
            case 1:
                UP.setVisibility(View.VISIBLE);
                break;
            case 3:
                RIGHT.setVisibility(View.VISIBLE);
                break;
            case 5:
                DOWN.setVisibility(View.VISIBLE);
                break;
            case 7:
                LEFT.setVisibility(View.VISIBLE);
                break;
            default:
                SetArrowInvisible();
                break;
        }
    }


    public void SetArrowInvisible(){
        UP.setVisibility(View.INVISIBLE);
        LEFT.setVisibility(View.INVISIBLE);
        RIGHT.setVisibility(View.INVISIBLE);
        DOWN.setVisibility(View.INVISIBLE);
    }


    public void CalcDir(int iXj, int iYj, int iAng, int iDistance){

        if (iDistance>255){
            iDistance=255;
        }
        if ((iXj>=0) && (iYj<=0)){
            MainMenu.mBluetooth.strCommandeL= "l"+String.valueOf(iDistance)+"\0";
            MainMenu.mBluetooth.strCommandeR= "r"+(int)(-iDistance*(Math.sin(((2*iAng*Pi)/180)+(Pi/2))))+"\0";
        }
        else if ((iXj<0) && (iYj<=0)){
            MainMenu.mBluetooth.strCommandeR= "r"+String.valueOf(iDistance)+"\0";
            MainMenu.mBluetooth.strCommandeL= "l"+(int)(-iDistance*(Math.sin(((2*iAng*Pi)/180)+(Pi/2))))+"\0";
        }
        else if ((iXj>0) && (iYj>0)){
            MainMenu.mBluetooth.strCommandeL= "l"+String.valueOf(-iDistance)+"\0";
            MainMenu.mBluetooth.strCommandeR= "r"+(int)(iDistance*(Math.sin(((2*iAng*Pi)/180)+(Pi/2))))+"\0";
        }
        else if ((iXj<0) && (iYj>0)){
            MainMenu.mBluetooth.strCommandeR= "r"+String.valueOf(-iDistance)+"\0";
            MainMenu.mBluetooth.strCommandeL= "l"+(int)(iDistance*(Math.sin(((2*iAng*Pi)/180)+(Pi/2))))+"\0";
        }
    }
}
