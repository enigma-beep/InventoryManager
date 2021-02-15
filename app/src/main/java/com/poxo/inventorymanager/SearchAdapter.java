package com.poxo.inventorymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context context;
    private ArrayList ProductName, ProductNo;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList getProductName() {
        return ProductName;
    }

    public void setProductName(ArrayList productName) {
        ProductName = productName;
    }

    public ArrayList getProductNo() {
        return ProductNo;
    }

    public void setProductNo(ArrayList productNo) {
        ProductNo = productNo;
    }

    SearchAdapter(Context context, ArrayList ProductName, ArrayList ProductNo){
        this.context=context;
        this.ProductName=ProductName;
        this.ProductNo=ProductNo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_list_template,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.MyViewHolder holder, int position) {
        holder.productName.setText(String.valueOf(ProductName.get(position)));
        holder.productNo.setText(String.valueOf(ProductNo.get(position)));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setMessage("Do you want to Locate "+holder.productName.getText()+" with Tag No. - "+ holder.productNo.getText() +"?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, LocateActivity.class);
                                i.putExtra("mEPC",holder.productNo.getText());
                                context.startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Locate Tag");
                alert.show();







            }
        });
    }

    @Override
    public int getItemCount() {
        return ProductName.size();
    }

    public void clearData() {
        ProductName.clear();
        ProductNo.clear();
    }

    public void updateList(ArrayList ProductName, ArrayList ProductNo){
        this.ProductName=ProductName;
        this.ProductNo=ProductNo;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productNo;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.list_card);
            productName=itemView.findViewById(R.id.list_productname);
            productNo=itemView.findViewById(R.id.list_productno);
        }
    }
 }
