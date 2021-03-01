package com.cs65.colorpal.viewmodels;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.FirebaseService;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseViewModel extends ViewModel {

    private final static String FIREBASE_VM_TAG = "FirebaseViewModel";
    private static final String LOG_TAG = "FirebaseViewModel";

    private FirebaseService firebaseService;
    private MutableLiveData<List<ColorPalette>> mHomeColorPaletteList = new MutableLiveData<>();

    public FirebaseViewModel(){
        firebaseService = new FirebaseService();
    }

    public LiveData<List<ColorPalette>> getHomeColorPalettes(){
        return mHomeColorPaletteList;
    }

    public LiveData<List<ColorPalette>> fetchHomeColorPalettesFromDB(){
        firebaseService.fetchPalettesReference()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(LOG_TAG, "Listen failed.", error);
                            return;
                        }
                        List<ColorPalette> colorPaletteList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            ColorPalette newColorPalette = new ColorPalette();
                            ArrayList<Integer> newSwatches = new ArrayList<Integer>();
                            for(int i=0; i<doc.getData().size(); i++){
                                String str = String.valueOf(doc.getData().get(String.valueOf(i)));
                                newSwatches.add(Integer.parseInt(str));
                            }
                            newColorPalette.setSwatches(newSwatches);
                            colorPaletteList.add(newColorPalette);
//                            Log.d(LOG_TAG, "Data: " + value);
                        }
                        mHomeColorPaletteList.setValue(colorPaletteList);
                    }
                });
        return mHomeColorPaletteList;
    }
}
