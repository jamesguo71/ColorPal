package com.cs65.colorpal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("papelog", String.valueOf(currentUser));
    }

    public void signOut(View v) {
        Intent loginIntent = new Intent( this, LoginActivity.class);
        mAuth.signOut();
        startActivity(loginIntent);
//        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            public void onComplete(Task<Void> task) {
//                startActivity(loginIntent);
//            }
//        });
    }
}