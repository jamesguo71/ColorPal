package com.cs65.colorpal.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.cs65.colorpal.R;
import com.cs65.colorpal.data.PaletteRepo;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.utils.Utils;

import org.json.JSONArray;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class PaletteViewModel extends AndroidViewModel {
    private static final String PALETTE_TAG = "PaletteViewModel";
    private PaletteRepo paletteRepo;
    private CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Palette> extractedColorPaletteData = new MutableLiveData<>();
    private MutableLiveData<Uri> selectedImage = new MutableLiveData<>();
    public LiveData<JSONArray> homePagePalettes;
    private MutableLiveData<ArrayList<Integer>> mSwatchesList = new MutableLiveData<>();

    public PaletteViewModel(Application application) {
        super(application);
        paletteRepo = new PaletteRepo(application);
    }

    private void createNew(Uri uri) throws IOException {
        Bitmap bitmap = convertUriToBitmap(uri);
        paletteRepo.createNew(bitmap);
    }

    private Bitmap convertUriToBitmap(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getApplication().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap img = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return img;
    }

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

    public void extractNewFromUri(Uri photoUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = convertUriToBitmap(photoUri);
                    extractColorPalette(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }}).start();
    }

    public void setSwatchesList(ArrayList<Integer> swatches){
        mSwatchesList.setValue(swatches);
    }

    public MutableLiveData<ArrayList<Integer>> getSwatches(){
        return mSwatchesList;
    }

    public void initSwatchesArrayList(){
        ArrayList<Integer> swatchValues = new ArrayList<>();
        List<Palette.Swatch> swatches = extractedColorPaletteData.getValue().getSwatches();
        if(swatches!=null) {
            for (Palette.Swatch swatch : swatches) {
                swatchValues.add(swatch.getRgb());
            }
        }
        mSwatchesList.postValue(swatchValues);
    }

    public void setSelectedImageUri(Uri uri){
        selectedImage.setValue(uri);
    }

    public LiveData<Palette> getColorPaletteData() {
        return extractedColorPaletteData;
    }

    public LiveData<Uri> getSelectedImage() {
        return selectedImage;
    }

    public void fetchHomePagePalettes(){
        homePagePalettes = paletteRepo.fetchData(PaletteRepo.COLOUR_LOVERS_URL);
    }

    public void savePaletteToDB() throws IOException {
        ColorPalette newColorPalette = new ColorPalette();
        newColorPalette.setBitmap(convertUriToBitmap(selectedImage.getValue()));
        newColorPalette.setSwatches(mSwatchesList.getValue());
        paletteRepo.savePaletteToDB(newColorPalette);
    }
}
