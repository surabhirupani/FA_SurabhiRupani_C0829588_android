package com.example.fa_surabhirupani_c0829588_android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_surabhirupani_c0829588_android.MapsActivity;
import com.example.fa_surabhirupani_c0829588_android.Model.Place;
import com.example.fa_surabhirupani_c0829588_android.R;


import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    
    private List<Place> items;
    private Context context;

    public PlaceAdapter(List<Place> items) {
        this.items = items;
        }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvStatus;
        View parentView;

        ViewHolder(View v) {
            super(v);
            tvItemName = v.findViewById(R.id.tvName);
            tvStatus = v.findViewById(R.id.tvStatus);
            parentView = v.findViewById(R.id.parentView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_place_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Place place = getItem(position);
        holder.tvItemName.setText(place.getPlaceName());
        if(place.getVisited()) {
            holder.tvStatus.setText("Visited already");
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.overlay_green_10));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.purple_500));
        } else {
            holder.tvStatus.setText("Supplosed to visit");
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.grey_700));

        }
//
        holder.parentView.setTag(position);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("place", place);
                context.startActivity(intent);
            }
        });
    }


    private Place getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
