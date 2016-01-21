package com.example.jvf.robot2;

/**
 * Created by Guatteri Jonathan on 18/12/2015.

 * This class create and manage a joystick
 * It manages the onTouch on the layout where you want to put the joystick
 * It draws the stick always at the center of the touch
 * If you want to debug there are some functions like: getAngle, getDistance, getDirection, getX and getY
 * you need to call this functions on the activity of your joystick and put the values on TextViews
 *
 * In the Activity of your Joystick
 * Call the class: JoyStickClass js; for exemple
 * To instantiate the class: js = new JoyStickClass(Application Context, Layout of the Joystick, Drawable of the Stick);
 * To set parameters of your joystick: exemple js.setLayoutSize(250,250);
 * To get joystick values: for exemple  textView1.setText("X : " + String.valueOf(js.getX()));
 */

//What we need
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class JoyStickClass {
    //Variables and Constants
    //Stick positions
    public static final int STICK_NONE = 0;
    public static final int STICK_UP = 1;
    public static final int STICK_UPRIGHT = 2;
    public static final int STICK_RIGHT = 3;
    public static final int STICK_DOWNRIGHT = 4;
    public static final int STICK_DOWN = 5;
    public static final int STICK_DOWNLEFT = 6;
    public static final int STICK_LEFT = 7;
    public static final int STICK_UPLEFT = 8;

    //Stick and layout ALPHA(Opacity)
    private int STICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 0;

    //Big circle OFFSET
    private int OFFSET = 0;

    //Declaration of objects we are going to use
    private Context mContext;
    private ViewGroup mLayout;
    private LayoutParams params;
    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;

    //Stick parameters
    private int stick_width, stick_height;

    //Stick informations
    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    //Touch state
    private boolean touch_state = false;

    //First we have to get the stick image from the resources
    //Context allows access to app-specific resources
    //parameters: we need our context, layout and the id of the image

    public JoyStickClass(Context context,ViewGroup layout, int stick_id){

        mContext = context;

        //Open the resource image (stick)
        stick = BitmapFactory.decodeResource(mContext.getResources(),stick_id);
        //Now we get the parameters of the stick
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        //Instantiations of our objects
        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;

        //Layout parameters (big image)
        params = mLayout.getLayoutParams();
    }

    //When you call this function you Draw the stick
    //Motion event: used to report movement (finger) events
    public void DrawStick(MotionEvent mevent) {
        //get the x,y positions of the event(finger)
        //Set the center
        position_x = (int) (mevent.getX() - (params.width / 2));
        position_y = (int) (mevent.getY() - (params.height / 2));
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle =(float) cal_angle(position_x, position_y);

        //If a gesture pressure has started we draw the stick
        if (mevent.getAction() == MotionEvent.ACTION_DOWN) {
            //If a gesture has started in the background image
            if (distance <= (params.width / 2) - OFFSET) {
                //we set the position where we have to draw
                draw.position(mevent.getX(), mevent.getY());
                //we draw the stick
                draw();
                touch_state = true;
            }
        }else if(mevent.getAction() == MotionEvent.ACTION_MOVE && touch_state){
            //If a gesture has started in the background image
            if (distance <= (params.width / 2) - OFFSET){
                //we set the position where we have to draw
                draw.position(mevent.getX(), mevent.getY());
                //we draw the stick
                draw();
                // if the user drag the stick with the finger out of the image
                //the stick stay near the big image border
            }else if(distance >= (params.width/2)-OFFSET){
                float x =(float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y)))* ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x,position_y)))* ((params.width / 2) - OFFSET));
                x += (params.width/2);
                y += (params.height/2);
                draw.position(x,y);
                draw();
            }else{
                mLayout.removeView(draw);
            }
            //if pressed gesture is finished we remove the stick
        }else if (mevent.getAction() == MotionEvent.ACTION_UP){
            mLayout.removeView(draw);
            touch_state = false;
        }
    }


//we extends view class
    private class DrawCanvas extends View {
        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(stick, x, y, paint);
        }

    //we draw the stick always at the center of touch
        private void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);
        }
    }

    //Function that return the angle value
    private double cal_angle(float x, float y) {
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x))+360;
        return 0;
    }

    //we look for errors if not we draw
    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) { }
        mLayout.addView(draw);
    }

    //we return information about the touch only if there is a touch state
    public int getX(){
        if(distance > min_distance && touch_state){
            return position_x;
        }
        //if not we return zero
        return 0;
    }
    public int getY(){
        if(distance > min_distance && touch_state){
            return position_y;
        }
        //if not we return zero
        return 0;
    }


    //Function that return the Angle
    public float getAngle(){
        if(distance > min_distance && touch_state){
            return angle;
        }
        //if not we return zero
        return 0;
    }


    public float getDistance(){
        if(distance > min_distance && touch_state){
            return distance;
        }
        //if not we return zero
        return 0;
    }


    //function that manage the 8 directions of the stick
    public int get8Direction() {
        if (distance > min_distance && touch_state) {
            if (angle >= 247.5 && angle < 292.5) {
                return STICK_UP;
            } else if (angle >= 292.5 && angle < 337.5) {
                return STICK_UPRIGHT;
            } else if (angle >= 337.5 || angle < 22.5) {
                return STICK_RIGHT;
            } else if (angle >= 22.5 && angle < 67.5) {
                return STICK_DOWNRIGHT;
            } else if (angle >= 67.5 && angle < 112.5) {
                return STICK_DOWN;
            } else if (angle >= 112.5 && angle < 157.5) {
                return STICK_DOWNLEFT;
            } else if (angle >= 157.5 && angle < 202.5) {
                return STICK_LEFT;
            } else if (angle >= 202.5 && angle < 247.5) {
                return STICK_UPLEFT;
            }
        } else if (distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;

    }


    //We need to set the informations about: layout, opacity, stick, offset..
    public void setOffset(int offset){
        OFFSET = offset;
    }


    public void setStickAlpha(int alpha){
        STICK_ALPHA = alpha;
        //we paint the opacity on the stick
        paint.setAlpha(alpha);
    }

    public void setLayoutAlpha(int alpha){
        LAYOUT_ALPHA = alpha;
        //we paint the opacity on the layout
        mLayout.getBackground().setAlpha(alpha);
    }


    public void setStickSize(int width, int height){
        //open the stick
        stick = Bitmap.createScaledBitmap(stick,width,height,false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }


    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }


    public void setMinimumDistance(int minDistance) {
        min_distance = minDistance;
    }


}
