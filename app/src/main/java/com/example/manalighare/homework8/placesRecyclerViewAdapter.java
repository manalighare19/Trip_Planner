package com.example.manalighare.homework8;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class placesRecyclerViewAdapter extends RecyclerView.Adapter<placesRecyclerViewAdapter.MyViewHolder> {


    ArrayList<places_to_visit> places_to_visit;
    //String userId;
    //MyInterface myInterface;

    public placesRecyclerViewAdapter(ArrayList<places_to_visit> items) {
        this.places_to_visit = items;
        //this.userId = userId;
        //this.myInterface = myInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_row_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        //vh.myInterface = this.myInterface;
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        places_to_visit items = places_to_visit.get(position);
        holder.place_name.setText(items.place_name);

    }

    @Override
    public int getItemCount() {
        return places_to_visit.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView place_name;

        public MyViewHolder(View v) {
            super(v);
            this.place_name = v.findViewById(R.id.place_name_textview);


        }


    }
}
