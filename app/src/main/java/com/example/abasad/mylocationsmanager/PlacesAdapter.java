package com.example.abasad.mylocationsmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
//    String[] SubjectValues;
    List<Place> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;

    public PlacesAdapter(Context context1, List<Place> SubjectValues1){

        SubjectValues = SubjectValues1;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public ViewHolder(View v){

            super(v);

            textView = (TextView)v.findViewById(R.id.textViewName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_layout,parent,false);
        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Place thePlace = SubjectValues.get(position);
        String placeTxt = "";
        placeTxt +=  context.getString(R.string.CommTxt) + ": " +  thePlace.comment + "\n";
        placeTxt +=   context.getString(R.string.LatTxt) + ": " +  thePlace.latitude + "\n";
        placeTxt +=  context.getString(R.string.LongTxt) + ": " +  thePlace.longitude + "\n";
        holder.textView.setText(placeTxt);




        if(thePlace.haveBeen)
        viewHolder1.itemView.setBackgroundColor(context.getColor(R.color.HaveBeenCol));
        else
        viewHolder1.itemView.setBackgroundColor(context.getColor(R.color.WantToCol));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return SubjectValues.size();
    }
}