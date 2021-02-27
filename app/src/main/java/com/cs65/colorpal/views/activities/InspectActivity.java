package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cs65.colorpal.views.activities.SwatchesDetailActivity.SWATCH_VALUES;

public class InspectActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private PaletteViewModel paletteViewModel;
    private RecyclerView swatchesView;
    private BottomNavigationView bottomNavigationView;
    private ImageView imageView;

    public static final String PHOTO_URI = "photoUri";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspect);
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
        imageView = findViewById(R.id.img);
        swatchesView = findViewById(R.id.colors);
        paletteViewModel.getSelectedImage().observe(this, uri -> onImageChanged(uri));

        paletteViewModel.getColorPalette().observe(this, palette -> addSwatches(palette));

        Intent intent = getIntent();
        if(intent!=null) {
            Uri photoUri = Uri.parse(intent.getStringExtra(PHOTO_URI));
            paletteViewModel.updateSelectedImage(photoUri);
            paletteViewModel.extractNewFromUri(photoUri);
        }

//        paletteViewModel.testDummyPic();
//        // TODO: remove later
//        Bitmap dummyPic = BitmapFactory.decodeResource(getResources(), R.drawable.nature_photo);
//        paletteViewModel.extractColorPalette(dummyPic);

        bottomNavigationView = findViewById(R.id.inspect_footer);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
    }

    private void onImageChanged(Uri uri){
        Picasso.with(this).load(uri).centerCrop().resize(700, 700).into(imageView);
    }

    private void addSwatches(Palette palette) {
        List<Palette.Swatch> swatches = palette.getSwatches();
        if (!swatches.isEmpty()) {
            SwatchListAdapter swatchesViewAdapter = new SwatchListAdapter(swatches, v -> openSwatchDetails());
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

    public void onEditClicked(){
        Intent intent = new Intent(this, EditActivity.class);
        ArrayList<Integer> swatchValues = paletteViewModel.getSwatchesArrayList();
        intent.putIntegerArrayListExtra(SWATCH_VALUES, swatchValues);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.inspect_cancel_button:
                finish();
                return true;
            case R.id.inspect_edit_button:
                onEditClicked();
                return true;
            case R.id.inspect_save_button:
                try {
                    paletteViewModel.savePaletteToDB();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
        }
        return false;
    }
}
