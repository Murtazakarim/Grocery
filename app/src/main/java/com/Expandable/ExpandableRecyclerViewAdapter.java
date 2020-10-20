package com.Expandable;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.Config.BaseURL;
import com.Model.Category_model;
import com.hmos.grocme.R;

/**
 * Created by Aroliant on 1/3/2018.
 */

public class ExpandableRecyclerViewAdapter extends RecyclerView.Adapter<ExpandableRecyclerViewAdapter.ViewHolder> {

    List<Category_model> nameList = new ArrayList<>();
    ArrayList<String> image = new ArrayList<String>();
    ArrayList<Integer> counter = new ArrayList<Integer>();
    ArrayList<ArrayList> itemNameList = new ArrayList<ArrayList>();
    Context context;

    public ExpandableRecyclerViewAdapter(Context context,
                                         List<Category_model> nameList) {
        this.nameList = nameList;
        this.context = context;


        for (int i = 0; i < nameList.size(); i++) {
            counter.add(0);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView product_image;
        ImageView dropBtn;
        RecyclerView cardRecyclerView;
        CardView cardView;
        RelativeLayout cardRelative;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryTitle);
            description = itemView.findViewById(R.id.categoryDescription);
            product_image = itemView.findViewById(R.id.product_img);
            dropBtn = itemView.findViewById(R.id.categoryExpandBtn);
            cardRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
            cardView = itemView.findViewById(R.id.cardView);
            cardRelative = itemView.findViewById(R.id.cardRelative);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_collapseview, parent, false);

        ExpandableRecyclerViewAdapter.ViewHolder vh = new ExpandableRecyclerViewAdapter.ViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(nameList.get(position).getTitle());

        if(nameList.get(position).getDescription().length() > 100)
            holder.description.setText(nameList.get(position).getDescription().substring(0,70) + "...");

        Picasso.with(context).load(BaseURL.IMG_CATEGORY_URL
                 + nameList.get(position).getImage()).into(holder.product_image);
        InnerRecyclerViewAdapter itemInnerRecyclerView = new InnerRecyclerViewAdapter(nameList.get(position).getCategory_sub_datas(), context);


        holder.cardRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (counter.get(position) % 2 == 0) {
                    holder.cardRelative.setBackgroundColor(context.getResources().getColor(R.color.yelow1));
                    holder.cardRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    holder.cardRelative.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.cardRecyclerView.setVisibility(View.GONE);
                }

                counter.set(position, counter.get(position) + 1);


            }
        });
        holder.cardRecyclerView.setAdapter(itemInnerRecyclerView);

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


}
