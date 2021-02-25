package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cs65.colorpal.views.fragments.HomeFragment;
import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private GoogleSignInClient googleSignInClient;
    private static final int SIGN_IN_CODE = 1001;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initializeSignInButton();
        initializeLoginViewModel();
        initializeGoogleSignInClient();
    }

    private void initializeSignInButton(){
        MaterialButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(v -> signIn());
    }

    private void initializeLoginViewModel(){
        loginViewModel = new LoginViewModel(getApplication());
    }

    private void initializeGoogleSignInClient(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    public  void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("papelog", String.valueOf(data));
        if (requestCode == SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                getAuthCredentials(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    public void getAuthCredentials(GoogleSignInAccount googleSignInAccount){
        String tokenId = googleSignInAccount.getIdToken();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenId, null );
        signInWithAuthCredential(authCredential);
    }

    private void signInWithAuthCredential(AuthCredential authCredential){
        loginViewModel.signInWithGoogle(authCredential);
        loginViewModel.authenticatedUserLiveData.observe(this, authenticatedUser ->{
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        });
    }

}
