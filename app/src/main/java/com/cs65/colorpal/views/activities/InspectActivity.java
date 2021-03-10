package com.cs65.colorpal.views.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs65.colorpal.R;
import com.cs65.colorpal.utils.Utils;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.cs65.colorpal.views.adapter.TagsGridAdapter;
import com.cs65.colorpal.views.fragments.UnsplashFragment;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cs65.colorpal.views.activities.PaletteDetailActivity.FROM;
import static com.cs65.colorpal.views.activities.PaletteDetailActivity.SWATCHES_KEY;

public class InspectActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private PaletteViewModel paletteViewModel;
    private RecyclerView swatchesView;
    private BottomNavigationView bottomNavigationView;
    private ImageView imageView;
    private Button addSelectedColorBtn;
    private RecyclerView tagsView;
    private TagsGridAdapter tagsGridAdapter;
    private EditText titleEditText;
    public static final String PHOTO_URI = "photoUri";
    public static final int EDIT_ACTIVITY_CODE = 2;
    public static final String SWATCH_VALUES = "values";
    public static final String ORIGINAL_SWATCH_VALUES = "original_values";
    private CharSequence[] privacyOptions = {
            "Public",
            "Private",
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspect);
        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
        imageView = findViewById(R.id.img);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);
        swatchesView = findViewById(R.id.colors);
        CardView selectedColor = findViewById(R.id.selected_color);
        addSelectedColorBtn = findViewById(R.id.add_palatte_btn);
        titleEditText = findViewById(R.id.inspect_title);

        paletteViewModel.getSelectedImage().observe(this, uri -> onImageSelected(uri));
        paletteViewModel.getSwatches().observe(this, list -> addSwatches(Utils.toSwatches(list)));
        paletteViewModel.getTags().observe(this, paletteTags -> tagsGridAdapter.setTags(paletteTags));
        paletteViewModel.getSelectedColor().observe(this, rgb -> selectedColor.setCardBackgroundColor(rgb));
        paletteViewModel.getTitle().observe(this, title -> titleEditText.setText(title));
        paletteViewModel.getPrivacy().observe(this, privacy -> onPrivacyChanged(privacy));

        if(savedInstanceState==null) {
            paletteViewModel.getColorPaletteData().observe(this, palette -> paletteViewModel.initSwatchesArrayList());
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Uri photoUri = Uri.parse(intent.getStringExtra(PHOTO_URI));
                String from = intent.getStringExtra(FROM);
                paletteViewModel.setSelectedImageUri(photoUri);

                if (from == null) {
                    paletteViewModel.extractNewFromPhoneUri(photoUri);
                } else if (from.equals(UnsplashFragment.UNSPLASH_FRAGMENT)) {
                    paletteViewModel.extractNewFromExternalUri(photoUri);
                } else if (from.equals(PaletteDetailActivity.PALETTE_DETAIL_ACTIVITY)) {
                    String docId = intent.getStringExtra(PaletteDetailActivity.ID_KEY);
                    paletteViewModel.setTitle(intent.getStringExtra(PaletteDetailActivity.TITLE_KEY));
                    paletteViewModel.setDocId(docId);
                    paletteViewModel.setSelectedImageUri(Uri.parse(intent.getStringExtra(PHOTO_URI)));
                    paletteViewModel.setOriginalSwatchesList(intent.getIntegerArrayListExtra(PaletteDetailActivity.SWATCHES_KEY));
                    paletteViewModel.setPrivacy(intent.getIntExtra(PaletteDetailActivity.PRIVACY_KEY, 0));
                    paletteViewModel.updateEditableByDocId(docId);
                }
            }
        }

        bottomNavigationView = findViewById(R.id.inspect_footer);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
        bottomNavigationView.getMenu().getItem(3).setCheckable(false);
        bottomNavigationView.getMenu().getItem(4).setCheckable(false);

        tagsView = findViewById(R.id.tags_view);
        tagsGridAdapter = new TagsGridAdapter(paletteViewModel.getTags().getValue(), this);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        tagsView.setLayoutManager(layoutManager);
        tagsView.setAdapter(tagsGridAdapter);

        setUpColorSelection();
        setUpEditText();
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

    private void onImageSelected(Uri uri){
        Glide.with(this).load(uri).into(imageView);
    }

    private void addSwatches(List<Palette.Swatch> swatches) {
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
        ArrayList<Integer> swatchValues = paletteViewModel.getSwatches().getValue();
        intent.putIntegerArrayListExtra(SWATCH_VALUES, swatchValues);
        startActivity(intent);
    }

    private void showAddTagDialog() {
        final View customLayout = getLayoutInflater().inflate(R.layout.input_dialog, null);
        final TextInputLayout textInputLayout = customLayout.findViewById(R.id.text_input_layout);
        textInputLayout.setHint("Name of Tag");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customLayout);
        builder.setTitle("Add a new tag");
        builder.setIcon(R.drawable.logo_icon);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TextInputEditText textInputEditText = customLayout.findViewById(R.id.tag_input_field);
                String text = textInputEditText.getText().toString();
                if(text.length()!=0) {
                    paletteViewModel.addTag(text);
                    tagsGridAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                intent.putIntegerArrayListExtra(ORIGINAL_SWATCH_VALUES, paletteViewModel.getOriginalSwatches());
                startActivityForResult(intent, EDIT_ACTIVITY_CODE);
                return true;
            case R.id.inspect_save_button:
                paletteViewModel.setTitle(titleEditText.getText().toString());
                Intent mainActvityIntent = new Intent(this, MainActivity.class);
                try {
                    paletteViewModel.savePaletteToDB();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent resultIntent = new Intent();
                resultIntent.putIntegerArrayListExtra(PaletteDetailActivity.SWATCHES_KEY, paletteViewModel.getSwatches().getValue());
                resultIntent.putParcelableArrayListExtra(PaletteDetailActivity.TAGS_KEY, paletteViewModel.getTags().getValue());
                resultIntent.putExtra(PaletteDetailActivity.TITLE_KEY, paletteViewModel.getTitle().getValue());
                resultIntent.putExtra(PaletteDetailActivity.PRIVACY_KEY,paletteViewModel.getPrivacy().getValue());
                setResult(RESULT_OK, resultIntent);

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFERENCE_NAME,0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.WHICH_FRAGMENT_TAG,MainActivity.LIBRARY_FRAGMENT_TAG);
                editor.commit();

                startActivity(mainActvityIntent);
                return true;
            case R.id.inspect_add_button:
                showAddTagDialog();
                return true;
            case R.id.inspect_public_button:
                showPrivacyDialog();
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpColorSelection() {
        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                Bitmap bitmap = imageView.getDrawingCache(true);
                float x = event.getX();
                float y = event.getY();
                int pixel = bitmap.getPixel((int)x, (int)y);
                paletteViewModel.selectColor(pixel);
            }
            return true;
        });
        addSelectedColorBtn.setOnClickListener((v -> {
            paletteViewModel.addSelectedColor();
        }));
        paletteViewModel.getAddColorEvent().observe(this, result -> {
            if (result.equals(PaletteViewModel.NO_SELECTED_COLOR)) {
                Toast.makeText(this, R.string.no_selected_color, Toast.LENGTH_SHORT).show();
            } else if (result.equals(PaletteViewModel.ADD_COLOR_SUCCEED)) {
                Toast.makeText(this, getString(R.string.successful_added_color), Toast.LENGTH_SHORT).show();
            } else if (result.equals(PaletteViewModel.COLOR_ALREADY_EXIST) ) {
                Toast.makeText(this, getString(R.string.failed_to_add_color), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpEditText(){
        titleEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                paletteViewModel.setTitle(titleEditText.getText().toString());
                titleEditText.clearFocus();
                return true;
            }
            return false;
        });
    }

    private void showPrivacyDialog(){
        MaterialAlertDialogBuilder privacyDialog = new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.logo_icon)
                .setTitle("Who can see this?")
                .setSingleChoiceItems(privacyOptions, paletteViewModel.getPrivacy().getValue(),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        paletteViewModel.setPrivacy(which);
                        dialog.dismiss();
                    }
                });
        privacyDialog.show();
    }

    private void onPrivacyChanged(int privacy){
        if(privacy==0){
            bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.ic_baseline_public_24);
            bottomNavigationView.getMenu().getItem(1).setTitle("Public");
        }else{
            bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.ic_baseline_person_24);
            bottomNavigationView.getMenu().getItem(1).setTitle("Private");
        }
    }
}
