package com.harsh.appbase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.harsh.appbase.networksync.CheckInternetConnection;
import com.harsh.appbase.usersession.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private EditText edtemail,edtpass;
    private String email,pass;
    private TextView appname,forgotpass,registernow;
    private UserSession session;
    public static final String TAG = "MyTag";
    boolean mailFound, passFound;
    String foundName = "", foundMobile = "", foundImage = "";

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e("Login CheckPoint","LoginActivity started");
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        edtemail = findViewById(R.id.email);
        edtpass = findViewById(R.id.password);

        Bundle registerinfo=getIntent().getExtras();
        if (registerinfo != null) {
                edtemail.setText(registerinfo.getString("email"));
        }

        session = new UserSession(getApplicationContext());

        //if user wants to register
        registernow = findViewById(R.id.register_now);
        registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,Register.class));
                finish();
            }
        });

        //if user forgets password
        forgotpass = findViewById(R.id.forgot_pass);
        forgotpass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
            }
        });


        //Validating login details
        Button button = findViewById(R.id.login_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(LoginActivity.this);

                email = edtemail.getText().toString();
                pass = edtpass.getText().toString();

                final String sessionphoto =  "https://cdn.shopify.com/s/files/1/0148/1300/3876/products/tata-nano-rubber-mats-grey-allure-auto-cm-543-1100x1100-imaecjxkgfhnb3hq_925d7e5f-5a84-425a-8e0e-01f7538c2346.jpg?v=1547046928";

                if(validateUsername(email) && validatePassword(pass)) {

                    //Progress Bar while connection establishes

                    final KProgressHUD progressDialog=  KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();

                    mailFound = false;
                    passFound = false;
                    myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Iterable<DataSnapshot> children = snapshot.getChildren();

                                for (DataSnapshot childrenSnapshot : children) {
                                    if(childrenSnapshot.getKey().equals("Email") && childrenSnapshot.getValue().equals(email)) {
                                        mailFound = true;
                                        break;
                                    }
                                }
                                if (mailFound) {
                                    for (DataSnapshot childrenSnapshot : children) {
                                        if (childrenSnapshot.getKey().equals("Name")) {
                                            foundName = Objects.requireNonNull(childrenSnapshot.getValue()).toString();
                                            break;
                                        }
                                    }
                                    for (DataSnapshot childrenSnapshot : children) {
                                        if(childrenSnapshot.getKey().equals("Password") && childrenSnapshot.getValue().equals(pass)) {
                                            passFound = true;
                                            break;
                                        }
                                    }
                                }
                                if(mailFound && passFound) {
                                    foundMobile = snapshot.getKey();
                                    break;
                                }
                            }

                            if(mailFound && passFound) {
                                // User verified

                                foundImage = foundMobile + pass;
                                session.createLoginSession(foundName, email, foundMobile, sessionphoto);
                                countFirebaseValues();
                                progressDialog.dismiss();

                                Intent loginSuccess = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(loginSuccess);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });


    }

    private void countFirebaseValues() {

        myRef.child("User").child(foundMobile).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.getKey().equals("Num_Of_Items")) {
                            session.setCartValue((int)snapshot.getValue());
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child("User").child(foundMobile).child("WishList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals("Num_Of_Items")) {
                        session.setCartValue((int)snapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validatePassword(String pass) {


        if (pass.length() < 4 || pass.length() > 20) {
            edtpass.setError("Password Must consist of 4 to 20 characters");
            return false;
        }
        return true;
    }

    private boolean validateUsername(String email) {

        if (email.length() < 4 || email.length() > 50) {
            edtemail.setError("Email Must consist of 4 to 50 characters");
            return false;
        } else if (!email.matches("^[A-za-z0-9.@]+")) {
            edtemail.setError("Only . and @ characters allowed");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            edtemail.setError("Email must contain @ and .");
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Login CheckPoint","LoginActivity resumed");
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        }

    @Override
    protected void onStop () {
        super.onStop();
        Log.e("Login CheckPoint","LoginActivity stopped");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
