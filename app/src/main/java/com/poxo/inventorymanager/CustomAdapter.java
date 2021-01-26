package com.poxo.inventorymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    private ArrayList ProductName, ProductNo, Found, AssetNo, MajorCat, MinorCat, AssetName, TagState, Type, Remarks, AssetCap, Quant, OriCost, CurCost, NetBlock;
    CustomAdapter(Context context,ArrayList ProductName, ArrayList ProductNo, ArrayList Found, ArrayList AssetNo, ArrayList MajorCat, ArrayList MinorCat, ArrayList AssetName, ArrayList TagState
            , ArrayList Type, ArrayList Remarks, ArrayList AssetCap, ArrayList Quant, ArrayList OriCost, ArrayList CurCost, ArrayList NetBlock){
        this.context=context;
        this.ProductName=ProductName;
        this.ProductNo=ProductNo;
        this.Found=Found;
        this.AssetNo=AssetNo;

        this.MajorCat=MajorCat;
        this.MinorCat=MinorCat;
        this.AssetName=AssetName;
        this.TagState=TagState;

        this.Type=Type;
        this.Remarks=Remarks;
        this.AssetCap=AssetCap;
        this.Quant=Quant;
        this.OriCost=OriCost;
        this.CurCost=CurCost;
        this.NetBlock=NetBlock;


    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.lst_template,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.productName.setText(String.valueOf(ProductName.get(position)));
        holder.productNo.setText(String.valueOf(ProductNo.get(position)));
        holder.found.setText(String.valueOf(Found.get(position)));
        holder.assetno.setText(String.valueOf(AssetNo.get(position)));
        holder.majorCat.setText(String.valueOf(MajorCat.get(position)));
        holder.minorCat.setText(String.valueOf(MinorCat.get(position)));
        holder.assetName.setText(String.valueOf(AssetName.get(position)));
        holder.tagState.setText(String.valueOf(TagState.get(position)));

        holder.type.setText(String.valueOf(Type.get(position)));
        holder.remarks.setText(String.valueOf(Remarks.get(position)));
        holder.assetCap.setText(String.valueOf(AssetCap.get(position)));
        holder.quant.setText(String.valueOf(Quant.get(position)));
        holder.oriCost.setText(String.valueOf(OriCost.get(position)));
        holder.curCost.setText(String.valueOf(CurCost.get(position)));
        holder.netBlock.setText(String.valueOf(NetBlock.get(position)));

        if(holder.found.getText().equals("1")){
            holder.lvContainer.setBackgroundColor(Color.parseColor("#1cdc5c"));
            holder.materialCardView.setBackgroundColor(Color.parseColor("#1cdc5c"));
            holder.productName.setTextColor(Color.WHITE);
            holder.productNo.setTextColor(Color.WHITE);
            holder.found.setTextColor(Color.WHITE);
            holder.assetno.setTextColor(Color.WHITE);
            holder.minorCat.setTextColor(Color.WHITE);
            holder.majorCat.setTextColor(Color.WHITE);
            holder.assetName.setTextColor(Color.WHITE);
            holder.tagState.setTextColor(Color.WHITE);
            holder.type.setTextColor(Color.WHITE);
            holder.remarks.setTextColor(Color.WHITE);
            holder.assetCap.setTextColor(Color.WHITE);
            holder.quant.setTextColor(Color.WHITE);
            holder.oriCost.setTextColor(Color.WHITE);
            holder.curCost.setTextColor(Color.WHITE);
            holder.netBlock.setTextColor(Color.WHITE);
        }else{
            holder.lvContainer.setBackgroundColor(Color.WHITE);
            holder.materialCardView.setBackgroundColor(Color.WHITE);
            holder.productName.setTextColor(Color.BLACK);
            holder.productNo.setTextColor(Color.BLACK);
            holder.found.setTextColor(Color.BLACK);
            holder.assetno.setTextColor(Color.BLACK);
            holder.majorCat.setTextColor(Color.BLACK);
            holder.minorCat.setTextColor(Color.BLACK);
            holder.assetName.setTextColor(Color.BLACK);
            holder.tagState.setTextColor(Color.BLACK);
            holder.type.setTextColor(Color.BLACK);
            holder.remarks.setTextColor(Color.BLACK);
            holder.assetCap.setTextColor(Color.BLACK);
            holder.quant.setTextColor(Color.BLACK);
            holder.oriCost.setTextColor(Color.BLACK);
            holder.curCost.setTextColor(Color.BLACK);
            holder.quant.setTextColor(Color.BLACK);

        }

    }


    @Override
    public int getItemCount() {
        return ProductName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView productName, productNo, found, assetno, majorCat, minorCat, assetName, tagState, type, remarks, assetCap, quant, oriCost, curCost, netBlock;
        LinearLayout lvContainer;
        MaterialCardView materialCardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView=itemView.findViewById(R.id.matcardview);
            lvContainer=itemView.findViewById(R.id.lvContainer);
            productName=itemView.findViewById(R.id.txtproductcompany);
            productNo=itemView.findViewById(R.id.txtproductname);
            found=itemView.findViewById(R.id.txtproductprice);
            assetno=itemView.findViewById(R.id.txtassetno);
            majorCat=itemView.findViewById(R.id.txtmajorcat);
            minorCat=itemView.findViewById(R.id.txtminorcat);
            assetName=itemView.findViewById(R.id.txtassetname);
            tagState=itemView.findViewById(R.id.txttagstate);
            type=itemView.findViewById(R.id.txttype);
            remarks=itemView.findViewById(R.id.txtremarks);
            assetCap=itemView.findViewById(R.id.txtassetcap);
            quant=itemView.findViewById(R.id.txtquant);
            oriCost=itemView.findViewById(R.id.txtoricost);
            curCost=itemView.findViewById(R.id.txtcurcost);
            netBlock=itemView.findViewById(R.id.txtnetblock);
        }
    }
}
