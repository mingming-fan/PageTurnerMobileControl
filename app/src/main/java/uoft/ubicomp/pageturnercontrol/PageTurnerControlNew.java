package uoft.ubicomp.pageturnercontrol;

/**
 * Created by Mingming on 8/30/2018.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PageTurnerControlNew extends Activity implements View.OnTouchListener {
    private final String TAG = PageTurnerControlNew.this.getClass().getSimpleName();
    //ImageView imageView_Screen;
    DrawView mDrawView;

    private int mWidth = 1440;
    private enum Direction {left, right, no}
    Direction mCurrent = Direction.no;
    String message2send = "";
    TCPClient mClient = null;
    String IP = "";
    float cancelThreshold = 20;
    float leftrightThreshold = 10;
    boolean initialTouch = true;
    float initialTouchX = 0;
    float initialTouchY = 0;
    Vibrator mVibrator;
    float dw = 0;
    float dh = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        IP = b.getString("IP");

        setContentView(R.layout.activity_controller_new);
        //imageView_Screen = (ImageView)findViewById(R.id.imageView_left);
        //imageView_Screen.setOnTouchListener(this);

        new TCPConnectionTask().execute("");

        mDrawView = new DrawView(this);
        mDrawView.setBackgroundColor(Color.WHITE);
        mDrawView.setOnTouchListener(this);
        setContentView(mDrawView);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        dw = currentDisplay.getWidth();
        dh = currentDisplay.getHeight();

        cancelThreshold = dh * 0.15f;
        leftrightThreshold = dw * 0.15f;

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
        float currentX = motionEvent.getX();
        float currentY = motionEvent.getY();

        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
//                if(initialTouch){
//                    initialTouchX = motionEvent.getX();
//                    initialTouchY = motionEvent.getY();
//                    initialTouch = false;
//                }
                initialTouchX = motionEvent.getX();
                initialTouchY = motionEvent.getY();
                mDrawView.setParams(currentX, currentY, cancelThreshold, leftrightThreshold, dw);
                mDrawView.invalidate();
                message2send = "down\\n";
                mClient.sendMessage(message2send);
                message2send = "";
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "coordinate: x: " + motionEvent.getX() + " , y: " + motionEvent.getY());

                if(currentY <= initialTouchY - cancelThreshold || currentY >= initialTouchY + cancelThreshold){
                    //cancel
                    message2send = "cancel\\n";
                   //vibrate
                    if (mVibrator.hasVibrator()) {
                        mVibrator.vibrate(300);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentY <= initialTouchY - cancelThreshold || currentY >= initialTouchY + cancelThreshold){
                    //cancel
                    message2send = "cancel\\n";
                    mClient.sendMessage(message2send);
                }
                else{
                    if(currentX > initialTouchX + leftrightThreshold){
                        message2send = "right\\n";
                    }
                    else if (currentX < initialTouchX - leftrightThreshold){
                        message2send = "left\\n";
                    }
                    else{
                        message2send = "up\\n";
                    }
                }
//                initialTouch = true;
                mClient.sendMessage(message2send);
                //reset everything
                message2send = "";
                mDrawView.setParams(0, 0, 0, 0, 0);
                mDrawView.invalidate();
                break;
            default:
                break;
        }
        return true;
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
