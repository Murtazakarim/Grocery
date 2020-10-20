package com.Expandable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;


import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.Config.BaseURL;
import com.Fragments.Product_fragment;
import com.Model.Category_subcat_model;
import com.hmos.grocme.MainActivity;
import com.hmos.grocme.R;

/**
 * Created by Aroliant on 1/3/2018.
 */

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder> {
    public ArrayList<Category_subcat_model> nameList = new ArrayList<>();
    private Context context;
    public InnerRecyclerViewAdapter(ArrayList<Category_subcat_model> nameList, Context context) {
this.context = context;
        this.nameList = nameList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemTextView);
            imageView = itemView.findViewById(R.id.img);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_expand_item_view, parent, false);

        InnerRecyclerViewAdapter.ViewHolder vh = new InnerRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("cat_id", nameList.get(position).getId());
                args.putString("cat_title", nameList.get(position).getTitle());
                fm.setArguments(args);
                FragmentManager fragmentManager = ((MainActivity)context).getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        });
        holder.name.setText(nameList.get(position).getTitle());
        Picasso.with(context).load(BaseURL.IMG_CATEGORY_URL
                + nameList.get(position).getImage()).into(holder.imageView);
        //holder.name.setText(nameList.get(position));

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


}
