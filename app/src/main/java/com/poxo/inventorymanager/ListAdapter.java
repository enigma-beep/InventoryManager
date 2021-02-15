package com.poxo.inventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    Context context;
    private ArrayList FileName;
    ListAdapter(Context context, ArrayList FileName){
        this.context=context;
        this.FileName=FileName;
    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.file_template,parent,false);
        return new ListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {
        holder.fileName.setText(String.valueOf(FileName.get(position)));

    }

    @Override
    public int getItemCount() {
        return FileName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView materialCardView;
        TextView fileName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView=itemView.findViewById(R.id.fileCard);
            fileName=itemView.findViewById(R.id.tvFileName);
        }
    }
}
