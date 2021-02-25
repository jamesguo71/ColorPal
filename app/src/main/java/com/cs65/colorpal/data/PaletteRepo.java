package com.cs65.colorpal.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import androidx.palette.graphics.Palette;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.ColourLoversService;
import com.cs65.colorpal.services.FirebaseService;
import io.reactivex.Single;

public class PaletteRepo {
    private static final int MAX_COLOR_COUNT = 32;
    private static FirebaseService firebaseService;
    private static ColourLoversService colourLoversService;
    private Application application;

    public PaletteRepo(Application application){
        firebaseService = new FirebaseService();
        colourLoversService = new ColourLoversService(application);
        getHomePagePalettes();
    }

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate());
    }

    public void createNew(Bitmap bitmap){
        ColorPalette newColorPalette = new ColorPalette();
        newColorPalette.setBitmap(bitmap);
        firebaseService.createNewPalette(newColorPalette);
    }

    public void getHomePagePalettes(){
        colourLoversService.fetchPalettes();
    }
}
