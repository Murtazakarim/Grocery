package com.Fragments;

import android.os.Bundle;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import com.Config.BaseURL;
import com.Model.Product_model;
import com.hmos.grocme.MainActivity;
import com.hmos.grocme.R;
import com.util.DatabaseHandler;


public class ProductDetailFragment extends Fragment {

public ProductDetailFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private DatabaseHandler dbcart;
    ArrayList<Product_model> modelList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        ImageView iv_image = (ImageView) view.findViewById(R.id.iv_product_detail_img);
        final ImageView iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
        final ImageView iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_product_detail_title);
        TextView tv_detail = (TextView) view.findViewById(R.id.tv_product_detail);
        final TextView tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
        final TextView tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
        final TextView tv_price = (TextView) view.findViewById(R.id.price);



        modelList = (ArrayList<Product_model>)getArguments().getSerializable("key");

        dbcart = new DatabaseHandler(getActivity());
        final int position = getArguments().getInt("pos");

        tv_title.setText(getArguments().getString("title"));
            ((MainActivity) getActivity()).setTitle(tv_title.getText());
        //tv_detail.setText(getArguments().getString("detail"));
        tv_contetiy.setText(getArguments().getString("qty"));
        tv_detail.setText(getArguments().getString("detail"));
        tv_price.setText(getActivity().getResources().getString(R.string.tv_pro_rs) + getArguments().getString("price") + getActivity().getResources().getString(R.string.currency));

        Glide.with(getActivity())
                .load(BaseURL.IMG_PRODUCT_URL + getArguments().getString("image"))

                .crossFade()
                .into(iv_image);
        if (Integer.valueOf(modelList.get(position).getStock())<=0){
            tv_add.setText(R.string.tv_out);
            tv_add.setTextColor(getActivity().getResources().getColor(R.color.black));
            tv_add.setBackgroundColor(getActivity().getResources().getColor(R.color.gray));
            tv_add.setEnabled(false);
            iv_minus.setEnabled(false);
            iv_plus.setEnabled(false);
        }

        else if (dbcart.isInCart(modelList.get(position).getProduct_id())) {
            iv_plus.setVisibility(View.VISIBLE);
            iv_minus.setVisibility(View.VISIBLE);
            tv_contetiy.setVisibility(View.VISIBLE);
            tv_add.setVisibility(View.INVISIBLE);

            //tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            tv_contetiy.setText(dbcart.getCartItemQty(modelList.get(position).getProduct_id()));
        } else {
            tv_add.setVisibility(View.VISIBLE);
            tv_add.setText(getActivity().getResources().getString(R.string.tv_pro_add));
        }



        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;

                tv_contetiy.setText(String.valueOf(qty));

                update(tv_contetiy, position);
            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                }

                update(tv_contetiy, position);

            }
        });
        return view;
    }

    void update(TextView tv_contetiy, int position) {
        HashMap<String, String> map = new HashMap<>();


        map.put("product_id", modelList.get(position).getProduct_id());
        map.put("product_name", modelList.get(position).getProduct_name());
        map.put("category_id", modelList.get(position).getCategory_id());
        map.put("product_description", modelList.get(position).getProduct_description());
        map.put("deal_price", modelList.get(position).getDeal_price());
        map.put("start_date", modelList.get(position).getStart_date());
        map.put("start_time", modelList.get(position).getStart_time());
        map.put("end_date", modelList.get(position).getEnd_date());
        map.put("end_time", modelList.get(position).getEnd_time());
        map.put("price", modelList.get(position).getPrice());
        map.put("product_image", modelList.get(position).getProduct_image());
        map.put("status", modelList.get(position).getStatus());
        map.put("in_stock", modelList.get(position).getIn_stock());
        map.put("unit_value", modelList.get(position).getUnit_value());
        map.put("unit", modelList.get(position).getUnit());
        map.put("increament", modelList.get(position).getIncreament());
        map.put("rewards", modelList.get(position).getRewards());
        map.put("stock", modelList.get(position).getStock());
        map.put("title", modelList.get(position).getTitle());


        if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
            if (dbcart.isInCart(map.get("product_id"))) {
                dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));

                // tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            } else {

                dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                // tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            }
        } else {
            dbcart.removeItemFromCart(map.get("product_id"));
            //tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }

        Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
        Double price = Double.parseDouble(map.get("price"));
        ((MainActivity) getActivity()).setCartCounter("" + dbcart.getCartCount());

    }



}
