package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.PaletteTag;
import com.cs65.colorpal.utils.Utils;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.cs65.colorpal.views.adapter.TagsGridAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PaletteDetailActivity extends AppCompatActivity {
    public static final String IMAGE_URL_KEY = "IMAGE_URL";
    public static final String SWATCHES_KEY = "SWATCHES";
    public static final String TAGS_KEY = "TAGS";
    private ImageView imageView;
    private RecyclerView paletteColors;
    private RecyclerView tagsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_detail);

        Intent intent = getIntent();
        Uri paletteUri = Uri.parse(intent.getStringExtra(IMAGE_URL_KEY));
        imageView = findViewById(R.id.palette_detail_image);
        Picasso.with(this).load(paletteUri).into(imageView);
        paletteColors = findViewById(R.id.palette_colors);
        tagsView = findViewById(R.id.palette_detail_tags_view);

        List<Palette.Swatch> swatches = Utils.toSwatches(intent.getIntegerArrayListExtra(SWATCHES_KEY));
        SwatchListAdapter adapter = new SwatchListAdapter(swatches, null);
        paletteColors.setAdapter(adapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        paletteColors.setLayoutManager(layoutManager);

        List<PaletteTag> tags = intent.getParcelableArrayListExtra(TAGS_KEY);
        tagsView = findViewById(R.id.palette_detail_tags_view);
        TagsGridAdapter tagsGridAdapter = new TagsGridAdapter(tags, this, false);
        FlexboxLayoutManager flexLayoutManager = new FlexboxLayoutManager(this);
        flexLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        tagsView.setLayoutManager(flexLayoutManager);
        tagsView.setAdapter(tagsGridAdapter);
    }
}