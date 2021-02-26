package com.cs65.colorpal.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

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

import org.json.JSONArray;

import io.reactivex.Single;

public class PaletteRepo {

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

    public PaletteRepo(Application application){
        requestQueue = Volley.newRequestQueue(application);
        firebaseService = new FirebaseService();
    }

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate());
    }

    public void createNew(Bitmap bitmap){
        ColorPalette newColorPalette = new ColorPalette();
        newColorPalette.setBitmap(bitmap);
        firebaseService.createNewPalette(newColorPalette);
    }

    public MutableLiveData<JSONArray> fetchData(String apiUrl){
        data = new MutableLiveData<>();
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
        return data;
    }

    public void savePaletteToDB(ColorPalette colorPalette){
        firebaseService.savePalette(colorPalette);
    }
}
