package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.UnsplashImage;

import java.util.ArrayList;

public class UnsplashImagesAdapter extends RecyclerView.Adapter<UnsplashImagesAdapter.ViewHolder> {

    ArrayList<UnsplashImage> imagesList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nameOfUser;

        public ViewHolder( View itemView) {
            super(itemView);

            nameOfUser = (TextView) itemView.findViewById(R.id.nameOfUser);
        }
    }

    public UnsplashImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View imageView = inflater.inflate(R.layout.unsplash_image_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    public void onBindViewHolder( UnsplashImagesAdapter.ViewHolder holder, int position) {
        UnsplashImage image = imagesList.get(position);
        TextView nameOfUserTextView = holder.nameOfUser;
        nameOfUserTextView.setText(image.getNameOfUser());
    }

    public int getItemCount() {
        return 0;
    }
}