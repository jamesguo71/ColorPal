package com.cs65.colorpal.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.cs65.colorpal.R;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;

public class

SearchableActivity extends AppCompatActivity{
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        adapter = new PaletteListAdapter(this, true, "");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchColor(query);
        }
    }

    private void searchColor(String query) {

    }


}