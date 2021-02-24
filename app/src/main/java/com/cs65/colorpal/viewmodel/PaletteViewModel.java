package com.cs65.colorpal.viewmodel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.palette.graphics.Palette;

import com.cs65.colorpal.R;
import com.cs65.colorpal.data.PaletteRepo;

import io.reactivex.disposables.CompositeDisposable;

public class PaletteViewModel extends ViewModel {
    private static final String PALETTE_TAG = "PaletteViewModel";
    private PaletteRepo paletteRepo = new PaletteRepo();
    private CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Palette> extractedColorPaletteData = new MutableLiveData<>();
    private MutableLiveData<Uri> selectedImage = new MutableLiveData<>();

    public PaletteViewModel() {}

    public void extractColorPalette(Bitmap bitmap) {
        disposables.add(paletteRepo.extractColorPalette(bitmap)
            .subscribe(palette -> extractedColorPaletteData.postValue(palette), error -> Log.e(PALETTE_TAG, error.toString()))
        );
    }

    // TODO: remove later
    public void testDummyPic() {
        Uri uri=Uri.parse("android.resource://com.cs65.colorpal/" + R.drawable.nature_photo);
        selectedImage.postValue(uri);
    }

    public LiveData<Palette> getColorPalette() {
        return extractedColorPaletteData;
    }

    public LiveData<Uri> getSelectedImage() {
        return selectedImage;
    }
}
