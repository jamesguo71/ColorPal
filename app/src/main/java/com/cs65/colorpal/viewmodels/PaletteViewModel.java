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

import com.cs65.colorpal.data.PaletteRepo;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.models.PaletteTag;
import com.cs65.colorpal.utils.Utils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;

public class PaletteViewModel extends AndroidViewModel{
    private static final String PALETTE_TAG = "PaletteViewModel";
    private PaletteRepo paletteRepo;
    private CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Palette> extractedColorPaletteData = new MutableLiveData<>();
    private MutableLiveData<Uri> selectedImage = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> mSwatchesList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> mOriginalSwatchesList = new MutableLiveData<>();
    public MutableLiveData<ArrayList<ColorPalette>> mHomeColorPaletteList;
    public MutableLiveData<ArrayList<ColorPalette>> mUserLibraryColorPaletteList;
    private MutableLiveData<ArrayList<PaletteTag>> mTagsList = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedColor = new MutableLiveData<>();
    private MutableLiveData<Boolean> addColorEvent = new MutableLiveData<>();
    private MutableLiveData<String> title = new MutableLiveData<>();

    public PaletteViewModel(Application application) throws InterruptedException {
        super(application);
        mTagsList.setValue(new ArrayList<>());
        paletteRepo = new PaletteRepo(application);
        title.setValue("New Palette");
        fetchHomeColorPalettes();
        fetchUserLibraryColorPalettes();
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

    public void extractNewFromPhoneUri(Uri photoUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = convertUriToBitmap(photoUri);
                    extractColorPalette(bitmap);
                    Utils.generateImageLabel(bitmap, mTagsList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }}).start();
    }

    public void extractNewFromExternalUri(Uri photoUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(photoUri.toString());
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    extractColorPalette(bitmap);
                    Utils.generateImageLabel(bitmap, mTagsList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    public void fetchHomeColorPalettes() throws InterruptedException {
        mHomeColorPaletteList = paletteRepo.fetchHomeColorPalettes();
    }

    public void fetchUserLibraryColorPalettes() throws InterruptedException {
        mUserLibraryColorPaletteList = paletteRepo.fetchUserLibraryColorPalettes();
    }

    public void searchByTag(String tag){
        mHomeColorPaletteList = paletteRepo.searchByTag(tag);
    }

    public LiveData<Palette> getColorPaletteData() {
        return extractedColorPaletteData;
    }

    public LiveData<Uri> getSelectedImage() {
        return selectedImage;
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
        mOriginalSwatchesList.setValue((ArrayList<Integer>) swatchValues.clone());
    }

    public void setSwatchesList(ArrayList<Integer> swatches){
        mSwatchesList.setValue(swatches);
    }

    public void setSelectedImageUri(Uri uri){
        selectedImage.setValue(uri);
    }

    public void setTitle(String newTitle) { title.postValue(newTitle);}

    public MutableLiveData<String> getTitle() { return title; }

    public MutableLiveData<ArrayList<PaletteTag>> getTags() { return mTagsList; }

    public void addTag(String text){
        PaletteTag newTag = new PaletteTag(text);
        mTagsList.getValue().add(newTag);
        mTagsList.setValue(mTagsList.getValue());
    }

    public void savePaletteToDB() throws IOException {
        ColorPalette newColorPalette = new ColorPalette();
        if(selectedImage.getValue().toString().contains("images.unsplash.com")){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    URL url = null;
                    try {
                        url = new URL(selectedImage.getValue().toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    newColorPalette.setBitmap(bitmap);
                }
            }.start();
        } else {
            newColorPalette.setBitmap(convertUriToBitmap(selectedImage.getValue()));
        }
        newColorPalette.setSwatches(mSwatchesList.getValue());
        newColorPalette.setTags(mTagsList.getValue());
        newColorPalette.setTitle(getTitle().getValue());
        newColorPalette.setDocId(createNewDocId());        //generate a new id
        paletteRepo.savePaletteToDB(newColorPalette);
    }

    public void selectColor(int pixel) {
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        int rgb = Color.rgb(red, green, blue);
        selectedColor.postValue(rgb);
    }

    public LiveData<Integer> getSelectedColor() {
        return selectedColor;
    }

    public void addSelectedColor() {
        ArrayList<Integer> swatches = mSwatchesList.getValue();
        Set<Integer> swatchesSet = new HashSet<>(swatches);
        Integer selectedColorRgb = selectedColor.getValue();
        if (swatchesSet.add(selectedColorRgb)) {
            addColorEvent.postValue(true);
            swatches.add(selectedColor.getValue());
            mSwatchesList.postValue(swatches);
        } else {
            addColorEvent.postValue(false);
        }
    }

    public LiveData<Boolean> getAddColorEvent() {
        return addColorEvent;
    }

    public void deletePaletteFromDb(String docId) throws InterruptedException { paletteRepo.deletePalette(docId); }

    public String createNewDocId() { return paletteRepo.createNewId(); }

    public ArrayList<Integer> getOriginalSwatches(){ return mOriginalSwatchesList.getValue(); }
}
