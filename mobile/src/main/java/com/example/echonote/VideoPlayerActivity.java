package com.example.echonote;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class VideoPlayerActivity extends Activity {
    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerView = new PlayerView(this);
        setContentView(playerView);

        String filePath = getIntent().getStringExtra("mp4_path");
        if (filePath != null) {
            java.io.File file = new java.io.File(filePath);
            android.util.Log.d("VideoPlayerActivity", "Trying to play file: " + filePath + ", exists: " + file.exists() + ", readable: " + file.canRead());
            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, "File does not exist or is not readable: " + filePath, Toast.LENGTH_LONG).show();
                android.util.Log.e("VideoPlayerActivity", "File does not exist or is not readable: " + filePath);
                finish();
                return;
            }
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            MediaItem mediaItem = MediaItem.fromUri(contentUri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    android.util.Log.e("VideoPlayerActivity", "Playback error: " + error.getMessage(), error);
                    Toast.makeText(VideoPlayerActivity.this, "Playback error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "No file path provided", Toast.LENGTH_SHORT).show();
            android.util.Log.e("VideoPlayerActivity", "No file path provided");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }
}
