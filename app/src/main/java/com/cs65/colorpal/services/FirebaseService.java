package com.cs65.colorpal.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.cs65.colorpal.models.ColorPalette;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

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

    @Override
    public void run() {

    }

    public void uploadImage(Bitmap bitmap) {
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
                Log.d("papelog", String.valueOf(imageRef.getDownloadUrl()));
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
}
