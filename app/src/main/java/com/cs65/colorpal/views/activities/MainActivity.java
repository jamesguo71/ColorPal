package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.cs65.colorpal.R;
import com.cs65.colorpal.models.User;
import com.cs65.colorpal.viewmodels.LoginViewModel;
import com.cs65.colorpal.views.fragments.HomeFragment;
import com.cs65.colorpal.views.fragments.LibraryFragment;
import com.cs65.colorpal.views.fragments.UnsplashFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private static final String LOG_TAG = "MainActvity";
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int GALLERY_REQUEST_CODE = 2;
    public static final String CAMERA_IMAGE_FILENAME = "mycolorpal";
    public static final String CAMERA_IMAGE_SUFFIX = ".jpg";
    public static final String PHOTO_URI = "photoUri";
    private MutableLiveData<Uri> currentPhotoPath;
    private FirebaseAuth mAuth;
    private Uri photoURI;
    private MaterialToolbar materialToolbar;
    private LoginViewModel loginViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            openFragment(new HomeFragment());
        }
        setBottomNavigationView();
        setUpTopNavigationView();
        initializeVariables();
    }

    private File createImageFile() throws IOException {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File  image = File.createTempFile(CAMERA_IMAGE_FILENAME, CAMERA_IMAGE_SUFFIX, storageDirectory );
        return image;
    }

    public void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void dispatchSelectPictureIntent(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
    }

    public void initializeVariables(){
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        currentPhotoPath = new MutableLiveData<>();
        materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
    }

    public LoginViewModel getLoginViewModelInstance(){
        return loginViewModel;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode != RESULT_OK) return;
        if(requestCode == CAMERA_REQUEST_CODE){
            Intent inspectIntent = new Intent(this, InspectActivity.class);
            inspectIntent.putExtra(PHOTO_URI, photoURI.toString());
            startActivity(inspectIntent);
        } else if ( requestCode == GALLERY_REQUEST_CODE){
            if(intent != null){

            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_navigation, menu);
        if( loginViewModel.authenticatedUser!= null){
            User user = loginViewModel.authenticatedUser.getValue();
            ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
            Log.d("papelog",user.getImage().toString());
            Glide.with(this).load(user.getImage().toString()).into(profileImage);
        }
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_button:
                materialToolbar.setTitle("Explore");
                HomeFragment homeFragment = new HomeFragment();
                openFragment(homeFragment);
                return true;
            case R.id.my_palettes_button:
                materialToolbar.setTitle("My Palettes");
                LibraryFragment libraryFragment = new LibraryFragment();
                openFragment(libraryFragment);
                return true;
            case R.id.unsplash_button:
                materialToolbar.setTitle("Unsplash Images");
                UnsplashFragment unsplashFragment = new UnsplashFragment();
                openFragment(unsplashFragment);
                return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more:
                signOut();
                break;
        }
        return true;
    }

    public void onStart() {
        super.onStart();
    }

    private void openFragment(Fragment fragment) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragment_container_view, fragment)
        .addToBackStack(null)
        .commit();
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public void setUpTopNavigationView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    public void signOut() {
        Intent loginIntent = new Intent( this, LoginActivity.class);
        mAuth.signOut();
        startActivity(loginIntent);
    }
}
