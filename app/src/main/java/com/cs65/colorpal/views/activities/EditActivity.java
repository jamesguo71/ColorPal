package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.EditViewModel;
import com.cs65.colorpal.views.adapter.SwatchGridAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cs65.colorpal.views.activities.SwatchesDetailActivity.SWATCH_VALUES;
import static java.lang.Math.min;

public class EditActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "EditActivity";
    public static final String SWATCH_VALUES = "values";
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

    //RecyclerView and swatches
    private ArrayList<Integer> mSwatches;
    private ArrayList<Integer> mSwatches0;
    private SwatchGridAdapter swatchGridAdapter;
    private RecyclerView swatchesView;

    //footer
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editViewModel = ViewModelProviders.of(this).get(EditViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {
            mSwatches = intent.getIntegerArrayListExtra(SWATCH_VALUES);
            mSwatches0 = (ArrayList<Integer>) mSwatches.clone();
        }

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

        //hex code
        hexTextView = (TextView) findViewById(R.id.color_hex);

        //set swatch adapter
        swatchesView = findViewById(R.id.swatches_grid);
        swatchGridAdapter = new SwatchGridAdapter(mSwatches,editViewModel,true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, min(mSwatches.size(),8));
        swatchesView.setLayoutManager(gridLayoutManager);
        swatchesView.setAdapter(swatchGridAdapter);

        //set observer for selected color
        editViewModel.getSelectedColor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                onSelectedColorChanged();
            }
        });
        editViewModel.setSelectedColor(0);

        bottomNavigationView = findViewById(R.id.edit_footer);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
    }

    public void onSaveClicked(){

    }

    //reset all colors
    public void onResetClicked(){
        for(int i = 0; i < mSwatches.size(); i++){
            mSwatches.set(i,mSwatches0.get(i));
        }
        swatchGridAdapter.notifyDataSetChanged();
        onSelectedColorChanged();
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

    //Update hex code and color view
    private void onColorChange(int r, int g, int b){
        //set hex code
        hexCode = String.format("#%02x%02x%02x", r, g, b);
        hexTextView.setText(hexCode);

        //set selected color in grid view
        mSwatches.set(editViewModel.getSelectedColor().getValue(),Color.rgb(r,g,b));
        swatchGridAdapter.notifyDataSetChanged();
    }

    //Update sliders on selected color changed
    private void onSelectedColorChanged(){
        int idx = editViewModel.getSelectedColor().getValue();
        rSlider.setProgress(Color.red(mSwatches.get(idx)));
        gSlider.setProgress(Color.green(mSwatches.get(idx)));
        bSlider.setProgress(Color.blue(mSwatches.get(idx)));
        onRGBChanged();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_cancel_button:
                finish();
                return true;
            case R.id.edit_reset_button:
                onResetClicked();
                return true;
            case R.id.edit_save_button:
                onSaveClicked();
                finish();
                return true;
        }
        return false;
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
