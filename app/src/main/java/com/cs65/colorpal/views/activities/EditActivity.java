package com.cs65.colorpal.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.cs65.colorpal.R;

import java.util.Random;

public class EditActivity extends AppCompatActivity {

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

    //TMP
    private View[] mColorViews = new View[5];
    private int[] mColors = new int[5];     //test
    private int mSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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

        mColorViews[0] = (View) findViewById(R.id.selected_color1);
        mColorViews[1] = (View) findViewById(R.id.selected_color2);
        mColorViews[2] = (View) findViewById(R.id.selected_color3);
        mColorViews[3] = (View) findViewById(R.id.selected_color4);
        mColorViews[4] = (View) findViewById(R.id.selected_color5);

        //TMP
        for(int i = 0; i < 5; i++) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mColors[i] = color;
            mColorViews[i].setBackgroundColor(color);
            mColorViews[i].setOnClickListener(colorOnClickListener);
        }
        mSelectedColor = 0;         //default selection: 0
        onSelectedColorChanged();
    }

    public void onSavePaletteEdit(){

    }

    public void onCancelPaletteEdit(){

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
//        Log.d("zwang", Color.red(color)+" "+Color.green(color)+" "+Color.blue(color));
        //set color
        onColorChange(r,g,b);
    }

    //Update hex code and color view
    private void onColorChange(int r, int g, int b){
        //set hex code
        hexCode = String.format("#%02x%02x%02x", r, g, b);
        hexTextView.setText(hexCode);

        //set selected color
        mColorViews[mSelectedColor].setBackgroundColor(Color.rgb(r,g,b));
    }

    //Update sliders on selected color changed
    private void onSelectedColorChanged(){
        rSlider.setProgress(Color.red(mColors[mSelectedColor]));
        gSlider.setProgress(Color.green(mColors[mSelectedColor]));
        bSlider.setProgress(Color.blue(mColors[mSelectedColor]));
        onRGBChanged();
    }

    //TODO: CHANGE TO LIST VIEW
    View.OnClickListener colorOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            for(int i = 0;i < 5; i++){
                if(mColorViews[i]==v){
                    mSelectedColor = i;
                    onSelectedColorChanged();
                }
            }
        }
    };

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
