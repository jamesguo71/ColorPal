package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.models.PaletteTag;
import com.cs65.colorpal.utils.Utils;
import com.cs65.colorpal.views.activities.PaletteDetailActivity;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
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
            if(palette.getTitle()!=null && palette.getTitle().length()!=0)
                holder.title.setText(palette.getTitle());
            else
                holder.title.setText("New Palette");
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

    public void remove(int position){
        mPalettes.remove(position);
    }

    public ColorPalette get(int position){
        return mPalettes.get(position);
    }

    public class PaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, username;
        public RecyclerView swatches;
        public ImageView profile;

        public PaletteViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.paletteTitle);
            username = itemView.findViewById(R.id.username);
            swatches = itemView.findViewById(R.id.paletteSwatches);
            profile = itemView.findViewById(R.id.profile_image);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            ColorPalette palette =  mPalettes.get(pos);

            Intent intent = new Intent(context, PaletteDetailActivity.class);

            // We can also pass "id" (DocId of ColorPalette?) to DetailActivity and retrieve the info via repo
            intent.putExtra(PaletteDetailActivity.IMAGE_URL_KEY, palette.getDownloadUrl());
            intent.putExtra(PaletteDetailActivity.SWATCHES_KEY, palette.getSwatches());
            intent.putExtra(PaletteDetailActivity.TAGS_KEY, (ArrayList<PaletteTag>) palette.getTags());
            context.startActivity(intent);
        }
    }
}
