package com.cs65.colorpal.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodel.PaletteViewModel;

public class InspectActivity extends AppCompatActivity {
    private static final int DEFAULT_COLOR = 0;
    private PaletteViewModel paletteViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspect);
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);

        ImageView imageView = findViewById(R.id.img);

        paletteViewModel.getSelectedImage().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                imageView.setImageURI(uri);
            }
        });

        paletteViewModel.getColorPalette().observe(this, new Observer<Palette>() {
            @Override
            public void onChanged(Palette palette) {
                addSwatches(palette);
            }
        });

        paletteViewModel.testDummyPic();
        // TODO: remove later
        Bitmap dummyPic = BitmapFactory.decodeResource(getResources(), R.drawable.nature_photo);
        paletteViewModel.extractColorPalette(dummyPic);
    }

    private void addSwatches(Palette palette) {
        View dominantView = findViewById(R.id.dominant);
        View vibrantView = findViewById(R.id.vibrant);
        View darkVibrantView = findViewById(R.id.dark_vibrant);
        View lightVibrantView = findViewById(R.id.light_vibrant);
        View mutedView = findViewById(R.id.muted);
        View darkMutedView = findViewById(R.id.dark_muted);
        View lightMutedView = findViewById(R.id.light_muted);

        dominantView.setBackgroundColor(palette.getDominantColor(DEFAULT_COLOR));
        vibrantView.setBackgroundColor(palette.getVibrantColor(DEFAULT_COLOR));
        darkVibrantView.setBackgroundColor(palette.getDarkVibrantColor(DEFAULT_COLOR));
        lightVibrantView.setBackgroundColor(palette.getLightVibrantColor(DEFAULT_COLOR));
        mutedView.setBackgroundColor(palette.getMutedColor(DEFAULT_COLOR));
        darkMutedView.setBackgroundColor(palette.getDarkMutedColor(DEFAULT_COLOR));
        lightMutedView.setBackgroundColor(palette.getLightMutedColor(DEFAULT_COLOR));
    }
}
