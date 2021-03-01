package com.cs65.colorpal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cs65.colorpal.utils.Utils;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PaletteDetailActivity extends AppCompatActivity {
    public static final String IMAGE_URL_KEY = "IMAGE_URL";
    public static final String SWATCHES_KEY = "SWATCHES";
    private ImageView imageView;
    private RecyclerView paletteColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_detail);

        Intent intent = getIntent();
        Uri paletteUri = Uri.parse(intent.getStringExtra(IMAGE_URL_KEY));
        imageView = findViewById(R.id.palette_detail_image);
        Picasso.with(this).load(paletteUri).into(imageView);
        paletteColors = findViewById(R.id.palette_colors);
        List<Palette.Swatch> swatches = Utils.toSwatches(intent.getIntegerArrayListExtra(SWATCHES_KEY));
        SwatchListAdapter adapter = new SwatchListAdapter(swatches, null);
        paletteColors.setAdapter(adapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        paletteColors.setLayoutManager(layoutManager);
    }
}