package com.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Fragments.Home_fragment;
import com.Model.Product_model;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.List;

import com.Config.BaseURL;
import com.Model.Deal_Of_Day_model;
import com.hmos.grocme.MainActivity;
import com.hmos.grocme.R;
import com.util.DatabaseHandler;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Deal_OfDay_Adapter extends RecyclerView.Adapter<Deal_OfDay_Adapter.MyViewHolder> {

    private List<Product_model> modelList;
    private Context context;
    public int counter;

    private DatabaseHandler dbcart;
    String language;
    SharedPreferences preferences;

    private Home_fragment.OnItemClickListener onItemClickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_nmae, product_prize, offer_product_prize, start_time, end_time;
        public Button btn_subcat_add;
        public LinearLayout llAddMinusBtns;
        public ImageView iv_plus, iv_minus;
        public  TextView tv_contetiy;
        public TextView tvOffPercentage,tvUnitValue;
        public int percentage;

        public ImageView image;
      LinearLayout showproduct;
        public MyViewHolder(View view) {
            super(view);

            product_nmae = (TextView) view.findViewById(R.id.product_name);
            product_prize = (TextView) view.findViewById(R.id.product_prize);
            offer_product_prize = (TextView) view.findViewById(R.id.offer_product_prize);
            start_time = (TextView) view.findViewById(R.id.start_time);
            end_time = (TextView) view.findViewById(R.id.end_time);
            showproduct=(LinearLayout) view.findViewById(R.id.showproduct);
            product_prize.setPaintFlags(product_prize.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            image = (ImageView) view.findViewById(R.id.iv_icon);
            btn_subcat_add=(Button)view.findViewById(R.id.btn_subcat_add);
            llAddMinusBtns=(LinearLayout)view.findViewById(R.id.llAddMinusBtns);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            tv_contetiy= (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tvOffPercentage=(TextView)view.findViewById(R.id.tvOffPercentage);
            tvUnitValue=(TextView)view.findViewById(R.id.tvUnitValue);


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
                    btn_subcat_add.setText(context.getResources().getString(R.string.tv_pro_update));
                } else {
                    dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                    btn_subcat_add.setText(context.getResources().getString(R.string.tv_pro_update));
                }
            } else {
                dbcart.removeItemFromCart(map.get("product_id"));
                btn_subcat_add.setText(context.getResources().getString(R.string.tv_pro_add));
//                visible = false;
                btn_subcat_add.setVisibility(View.VISIBLE);
//                iv_plus.setVisibility(View.INVISIBLE);
//                iv_minus.setVisibility(View.INVISIBLE);
//                tv_contetiy.setVisibility(View.INVISIBLE);

            }
            Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));

//            Double price = Double.parseDouble(map.get("price").trim());
//            Double reward = Double.parseDouble(map.get("rewards"));
//            tv_reward.setText("" + reward * items);
//            tv_total.setText("" + price * items);
            ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
        }

        public void bind(
                final Product_model item, final int quantity, final Home_fragment.OnItemClickListener listener) {
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onItemClick(item,quantity);
                        }
                    });
        }

    }

    public Deal_OfDay_Adapter(List<Product_model> modelList, Activity activity,
                              Home_fragment.OnItemClickListener onItemClickListener) {
        this.modelList = modelList;
        this.onItemClickListener=onItemClickListener;
    }

    @Override
    public Deal_OfDay_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_deal_of_the_day, parent, false);

        context = parent.getContext();
        dbcart = new DatabaseHandler(context);


        return new Deal_OfDay_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Deal_OfDay_Adapter.MyViewHolder holder, final int position) {

        Product_model mList = modelList.get(position);



        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");

         if (mList.getStatus().equals("1")) {

            holder.offer_product_prize.setText(context.getResources().getString(R.string.currency) + mList.getDeal_price());
            holder.offer_product_prize.setTextColor(context.getResources().getColor(R.color.green));


        } else if (mList.getStatus().equals("0")) {
             holder.offer_product_prize.setText(context.getResources().getString(R.string.currency) + mList.getPrice());

//            holder.offer_product_prize.setText("Expired");
            //holder.offer_product_prize.setTextColor(context.getResources().getColor(R.color.color_3));

        }

        holder.percentage=(int)(100-((Double.parseDouble(mList.getDeal_price())/Double.parseDouble(mList.getPrice()))*100));
         holder.tvOffPercentage.setText(holder.percentage+"% OFF");


        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);
        holder.product_prize.setText(context.getResources().getString(R.string.currency) + mList.getPrice());
        holder.tvUnitValue.setText(mList.getUnit_value()+" "+mList.getUnit());
        if (language.contains("english")) {
            if(mList.getProduct_name().length() > 23)
            holder.product_nmae.setText(mList.getProduct_name().substring(0,23) + "...");
            else
                holder.product_nmae.setText(mList.getProduct_name());

            holder.product_prize.setText( context.getResources().getString(R.string.currency) + mList.getPrice());
        }
        else {
            holder.product_nmae.setText(mList.getProduct_name_arb());
            holder.product_prize.setText(context.getResources().getString(R.string.currency) + mList.getPrice());

        }


        holder.btn_subcat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    holder.llAddMinusBtns.setVisibility(View.VISIBLE);
                    holder.btn_subcat_add.setVisibility(View.GONE);


                    holder.iv_plus.performClick();

            }
        });

        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                qty = qty + 1;
                holder.tv_contetiy.setText(String.valueOf(qty));

                holder.update(position);
            }
        });

        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = 0;
                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase("")) {
                    qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                }

                if (qty > 0) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));
                }
                if(qty==0){
                        holder.llAddMinusBtns.setVisibility(View.GONE);
                        holder.btn_subcat_add.setVisibility(View.VISIBLE);

                }

                holder.update(position);

            }
        });

        if(Integer.valueOf(mList.getStock())>0){
            holder.btn_subcat_add.setEnabled(true);
            holder.btn_subcat_add.setText("ADD");
            holder.btn_subcat_add.setEnabled(true);
            holder.iv_minus.setEnabled(true);
            holder.iv_plus.setEnabled(true);
        }else{
            holder.btn_subcat_add.setEnabled(false);
            holder.btn_subcat_add.setText(R.string.tv_out);
            holder.btn_subcat_add.setTextColor(context.getResources().getColor(R.color.black));
            holder.btn_subcat_add.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.btn_subcat_add.setEnabled(false);
            holder.iv_minus.setEnabled(false);
            holder.iv_plus.setEnabled(false);
            holder.btn_subcat_add.setTextSize(8);
        }


        if (Integer.valueOf(modelList.get(position).getStock())<=0){
            holder.btn_subcat_add.setText(R.string.tv_out);
            holder.btn_subcat_add.setTextColor(context.getResources().getColor(R.color.black));
            holder.btn_subcat_add.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.btn_subcat_add.setEnabled(false);
            holder.iv_minus.setEnabled(false);
            holder.iv_plus.setEnabled(false);
        }

        else  if (dbcart.isInCart(mList.getProduct_id())) {
            holder.llAddMinusBtns.setVisibility(View.VISIBLE);
            holder.btn_subcat_add.setVisibility(View.GONE);
            holder.btn_subcat_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));
        } else {
            holder.btn_subcat_add.setText(context.getResources().getString(R.string.tv_pro_add));
            holder.llAddMinusBtns.setVisibility(View.GONE);

        }


        holder.bind(mList,Integer.parseInt(holder.tv_contetiy.getText().toString()), onItemClickListener);


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


}

