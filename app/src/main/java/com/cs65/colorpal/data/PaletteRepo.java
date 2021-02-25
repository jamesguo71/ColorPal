package com.cs65.colorpal.data;

import android.graphics.Bitmap;
import androidx.palette.graphics.Palette;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.FirebaseService;

import io.reactivex.Single;

public class PaletteRepo {
    private static final int MAX_COLOR_COUNT = 32;
    private static FirebaseService firebaseService;

    public PaletteRepo(){
        firebaseService = new FirebaseService();
    }

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate());
    }

    public void createNew(String imageUrl){
        ColorPalette newColorPalette = new ColorPalette();
        newColorPalette.setImageUrl(imageUrl);
        firebaseService.createNewPalette(newColorPalette);
    }
}
