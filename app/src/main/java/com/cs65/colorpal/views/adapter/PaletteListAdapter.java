package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.views.activities.PaletteDetailActivity;
import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.utils.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

public class PaletteListAdapter extends RecyclerView.Adapter<PaletteListAdapter.PaletteViewHolder> {

    private final LayoutInflater mInflater;
    private final Context context;
    private List<ColorPalette> mPalettes;

    public PaletteListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
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
        List<Palette.Swatch> swatches = Utils.toSwatches(palette.getSwatches());
        if (!swatches.isEmpty()) {
            SwatchListAdapter swatchesViewAdapter = new SwatchListAdapter(swatches, holder);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            holder.title.setText("Nature Scene");
            holder.username.setText("By " + palette.getUsername());
            holder.swatches.setLayoutManager(layoutManager);
            holder.swatches.setAdapter(swatchesViewAdapter);
        }
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
        public TextView title, username;
        public RecyclerView swatches;

        public PaletteViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.paletteTitle);
            username = itemView.findViewById(R.id.username);
            swatches = itemView.findViewById(R.id.paletteSwatches);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            ColorPalette palette =  mPalettes.get(pos);

            Intent intent = new Intent(context, PaletteDetailActivity.class);

            // Todo: Currently palette images unset, so getImageUrl returns null.
//            intent.putExtra(PaletteDetailActivity.IMAGE_URL_KEY, palette.getImageUrl());
            // Todo: Remove after real images available. use hardcoded nature pic now.
            intent.putExtra(PaletteDetailActivity.IMAGE_URL_KEY, palette.getDownloadUrl());
            intent.putExtra(PaletteDetailActivity.SWATCHES_KEY, palette.getSwatches());
            context.startActivity(intent);
        }
    }
}
