package com.cs65.colorpal.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class PaletteRepo  {

    private static final int MAX_COLOR_COUNT = 32;
    private static FirebaseService firebaseService;

    /**
     * From Android's documentation:
     * "The RequestQueue manages worker threads for running the network operations,
     * reading from and writing to the cache, and parsing responses"
     */

    private RequestQueue requestQueue;
    private MutableLiveData<JSONArray> data;
    private static final String LOG_TAG =  "ColourLoversService";
    private static MutableLiveData<ArrayList<ColorPalette>> mHomeColorPaletteList;
    private static MutableLiveData<ArrayList<ColorPalette>> mUserLibraryColorPaletteList;
    private FirebaseUser currentUser;

    public PaletteRepo(Application application){
        requestQueue = Volley.newRequestQueue(application);
        firebaseService = new FirebaseService();
        mHomeColorPaletteList = new MutableLiveData<>();
        mUserLibraryColorPaletteList = new MutableLiveData<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate()).subscribeOn(Schedulers.computation());
    }

    public ArrayList<ColorPalette> convertFromSnapshotsToColourPalettes(ArrayList<QueryDocumentSnapshot> documentSnapshots){
        ArrayList<ColorPalette> palettes = new ArrayList<>();
        for (QueryDocumentSnapshot document : documentSnapshots) {
            ColorPalette colorPalette = document.toObject(ColorPalette.class);
            palettes.add(colorPalette);
        }

        return palettes;
    }

    public void savePaletteToDB(ColorPalette colorPalette){
        firebaseService.createNewPalette(colorPalette);
    }

    public MutableLiveData<ArrayList<ColorPalette>> fetchHomeColorPalettes() throws InterruptedException {

        new Thread(){
            @Override
            public void run() {
                super.run();
                if(currentUser != null){
                    firebaseService.fetchPalettesReference()
                            .whereNotEqualTo("userId", currentUser.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<QueryDocumentSnapshot> snapshots = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            snapshots.add(doc);
                                        }
                                        mHomeColorPaletteList.postValue(convertFromSnapshotsToColourPalettes(snapshots));
                                    } else {
                                        Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        }.start();

        return mHomeColorPaletteList;
    }

    public MutableLiveData<ArrayList<ColorPalette>> fetchUserLibraryColorPalettes() throws InterruptedException {

        new Thread(){
            @Override
            public void run() {
                super.run();
                if(currentUser != null){
                    firebaseService.fetchPalettesReference()
                            .whereEqualTo("userId", currentUser.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<QueryDocumentSnapshot> snapshots = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            snapshots.add(doc);
                                        }
                                        mUserLibraryColorPaletteList.postValue(convertFromSnapshotsToColourPalettes(snapshots));
                                    } else {
                                        Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        }.start();
        return mUserLibraryColorPaletteList;
    }
}
