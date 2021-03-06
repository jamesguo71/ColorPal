package com.cs65.colorpal.views.adapter;

import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.views.activities.InspectActivity;
import com.cs65.colorpal.views.activities.SwatchesDetailActivity;

import java.util.List;

public class SwatchListAdapter  extends RecyclerView.Adapter<SwatchListAdapter.SwatchListViewHolder> {
    List<Palette.Swatch> swatches;
    View.OnClickListener listener;

    public SwatchListAdapter(List<Palette.Swatch> swatches, View.OnClickListener listener ) {
        this.swatches = swatches;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SwatchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swatch, parent, false);
        return new SwatchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwatchListViewHolder holder, int position) {
        Palette.Swatch swatch = swatches.get(position);

        View swatchView = holder.getSwatchView();
        swatchView.setBackgroundColor(swatch.getRgb());
    }

    @Override
    public int getItemCount() {
        return swatches.size();
    }

    class SwatchListViewHolder extends RecyclerView.ViewHolder {
        private final View swatchView;

        public SwatchListViewHolder(@NonNull View itemView) {
            super(itemView);
            swatchView = itemView.findViewById(R.id.swatch_item);
            ViewGroup.LayoutParams layoutParams = swatchView.getLayoutParams();
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            layoutParams.width = width / swatches.size();
            swatchView.setOnClickListener(listener);
        }

        public View getSwatchView() {
            return swatchView;
        }
    }
}
