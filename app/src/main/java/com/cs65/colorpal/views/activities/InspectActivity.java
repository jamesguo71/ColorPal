package com.cs65.colorpal.views.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.utils.Utils;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.cs65.colorpal.views.adapter.TagsGridAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cs65.colorpal.views.activities.SwatchesDetailActivity.SWATCH_VALUES;

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
        paletteViewModel.getColorPaletteData().observe(this, palette -> paletteViewModel.initSwatchesArrayList());
        paletteViewModel.getSwatches().observe(this, list -> addSwatches(Utils.toSwatches(list)));
        paletteViewModel.getTags().observe(this, paletteTags -> tagsGridAdapter.notifyDataSetChanged());
        paletteViewModel.getSelectedColor().observe(this, rgb -> selectedColor.setCardBackgroundColor(rgb));

        Intent intent = getIntent();
        if(intent!=null && intent.getExtras()!=null) {
            Uri photoUri = Uri.parse(intent.getStringExtra(PHOTO_URI));
            paletteViewModel.setSelectedImageUri(photoUri);
            paletteViewModel.extractNewFromUri(photoUri);
        }

        bottomNavigationView = findViewById(R.id.inspect_footer);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
        bottomNavigationView.getMenu().getItem(3).setCheckable(false);

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
        imageView.setImageURI(uri);
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
        EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new tag");
        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(edittext);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String text = edittext.getText().toString();
                if(text.length()!=0) {
                    paletteViewModel.addTag(text);
                    tagsGridAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            case R.id.inspect_add_button:
                showAddTagDialog();
                return true;
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
        paletteViewModel.getAddColorEvent().observe(this, success -> {
            if (success) {
                Toast.makeText(this, getString(R.string.successful_added_color), Toast.LENGTH_SHORT).show();
            } else {
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
}
