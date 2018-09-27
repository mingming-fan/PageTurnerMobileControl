package uoft.ubicomp.pageturnercontrol;

/**
 * Created by Mingming on 8/30/2018.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class PageTurnerControlNew extends Activity implements View.OnTouchListener {
    private final String TAG = PageTurnerControlNew.this.getClass().getSimpleName();

    DrawView mDrawView;
    String message2send = "";
    TCPClient mClient = null;
    String IP = "";
    float cancelThreshold = 20;
    float leftrightThreshold = 10;
    float initialTouchX = 0;
    float initialTouchY = 0;
    float previousTouchX = 0;
    float previousTouchY = 0;
    Vibrator mVibrator;
    float dw = 0;
    float dh = 0;
    final float threshold = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        IP = b.getString("IP");
        setContentView(R.layout.activity_controller_new);
        new TCPConnectionTask().execute("");

        mDrawView = new DrawView(this);
        mDrawView.setBackgroundColor(Color.BLACK);
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
                   //vibrate
                    if ((Math.abs(previousTouchX - currentX) > threshold ||  Math.abs(previousTouchY - currentY) > threshold ) && mVibrator.hasVibrator()) {
                        customVibratePattern("updown");
                    }
                }
                else{
                    if(currentX > initialTouchX + leftrightThreshold){
                        //vibrate
                        if ((Math.abs(previousTouchX - currentX) > threshold ||  Math.abs(previousTouchY - currentY) > threshold ) && mVibrator.hasVibrator()) {
                            customVibratePattern("leftright");
                        }
                    }
                    else if (currentX < initialTouchX - leftrightThreshold){
                        //vibrate
                        if ((Math.abs(previousTouchX - currentX) > threshold ||  Math.abs(previousTouchY - currentY) > threshold ) && mVibrator.hasVibrator()) {
                            customVibratePattern("leftright");
                        }
                    }
                    else{
                            mVibrator.cancel();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentY <= initialTouchY - cancelThreshold || currentY >= initialTouchY + cancelThreshold){
                    //cancel
                    message2send = "cancel\\n";
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
                mVibrator.cancel();
                mClient.sendMessage(message2send);
                //reset everything
                message2send = "";
                mDrawView.setParams(0, 0, 0, 0, 0);
                mDrawView.invalidate();
                break;
            default:
                break;
        }
        previousTouchX = currentX;
        previousTouchY = currentY;

        return true;
    }

    private void customVibratePattern(String direction) {
        // 0 : Start without a delay
        // 200 : Vibrate for 400 milliseconds
        // 200 : Pause for 200 milliseconds
        // 200 : Vibrate for 400 milliseconds
        long[] mVibratePattern = new long[]{0, 100, 100, 100, 100};
        long[] mVibratePattern2 = new long[]{0,500, 5, 500, 5};

        // -1 : Do not repeat this pattern
        // pass 0 if you want to repeat this pattern from 0th index
        if(direction.equals("updown")){
            mVibrator.vibrate(mVibratePattern2, 0);
        }
        else{
            mVibrator.vibrate(mVibratePattern, 0);
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
