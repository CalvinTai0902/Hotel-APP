package com.learn.hotelapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.ViewHolder> {

    private static List<item_ad> adList;

    public AttractionsAdapter(List<item_ad> itemAdList) {
        this.adList = itemAdList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_attractions_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        item_ad attraction = adList.get(position);
        holder.imageView.setBackground(attraction.getItemImg());
        holder.description.setText(attraction.getItemDesc());

        // Next button
        holder.btn_ad_next.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), launchPage.class);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, btn_ad_next;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_ad);
            description = itemView.findViewById(R.id.tv_ad_desc);
            btn_ad_next = itemView.findViewById(R.id.btn_ad_next);
        }
    }

    public void removeItem(int position) {
        adList.remove(position);
        notifyItemRemoved(position);
    }
}
