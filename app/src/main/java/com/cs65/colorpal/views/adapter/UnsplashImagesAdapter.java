package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.UnsplashImage;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnsplashImagesAdapter extends RecyclerView.Adapter<UnsplashImagesAdapter.ViewHolder> {

    private ArrayList<UnsplashImage> imagesList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nameOfUser;
        private ImageView imageView;

        public ViewHolder( View itemView) {
            super(itemView);
            nameOfUser = (TextView) itemView.findViewById(R.id.nameOfUser);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public UnsplashImagesAdapter(ArrayList<UnsplashImage> images) {
        this.imagesList = images;
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
        ImageView imageView = holder.imageView;
        addImage(image.getImage_view_url(), imageView);

    }

    public int getItemCount() {
        return imagesList.size();
    }

    public void addImage(String url, ImageView imageView){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap finalBitmap = bitmap;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(finalBitmap != null)
                        imageView.setImageBitmap(finalBitmap);
                    }
                });
            }
        });
    }
}