package com.cs65.colorpal.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs65.colorpal.models.ColorPalette;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FirebaseService {


    private FirebaseFirestore db;
    private static final String PALETTES_COLLECTION  = "palettes";
    private static final String LOG_TAG = "FirebaseService";
    private static final String USER_ID = "userId";
    private Bitmap imageBitmap;
    private FirebaseUser currentUser;

    public FirebaseService(){
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void createNewPalette(ColorPalette colorPalette){
        savePalette(colorPalette);
    }

    public Map<String, Object> createPaletteData(ColorPalette palette){

        Map<String, Object> paletteData = new HashMap<>();
        paletteData.put("username", currentUser.getDisplayName());
        paletteData.put("userId", currentUser.getUid());
        paletteData.put("swatches", palette.getSwatches());
        paletteData.put("downloadUrl", palette.getDownloadUrl());
        paletteData.put("tags", palette.getTags());
        paletteData.put("title", palette.getTitle());
        paletteData.put("docId", palette.getDocId());
        paletteData.put("privacy", palette.getPrivacy());
        return paletteData;
    }

    public CollectionReference fetchPalettesReference(){
        return db.collection(PALETTES_COLLECTION);
    }

    private void savePalette(ColorPalette colorPalette){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Map<String, Object> paletteData = createPaletteData(colorPalette);
                String docId =  colorPalette.getDocId();
                db.collection(PALETTES_COLLECTION)
                        .document(docId)
                        .set(paletteData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + docId);
                            uploadImage(colorPalette, docId);
                        })
                        .addOnFailureListener(e -> Log.w(LOG_TAG, "Error adding document", e));
            }
        }.start();
    }

    public void uploadImage(ColorPalette colorPalette, String docId) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                final StorageReference imageRef = storageRef.child("images/" + currentUser.getUid() +"-" + UUID.randomUUID());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = colorPalette.getBitmap();
                Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true)
                        .compress(Bitmap.CompressFormat.JPEG, 80, baos);
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
                            Map<String, Object> update = new HashMap<>();
                            update.put("downloadUrl", downloadUri.toString());
                            db.collection(PALETTES_COLLECTION)
                                    .document(docId)
                                    .update(update);

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        }.start();
    }
}
