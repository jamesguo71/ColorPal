package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.views.activities.InspectActivity;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class PaletteListAdapter extends RecyclerView.Adapter<PaletteListAdapter.PaletteViewHolder> {

    private final LayoutInflater mInflater;
    private final Context context;
    private List<ColorPalette> mPalettes;

    public PaletteListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);     // Crashes here sometimes
        this.context = context;
    }

    @NonNull
    @Override
    public PaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.palette_item_view, parent, false);
        return new PaletteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PaletteViewHolder holder, int position) {
        if (mPalettes != null) {
            ColorPalette palette = mPalettes.get(position);
            setupPalette(holder, palette);
        } else {
            holder.title.setText("just a placeholder...");
        }
    }


    private void setupPalette(PaletteViewHolder holder, ColorPalette palette) {
        List<Palette.Swatch> swatches = toSwatches(palette.getSwatches());
        if (!swatches.isEmpty()) {
            SwatchListAdapter swatchesViewAdapter = new SwatchListAdapter(swatches);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            holder.title.setText("Nature Scene");
            holder.swatches.setLayoutManager(layoutManager);
            holder.swatches.setAdapter(swatchesViewAdapter);
        }
    }

    private List<Palette.Swatch> toSwatches(ArrayList<Integer> swatches) {
        int DUMMY = 100;
//        swatches = new ArrayList<>();
//        swatches.add(1);
//        swatches.add(2);
//        swatches.add(3);
//        swatches.add(4);
//        swatches.add(5);
//        swatches.add(6);
        List<Palette.Swatch> newSwatches = new ArrayList<>();

        for (Integer swatch: swatches){
            newSwatches.add(new Palette.Swatch(swatch, DUMMY));
        }
        return newSwatches;
    }

    public void setPalettes(List<ColorPalette> palettes) {
        mPalettes = palettes;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (mPalettes == null)
            return 0;
        else
            return mPalettes.size();
    }

    public class PaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public RecyclerView swatches;

        public PaletteViewHolder(@NonNull View itemView) {
            super(itemView);
//            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.paletteTitle);
            swatches = itemView.findViewById(R.id.paletteSwatches);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            ColorPalette palette =  mPalettes.get(pos);
            Intent intent = new Intent(context, InspectActivity.class);
            context.startActivity(intent);
        }
    }
}
