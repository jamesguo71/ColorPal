package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    private void initializeGoogleSignInClient(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestProfile()
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInClient.revokeAccess();
    }

    public  void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                getAuthCredentials(account);
            } catch (ApiException e) {
                Log.w(TAG,  e.toString());
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
        loginViewModel.authenticatedUser.observe(this, authenticatedUser ->{
            String username = mAuth.getCurrentUser().getDisplayName();
            Intent loadingIntent = new Intent(this, LoadingActivity.class);
            loadingIntent.putExtra("message", "Hi " + username + "!");
            loadingIntent.putExtra("nextActivity", LoadingActivity.MAIN_ACTIVITY);
            startActivity(loadingIntent);
        });
    }

}
