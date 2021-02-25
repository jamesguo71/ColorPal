package com.cs65.colorpal.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.views.adapter.SwatchDetailsListAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

public class SwatchesDetailActivity extends AppCompatActivity {
    public static final String SWATCH_VALUES = "values";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swatches_list);
        RecyclerView swatchesView = findViewById(R.id.swatch_details_list);

        Intent intent = getIntent();
        if (intent != null) {
            List<Integer> swatchValues = intent.getIntegerArrayListExtra(SWATCH_VALUES);
            SwatchDetailsListAdapter swatchDetailsListAdapter = new SwatchDetailsListAdapter(swatchValues);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            swatchesView.setLayoutManager(layoutManager);
            swatchesView.setAdapter(swatchDetailsListAdapter);
        }
    }
}
