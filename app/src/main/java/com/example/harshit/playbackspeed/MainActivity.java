package com.example.harshit.playbackspeed;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static float[] speedArray = null;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private float[] speeds = new float[]{0.5f, 2.0f, 4.0f, 0.25f, 2.5f};
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        surfaceView = (SurfaceView)findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        Intent videoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        videoIntent.setType("video/*");
        startActivityForResult(videoIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getData() != null){
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(getApplicationContext(), data.getData());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int timeInSec = Integer.parseInt(time) / 1000;
                retriever.release();
                mediaPlayer.setDataSource(getApplicationContext(), data.getData());
                Intent drawIntent = new Intent(MainActivity.this, FingerPaintActivity.class);
                drawIntent.putExtra("duration", timeInSec);
                startActivity(drawIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("https://www.youtube.com/watch?v=fTvelC4ZZds"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (speedArray != null){
            mediaPlayer.setDisplay(holder);
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speedArray[index]));
            changeSpeed();
        }
    }

    private void changeSpeed() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (index <= speedArray.length - 1) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speedArray[index]));
                    changeSpeed();
                }
            }
        }, (long) (1000 / speedArray[index++]));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
