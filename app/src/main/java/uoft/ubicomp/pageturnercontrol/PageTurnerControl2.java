package uoft.ubicomp.pageturnercontrol;

/**
 * Created by Mingming on 8/30/2018.
 */
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class PageTurnerControl2 extends Activity implements View.OnTouchListener {
    private final String TAG = PageTurnerControl2.this.getClass().getSimpleName();
    ImageView imageView_left;
    ImageView imageView_right;
    ImageView imageView_arrow;

    private GestureDetectorCompat mDetector_left;
    private GestureDetectorCompat mDetector_right;
    private GestureDetectorCompat mDetector_arrow;

    private int mWidth = 1440;

    private enum Direction {left, right, no}

    Direction mCurrent = Direction.no;

    String message2send = "";

    TCPClient mClient = null;
    String IP = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Bundle b = getIntent().getExtras();
        IP = b.getString("IP");

        new TCPConnectionTask().execute("");

        imageView_left = (ImageView)findViewById(R.id.imageView_left);
        imageView_left.setOnTouchListener(this);
        imageView_right = (ImageView)findViewById(R.id.imageView_right);
        imageView_right.setOnTouchListener(this);

        imageView_arrow = (ImageView)findViewById(R.id.imageView_arrow);
        imageView_arrow.setVisibility(View.INVISIBLE);
        imageView_arrow.setOnTouchListener(this);

        mDetector_left = new GestureDetectorCompat(this,new MyGestureListenerLeft());
        mDetector_right = new GestureDetectorCompat(this,new MyGestureListenerRight());
        mDetector_arrow = new GestureDetectorCompat(this,new MyGestureListenerOnArrow());
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

        switch(view.getId()){
            case R.id.imageView_left:
                mDetector_left.onTouchEvent(motionEvent);
                break;
            case R.id.imageView_right:
                mDetector_right.onTouchEvent(motionEvent);
                break;
            case R.id.imageView_arrow:
                mDetector_arrow.onTouchEvent(motionEvent);
                break;
            default:
                break;
        }
        return true;
    }


    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListenerLeft extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG,"left onDown: ");
            // don't return false here or else none of the other
            // gestures will work
            message2send = "LEFT TOUCH\\n";
            mClient.sendMessage(message2send);
            mClient.stopClient();
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "left: onSingleTapConfirmed: ");
            message2send = "TOUCH RELEASED\\n";
            mClient.sendMessage(message2send);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "left: onLongPress: ");
            imageView_arrow.setImageResource(R.drawable.righarrow);
            imageView_arrow.setVisibility(View.VISIBLE);
            mCurrent = Direction.right;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "left onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i(TAG, "left: onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "left: onFling: ");
            return true;
        }
    }

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListenerRight extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG,"right: onDown: ");
            // don't return false here or else none of the other
            // gestures will work
            //"RIGHT TOUCH\n"
            message2send = "RIGHT TOUCH\\n";
            mClient.sendMessage(message2send);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "right: onSingleTapConfirmed: ");
            //"TOUCH RELEASED\n"
            message2send = "TOUCH RELEASED\\n";
            mClient.sendMessage(message2send);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "right: onLongPress: ");
            imageView_arrow.setImageResource(R.drawable.leftarrow);
            imageView_arrow.setVisibility(View.VISIBLE);
            mCurrent = Direction.left;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "right: onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i(TAG, "right: onScroll: ");

            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "right: onFling: ");
            return true;
        }
    }


    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListenerOnArrow extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG,"arrow: onDown: ");
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "arrow: onSingleTapConfirmed: ");
            //"TOUCH RELEASED\n"
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "arrow: onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "arrow: onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i(TAG, "coordinate: x: " + e2.getX() + " , y: " + e2.getY());
            //Log.i(TAG, "right: onScroll: x: " + distanceX + ", y: " + distanceY);
            float currentX = e2.getX();
            if(mCurrent == Direction.right)
            {
                if(currentX > mWidth / 8.0 * 7.0){
                    message2send = "LEFT TURN END\\n";
                }
                else if(currentX > mWidth / 4.0 * 3.0 && currentX < mWidth / 8.0 * 7.0 ){
                    message2send = "LEFT TURN 3 PAGE\\n";
                }
                else if(currentX < mWidth / 4.0 * 3.0 && currentX > mWidth / 2.0)
                {
                    message2send = "LEFT TURN 2 PAGE\\n";
                }
                else if(currentX > mWidth / 4.0 && currentX < mWidth / 2.0){
                    message2send = "LEFT TURN 1 PAGE\\n";
                }
                else if(currentX < mWidth / 4.0){
                    return true;
                }

            }
            else if(mCurrent == Direction.left)
            {
                if(currentX > mWidth / 4.0 * 3.0){
                    return true;
                }
                else if(currentX < mWidth / 4.0 * 3.0 && currentX > mWidth / 2.0)
                {
                    message2send = "RIGHT TURN 1 PAGE\\n";
                }
                else if(currentX > mWidth / 4.0 && currentX < mWidth / 2.0){
                    message2send = "RIGHT TURN 2 PAGE\\n";
                }
                else if(currentX < mWidth / 4.0 && currentX > mWidth / 8.0){
                    message2send = "RIGHT TURN 3 PAGE\\n";
                }
                else if(currentX < mWidth / 8.0){
                    message2send = "RIGHT TURN END\\n";
                }
            }

            mClient.sendMessage(message2send);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "arrow: onFling: ");
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
