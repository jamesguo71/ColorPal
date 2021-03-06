package com.cs65.colorpal.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.palette.graphics.Palette;

import java.util.ArrayList;

public class EditViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> mSelectedColor = new MutableLiveData<Integer>();
    private MutableLiveData<ArrayList<Integer>> mSwatches = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> mSwatches0 = new MutableLiveData<>();

    public EditViewModel(Application application){
        super(application);
    }

    public void setSelectedColor(int colorIdx){
        mSelectedColor.setValue(colorIdx);
    }

    public MutableLiveData<Integer> getSelectedColor(){
        return mSelectedColor;
    }

    public void setSwatches(ArrayList<Integer> swatches) { mSwatches.setValue(swatches); }

    public MutableLiveData<ArrayList<Integer>> getSwatches() { return mSwatches; }

    public void setSwatches0(ArrayList<Integer> swatches) { mSwatches0.setValue(swatches); }

    //update selected color
    public void onColorChange(int color){
        mSwatches.getValue().set(mSelectedColor.getValue(),color);
    }

    public int getSelectedColorValue(){ return mSwatches.getValue().get(mSelectedColor.getValue()); }

    public void resetSwatches() {
        ArrayList<Integer> newSwatches = new ArrayList<>();
        for (int i = 0; i < mSwatches0.getValue().size(); i++) {
            newSwatches.add(mSwatches0.getValue().get(i));
        }
        mSwatches.setValue(newSwatches);
    }
}
