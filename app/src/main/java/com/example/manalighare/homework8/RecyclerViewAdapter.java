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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    ArrayList<item_in_trip> items_in_trip;
    //String userId;
    //MyInterface myInterface;

    public RecyclerViewAdapter(ArrayList<item_in_trip> items) {
        this.items_in_trip = items;
        //this.userId = userId;
        //this.myInterface = myInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        //vh.myInterface = this.myInterface;
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        item_in_trip items = items_in_trip.get(position);
        holder.trip_name.setText(items.trip_Name);
        holder.trip_date.setText(items.travel_date);
        holder.trip_destination.setText(items.travel_destination);
        holder.user_name.setText(items.user_name);
        holder.object1 = items;

    }

    @Override
    public int getItemCount() {
        return items_in_trip.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView trip_name;
        public TextView trip_destination;
        public TextView trip_date;
        public TextView user_name;


        public ImageView info_button;
        public item_in_trip object1;
        //public MyInterface myInterface;

        public MyViewHolder(View v) {
            super(v);
            this.trip_name = v.findViewById(R.id.Trip_Name);
            this.trip_destination = v.findViewById(R.id.destination_city);
            this.trip_date = v.findViewById(R.id.date_of_travel);
            this.info_button=v.findViewById(R.id.info_button);
            this.user_name=v.findViewById(R.id.user_name);


            this.info_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("demo", "onClick: info !!! ");

                    Intent trip_details_intent=new Intent(view.getContext(),trip_details_activity.class);
                    trip_details_intent.putExtra("trip_id",object1.trip_id);
                    view.getContext().startActivity(trip_details_intent);


                }
            });
        }


    }
}
