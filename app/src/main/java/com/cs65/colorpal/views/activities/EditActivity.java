package com.cs65.colorpal.views.activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.EditViewModel;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SwatchGridAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class EditActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "EditActivity";
    public static final String SWATCH_VALUES = "values";
    public static final String ORIGINAL_SWATCH_VALUES = "original_values";
    public static final int EDIT_ACTIVITY_CODE = 2;
    private EditViewModel editViewModel;

    private SeekBar hSlider;
    private SeekBar sSlider;
    private SeekBar brSlider;
    private SeekBar rSlider;
    private SeekBar gSlider;
    private SeekBar bSlider;

    private TextView hTextView;
    private TextView sTextView;
    private TextView brTextView;
    private TextView rTextView;
    private TextView gTextView;
    private TextView bTextView;

    private TextView hexTextView;
    private String hexCode;
    private float[] hsb = new float[3];
    private SwatchGridAdapter swatchGridAdapter;
    private RecyclerView swatchesView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editViewModel = ViewModelProviders.of(this).get(EditViewModel.class);
        initializeSliders();

        //set observer for selected color
        editViewModel.getSelectedColor().observe(this, integer -> onSelectedColorChanged());
        editViewModel.setSelectedColor(0);
        editViewModel.getSwatches().observe(this, swatches -> updateSwatchesViews(swatches));

        Intent intent = getIntent();
        if (intent != null) {
            editViewModel.setSwatches(intent.getIntegerArrayListExtra(SWATCH_VALUES));
            editViewModel.setSwatches0(intent.getIntegerArrayListExtra(ORIGINAL_SWATCH_VALUES));
        }

        //hex code
        hexTextView = (TextView) findViewById(R.id.color_hex);

        //set swatch adapter
        swatchesView = findViewById(R.id.swatches_grid);
        swatchGridAdapter = new SwatchGridAdapter(editViewModel.getSwatches().getValue(),editViewModel,true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);
        swatchesView.setLayoutManager(gridLayoutManager);
        swatchesView.setAdapter(swatchGridAdapter);


        bottomNavigationView = findViewById(R.id.edit_footer);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
        bottomNavigationView.getMenu().getItem(3).setCheckable(false);

//        swatchesView.setItemAnimator();
//        swatchesView.animate(new Animator());
//        Animation animation = AnimationUtils.loadAnimation()
    }

    private void updateSwatchesViews(ArrayList<Integer> swatches){
        swatchGridAdapter.setSwatches(swatches);
    }

    //reset all colors
    private void onResetClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset all colors?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            editViewModel.resetSwatches();
            swatchGridAdapter.notifyDataSetChanged();
            onSelectedColorChanged();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onRemoveColorClicked(){
        if(editViewModel.getSwatches().getValue().size()<2){
            minSwatchDialogHandler();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove this color?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            swatchGridAdapter.remove(editViewModel.getSelectedColor().getValue());
            swatchGridAdapter.notifyItemRemoved(editViewModel.getSelectedColor().getValue());
            editViewModel.setSelectedColor(max(0,editViewModel.getSelectedColor().getValue()-1));
            onSelectedColorChanged();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void minSwatchDialogHandler(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You need to have at least one color!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //set color when RGB is changed
    private void onRGBChanged(){
        int r = rSlider.getProgress();
        int g = gSlider.getProgress();
        int b = bSlider.getProgress();
        //change HSB sliders
        Color.RGBToHSV(r,g,b,hsb);
        hSlider.setProgress((int)hsb[0]);
        sSlider.setProgress((int)(hsb[1]*100));
        brSlider.setProgress((int)(hsb[2]*100));
        onColorChange(r,g,b);
    }

    //set color when HSB is changed
    private void onHSBChanged() {
        hsb[0] = hSlider.getProgress();
        hsb[1] = sSlider.getProgress()/100.0f;
        hsb[2] = brSlider.getProgress()/100.0f;

        int color = Color.HSVToColor(hsb);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        rSlider.setProgress(r);
        gSlider.setProgress(g);
        bSlider.setProgress(b);
        onColorChange(r,g,b);
    }

    //Update hex code and color view when slider is used
    private void onColorChange(int r, int g, int b){
        //set hex code
        hexCode = String.format("#%02x%02x%02x", r, g, b);
        hexTextView.setText(hexCode);

        //set selected color in grid view
        editViewModel.onColorChange(Color.rgb(r,g,b));
        swatchGridAdapter.notifyDataSetChanged();
    }

    //Update sliders on selecting a new color
    private void onSelectedColorChanged(){
        int color = editViewModel.getSelectedColorValue();
        rSlider.setProgress(Color.red(color));
        gSlider.setProgress(Color.green(color));
        bSlider.setProgress(Color.blue(color));
        onRGBChanged();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_cancel_button:
                finish();
                return true;
            case R.id.edit_remove_button:
                onRemoveColorClicked();
                return true;
            case R.id.edit_reset_button:
                onResetClicked();
                return true;
            case R.id.edit_save_button:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(SWATCH_VALUES, editViewModel.getSwatches().getValue());
                setResult(RESULT_OK, resultIntent);
                finish();
                return true;
        }
        return false;
    }

    private void initializeSliders(){
        hSlider = (SeekBar) findViewById(R.id.H_slider);
        sSlider = (SeekBar) findViewById(R.id.S_slider);
        brSlider = (SeekBar) findViewById(R.id.Br_slider);
        rSlider = (SeekBar) findViewById(R.id.R_slider);
        gSlider = (SeekBar) findViewById(R.id.G_slider);
        bSlider = (SeekBar) findViewById(R.id.B_slider);

        //initialize textviews
        hTextView = (TextView) findViewById(R.id.H_number);
        sTextView = (TextView) findViewById(R.id.S_number);
        brTextView = (TextView) findViewById(R.id.Br_number);
        rTextView = (TextView) findViewById(R.id.R_number);
        gTextView = (TextView) findViewById(R.id.G_number);
        bTextView = (TextView) findViewById(R.id.B_number);

        //set onChangeListener for sliders
        hSlider.setOnSeekBarChangeListener(hSliderChangeListener);
        sSlider.setOnSeekBarChangeListener(sSliderChangeListener);
        brSlider.setOnSeekBarChangeListener(brSliderChangeListener);
        rSlider.setOnSeekBarChangeListener(rSliderChangeListener);
        gSlider.setOnSeekBarChangeListener(gSliderChangeListener);
        bSlider.setOnSeekBarChangeListener(bSliderChangeListener);
    }

    SeekBar.OnSeekBarChangeListener hSliderChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            hTextView.setText(String.valueOf(progress)+"\u00B0");
            if(fromUser) onHSBChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener sSliderChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            sTextView.setText(String.valueOf(progress)+"%");
            if(fromUser) onHSBChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener brSliderChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            brTextView.setText(String.valueOf(progress)+"%");
            if(fromUser) onHSBChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener rSliderChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            rTextView.setText(String.valueOf(progress));
            if(fromUser) onRGBChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener gSliderChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            gTextView.setText(String.valueOf(progress));
            if(fromUser) onRGBChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener bSliderChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            bTextView.setText(String.valueOf(progress));
            if(fromUser) onRGBChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
