package com.cs65.colorpal.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cs65.colorpal.models.ColorPalette;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseService {

    private FirebaseFirestore db;
    private static final String PALETTES_COLLECTION  = "palettes";
    private static final String LOG_TAG = "FirebaseService";

    public FirebaseService(){
        db = FirebaseFirestore.getInstance();
    }

    public void createNewPalette(ColorPalette colorPalette){
        db.collection(PALETTES_COLLECTION)
                .add(colorPalette)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "ColorPalette added with following ID " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                })
        ;
    }
}
