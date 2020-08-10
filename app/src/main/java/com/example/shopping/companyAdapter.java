package com.example.shopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class companyAdapter extends RecyclerView.Adapter<companyAdapter.ViewHolder> {
    Context context;
    public List<company> arrayList;
//    Adapter adapter;

    public companyAdapter(ArrayList<company> list, Context context) {
        this.arrayList = list;
        this.context = context;
//        adapter = (Adapter) context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_company, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            /*Picasso.get()
                    .load(arrayList.get(position).getLogo())
                    .into(holder.imageView);*/
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(context).load(arrayList.get(position).getLogo()).apply(requestOptions).into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = arrayList.get(position).getName();
                    Intent intent = new Intent(context, ProductViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("CNAME", name);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
  /*  public interface Adapter{
        void onServiceItemClicked(View view,int position,String price, String name, String image);
    }*/
}

