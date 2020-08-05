package com.example.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.ViewHolder> {
    Context context;
    public List<transaction> arrayList;

    public transactionAdapter(ArrayList<transaction> list, Context context) {
        this.arrayList = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull transactionAdapter.ViewHolder holder, int position) {
        try {
            holder.rupee.setText("₹" + arrayList.get(position).getPrice());
            holder.name.setText(arrayList.get(position).getName());
            holder.date.setText(arrayList.get(position).getDate());
            holder.status.setText(arrayList.get(position).getStatus());
            Picasso.get().load(arrayList.get(position).getImage()).into(holder.imageView);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rupee, name, date, status;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rupee = (TextView) itemView.findViewById(R.id.price);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            status = (TextView) itemView.findViewById(R.id.status);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
