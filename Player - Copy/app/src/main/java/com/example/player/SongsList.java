package com.example.player;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class SongsList extends AppCompatActivity implements View.OnClickListener {

    ListView mSongsList;
    String[] items;
    PlayerScreen playerScreen = new PlayerScreen();
    private Button mPlayButton, mPauseButton, mNextButton;
    private TextView mSongDetail;
    private ImageView mAlbumArt;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);

        mediaPlayer = PlayerScreen.mediaPlayer;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(15);
        actionBar.show();

        mSongsList = findViewById(R.id.songsList);
        mPlayButton = findViewById(R.id.playSmall);
        mPauseButton = findViewById(R.id.pause_small);
        mNextButton = findViewById(R.id.nextSmall);
        mSongDetail = findViewById(R.id.songPlaying);
        mAlbumArt = findViewById(R.id.imageView);

        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mSongDetail.setOnClickListener(this);
        mAlbumArt.setOnClickListener(this);


        ArrayList<File> songsList2 = new ArrayList<>();

        songsList2 = searchSongs(Environment.getExternalStorageDirectory());
        final ArrayList<File> songsList1 = songsList2;
        items = new String[songsList1.size()];

        for (int i = 0; i < songsList1.size(); i++) {
            items[i] = songsList1.get(i).getName().toString().replace(".mp3", "")
                    .replace(".m4a", "");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.song_layout, R.id.songName, items);
        mSongsList.setAdapter(arrayAdapter);

        if (items.length > 0) {
            Toast.makeText(this, songsList1.size() + " songs added", Toast.LENGTH_SHORT).show();
        }

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), PlayerScreen.class)
                        .putExtra("position", position)
                        .putExtra("songsList", songsList1));
            }
        });


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.playSmall:
                if (!PlayerScreen.mediaPlayer.isPlaying()) {
                    playerScreen.uri = Uri.parse(playerScreen.mySongs.get(playerScreen.position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), playerScreen.uri);
                    mSongDetail.setText(playerScreen.mySongs.get(playerScreen.position).getName().toString().replace(".mp3", ""));
                    mSongDetail.setTextColor(Color.BLACK);
                    playerScreen.getSongAlbumArt();
                    playerScreen.extractColorFromBitmap();
                    mediaPlayer.start();
                    mPlayButton.setVisibility(View.INVISIBLE);
                    mPauseButton.setVisibility(View.VISIBLE);
                    playerScreen.updateSeekBar.start();
                }
                break;
            case R.id.pause_small:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mPlayButton.setVisibility(View.VISIBLE);
                    mPauseButton.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.nextSmall:
                mediaPlayer.stop();
                playerScreen.position = (playerScreen.position + 1) % playerScreen.mySongs.size();
                playerScreen.uri = Uri.parse(playerScreen.mySongs.get(playerScreen.position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), playerScreen.uri);
                playerScreen.getSongAlbumArt();
                playerScreen.extractColorFromBitmap();
                mSongDetail.setText(playerScreen.mySongs.get(playerScreen.position).getName().toString().replace(".mp3", ""));
                mSongDetail.setTextColor(Color.BLACK);
                PlayerScreen.mediaPlayer.start();
                if (mediaPlayer.isPlaying() && mPlayButton.isEnabled()) {
                    mPlayButton.setVisibility(View.INVISIBLE);
                    mPauseButton.setVisibility(View.VISIBLE);
                }
                playerScreen.updateSeekBar.start();
                break;
            case R.id.albumArt:
                Intent intent1 = new Intent(this, PlayerScreen.class);
                startActivity(intent1);
                break;
            case R.id.songPlaying:
                Intent intent = new Intent(this, PlayerScreen.class);
                startActivity(intent);
                break;
        }
    }

    public ArrayList<File> searchSongs(File root) {
        ArrayList<File> mp3List = new ArrayList<>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                if (singleFile.getName().toUpperCase().contains("MUSIC") ||
                        singleFile.getName().toUpperCase().contains("DOWNLOAD") ||
                        singleFile.getName().toUpperCase().contains("SHARE")) {
                    mp3List.addAll(searchSongs(singleFile));
                }
            } else {
                if (singleFile.getName().endsWith(".mp3")) {
                    mp3List.add(singleFile);
                }
            }
        }
        return mp3List;
    }

}