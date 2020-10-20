package com.hmos.grocme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;


public class PhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    PhoneNumberUtil phoneUtil;
    private EditText editTextMobile;
    private String phoneNumber;


    public static String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
    };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        checkPermissions();
        editTextMobile = findViewById(R.id.phone_number);
        ccp = findViewById(R.id.ccp);
        ccp.setDefaultCountryUsingNameCode("us");
        phoneUtil = PhoneNumberUtil.getInstance();

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setTitle("Verify your phone number");
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                phoneNumber = ccp.getSelectedCountryCodeWithPlus() + editTextMobile.getText().toString();
            }
        });

        findViewById(R.id.close_btn_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(editTextMobile.getText().toString().length() == 10)
                {
                    findViewById(R.id.send_code).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    findViewById(R.id.send_code).setEnabled(true);

                } else
                {
                    findViewById(R.id.send_code).setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    findViewById(R.id.send_code).setEnabled(false);
                }

                if (editTextMobile.getText().toString().contentEquals("0")) {
                    editTextMobile.setText("");
                }

                if (editTextMobile.getText().toString().length() > 10) {
                    editTextMobile.setText(editTextMobile.getText().toString().subSequence(0, 10));
                    editTextMobile.setSelection(editTextMobile.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.send_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = editTextMobile.getText().toString();
                number.replaceAll(" +", "");

                phoneNumber = ccp.getSelectedCountryCodeWithPlus() + number;

                if (validatePhone(number) && editTextMobile.getText().toString().length() < 10) {
                    editTextMobile.setError("Enter a valid mobile");
                    editTextMobile.requestFocus();
                    return;
                }

                Intent intent = new Intent(PhoneNumberActivity.this, CodeActivity.class);
                intent.putExtra("mobile", phoneNumber);
                startActivity(intent);

            }
        });
    }

    private boolean validatePhone(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(phoneNumber, null);
            return phoneUtil.isValidNumber(phoneNumberProto); // returns true if valid

        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
