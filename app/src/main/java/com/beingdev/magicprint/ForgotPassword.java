package com.beingdev.magicprint;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.beingdev.magicprint.networksync.CheckInternetConnection;
import com.beingdev.magicprint.networksync.PasswordRequest;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class ForgotPassword extends AppCompatActivity {

    private TextView appname;
    private EditText forgotpassemail;
    private String getEmail;
    private Button sendotp;
    private TextView back;

    private RequestQueue requestQueue;
    private String sessionname,sessionmobile,sessionemail,sessionpassword;
    public static final String TAG = "MyTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        forgotpassemail = findViewById(R.id.forgotpassemail);
        sendotp = findViewById(R.id.sendotp);
        back = findViewById(R.id.goback);


        requestQueue = Volley.newRequestQueue(ForgotPassword.this);
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getEmail = forgotpassemail.getText().toString();

                final KProgressHUD progressDialog = KProgressHUD.create(ForgotPassword.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();

                PasswordRequest passwordRequest = new PasswordRequest(getEmail, new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        Log.e("values recieved" , response);

                        progressDialog.dismiss();
                        // Response from the server is in the form if a JSON, so we need a JSON Object
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e("Flag recieved", jsonObject.getBoolean("success")+" ");

                            if (jsonObject.getBoolean("success")) {


                                //Passing all received data from server to next activity
                                sessionname = jsonObject.getString("name");
                                sessionmobile = jsonObject.getString("mobile");
                                sessionemail = jsonObject.getString("email");
                                sessionpassword = jsonObject.getString("password");

                                Log.e("session values ", sessionemail);

                                //sending mail to the specified info
                                sendMail();

                            } else {
                                if (jsonObject.getString("status").equals("INVALID"))
                                    Toasty.error(ForgotPassword.this, "User Not Found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toasty.error(ForgotPassword.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof ServerError)
                            Toasty.error(ForgotPassword.this, "Server Error", Toast.LENGTH_SHORT).show();
                        else if (error instanceof TimeoutError)
                            Toasty.error(ForgotPassword.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                        else if (error instanceof NetworkError)
                            Toasty.error(ForgotPassword.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                    }


                });

                requestQueue.add(passwordRequest);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void sendMail() {

        BackgroundMail.newBuilder(ForgotPassword.this)
                .withSendingMessage("Sending Password to your Email")
                .withSendingMessageSuccess("Kindly Check Your email For Password")
                .withSendingMessageError("Failed to send password ! Try Again !")
                .withUsername("beingdevofficial@gmail.com")
                .withPassword("Singh@30")
                .withMailto(sessionemail)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Magic Print Password")
                .withBody("Hello Mr/Miss " + sessionname + "\n " + getString(R.string.send_password1) + sessionpassword + getString(R.string.send_password2))
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {

                        //do some magic

                        Toasty.success(ForgotPassword.this, "Password sent to Email Account",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                        finish();
                    }
                })
                .send();

    }


    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}
