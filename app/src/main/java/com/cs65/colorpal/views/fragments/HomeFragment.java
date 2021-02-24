package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cs65.colorpal.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Log.d("papelog", String.valueOf(currentUser));
//    }
//
//    public void signOut(View v) {
//        Intent loginIntent = new Intent( this, LoginActivity.class);
//        mAuth.signOut();
//        startActivity(loginIntent);
//    }
}