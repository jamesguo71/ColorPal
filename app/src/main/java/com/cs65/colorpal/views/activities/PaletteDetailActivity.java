package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PaletteDetailActivity extends AppCompatActivity {
    public static final String IMAGE_URL_KEY = "IMAGE_URL";
    public static final String SWATCHES_KEY = "SWATCHES";
    public static final String TAGS_KEY = "TAGS";
    public static final String TITLE_KEY = "TITLE";
    public static final String SHOW_EDIT_BUTTON_TAG = "SHOW_EDIT_BUTTON_TAG";
    public static final String USERNAME_KEY = "USERNAME";
    private ImageView imageView;
    private RecyclerView paletteColors;
    private RecyclerView tagsView;
    private TextView paletteDetailTitle, cardPaletteName, cardPaletteCreatorName;

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

        paletteDetailTitle = findViewById(R.id.palette_detail_title);
        cardPaletteName = findViewById(R.id.card_name);
        cardPaletteCreatorName = findViewById(R.id.card_creators_name);

        paletteDetailTitle.setText(intent.getStringExtra(TITLE_KEY));
        cardPaletteName.setText(intent.getStringExtra(TITLE_KEY));
        cardPaletteCreatorName.setText(intent.getStringExtra(USERNAME_KEY));

        ArrayList<Integer> swatchValues = intent.getIntegerArrayListExtra(SWATCHES_KEY);
        List<Palette.Swatch> swatches = Utils.toSwatches(swatchValues);
        SwatchListAdapter adapter = new SwatchListAdapter(swatches, v -> {
            Intent i = new Intent(this, SwatchesDetailActivity.class);
            i.putIntegerArrayListExtra(SwatchesDetailActivity.SWATCH_VALUES, swatchValues);
            startActivity(i);
        });
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

        setUpEditButton();
    }

    public void setUpEditButton(){
        Boolean showButton = getIntent().getBooleanExtra(SHOW_EDIT_BUTTON_TAG, true);
        if(showButton.equals(false)) {
            MaterialButton materialButton = findViewById(R.id.edit_palette_button);
            materialButton.setVisibility(View.GONE);
        }
    }
}