package com.cs65.colorpal.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cs65.colorpal.models.ColorPalette;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class FirebaseService implements Runnable{

    private FirebaseFirestore db;
    private static final String PALETTES_COLLECTION  = "palettes";
    private static final String LOG_TAG = "FirebaseService";
    private Bitmap imageBitmap;

    public FirebaseService(){
        db = FirebaseFirestore.getInstance();
    }

    public void createNewPalette(ColorPalette colorPalette){
        uploadImage(colorPalette.getBitmap());
        savePalette(colorPalette);
//        db.collection(PALETTES_COLLECTION)
//                .add(colorPalette)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(LOG_TAG, "ColorPalette added with following ID " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(LOG_TAG, "Error adding document", e);
//                    }
//                })
//        ;
    }

    public void savePalette(ColorPalette colorPalette){
        new Thread(){
            @Override
            public void run() {
                super.run();
                ArrayList<Integer> swatches = colorPalette.getSwatches();
                Map<String, Integer> swatchMap= new HashMap<>();
                for(int i = 0; i<swatches.size(); i++){
                    swatchMap.put(String.valueOf(i),swatches.get(i));
                }

                db.collection(PALETTES_COLLECTION)
                        .add(swatchMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(LOG_TAG, "Error adding document", e);
                            }
                        });
            }
        }.start();
    }

    public CollectionReference fetchHomePalettesRef(){
        return db.collection(PALETTES_COLLECTION);
    }

    @Override
    public void run() {
    }

    public void uploadImage(Bitmap bitmap) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                final StorageReference imageRef = storageRef.child("images/" + "1");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Asynchronously uploads byte data to this StorageReference
                UploadTask uploadTask = imageRef.putBytes(data);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        };
    }
}
