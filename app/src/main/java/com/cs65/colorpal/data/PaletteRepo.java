package com.cs65.colorpal.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.models.PaletteTag;
import com.cs65.colorpal.services.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class PaletteRepo  {

    private static final int MAX_COLOR_COUNT = 16;
    private static FirebaseService firebaseService;

    /**
     * From Android's documentation:
     * "The RequestQueue manages worker threads for running the network operations,
     * reading from and writing to the cache, and parsing responses"
     */

    private RequestQueue requestQueue;
    private MutableLiveData<JSONArray> data;
    private static final String LOG_TAG =  "PaletteRepo";
    private static MutableLiveData<ArrayList<ColorPalette>> mHomeColorPaletteList;
    private static MutableLiveData<ArrayList<ColorPalette>> mUserLibraryColorPaletteList;
    private static MutableLiveData<ColorPalette> fetchedPalette;
    private FirebaseUser currentUser;

    public PaletteRepo(Application application){
        requestQueue = Volley.newRequestQueue(application);
        firebaseService = new FirebaseService();
        mHomeColorPaletteList = new MutableLiveData<>();
        mUserLibraryColorPaletteList = new MutableLiveData<>();
        fetchedPalette = new MutableLiveData<>();
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
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<QueryDocumentSnapshot> snapshots = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            if(doc.toObject(ColorPalette.class).getPrivacy()==0)
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

    public String createNewId(){
        return firebaseService.fetchPalettesReference().document().getId();
    }

    public void deletePalette(String docId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                DocumentReference docRef = firebaseService.fetchPalettesReference().document(docId);
                //delete all fields
                Map<String,Object> updates = new HashMap<>();
                updates.put("username", FieldValue.delete());
                updates.put("userId", FieldValue.delete());
                updates.put("swatches", FieldValue.delete());
                updates.put("downloadUrl", FieldValue.delete());
                updates.put("tags", FieldValue.delete());
                updates.put("docId", FieldValue.delete());
                docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deletePaletteDoc(docId);
                    }
                });
            }
        }.start();
    }

    private void deletePaletteDoc(String docId){
        //delete document
        new Thread(){
            @Override
            public void run() {
                super.run();
                firebaseService.fetchPalettesReference()
                        .document(docId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(LOG_TAG, "DocumentSnapshot "+docId+" successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(LOG_TAG, "Error deleting document", e);
                            }
                        });
            }
        }.start();
    }

    public void searchByTag(String tag, MutableLiveData<ArrayList<ColorPalette>> mHomeColorPaletteList) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                firebaseService.fetchPalettesReference()
                        .whereArrayContainsAny("tags",
                                Arrays.asList(
                                        new PaletteTag(tag),
                                        tag.length() > 0 ? new PaletteTag(tag.substring(0, 1).toUpperCase() + tag.substring(1)):tag)
                        )
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<QueryDocumentSnapshot> snapshots = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        if(doc.toObject(ColorPalette.class).getPrivacy()==0)
                                            snapshots.add(doc);
                                    }
                                    mHomeColorPaletteList.postValue(convertFromSnapshotsToColourPalettes(snapshots));
                                } else {
                                    Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }.start();

//        return mHomeColorPaletteList;
    }

    public void updatePalette(ColorPalette colorPalette, String docId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                DocumentReference docRef = firebaseService.fetchPalettesReference().document(docId);
                Map<String,Object> updates = new HashMap<>();
                updates.put("swatches", colorPalette.getSwatches());
                updates.put("tags", colorPalette.getTags());
                updates.put("title", colorPalette.getTitle());
                updates.put("privacy", colorPalette.getPrivacy());
                docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error updating document", e);
                    }
                });
            }
        }.start();
    }

    public void updateEditableByDocId(String docId, MutableLiveData<String> title,
                                      MutableLiveData<ArrayList<Integer>> swatchesList,
                                      MutableLiveData<ArrayList<PaletteTag>> tagsList,
                                      MutableLiveData<Integer> privacy){
        DocumentReference docRef = firebaseService.fetchPalettesReference().document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ColorPalette colorPalette = document.toObject(ColorPalette.class);
                        title.postValue(colorPalette.getTitle());
                        swatchesList.postValue(colorPalette.getSwatches());
                        tagsList.postValue((ArrayList<PaletteTag>) colorPalette.getTags());
                        privacy.postValue(colorPalette.getPrivacy());
                        Log.d(LOG_TAG, "Find documentSnapshot data.");
                    } else {
                        Log.d(LOG_TAG, "No such document");
                    }
                } else {
                    Log.d(LOG_TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public MutableLiveData<ColorPalette> getPaletteById(String paletteId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                    firebaseService.fetchPalettesReference()
                            .document(paletteId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ColorPalette palette = document.toObject(ColorPalette.class);
                                if (palette != null) {
                                    if (!palette.getDownloadUrl().equals(""))
                                        fetchedPalette.postValue(palette);
                                    else {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    Thread.sleep(100);
                                                    getPaletteById(paletteId);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                    }
                                }
                            } else {
                                Log.d(LOG_TAG, "No such document");
                            }
                        } else {
                            Log.d(LOG_TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        }.start();
        return fetchedPalette;
    }
}
