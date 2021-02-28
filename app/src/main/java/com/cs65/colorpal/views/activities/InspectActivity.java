package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.utils.Utils;
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
    public static final int EDIT_ACTIVITY_CODE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspect);
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
        imageView = findViewById(R.id.img);
        swatchesView = findViewById(R.id.colors);
        paletteViewModel.getSelectedImage().observe(this, uri -> onImageChanged(uri));

//        paletteViewModel.getColorPaletteData().observe(this, palette -> addSwatches(palette));
        paletteViewModel.getColorPaletteData().observe(this, palette -> paletteViewModel.initSwatchesArrayList());

        paletteViewModel.getSwatches().observe(this, new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> list) {
                addSwatches(Utils.toSwatches(list));
            }
        });

        Intent intent = getIntent();
        if(intent!=null) {
            Uri photoUri = Uri.parse(intent.getStringExtra(PHOTO_URI));
            paletteViewModel.setSelectedImageUri(photoUri);
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

    // Update swatches from Edit Activity
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode != RESULT_OK) return;
        if(requestCode == EDIT_ACTIVITY_CODE){
            ArrayList<Integer> editedSwatches = intent.getIntegerArrayListExtra(SWATCH_VALUES);
            paletteViewModel.setSwatchesList(editedSwatches);
        }
    }

    private void onImageChanged(Uri uri){
        Picasso.with(this).load(uri).centerCrop().resize(700, 700).into(imageView);
    }

    private void addSwatches(List<Palette.Swatch> swatches) {
//        List<Palette.Swatch> swatches = palette.getSwatches();
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
//        Palette palette = paletteViewModel.getColorPaletteData().getValue();
//        List<Palette.Swatch> swatches = palette.getSwatches();
//        ArrayList<Integer> swatchValues = new ArrayList<>();
//        for (Palette.Swatch swatch : swatches) {
//            swatchValues.add(swatch.getRgb());
//        }
        ArrayList<Integer> swatchValues = paletteViewModel.getSwatches().getValue();
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
                Intent intent = new Intent(this, EditActivity.class);
                ArrayList<Integer> swatchValues = paletteViewModel.getSwatches().getValue();
                intent.putIntegerArrayListExtra(SWATCH_VALUES, swatchValues);
                startActivityForResult(intent, EDIT_ACTIVITY_CODE);
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
