package com.cs65.colorpal.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.palette.graphics.Palette;

public class EditViewModel extends ViewModel {
    private MutableLiveData<Integer> mSelectedColor = new MutableLiveData<Integer>();

    public EditViewModel(){}

    public void setSelectedColor(int colorIdx){
        mSelectedColor.setValue(colorIdx);
    }

    public MutableLiveData<Integer> getSelectedColor(){
        return mSelectedColor;
    }
}
