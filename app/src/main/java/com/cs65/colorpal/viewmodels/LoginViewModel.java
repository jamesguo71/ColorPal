package com.cs65.colorpal.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.cs65.colorpal.models.User;
import com.cs65.colorpal.repos.LoginRepository;
import com.google.firebase.auth.AuthCredential;

public class LoginViewModel extends AndroidViewModel {

    private LoginRepository loginRepository;
    public LiveData<User> authenticatedUserLiveData; // Not changing data

    public LoginViewModel(Application application){
        super(application);
        loginRepository = new LoginRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = loginRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }


}
