package uoft.ubicomp.pageturnercontrol;

/**
 * Created by Mingming on 8/30/2018.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PageTurnerControl extends Activity implements View.OnTouchListener {
    private final String TAG = PageTurnerControl.this.getClass().getSimpleName();
    ImageView imageView_leftright;

    private GestureDetectorCompat mDetector_leftright;
    private int mWidth = 1440;

    private enum Direction {left, right, no}

    Direction mCurrent = Direction.no;

    String message2send = "";

    TCPClient mClient = null;
    String IP = "";

    boolean longPressed = false;

    TextView tv_command;
    Bitmap mBitmap;
    Canvas mCanvas;
    Paint mPaint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Bundle b = getIntent().getExtras();
        IP = b.getString("IP");

        new TCPConnectionTask().execute("");

        imageView_leftright = (ImageView)findViewById(R.id.imageView_left);
        imageView_leftright.setOnTouchListener(this);
        mDetector_leftright = new GestureDetectorCompat(this,new MyGestureListenerLeftRight());

        tv_command = (TextView)findViewById(R.id.textView_Command);

//        Display currentDisplay = getWindowManager().getDefaultDisplay();
//        float dw = currentDisplay.getWidth();
//        float dh = currentDisplay.getHeight();

//        mBitmap = Bitmap.createBitmap((int)dw, (int)dh, Bitmap.Config.ARGB_8888);
//
//        mCanvas = new Canvas(mBitmap);
//        mPaint = new Paint();
//        mPaint.setColor(Color.BLACK);
//        imageView_leftright.setImageBitmap(mBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        message2send = "\\q";
        mClient.sendMessage(message2send);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Log.i(TAG, "touch UI: " + view.getId());
        mWidth = view.getWidth();
        float currentX = motionEvent.getX();
        float currentY = motionEvent.getY();

        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(currentX > mWidth / 2){
                    Log.d(TAG,"right onDown: ");
                    message2send = "RIGHT TOUCH\\n";
                    imageView_leftright.setImageResource(R.drawable.leftnew);
                    mCurrent = Direction.left;
                }
                else if(currentX < mWidth / 2){
                    Log.d(TAG,"Left onDown: ");
                    message2send = "LEFT TOUCH\\n";
                    imageView_leftright.setImageResource(R.drawable.rightnew);
                    mCurrent = Direction.right;
                }
                mClient.sendMessage(message2send);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "coordinate: x: " + motionEvent.getX() + " , y: " + motionEvent.getY());
                //Log.i(TAG, "right: onScroll: x: " + distanceX + ", y: " + distanceY);
                if(mCurrent == Direction.right)
                {
                    if(currentX > mWidth / 10.0 * 9.0){
                        message2send = "LEFT TURN END\\n";
                        tv_command.setText("Left Turn End");

                    }
                    else if(currentX > mWidth / 5.0 * 4.0 && currentX < mWidth / 10.0 * 9.0 ){
                        message2send = "LEFT TURN 3 PAGE\\n";
                        tv_command.setText("Left Turn 3 Pages");
                    }
                    else if(currentX < mWidth / 5.0 * 4.0 && currentX > mWidth / 5.0 * 3.0)
                    {
                        message2send = "LEFT TURN 2 PAGE\\n";
                        tv_command.setText("Left Turn 2 Pages");
                    }
                    else if(currentX > mWidth / 5.0 && currentX < mWidth / 5.0 * 3.0){
                        message2send = "LEFT TURN 1 PAGE\\n";
                        tv_command.setText("Left Turn 1 Page");
                    }
                    else if(currentX < mWidth / 5.0){
                        message2send = "";
                        tv_command.setText(" ");
                        return true;
                    }
                }
                else if(mCurrent == Direction.left)
                {
                    if(currentX > mWidth / 5.0 * 4.0){
                        message2send = "";
                        tv_command.setText(" ");
                        return true;
                    }
                    else if(currentX < mWidth / 5.0 * 4.0 && currentX > mWidth / 5.0 * 2.0)
                    {
                        message2send = "RIGHT TURN 1 PAGE\\n";
                        tv_command.setText("Right Turn 1 Page");
                    }
                    else if(currentX > mWidth / 5.0 && currentX < mWidth / 5.0 * 2.0){
                        message2send = "RIGHT TURN 2 PAGE\\n";
                        tv_command.setText("Right Turn 2 Pages");
                    }
                    else if(currentX < mWidth / 5.0 && currentX > mWidth / 10.0){
                        message2send = "RIGHT TURN 3 PAGE\\n";
                        tv_command.setText("Right Turn 3 Pages");
                    }
                    else if(currentX < mWidth / 10.0){
                        message2send = "RIGHT TURN END\\n";
                        tv_command.setText("Right Turn End");
                    }
                }
                Log.i(TAG, "message: " + message2send);
                mClient.sendMessage(message2send);
                break;
            case MotionEvent.ACTION_UP:
                message2send = "TOUCH RELEASED\\n";
                mClient.sendMessage(message2send);
                imageView_leftright.setImageResource(R.drawable.leftright);
                tv_command.setText(" ");
                break;
            default:
                break;
        }

        //mDetector_leftright.onTouchEvent(motionEvent);
        return true;
    }


    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListenerLeftRight extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "left: onSingleTapConfirmed: ");
            longPressed = false;
            message2send = "TOUCH RELEASED\\n";
            mClient.sendMessage(message2send);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "left: onLongPress: ");
            longPressed = true;
            float currentX = e.getX();
            if(currentX > mWidth / 2){
                Log.d(TAG,"long press right onDown: ");
                imageView_leftright.setImageResource(R.drawable.leftarrow);
                mCurrent = Direction.left;
            }
            else if(currentX < mWidth / 2){
                Log.d(TAG,"Left onDown: ");
                imageView_leftright.setImageResource(R.drawable.righarrow);
                mCurrent = Direction.right;
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "left onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
//            longPressed = false;
//            Log.i(TAG, "coordinate: x: " + e2.getX() + " , y: " + e2.getY());
//            //Log.i(TAG, "right: onScroll: x: " + distanceX + ", y: " + distanceY);
//            float currentX = e2.getX();
//            if(mCurrent == Direction.right)
//            {
//                if(currentX > mWidth / 8.0 * 7.0){
//                    message2send = "LEFT TURN END\\n";
//                }
//                else if(currentX > mWidth / 4.0 * 3.0 && currentX < mWidth / 8.0 * 7.0 ){
//                    message2send = "LEFT TURN 3 PAGE\\n";
//                }
//                else if(currentX < mWidth / 4.0 * 3.0 && currentX > mWidth / 2.0)
//                {
//                    message2send = "LEFT TURN 2 PAGE\\n";
//                }
//                else if(currentX > mWidth / 4.0 && currentX < mWidth / 2.0){
//                    message2send = "LEFT TURN 1 PAGE\\n";
//                }
//                else if(currentX < mWidth / 4.0){
//                    return true;
//                }
//
//            }
//            else if(mCurrent == Direction.left)
//            {
//                if(currentX > mWidth / 4.0 * 3.0){
//                    return true;
//                }
//                else if(currentX < mWidth / 4.0 * 3.0 && currentX > mWidth / 2.0)
//                {
//                    message2send = "RIGHT TURN 1 PAGE\\n";
//                }
//                else if(currentX > mWidth / 4.0 && currentX < mWidth / 2.0){
//                    message2send = "RIGHT TURN 2 PAGE\\n";
//                }
//                else if(currentX < mWidth / 4.0 && currentX > mWidth / 8.0){
//                    message2send = "RIGHT TURN 3 PAGE\\n";
//                }
//                else if(currentX < mWidth / 8.0){
//                    message2send = "RIGHT TURN END\\n";
//                }
//            }
//
//            Log.i(TAG, "message: " + message2send);
//            mClient.sendMessage(message2send);
//            return true;
            return false;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            longPressed = false;
            Log.d(TAG, "left: onFling: ");
            return true;
        }
    }

    public class TCPConnectionTask extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {
            mClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, IP);
            mClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //handle messages from server
        }
    }
}
