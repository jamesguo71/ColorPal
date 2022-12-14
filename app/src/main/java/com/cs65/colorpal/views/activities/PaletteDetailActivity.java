package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.models.PaletteTag;
import com.cs65.colorpal.utils.Utils;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.cs65.colorpal.views.adapter.TagsGridAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.cs65.colorpal.views.activities.MainActivity.PALETTE_DETAIL_REQUEST;

public class PaletteDetailActivity extends AppCompatActivity {
    public static final String PALETTE_DETAIL_ACTIVITY = "PALETTE_DETAIL_ACTIVITY";
    public static final String IMAGE_URL_KEY = "IMAGE_URL";
    public static final String SWATCHES_KEY = "SWATCHES";
    public static final String TAGS_KEY = "TAGS";
    public static final String TAG_KEY = "TAG";
    public static final String TITLE_KEY = "TITLE";
    public static final String SHOW_EDIT_BUTTON_TAG = "SHOW_EDIT_BUTTON_TAG";
    public static final String USERNAME_KEY = "USERNAME";
    public static final String ID_KEY = "ID";
    public static final String PRIVACY_KEY = "PRIVACY";
    public static final String FROM = "from";
    public static final int INSPECT_ACTIVITY_CODE = 1;
    private ImageView imageView;
    private RecyclerView paletteColors;
    private RecyclerView tagsView;
    private TextView paletteDetailTitle, cardPaletteName, cardPaletteCreatorName;
    private MaterialButton materialButton;
    private TagsGridAdapter tagsGridAdapter;
    private SwatchListAdapter adapter;
    private CircularProgressIndicator circularProgressIndicator;
    private  ImageView privacyImageView;
    private PaletteViewModel paletteViewModel;
    private ColorPalette colorPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_detail);

        imageView = findViewById(R.id.palette_detail_image);
        paletteColors = findViewById(R.id.palette_colors);
        tagsView = findViewById(R.id.palette_detail_tags_view);

        circularProgressIndicator = findViewById(R.id.palette_detail_image_progress_bar);

        paletteDetailTitle = findViewById(R.id.palette_detail_title);
        cardPaletteName = findViewById(R.id.card_name);
        cardPaletteCreatorName = findViewById(R.id.card_creators_name);

        setUpEditButton();
        setupViewModel();
    }

    public void setupViewModel(){
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
        paletteViewModel.getPaletteById(getIntent().getStringExtra(ID_KEY)).observe(this, Observer -> {

            LinearProgressIndicator linearProgressIndicator = findViewById(R.id.progress_bar);
            ScrollView scrollView = findViewById(R.id.palette_detail_scrollView);

            linearProgressIndicator.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            colorPalette = Observer;

            paletteDetailTitle.setText(Observer.getTitle());
            cardPaletteName.setText(Observer.getTitle());
            cardPaletteCreatorName.setText(Observer.getUsername());

            ArrayList<Integer> swatchValues = Observer.getSwatches();
            List<Palette.Swatch> swatches = Utils.toSwatches(swatchValues);
            adapter = new SwatchListAdapter(swatches, v -> {
                Intent i = new Intent(this, SwatchesDetailActivity.class);
                i.putIntegerArrayListExtra(SwatchesDetailActivity.SWATCH_VALUES, swatchValues);
                startActivity(i);
            });
            paletteColors.setAdapter(adapter);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            paletteColors.setLayoutManager(layoutManager);

            List<PaletteTag> tags = Observer.getTags();
            tagsView = findViewById(R.id.palette_detail_tags_view);
            tagsGridAdapter = new TagsGridAdapter(tags, this, false);
            FlexboxLayoutManager flexLayoutManager = new FlexboxLayoutManager(this);
            flexLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
            tagsView.setLayoutManager(flexLayoutManager);
            tagsView.setAdapter(tagsGridAdapter);
            Uri paletteUri = Uri.parse(Observer.getDownloadUrl());
            Picasso.with(this).load(paletteUri).into(imageView);

            privacyImageView = (ImageView) findViewById(R.id.palette_detail_privacy);
            setPrivacy(Observer.getPrivacy());


        });
    }

    public void setUpEditButton(){
        Boolean showButton = getIntent().getBooleanExtra(SHOW_EDIT_BUTTON_TAG, true);
        materialButton = findViewById(R.id.edit_palette_button);
        if(showButton.equals(false)) {
            materialButton.setVisibility(View.GONE);
        }

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InspectActivity.class);
                if(colorPalette != null){
                    intent.putExtra("from",PALETTE_DETAIL_ACTIVITY);
                    intent.putExtra(InspectActivity.PHOTO_URI, colorPalette.getDownloadUrl());
                    intent.putExtra(TITLE_KEY, colorPalette.getTitle());
                    intent.putIntegerArrayListExtra(SWATCHES_KEY, colorPalette.getSwatches());
                    intent.putParcelableArrayListExtra(TAGS_KEY, (ArrayList<? extends Parcelable>) colorPalette.getTags());
                    intent.putExtra(ID_KEY, colorPalette.getDocId());
                    intent.putExtra(PRIVACY_KEY, colorPalette.getPrivacy());
                    startActivityForResult(intent, INSPECT_ACTIVITY_CODE);
                }
//                startActivity(intent);
            }
        });
    }

    public void progressBarHandler(){

    }


    public void searchImagesByTag(String tag) {
        if (tag == null || tag.length() == 0) {
            return;
        }
        Intent data = new Intent();
        data.putExtra(TAG_KEY, tag);
        setResult(PALETTE_DETAIL_REQUEST, data);
        finish();
    }

    // Update from Inspect Activity
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode != RESULT_OK) return;
        if(requestCode == INSPECT_ACTIVITY_CODE){
            ArrayList<Integer> editedSwatches = intent.getIntegerArrayListExtra(SWATCHES_KEY);
            List<Palette.Swatch> swatches = Utils.toSwatches(editedSwatches);
            adapter.setSwatches(swatches);
            tagsGridAdapter.setTags(intent.getParcelableArrayListExtra(TAGS_KEY));
            paletteDetailTitle.setText(intent.getStringExtra(TITLE_KEY));
            cardPaletteName.setText(intent.getStringExtra(TITLE_KEY));
            setPrivacy(intent.getIntExtra(PRIVACY_KEY,0));
        }
    }

    private void setPrivacy(int privacy){
        if(privacy==0){
            privacyImageView.setImageResource(R.drawable.ic_baseline_public_24);
        }else{
            privacyImageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }
}