package com.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Config.BaseURL;
import com.Model.Product_model;
import com.hmos.grocme.MainActivity;
import com.hmos.grocme.R;
import com.util.DatabaseHandler;

import static android.content.Context.MODE_PRIVATE;


public class Product_adapter extends RecyclerView.Adapter<Product_adapter.MyViewHolder> {

    private List<Product_model> modelList;
    private Context context;
    private DatabaseHandler dbcart;
    String language;
SharedPreferences preferences;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_reward, tv_total, tv_contetiy, tv_add;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
        public Double reward;
        LinearLayout card;

        public boolean visible;
        public MyViewHolder(View view) {
            super(view);

            visible = false;
            card= view.findViewById(R.id.card_view);
            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            iv_remove.setVisibility(View.GONE);
            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            tv_add.setOnClickListener(this);
            iv_logo.setOnClickListener(this);
            card.setOnClickListener(this);



//            CardView cardView = (CardView) view.findViewById(R.id.card_view);
//            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if (id == R.id.iv_subcat_plus) {
                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;
                tv_contetiy.setText(String.valueOf(qty));

                update(position);
            } else if (id == R.id.iv_subcat_minus) {

                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase("")) {
                    qty = Integer.valueOf(tv_contetiy.getText().toString());
                }

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                }

                update(position);


            } else if (id == R.id.tv_subcat_add) {
                if (!visible) {


                    tv_add.setVisibility(View.INVISIBLE);
                    iv_plus.setVisibility(View.VISIBLE);
                    iv_minus.setVisibility(View.VISIBLE);
                    tv_contetiy.setVisibility(View.VISIBLE);

                    visible = true;
                    iv_plus.performClick();
                } else {

                    tv_add.animate().alpha(1f).setDuration(1000)
                            .setInterpolator(new AccelerateInterpolator()).start();
//                    HashMap<String, String> map = new HashMap<>();
//                    preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
//                    language = preferences.getString("language", "");
//
//
//                    map.put("product_id", modelList.get(position).getProduct_id());
//                    map.put("product_name", modelList.get(position).getProduct_name());
//                    map.put("category_id", modelList.get(position).getCategory_id());
//                    map.put("product_description", modelList.get(position).getProduct_description());
//                    map.put("deal_price", modelList.get(position).getDeal_price());
//                    map.put("start_date", modelList.get(position).getStart_date());
//                    map.put("start_time", modelList.get(position).getStart_time());
//                    map.put("end_date", modelList.get(position).getEnd_date());
//                    map.put("end_time", modelList.get(position).getEnd_time());
//                    map.put("price", modelList.get(position).getPrice());
//                    map.put("product_image", modelList.get(position).getProduct_image());
//                    map.put("status", modelList.get(position).getStatus());
//                    map.put("in_stock", modelList.get(position).getIn_stock());
//                    map.put("unit_value", modelList.get(position).getUnit_value());
//                    map.put("unit", modelList.get(position).getUnit());
//                    map.put("increament", modelList.get(position).getIncreament());
//                    map.put("rewards", modelList.get(position).getRewards());
//                    map.put("stock", modelList.get(position).getStock());
//                    map.put("title", modelList.get(position).getTitle());
//
//
//                    if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
//                        if (dbcart.isInCart(map.get("product_id"))) {
//                            dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                            tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                        } else {
//                            dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                            tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                        }
//                    } else {
//                        dbcart.removeItemFromCart(map.get("product_id"));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//                    }
//                    Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
//
//                    Double price = Double.parseDouble(map.get("price").trim());
//                    Double reward = Double.parseDouble(map.get("rewards"));
//                    tv_reward.setText("" + reward * items);
//                    tv_total.setText("" + price * items);
//                    ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
                }
                } else if (id == R.id.card_view) {
                    preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
                    language = preferences.getString("language", "");
                    Log.d("lang", language);
                    if (language.contains("english")) {
                        showProductDetail(modelList.get(position).getProduct_image(),
                                modelList.get(position).getProduct_name(),
                                modelList.get(position).getProduct_description(),
                                "",
                                position, tv_contetiy.getText().toString(),
                                modelList.get(position).getPrice());
                    } else {
                        showProductDetail(modelList.get(position).getProduct_image(),
                                modelList.get(position).getProduct_name_arb(),
                                modelList.get(position).getProduct_description_arb(),
                                "",
                                position, tv_contetiy.getText().toString(),
                                modelList.get(position).getPrice());
                    }
                }


            }

            void update(int position) {
                HashMap<String, String> map = new HashMap<>();
                preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
                language = preferences.getString("language", "");

        android.util.Log.e("category_id",""+modelList.get(position).getCategory_id());
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
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    } else {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    }
                    tv_add.setVisibility(View.GONE);
                } else {
                    dbcart.removeItemFromCart(map.get("product_id"));
                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
                    visible = false;
                    tv_add.setVisibility(View.VISIBLE);
                    iv_plus.setVisibility(View.INVISIBLE);
                    iv_minus.setVisibility(View.INVISIBLE);
                    tv_contetiy.setVisibility(View.INVISIBLE);

                }
                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));

                Double price = Double.parseDouble(map.get("price").trim());
                Double reward = Double.parseDouble(map.get("rewards"));
                tv_reward.setText("" + reward * items);
                tv_total.setText("" + price * items);
                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
            }
        }




    public Product_adapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        dbcart = new DatabaseHandler(context);
    }

    @Override
    public Product_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_rv, parent, false);
        context = parent.getContext();
        return new Product_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Product_adapter.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
                .centerCrop()
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
        if (language.contains("english")) {
            holder.tv_title.setText(mList.getProduct_name());
        }
        else {
            holder.tv_title.setText(mList.getProduct_name_arb());

        }
        holder.tv_reward.setText(mList.getRewards());
        holder.tv_price.setText(context.getResources().getString(R.string.tv_pro_price) + mList.getUnit_value() + " " +
                mList.getUnit() + mList.getPrice()+ context.getResources().getString(R.string.currency));
        if (Integer.valueOf(modelList.get(position).getStock())<=0){
            holder.tv_add.setText(R.string.tv_out);
            holder.tv_add.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_add.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.tv_add.setEnabled(false);
            holder.iv_minus.setEnabled(false);
            holder.iv_plus.setEnabled(false);
        }

        else  if (dbcart.isInCart(mList.getProduct_id())) {
            holder.visible = true;
            holder.tv_add.setVisibility(View.GONE);
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));
        } else {
            holder.visible = false;
            holder.iv_plus.setVisibility(View.INVISIBLE);
            holder.iv_minus.setVisibility(View.INVISIBLE);
            holder.tv_contetiy.setVisibility(View.INVISIBLE);
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }
        Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
        Double price = Double.parseDouble(mList.getPrice());
        Double reward = Double.parseDouble(mList.getRewards());
        holder.tv_total.setText("" + price * items);
       holder.tv_reward.setText("" + reward * items);

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    private void showImage(String image) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image_cancle = (ImageView) dialog.findViewById(R.id.iv_dialog_cancle);
        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_dialog_img);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
                .placeholder(R.drawable.icon)
                .crossFade()
                .into(iv_image);

        iv_image_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showProductDetail(String image, String title, String description, String detail, final int position, String qty, String price) {

        Bundle args = new Bundle();
        Fragment fm = new com.Fragments.ProductDetailFragment();

        args.putString("title",title);
        args.putString("image", image);
        args.putString("detail", description);
        args.putInt("pos", position);
        args.putString("qty", qty);
        args.putString("price", price);
        ArrayList<Product_model> listOfStrings = new ArrayList<>(modelList.size());
        listOfStrings.addAll(modelList);
        args.putSerializable("key", listOfStrings);
        fm.setArguments(args);
        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();

//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_product_detail);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//        dialog.show();
//
//        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_product_detail_img);
//        final ImageView iv_minus = (ImageView) dialog.findViewById(R.id.iv_subcat_minus);
//        final ImageView iv_plus = (ImageView) dialog.findViewById(R.id.iv_subcat_plus);
//        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_product_detail_title);
//        TextView tv_detail = (TextView) dialog.findViewById(R.id.tv_product_detail);
//        final TextView tv_contetiy = (TextView) dialog.findViewById(R.id.tv_subcat_contetiy);
//        final TextView tv_add = (TextView) dialog.findViewById(R.id.tv_subcat_add);
//        final TextView tv_price = (TextView) dialog.findViewById(R.id.price);
//
//        tv_title.setText(title);
//        tv_detail.setText(detail);
//        tv_contetiy.setText(qty);
//        tv_detail.setText(description);
//        tv_price.setText(context.getResources().getString(R.string.tv_pro_rs) + price+ context.getResources().getString(R.string.currency));
//
//        Glide.with(context)
//                .load(BaseURL.IMG_PRODUCT_URL + image)
//
//                .crossFade()
//                .into(iv_image);
//        if (Integer.valueOf(modelList.get(position).getStock())<=0){
//            tv_add.setText(R.string.tv_out);
//            tv_add.setTextColor(context.getResources().getColor(R.color.black));
//            tv_add.setBackgroundColor(context.getResources().getColor(R.color.gray));
//            tv_add.setEnabled(false);
//            iv_minus.setEnabled(false);
//            iv_plus.setEnabled(false);
//        }
//
//        else if (dbcart.isInCart(modelList.get(position).getProduct_id())) {
//            iv_plus.setVisibility(View.VISIBLE);
//            iv_minus.setVisibility(View.VISIBLE);
//            tv_contetiy.setVisibility(View.VISIBLE);
//            tv_add.setVisibility(View.INVISIBLE);
//
//            //tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//            tv_contetiy.setText(dbcart.getCartItemQty(modelList.get(position).getProduct_id()));
//        } else {
//            tv_add.setVisibility(View.VISIBLE);
//            tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//        }
//
//        tv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                HashMap<String, String> map = new HashMap<>();
////                preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
////                language=preferences.getString("language","");
////
////                    map.put("product_id", modelList.get(position).getProduct_id());
////                    map.put("product_name", modelList.get(position).getProduct_name());
////                    map.put("category_id", modelList.get(position).getCategory_id());
////                    map.put("product_description", modelList.get(position).getProduct_description());
////                    map.put("deal_price", modelList.get(position).getDeal_price());
////                    map.put("start_date", modelList.get(position).getStart_date());
////                    map.put("start_time", modelList.get(position).getStart_time());
////                    map.put("end_date", modelList.get(position).getEnd_date());
////                    map.put("end_time", modelList.get(position).getEnd_time());
////                    map.put("price", modelList.get(position).getPrice());
////                    map.put("product_image", modelList.get(position).getProduct_image());
////                    map.put("status", modelList.get(position).getStatus());
////                    map.put("in_stock", modelList.get(position).getIn_stock());
////                    map.put("unit_value", modelList.get(position).getUnit_value());
////                    map.put("unit", modelList.get(position).getUnit());
////                    map.put("increament", modelList.get(position).getIncreament());
////                    map.put("rewards", modelList.get(position).getRewards());
////                    map.put("stock", modelList.get(position).getStock());
////                    map.put("title", modelList.get(position).getTitle());
////
////
////                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
////                    if (dbcart.isInCart(map.get("product_id"))) {
////                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
////
////                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
////                    } else {
////
////                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
////                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
////                    }
////                } else {
////                    dbcart.removeItemFromCart(map.get("product_id"));
////                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
////                }
////
////                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
////                Double price = Double.parseDouble(map.get("price"));
////                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
////
////                notifyItemChanged(position);
//
//            }
//        });
//
//        iv_plus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int qty = Integer.valueOf(tv_contetiy.getText().toString());
//                qty = qty + 1;
//
//                tv_contetiy.setText(String.valueOf(qty));
//
//                update(tv_contetiy, position);
//            }
//        });
//
//        iv_minus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int qty = 0;
//                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
//                    qty = Integer.valueOf(tv_contetiy.getText().toString());
//
//                if (qty > 0) {
//                    qty = qty - 1;
//                    tv_contetiy.setText(String.valueOf(qty));
//                }
//
//                update(tv_contetiy, position);
//
//            }
//        });

    }

    void update(TextView tv_contetiy, int position) {
        HashMap<String, String> map = new HashMap<>();
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");

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
        ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

        notifyItemChanged(position);
    }

}