package com.example.player;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class PlayerScreen extends AppCompatActivity implements View.OnClickListener {

    public static MediaPlayer mediaPlayer;
    public ImageView mImageView;
    public ArrayList<File> mySongs;
    public int position;
    public Uri uri;
    public TextView mSongDescription, mPlayedDuration, mTotalDuration;
    public Button mButtonPlay, mButtonPause, mButtonNext, mButtonPrevious, mButtonShuffle;
    public Bitmap bitmap;
    public Handler elapsedDurationHandler = new Handler();
    /**
     * get the duration of the song being played
     */
    public Runnable updateElapsedDuration = new Runnable() {
        @Override
        public void run() {
            Integer elapsedDuration = mediaPlayer.getCurrentPosition();
            Long minutes, seconds;
            String sec;
            minutes = TimeUnit.MILLISECONDS.toMinutes((long) elapsedDuration);
            seconds = TimeUnit.MILLISECONDS.toSeconds((long) elapsedDuration) % 60;
            if (seconds < 10) {
                sec = "0" + seconds.toString();
            } else
                sec = seconds.toString();
            mPlayedDuration.setText(minutes.toString() + ":" + sec);
            elapsedDurationHandler.postDelayed(this, 10);
            mPlayedDuration.setTextColor(Color.BLACK);
        }
    };
    Thread updateSeekBar;
    private SeekBar seekBar;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_player_screen);

        seekBar = findViewById(R.id.seekBar);
        mButtonPlay = findViewById(R.id.buttonPlay);
        mButtonPause = findViewById(R.id.buttonPause);
        mButtonNext = findViewById(R.id.buttonNext);
        mButtonPrevious = findViewById(R.id.buttonPrevious);
        mButtonShuffle = findViewById(R.id.shuffleButton);
        mSongDescription = findViewById(R.id.songDescription);
        mPlayedDuration = findViewById(R.id.playedDuration);
        mTotalDuration = findViewById(R.id.totalDuration);
        mImageView = findViewById(R.id.albumArt);
        mRelativeLayout = findViewById(R.id.relativeLayoutPlayerScreen);

        mButtonPlay.setOnClickListener(this);
        mButtonPause.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonPrevious.setOnClickListener(this);
        mButtonShuffle.setOnClickListener(this);


        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                seekBar.setMax(totalDuration);
                while (currentPosition < totalDuration) {
                    try {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songsList");
        position = bundle.getInt("position", 0);

        uri = Uri.parse(mySongs.get(position).toString());
        getSongAlbumArt();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        mSongDescription.setText(mySongs.get(position).getName().replace(".mp3", ""));
        seekBar.setMax(mediaPlayer.getDuration());
        elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
        getSongDuration();
        updateSeekBar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateSeekBar.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                updateSeekBar.start();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                updateSeekBar.start();
            }
        });

        /**
         * extracting color from the bitmap
         * written on 23-06-2018
         * */
        extractColorFromBitmap();

        /**
         * on current song completion
         * */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                position = position + 1;
                if (position == (mySongs.size() - 1))
                    position = 0;
                uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                getSongAlbumArt();
                getSongDuration();
                extractColorFromBitmap();
                mediaPlayer.start();
                elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
                mSongDescription.setText(mySongs.get(position).getName().replace(".mp3", ""));
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar.start();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttonPlay:
                if (!mediaPlayer.isPlaying()) {
                    uri = Uri.parse(mySongs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mSongDescription.setText(mySongs.get(position).getName().replace(".mp3", ""));
                    getSongAlbumArt();
                    getSongDuration();
                    extractColorFromBitmap();
                    elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
                    mediaPlayer.start();
                    mButtonPlay.setVisibility(View.INVISIBLE);
                    mButtonPause.setVisibility(View.VISIBLE);
                    updateSeekBar.start();
                }
                break;
            case R.id.buttonPause:
                if (mediaPlayer.isPlaying()) {
                    /*uri = Uri.parse(mySongs.get(position).toString());
                    getSongAlbumArt();
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mSongDescription.setText(mySongs.get(position).getName().toString().replace(".mp3", ""));
                    getSongDuration();
                    elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
                    extractColorFromBitmap();*/
                    mediaPlayer.pause();
                    mButtonPlay.setVisibility(View.VISIBLE);
                    mButtonPause.setVisibility(View.INVISIBLE);
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                break;
            case R.id.buttonNext:
                mediaPlayer.stop();
                position = (position + 1) % mySongs.size();
                uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                getSongAlbumArt();
                getSongDuration();
                extractColorFromBitmap();
                mSongDescription.setText(mySongs.get(position).getName().replace(".mp3", ""));
                elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
                mediaPlayer.start();
                if (mediaPlayer.isPlaying() && mButtonPlay.isEnabled()) {
                    mButtonPlay.setVisibility(View.INVISIBLE);
                    mButtonPause.setVisibility(View.VISIBLE);
                }
                updateSeekBar.start();
                break;
            case R.id.buttonPrevious:
                mediaPlayer.stop();
                position = (position - 1 < 0) ? (mySongs.size() - 1) : (position - 1);
                uri = Uri.parse(mySongs.get((position) % mySongs.size()).toString());
                getSongAlbumArt();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mSongDescription.setText(mySongs.get(position).getName().replace(".mp3", ""));
                getSongDuration();
                elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
                extractColorFromBitmap();
                mediaPlayer.start();
                if (!mediaPlayer.isPlaying() && mButtonPause.isEnabled()) {
                    mButtonPause.setVisibility(View.INVISIBLE);
                    mButtonPlay.setVisibility(View.VISIBLE);
                }
                updateSeekBar.start();
                break;
            case R.id.shuffleButton:
                mediaPlayer.pause();
                Random random = new Random();
                position = random.nextInt(mySongs.size());
                uri = Uri.parse(mySongs.get((position) % mySongs.size()).toString());
                getSongAlbumArt();
                getSongDuration();
                extractColorFromBitmap();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mSongDescription.setText(mySongs.get(position).getName().replace(".mp3", ""));
                elapsedDurationHandler.postDelayed(updateElapsedDuration, 100);
                mediaPlayer.start();
                mButtonPause.setVisibility(View.VISIBLE);
                mButtonPlay.setVisibility(View.INVISIBLE);
                updateSeekBar.start();
                break;
        }
    }

    public void getSongAlbumArt() {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        byte[] data = retriever.getEmbeddedPicture();
        if (data == null) {
            mImageView.setImageResource(R.mipmap.album_art);
        } else {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mImageView.setImageBitmap(bitmap);
            retriever.release();
        }
    }

    public void extractColorFromBitmap() {
        bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                setWholeSwatch(mRelativeLayout, palette.getLightMutedSwatch());
            }
        });
    }

    private void setWholeSwatch(RelativeLayout relativeLayout, Palette.Swatch currentSwatch) {
        if (currentSwatch != null) {
            relativeLayout.setBackgroundColor(currentSwatch.getRgb());
            mSongDescription.setTextColor(currentSwatch.getTitleTextColor());
        }
    }

    /**
     * get the total duration of the song and display above the seekbar
     */
    private void getSongDuration() {
        /*Integer songTotalDuration = mediaPlayer.getDuration();
        Integer minutes, seconds;
        String second;
        minutes = (songTotalDuration / 1000) / 60;
        seconds = (songTotalDuration / 1000) % 60;
        if (seconds < 10) {
            second = seconds.toString() + "0";
        } else
            second = seconds.toString();*/
        Integer songTotalDuration = mediaPlayer.getDuration();
        Long minutes, seconds;
        String sec;
        minutes = TimeUnit.MILLISECONDS.toMinutes((long) songTotalDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds((long) songTotalDuration) % 60;
        if (seconds < 10) {
            sec = "0" + seconds.toString();
        } else
            sec = seconds.toString();
        mTotalDuration.setText(minutes.toString() + ":" + sec);
        mTotalDuration.setTextColor(Color.BLACK);
    }
}
