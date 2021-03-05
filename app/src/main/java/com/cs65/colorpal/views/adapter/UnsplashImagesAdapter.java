package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.UnsplashImage;
import com.cs65.colorpal.views.activities.InspectActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UnsplashImagesAdapter extends RecyclerView.Adapter<UnsplashImagesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView nameOfUser;
        private ImageView imageView;
        private Context context;
        private ArrayList<UnsplashImage> imagesList;

        public ViewHolder( View itemView, Context context, ArrayList<UnsplashImage> imagesList) {
            super(itemView);
            this.nameOfUser = (TextView) itemView.findViewById(R.id.nameOfUser);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.context = context;
            this.imagesList = imagesList;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            Intent intent = new Intent(context, InspectActivity.class);
            intent.putExtra(InspectActivity.PHOTO_URI, imagesList.get(pos).getImage_view_url());
            context.startActivity(intent);
        }
    }

    private ArrayList<UnsplashImage> imagesList;
    private Context context;


    public UnsplashImagesAdapter(ArrayList<UnsplashImage> images) {
        this.imagesList = images;
    }

    public UnsplashImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View imageView = inflater.inflate(R.layout.unsplash_image_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(imageView, context, imagesList);
        return viewHolder;
    }

    public void onBindViewHolder( UnsplashImagesAdapter.ViewHolder holder, int position) {
        UnsplashImage image = imagesList.get(position);

        TextView nameOfUserTextView = holder.nameOfUser;
        nameOfUserTextView.setText(image.getNameOfUser());

        ImageView imageView = holder.imageView;
        // Picasso by default does not download on main application by default
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context).load(image.getImage_view_url()).into(imageView);
    }

    public int getItemCount() {
        return imagesList.size();
    }

}