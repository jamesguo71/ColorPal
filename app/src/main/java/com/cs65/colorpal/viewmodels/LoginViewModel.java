package com.cs65.colorpal.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.cs65.colorpal.models.User;
import com.cs65.colorpal.data.LoginRepository;
import com.google.firebase.auth.AuthCredential;

public class LoginViewModel extends AndroidViewModel {

    private static LoginRepository loginRepository;
    public static LiveData<User> authenticatedUser; // Not changing data

    public LoginViewModel(Application application){
        super(application);
        loginRepository = new LoginRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUser = loginRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }
}
