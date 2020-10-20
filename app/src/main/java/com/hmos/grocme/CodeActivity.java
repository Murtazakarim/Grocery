package com.hmos.grocme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CodeActivity extends AppCompatActivity {
    EditText editTextCode;
    Button confirm_btn;
    FirebaseAuth mAuth;
    String phoneNumber;
    ProgressDialog progressDialog;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
//                editTextCode.setText(code);
                //verifying the code
//                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(CodeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_code);

//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            setTitle("Enter the Code");
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);
        confirm_btn = findViewById(R.id.confirm_number_btn);
        phoneNumber = getIntent().getStringExtra("mobile");
        sendVerificationCode(phoneNumber);
        ((TextView) findViewById(R.id.infoText)).setText("Please enter 6 digit code sent on\nNumber " + phoneNumber);

        progressDialog = new ProgressDialog(CodeActivity.this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Sending Code");
        progressDialog.show();
        findViewById(R.id.close_btn_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextCode.getText().toString().length() == 6) {
                    findViewById(R.id.confirm_number_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    findViewById(R.id.confirm_number_btn).setEnabled(true);
                } else {
                    findViewById(R.id.confirm_number_btn).setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    findViewById(R.id.confirm_number_btn).setEnabled(false);

                }

                if (s.length() > 5) {
                    String code = editTextCode.getText().toString().trim();
                    if (code.isEmpty() || code.length() < 6) {
                        editTextCode.setError("Enter valid code");
                        editTextCode.requestFocus();
                        return;
                    }

                    //verifying the code entered manually
//                        verifyVerificationCode(code);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 5000);
    }

    private void verifyVerificationCode(final String code) {
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Verifying Code");
        progressDialog.show();
        PhoneAuthCredential credential = null;
        try {
            credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Verification Error try again", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }

        if (credential == null)
            return;
        mAuth.signInWithCredential(credential).addOnCompleteListener(CodeActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //verification successful we will start the profile activity
                    progressDialog.dismiss();
                    Toast.makeText(CodeActivity.this, "Verified", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(CodeActivity.this, RegisterActivity.class).putExtra("phone", phoneNumber));
                    finish();
                } else {

                    progressDialog.dismiss();
                    //verification unsuccessful.. display an error message
                    String message = "Something is wrong, we will fix it soon...";

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered";
                    }
                    Toast.makeText(CodeActivity.this, message, Toast.LENGTH_SHORT).show();
//                    Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
//                    snackbar.setAction("Dismiss", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
//                    snackbar.show();
                }
            }
        });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
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
