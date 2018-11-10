package com.example.player;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1;
    Boolean firstStart;
    Boolean permissionGranted = false;
    private ViewPager mSlideViewPager;
    private LinearLayout mDotsLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button mBackButton, mNextButton;
    private int mCurrentPage;
    @NonNull
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            mBackButton.setEnabled(false);
            mBackButton.setVisibility(View.INVISIBLE);

            if (mCurrentPage == 0) {
                mBackButton.setEnabled(false);
                mBackButton.setVisibility(View.INVISIBLE);
                mNextButton.setEnabled(true);

                mNextButton.setText("Next");
                mBackButton.setText("");
            } else if (mCurrentPage == mDots.length - 1) {
                mBackButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);

                mNextButton.setText("Finish");
                mBackButton.setText("Back");

                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSongsList();
                    }
                });

            } else {
                mBackButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);

                mNextButton.setText("Next");
                mBackButton.setText("Back");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_main);

        /**
         * checking if the application has been opened for the first time or not
         * */
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        firstStart = sharedPreferences.getBoolean("firstStart", true);

        /**
         *Checking if the permission is granted
         */
        if ((ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        } else {
            openSongsList();
        }
        /**
         * animation for the color transition
         * */
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();

        mSlideViewPager = findViewById(R.id.slideViewPager1);
        mDotsLayout = findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        mBackButton = findViewById(R.id.previousButton);
        mNextButton = findViewById(R.id.nextButton);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotsLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(30);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }


    private void openSongsList() {
        Intent intent = new Intent(this, SongsList.class);
        startActivity(intent);
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
        finish();
    }

    /**
     * checking if the permission is denied then exit the app
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        openSongsList();
                        permissionGranted = true;
                    }
                } else {
                    closeNow();
                }
                break;
        }
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }
}
