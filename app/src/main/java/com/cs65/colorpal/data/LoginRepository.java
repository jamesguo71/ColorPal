package com.cs65.colorpal.data;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.cs65.colorpal.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final String TAG = "LoginRepository";

    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    Uri image = firebaseUser.getPhotoUrl();
                    Log.d("papelog", String.valueOf(image));
                    User user = new User(name, email, uid, image);
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                Log.d(TAG, authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

}
