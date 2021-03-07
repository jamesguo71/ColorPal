package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.views.activities.ColorInfoActivity;
import com.cs65.colorpal.views.fragments.UnsplashFragment;

import java.util.List;

import static com.cs65.colorpal.views.activities.PaletteDetailActivity.FROM;

public class SwatchDetailsListAdapter extends RecyclerView.Adapter<SwatchDetailsListAdapter.SwatchDetailsListViewHolder> {
    private List<Integer> swatchValues;

    public SwatchDetailsListAdapter(List<Integer> swatchValues) {
        this.swatchValues = swatchValues;
    }

    @NonNull
    @Override
    public SwatchDetailsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swatch_details, parent, false);
        return new SwatchDetailsListViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull SwatchDetailsListViewHolder holder, int position) {
        Integer swatchValue = swatchValues.get(position);

        View swatchSquareView = holder.swatchSquareView;
        swatchSquareView.setBackgroundColor(swatchValue);
        TextView swatchValueView = holder.swatchValueView;
        swatchValueView.setText("#" + Integer.toHexString(swatchValue).substring(2));
    }

    @Override
    public int getItemCount() {
        return swatchValues.size();
    }

    class SwatchDetailsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View swatchSquareView;
        private final TextView swatchValueView;
        private Context context;

        public SwatchDetailsListViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            swatchSquareView = itemView.findViewById(R.id.swatch_square);
            swatchValueView = (TextView) itemView.findViewById(R.id.swatch_value);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Intent intent = new Intent(context, ColorInfoActivity.class);
            String swatchValue = Integer.toHexString(swatchValues.get(position)).substring(2);
            intent.putExtra(ColorInfoActivity.COLOR_TAG, swatchValue);
            intent.putExtra(FROM, UnsplashFragment.UNSPLASH_FRAGMENT);
            context.startActivity(intent);
        }
    }
}
