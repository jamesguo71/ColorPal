package com.cs65.colorpal.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.FirebaseService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Single;

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
    public static final String COLOUR_LOVERS_URL = "https://www.colourlovers.com/api/palettes?format=json";
    private MutableLiveData<ArrayList<ColorPalette>> mHomeColorPaletteList;

    public PaletteRepo(Application application){
        requestQueue = Volley.newRequestQueue(application);
        firebaseService = new FirebaseService();
        mHomeColorPaletteList = new MutableLiveData<>();
    }

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate());
    }

    public void createNew(Bitmap bitmap){
        ColorPalette newColorPalette = new ColorPalette();
        newColorPalette.setBitmap(bitmap);
        firebaseService.createNewPalette(newColorPalette);
    }

    public ArrayList<ColorPalette> convertFromSnapshotsToColourPalettes(ArrayList<QueryDocumentSnapshot> documentSnapshots){
        ArrayList<ColorPalette> palettes = new ArrayList<>();
        for (QueryDocumentSnapshot document : documentSnapshots) {
            ColorPalette colorPalette = document.toObject(ColorPalette.class);
            palettes.add(colorPalette);
        }
        return palettes;
    }

    public MutableLiveData<JSONArray> fetchData(String apiUrl) throws InterruptedException {
        data = new MutableLiveData<>();
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                        (Request.Method.GET, apiUrl, null,
                                new Response.Listener<JSONArray>() {
                                    public void onResponse(JSONArray response) {
                                        data.setValue(response);
                                    }
                                }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                requestQueue.add(jsonArrayRequest);
            }
        };
        t.start();
        return data;
    }

    public void savePaletteToDB(ColorPalette colorPalette){
        firebaseService.savePalette(colorPalette);
    }

    public MutableLiveData<ArrayList<ColorPalette>> fetchHomeColorPalettes() throws InterruptedException {
        firebaseService.fetchHomePalettesRef()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(LOG_TAG, "Listen failed.", error);
                            return;
                        }
                        ArrayList<QueryDocumentSnapshot> snapshots = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : documentSnapshots) {
                            snapshots.add(doc);
                        }
                        mHomeColorPaletteList.setValue(convertFromSnapshotsToColourPalettes(snapshots));
                    }
                });
        return mHomeColorPaletteList;
    }
}
