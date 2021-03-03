package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cs65.colorpal.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    public final static String MAIN_ACTIVITY = "MainActivity";
    private  String nextActivity, message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent intent = getIntent();
        message = intent.getStringExtra("message");
        nextActivity = intent.getStringExtra("nextActivity");
        TextView textView = (TextView) findViewById(R.id.loading_message);
        textView.setText(message);
        goToNextActivity();
    }

    public void goToNextActivity(){
        Intent intent = null;
        if(nextActivity.equals(MAIN_ACTIVITY)){
            intent = new Intent(this, MainActivity.class);
        }
        Intent finalIntent = intent;
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(finalIntent != null)
                        startActivity(finalIntent);
                    }
                }, 3000
        );
    }
}
