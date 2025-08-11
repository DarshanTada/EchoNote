package com.example.echonote;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MOMDetailActivity extends Activity {

    private TextView titleView, dateView, tagView, readView;
    private Button playBtn;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_detail);

        titleView = (TextView) findViewById(R.id.tv_title);
        dateView = (TextView) findViewById(R.id.tv_date);
        tagView = (TextView) findViewById(R.id.tv_tag);
        readView = (TextView) findViewById(R.id.tv_read);
        playBtn = (Button) findViewById(R.id.btn_play);

        // Receive MOM details from intent
        titleView.setText(getIntent().getStringExtra("mom_title"));
        dateView.setText(getIntent().getStringExtra("mom_date"));
        tagView.setText(getIntent().getStringExtra("mom_tag"));
        boolean isRead = getIntent().getBooleanExtra("mom_read", false);
        readView.setText(isRead ? "Read" : "Unread");

        // TODO: Set actual audioFilePath from storage
        audioFilePath = getFilesDir().getAbsolutePath() + "/dummy_audio.3gp";

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });
    }

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to play audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playBtn.setText("Play");
            } else {
                mediaPlayer.start();
                playBtn.setText("Pause");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
