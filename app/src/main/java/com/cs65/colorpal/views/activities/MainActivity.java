package com.cs65.colorpal.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs65.colorpal.R;
import com.cs65.colorpal.views.fragments.HomeFragment;
import com.cs65.colorpal.views.fragments.LibraryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment(new HomeFragment());
        setBottomNavigationView();
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_button:
                HomeFragment homeFragment = new HomeFragment();
                openFragment(homeFragment);
                return true;
            case R.id.my_palettes_button:
                LibraryFragment libraryFragment = new LibraryFragment();
                openFragment(libraryFragment);
                return true;
            case R.id.settings_button:
                return true;
        }
        return false;
    }

    private void openFragment(Fragment fragment) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragment_container_view, fragment)
        .addToBackStack(null)
        .commit();
    }
}
