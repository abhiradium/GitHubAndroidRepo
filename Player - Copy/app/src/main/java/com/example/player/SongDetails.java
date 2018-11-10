package com.example.player;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SongDetails {
    public static MediaPlayer mediaPlayer;
    public ImageView mImageView;
    public ArrayList<File> mySongs;
    public int position;
    public Uri uri;
    public TextView mSongDescription, mPlayedDuration, mTotalDuration;
    public Button mButtonPlay, mButtonPause, mButtonNext, mButtonPrevious, mButtonShuffle;
    public Bitmap bitmap;
    public Handler elapsedDurationHandler = new Handler();
    Thread updateSeekBar;
    private SeekBar seekBar;
    private RelativeLayout mRelativeLayout;
}
