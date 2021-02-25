package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static com.cs65.colorpal.views.activities.SwatchesDetailActivity.SWATCH_VALUES;

public class InspectActivity extends AppCompatActivity {
    private PaletteViewModel paletteViewModel;
    private RecyclerView swatchesView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspect);
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);

        ImageView imageView = findViewById(R.id.img);
        swatchesView = findViewById(R.id.colors);

        paletteViewModel.getSelectedImage().observe(this, uri -> imageView.setImageURI(uri));

        paletteViewModel.getColorPalette().observe(this, palette -> addSwatches(palette));

        paletteViewModel.testDummyPic();
        // TODO: remove later
        Bitmap dummyPic = BitmapFactory.decodeResource(getResources(), R.drawable.nature_photo);
        paletteViewModel.extractColorPalette(dummyPic);
    }

    private void addSwatches(Palette palette) {
        List<Palette.Swatch> swatches = palette.getSwatches();
        if (!swatches.isEmpty()) {
            SwatchListAdapter swatchesViewAdapter = new SwatchListAdapter(swatches);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            swatchesView.setLayoutManager(layoutManager);
            swatchesView.setAdapter(swatchesViewAdapter);
        }
    }

    public void openSwatchDetails() {
        Intent intent = new Intent(this, SwatchesDetailActivity.class);
        Palette palette = paletteViewModel.getColorPalette().getValue();
        List<Palette.Swatch> swatches = palette.getSwatches();
        ArrayList<Integer> swatchValues = new ArrayList<>();
        for (Palette.Swatch swatch : swatches) {
            swatchValues.add(swatch.getRgb());
        }
        intent.putIntegerArrayListExtra(SWATCH_VALUES, swatchValues);
        startActivity(intent);
    }
}
