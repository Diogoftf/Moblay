package com.example.moblay;

import android.content.Context;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, VideoController.MediaPlayerControl,
        GestureOverlayView.OnGesturePerformedListener, SensorEventListener {

    private GestureOverlayView gestureOverlayView = null;
    private GestureLibrary gestureLibrary = null;

    private SurfaceView _surfaceView;
    private RelativeLayout _relLay;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private String videoPath;
    private ArrayList<String> videosPaths;
    private VideoController mediaController;
    private Handler handler;
    private Runnable run;
    private int position;
    private boolean stop = false;
    private SensorManager mgr;
    private Sensor proximity;
    private Vibration vib;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        GestureOverlayView gestureOverlayView = (GestureOverlayView)findViewById(R.id.gesture);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
        mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        proximity = mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (!gestureLibrary.load()) {
            finish();
        }

        vib = new Vibration();

        handler = new Handler();

        videosPaths = getIntent().getExtras().getStringArrayList("paths");
        position = getIntent().getIntExtra("position", 0);

        mediaController = new VideoController(this);

        _relLay = (RelativeLayout) findViewById(R.id.relLay);

        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = _surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        mediaPlayer = new MediaPlayer();

        _relLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaController != null){
                    mediaController.show();
                }
                return false;
            }
        });

    }

    /* Initialise class or instance variables. */
    private void init(Context context)
    {
        if(gestureLibrary == null)
        {
            // Load custom gestures from gesture.txt file.
            gestureLibrary = GestureLibraries.fromRawResource(context, R.raw.gesture);

            if(!gestureLibrary.load())
            {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setMessage("Custom gesture file load failed.");
                alertDialog.show();

                finish();
            }
        }

        if(gestureOverlayView == null)
        {
            gestureOverlayView = (GestureOverlayView)findViewById(R.id.gesture);
        }
    }

    @Override
    protected void onPause() {
        mgr.unregisterListener(this, proximity);
        mediaPlayer.pause();
        super.onPause();
        Log.d("SCREEN", "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SCREEN", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SCREEN", "onStop");
    }

    @Override
    protected void onResume() {
        mgr.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
        mediaPlayer.start();
        Log.d("SCREEN", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SCREEN", "onDestroy");
        releaseMediaPlayer();
        handler.removeCallbacks(run);
    }

    private void releaseMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initMediaPlayer(position);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setVideoSize();
        mediaPlayer.start();

        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(_relLay);

        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibratePhone(getApplicationContext());
                nextVideo();

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibratePhone(getApplicationContext());
                previousVideo();
            }
        });


        handler.post(run = new Runnable() {

            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();

            }
        });
    }

    public void initMediaPlayer(int pos){

        // set mediaPlayer to surfaceView
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(VideoPlayerActivity.this);


        try {
            mediaPlayer.setDataSource(videosPaths.get(pos));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(VideoPlayerActivity.this,
                    "something wrong!\n" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        if(mediaPlayer == null){
            return 0;
        }
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        if(mediaPlayer == null){
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mediaPlayer == null){
            return false;
        }
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    private void setVideoSize() {

        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        // status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        screenHeight = screenHeight - statusBarHeight;

        // navigation bar height
        int navigationBarHeight = 0;
        resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        screenHeight = screenHeight - navigationBarHeight;


        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = _surfaceView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        _surfaceView.setLayoutParams(lp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setVideoSize();
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView o, Gesture g) {

        // Recognize the gesture and return prediction list.
        ArrayList<Prediction> predictionList = gestureLibrary.recognize(g);

        int size = predictionList.size();

        if(size > 0)
        {
            StringBuffer messageBuffer = new StringBuffer();

            // Get the first prediction.
            Prediction firstPrediction = predictionList.get(0);

            /* Higher score higher gesture match. */
            if(firstPrediction.score > 2)
            {
                String action = firstPrediction.name;

                messageBuffer.append("Your gesture match " + action);

                if(action.equals("Play_pause")) {
                    if(isPlaying()){
                        //stops the video
                        pause();
                    }
                    else {
                        //plays the video
                        start();
                    }
                    mediaController.updatePausePlay();
                } else if(action.equals("Next")) {
                    //go to next video
                    nextVideo();
                }
                else if(action.equals("Previous")) {
                    //go to previous video
                    previousVideo();
                }
            }else
            {
                messageBuffer.append("Your gesture do not match any predefined gestures.");
            }

            // Display a toast with related messages.
            Toast toast = Toast.makeText(getApplicationContext(), messageBuffer.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void nextVideo(){
        if(position < videosPaths.size()-1 ) {
            position++;
        } else {
            position = 0;
        }

        if(mediaPlayer != null) {
            mediaPlayer.reset();
        }

        initMediaPlayer(position);
    }

    public void previousVideo(){
        if (position <= 0) {
            position = videosPaths.size()-1;
        }
        else {
            position--;
        }

        if(mediaPlayer != null) {
            mediaPlayer.reset();
        }
        
        initMediaPlayer(position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0.0) {
            mediaPlayer.setVolume(0,0);
        } else {
            mediaPlayer.setVolume(1,1);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
