package com.example.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    //array of drawable icons
    @NonNull
    public int[] slideImages = {
            R.drawable.headphones,
            R.drawable.music_player,
            R.drawable.like
    };
    @NonNull
    public String[] descriptions = {
            "Play it",
            "Listen it",
            "Love it"
    };
    Context context;
    @Nullable
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {

        this.context = context;
    }

    @Override
    public int getCount() {

        return descriptions.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideDescription = view.findViewById(R.id.description);

        slideImageView.setImageResource(slideImages[position]);
        slideDescription.setText(descriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
