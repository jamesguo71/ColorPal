package com.cs65.colorpal.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.palette.graphics.Palette;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.ColourLoversService;
import com.cs65.colorpal.services.FirebaseService;

import org.json.JSONArray;

import io.reactivex.Single;

public class PaletteRepo {
    private static final int MAX_COLOR_COUNT = 32;
    private static FirebaseService firebaseService;
    private static ColourLoversService colourLoversService;
    private Application application;

    public PaletteRepo(Application application){
        firebaseService = new FirebaseService();
        colourLoversService = new ColourLoversService(application);
    }

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate());
    }

    public void createNew(Bitmap bitmap){
        ColorPalette newColorPalette = new ColorPalette();
        newColorPalette.setBitmap(bitmap);
        firebaseService.createNewPalette(newColorPalette);
    }

    public JSONArray fetchHomePagePalettes(){
        JSONArray temp = colourLoversService.fetchPalettes();
        Log.d("papelog", String.valueOf(temp));
       return temp;
    }
}
