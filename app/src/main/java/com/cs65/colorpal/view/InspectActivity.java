package com.cs65.colorpal.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodel.PaletteViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

public class InspectActivity extends AppCompatActivity {
    private PaletteViewModel paletteViewModel;
    private RecyclerView swatchesView;
    private  SwatchListAdapter swatchesViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspect);
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);

        ImageView imageView = findViewById(R.id.img);
        swatchesView = findViewById(R.id.colors);

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
        List<Palette.Swatch> swatches = palette.getSwatches();
        if (!swatches.isEmpty()) {
            swatchesViewAdapter = new SwatchListAdapter(swatches);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            swatchesView.setLayoutManager(layoutManager);
            swatchesView.setAdapter(swatchesViewAdapter);
        }
    }
}
