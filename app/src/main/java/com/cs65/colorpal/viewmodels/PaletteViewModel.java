package com.cs65.colorpal.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.cs65.colorpal.R;
import com.cs65.colorpal.data.PaletteRepo;

import org.json.JSONArray;

import java.io.FileDescriptor;
import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;

public class PaletteViewModel extends AndroidViewModel {
    private static final String PALETTE_TAG = "PaletteViewModel";
    private PaletteRepo paletteRepo;
    private CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Palette> extractedColorPaletteData = new MutableLiveData<>();
    private MutableLiveData<Uri> selectedImage = new MutableLiveData<>();
    public JSONArray homePagePalettes;

    public PaletteViewModel(Application application) {
        super(application);
        paletteRepo = new PaletteRepo(application);
        fetchHomePagePalettes();
    }

    public void createNew(Uri uri) throws IOException {
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

    public LiveData<Palette> getColorPalette() {
        return extractedColorPaletteData;
    }

    public LiveData<Uri> getSelectedImage() {
        return selectedImage;
    }

    public JSONArray getHomePagePalettes(){
        return homePagePalettes;
    }

    public void fetchHomePagePalettes(){
        homePagePalettes = paletteRepo.fetchHomePagePalettes();
    }

    public void setSelectedImage(Uri photoURI) throws IOException {
        selectedImage.setValue(photoURI);
        createNew(photoURI);
    }
}
