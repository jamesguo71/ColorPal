package com.cs65.colorpal.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.min;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";

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

    //TMP: colors
    private int mColorNum = 16;
    private int[] mColors = new int[mColorNum];

    //RecyclerView and swatches
    private ArrayList<Palette.Swatch> mSwatches;
    private ArrayList<Palette.Swatch> mSwatches0;
    private SwatchGridAdapter swatchGridAdapter;
    private RecyclerView swatchesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editViewModel = ViewModelProviders.of(this).get(EditViewModel.class);

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

        //TMP: create random colors
        mSwatches = new ArrayList<Palette.Swatch>();
        for(int i = 0; i < mColorNum; i++) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mColors[i] = color;
            Palette.Swatch swatch = new Palette.Swatch(color,1);
            mSwatches.add(swatch);
        }
        mSwatches0 = (ArrayList<Palette.Swatch>) mSwatches.clone();

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
    }

    //reset all colors
    public void onResetClick(View view){
        for(int i = 0; i < mSwatches.size(); i++){
            Palette.Swatch s = mSwatches0.get(i);
            Palette.Swatch swatch = new Palette.Swatch(s.getRgb(),1);
            mSwatches.set(i,swatch);
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
        Palette.Swatch swatch = new Palette.Swatch(Color.rgb(r,g,b),1);
        mSwatches.set(editViewModel.getSelectedColor().getValue(),swatch);      //TODO
        swatchGridAdapter.notifyDataSetChanged();
    }

    //Update sliders on selected color changed
    private void onSelectedColorChanged(){
        int idx = editViewModel.getSelectedColor().getValue();
        rSlider.setProgress(Color.red(mColors[idx]));
        gSlider.setProgress(Color.green(mColors[idx]));
        bSlider.setProgress(Color.blue(mColors[idx]));
        onRGBChanged();
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
