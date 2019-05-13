package com.beingdev.magicprint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.beingdev.magicprint.networksync.ChangePhotoRequest;
import com.beingdev.magicprint.networksync.CheckInternetConnection;
import com.beingdev.magicprint.networksync.UpdateRequest;
import com.beingdev.magicprint.usersession.UserSession;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class UpdateData extends AppCompatActivity {

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    private Button button;
    private EditText edtname,edtemail,edtmobile;
    CircleImageView primage;
    private TextView namebutton;
    private ImageView changeprofilepic;
    Bitmap profilePicture;
    boolean IMAGE_STATUS = false;
    String changedImage;

    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile,newemail;
    private String check;
    private RequestQueue requestQueue;
    public static final String TAG = "MyTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        initialize();

        requestQueue = Volley.newRequestQueue(UpdateData.this);

        //retrieve session values and display on listviews
        getValues();


        //onclick of upload icon user should be able to click and change profile pic
        changeprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Dexter.withActivity(UpdateData.this)
                        .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    // do you work now

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1000);
                //result will be available in onActivityResult which is overridden
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // permission is denied permenantly, navigate user to app settings
                                    Snackbar.make(view, "Kindly grant Required Permission", Snackbar.LENGTH_LONG)
                                            .setAction("Allow", null).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();
            }
        });


        //Updating user details and after update send user to profile page
        button=findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateName() && validateEmail() && validateNumber()) {

                    name = edtname.getText().toString();
                    newemail = edtemail.getText().toString();
                    mobile = edtmobile.getText().toString();

                    final KProgressHUD progressDialog = KProgressHUD.create(UpdateData.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();

                    UpdateRequest updateRequest = new UpdateRequest(name, mobile, email, newemail, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progressDialog.dismiss();
                            try {
                                if (new JSONObject(response).getBoolean("success")) {
                                    Toasty.success(UpdateData.this, "Updated Succesfully", Toast.LENGTH_LONG, true).show();

                                    session.createLoginSession(name,newemail,mobile,photo);

                                    Intent registersuccess = new Intent(UpdateData.this, Profile.class);
                                    startActivity(registersuccess);
                                    finish();
                                } else {
                                    Toasty.error(UpdateData.this, "User is not registered", Toast.LENGTH_LONG, true).show();
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });
                    requestQueue.add(updateRequest);
                }else{
                    Toasty.warning(UpdateData.this,"Incorrect Details Entered",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void convertBitmapToString(Bitmap profilePicture) {
            /*
                Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
                so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
            */
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        changedImage = Base64.encodeToString(array, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            //Image Successfully Selected
            try {
                //parsing the Intent data and displaying it in the imageview
                Uri imageUri = data.getData();//Geting uri of the data
                InputStream imageStream = getContentResolver().openInputStream(imageUri);//creating an imputstrea
                profilePicture = BitmapFactory.decodeStream(imageStream);//decoding the input stream to bitmap
                primage.setImageBitmap(profilePicture);
                IMAGE_STATUS = true;//setting the flag
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Updating profile pic manually
        final KProgressHUD progressDialog=  KProgressHUD.create(UpdateData.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploading Photo..")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        convertBitmapToString(profilePicture);

        final ChangePhotoRequest changePhotoRequest = new ChangePhotoRequest(email,changedImage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("response",response);
                progressDialog.dismiss();
                try {
                    if (new JSONObject(response).getBoolean("success")) {
                        Toasty.success(UpdateData.this, "Updated Succesfully", Toast.LENGTH_SHORT, true).show();

                    } else {
                        Toasty.error(UpdateData.this, "User not registered", Toast.LENGTH_SHORT, true).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
        requestQueue.add(changePhotoRequest);
    }


    private boolean validateNumber() {

        check = edtmobile.getText().toString();
        Log.e("inside number",check.length()+" ");
        if (check.length()>10) {
            return false;
        }else if(check.length()<10){
            return false;
        }
        return true;
    }

    private boolean validateEmail() {

        check = edtemail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
            return false;
        }

        return true;
    }

    private boolean validateName() {

        check = edtname.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtname.setError("Name Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edtemail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edtemail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edtemail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length()>10) {
                edtmobile.setError("Number cannot be grated than 10 digits");
            }else if(check.length()<10){
                edtmobile.setError("Number should be 10 digits");
            }
        }

    };

    private void initialize() {

        namebutton =findViewById(R.id.name_button);
        primage = findViewById(R.id.profilepic);
        edtname =findViewById(R.id.name);
        edtemail =findViewById(R.id.email);
        edtmobile =findViewById(R.id.number);
        changeprofilepic = findViewById(R.id.changeprofilepic);

        edtname.addTextChangedListener(nameWatcher);
        edtemail.addTextChangedListener(emailWatcher);
        edtmobile.addTextChangedListener(numberWatcher);

    }

    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
        photo = user.get(UserSession.KEY_PHOTO);

        //setting values
        edtemail.setText(email);
        edtmobile.setText(mobile);
        edtname.setText(name);
        namebutton.setText(name);

        Glide.with(getBaseContext()).load(photo).into(primage);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewCart(View view) {
        startActivity(new Intent(UpdateData.this,Cart.class));
        finish();
    }

    public void viewProfile(View view) {
        startActivity(new Intent(UpdateData.this,Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.stop();
        }
    }
}
