package com.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Model.Product_model;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Adapter.Deal_OfDay_Adapter;
import com.Adapter.Home_Icon_Adapter;
import com.Adapter.Home_adapter;
import com.Config.BaseURL;
import com.Expandable.ExpandableRecyclerViewAdapter;
import com.Model.Category_model;
import com.Model.Deal_Of_Day_model;
import com.Model.Home_Icon_model;
import com.hmos.grocme.AppController;
import com.hmos.grocme.CustomSlider;
import com.hmos.grocme.MainActivity;
import com.hmos.grocme.R;
import com.util.ConnectivityReceiver;
import com.util.CustomVolleyJsonRequest;
import com.util.RecyclerTouchListener;


public class Home_fragment extends Fragment {
    private static String TAG = Home_fragment.class.getSimpleName();
    private SliderLayout imgSlider, banner_slider;
    private RecyclerView rv_items, rv_deal_of_day;
    private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;
    private boolean isSubcat = false;
    LinearLayout Search_layout;
    String getid;
    String getcat_title;
    NestedScrollView scrollView;
    SharedPreferences sharedpreferences;

    //Home Icons
    private Home_Icon_Adapter menu_adapter;
    private List<Home_Icon_model> menu_models = new ArrayList<>();


    //Deal O Day
    private Deal_OfDay_Adapter deal_ofDay_adapter;
    private List<Product_model> deal_of_day_models = new ArrayList<>();

    LinearLayout Deal_Frame_layout;

    private TextView timer, View_all_deals;


    View view;

    public Home_fragment() {

    }
    public interface OnItemClickListener {
        void onItemClick(Product_model item,int quantity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        view1 = view;
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.app_name));
        ((MainActivity) getActivity()).updateHeader();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity) getActivity()).finish();
                    return true;
                }
                return false;
            }
        });
        //Check Internet Connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetSliderRequest();
            makeGetBannerSliderRequest();
            makeGetFeaturedSlider();

            makeGetCategoryRequest();
//            make_menu_items();
            make_deal_od_the_day();



        }

        View_all_deals = (TextView) view.findViewById(R.id.view_all_deals);
        Deal_Frame_layout = (LinearLayout) view.findViewById(R.id.deal_frame_layout);





        //Scroll View
        scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setSmoothScrollingEnabled(true);

        //Search
        Search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        Search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fm = new Search_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });
        //Slider
        imgSlider = (SliderLayout) view.findViewById(R.id.home_img_slider);
        //banner_slider = (SliderLayout) view.findViewById(R.id.relative_banner);



        //Catogary Icons
        rv_items = (RecyclerView) view.findViewById(R.id.rv_home);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false);
        rv_items.setLayoutManager(gridLayoutManager);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setNestedScrollingEnabled(false);

        //DealOf the Day
        rv_deal_of_day = (RecyclerView) view.findViewById(R.id.rv_deal);
//        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        LinearLayoutManager gridLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        rv_deal_of_day.setLayoutManager(gridLayoutManager1);
        rv_deal_of_day.setItemAnimator(new DefaultItemAnimator());
        rv_deal_of_day.setNestedScrollingEnabled(false);
        rv_deal_of_day.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));


        //make_menu_items Icons
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                    private static final float SPEED = 2000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);


        //Recycler View Shop By Catogary
//        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                getid = category_modelList.get(position).getId();
//                getcat_title = category_modelList.get(position).getTitle();
//                Bundle args = new Bundle();
//                Fragment fm = new Product_fragment();
//                args.putString("cat_id", getid);
//                args.putString("cat_title", getcat_title);
//                fm.setArguments(args);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));


        //Recycler View Deal Of Day
//        rv_deal_of_day.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_deal_of_day, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                getid = deal_of_day_models.get(position).getId();
//                Bundle args = new Bundle();
//                Fragment fm = new Product_fragment();
//                args.putString("cat_deal", "2");
//                fm.setArguments(args);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
        View_all_deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("cat_deal", "2");
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });






        return view;
    }


    private void makeGetSliderRequest() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_SLIDER_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        Log.e("SliderResponse", response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                HashMap<String, String> url_maps = new HashMap<String, String>();
                                url_maps.put("slider_title", jsonObject.getString("slider_title"));
                                url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                                url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                                listarray.add(url_maps);
                            }
                            for (HashMap<String, String> name : listarray) {
                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                imgSlider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
//                                           Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_fragment();
                                        args.putString("id", "15");//sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm).addToBackStack(null).commit();
                                    }
                                });


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(req);

    }

    private void makeGetBannerSliderRequest() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_BANNER_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                HashMap<String, String> url_maps = new HashMap<String, String>();
                                url_maps.put("slider_title", jsonObject.getString("slider_title"));
                                url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                                url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                                listarray.add(url_maps);
                            }
                            for (HashMap<String, String> name : listarray) {
//                                CustomSlider textSliderView = new CustomSlider(getActivity());
//                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
//                                textSliderView.bundle(new Bundle());
//                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
//                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
//                                banner_slider.addSlider(textSliderView);
//                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
//                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                    @Override
//                                    public void onSliderClick(BaseSliderView slider) {
//                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
//                                        Bundle args = new Bundle();
//                                        Fragment fm = new Product_fragment();
//                                        args.putString("id", sub_cat);
//                                        fm.setArguments(args);
//                                        FragmentManager fragmentManager = getFragmentManager();
//                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                                .addToBackStack(null).commit();
//                                    }
//                                });



                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                imgSlider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
//                                        Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_fragment();
                                        args.putString("id", "1");//sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm).addToBackStack(null).commit();
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(req);

    }


    private void makeGetFeaturedSlider() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_FEAATURED_SLIDER_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        Log.d("FeaturedSlider", response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                HashMap<String, String> url_maps = new HashMap<String, String>();
                                url_maps.put("slider_title", jsonObject.getString("slider_title"));
                                url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                                url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                                listarray.add(url_maps);
                            }
                            for (HashMap<String, String> name : listarray) {
//                                CustomSlider textSliderView = new CustomSlider(getActivity());
//                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
//                                textSliderView.bundle(new Bundle());
//                                //  textSliderView.getBundle().putString("extra", name.get("slider_title"));
////                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
////                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
//
//
//                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
//                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
//                                imgSlider.addSlider(textSliderView);
//                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
//
//
//                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                    @Override
//                                    public void onSliderClick(BaseSliderView slider) {
//                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
//                                        Bundle args = new Bundle();
//                                        Fragment fm = new Product_fragment();
//                                        args.putString("id", sub_cat);
//                                        fm.setArguments(args);
//                                        FragmentManager fragmentManager = getFragmentManager();
//                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                                .addToBackStack(null).commit();
//                                    }
//                                });



                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                imgSlider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
//                                        Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_fragment();
                                        args.putString("id", "6");//sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm).addToBackStack(null).commit();
                                    }
                                });



                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(req);

    }

    private void makeGetCategoryRequest() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                Log.e("CategoryResponse", response.toString());
                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Category_model>>() {
                            }.getType();
                            category_modelList = gson.fromJson(response.getString("data"), listType);

                            ArrayList<Category_model> categoryModels = new ArrayList<>();
                            for(Category_model cat : category_modelList){
                                if(cat.getCategory_sub_datas() != null)
                                categoryModels.add(cat);
                            }
                            ExpandableRecyclerViewAdapter expandableCategoryRecyclerViewAdapter =
                                    new ExpandableRecyclerViewAdapter(getActivity(), categoryModels);

                            //expanderRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                            rv_items.setAdapter(expandableCategoryRecyclerViewAdapter);
                            //adapter = new Home_adapter(category_modelList);
                           // rv_items.setAdapter(adapter);
                            expandableCategoryRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void make_deal_od_the_day() {
//        String tag_json_obj = "json_category_req";
        String tag_json_obj = "json_product_req";

        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
//        params.put("parent", "");
        params.put("dealproduct", "2");

        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ALL_DEAL_OF_DAY_PRODUCTS, params, new Response.Listener<JSONObject>() {

            //GET
//          GET_DEAL_OF_DAY_PRODUCTS
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        Gson gson = new Gson();
                        if (status) {
                            view1.findViewById(R.id.progress1).setVisibility(View.GONE);
                            Type listType = new TypeToken<List<Product_model>>() {
                            }.getType();

                            android.util.Log.e("DealsResponse",""+response.toString());
                            deal_of_day_models = gson.fromJson(response.getString("Deal_of_the_day"), listType);

                            for(Product_model model : deal_of_day_models) {
                                Log.e("ee", model.getStatus() + " " + model.getProduct_name());
                            }

                            deal_ofDay_adapter = new Deal_OfDay_Adapter(deal_of_day_models, getActivity(), new OnItemClickListener() {
                                @Override
                                public void onItemClick(Product_model item,int quantity) {
                                    showProductDetail(item.getProduct_image(),
                                            item.getProduct_name(),
                                            item.getProduct_description(),
                                            "",
                                            deal_of_day_models.indexOf(item), ""+quantity,
                                            item.getPrice());
                                }
                            });
                            rv_deal_of_day.setAdapter(deal_ofDay_adapter);
                            deal_ofDay_adapter.notifyDataSetChanged();
                            if (getActivity() != null) {
                                if (deal_of_day_models.isEmpty()) {
                                    //  Toast.makeText(getActivity(), "No Deal For Day", Toast.LENGTH_SHORT).show();
                                    rv_deal_of_day.setVisibility(View.GONE);
                                    Deal_Frame_layout.setVisibility(View.GONE);

                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Response", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

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
        ArrayList<Product_model> listOfStrings = new ArrayList<>(deal_of_day_models.size());
        listOfStrings.addAll(deal_of_day_models);
        args.putSerializable("key", listOfStrings);
        fm.setArguments(args);
        FragmentManager fragmentManager = (getActivity()).getFragmentManager();
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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        //Defining retrofit api service

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void reviewOnApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " APP");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
//    public  boolean isPermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (getContext().checkSelfPermission(android.Manifest.permission.CALL_PHONE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v("TAG","Permission is granted");
//                return true;
//            } else {
//
//                Log.v("TAG","Permission is revoked");
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
//                return false;
//            }
//        }
//        else { //permission is automatically granted on sdk<23 upon installation
//            Log.v("TAG","Permission is granted");
//            return true;
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//
//            case 1: {
//
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
//                    call_action();
//                } else {
//                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
//    public void call_action(){
//
//        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:" + "919889887711"));
//        startActivity(callIntent);
//    }
}
