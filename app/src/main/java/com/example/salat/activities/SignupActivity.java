package com.example.salat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salat.R;
import com.example.salat.models.Database;
import com.example.salat.models.User;
import com.example.salat.utils.Sharedprefs;
import com.example.salat.utils.Utils;
import com.example.salat.viewModels.FirebaseViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class SignupActivity extends AppCompatActivity {

    EditText userName_edit, password_edit, repassword_edit , email_edit;
    Button button;
    Database db;
    private boolean isValidate = true;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Sharedprefs sharedprefs;
    private FirebaseViewModel model;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy HH:mm:ss", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mAuth = FirebaseAuth.getInstance();
        sharedprefs = new Sharedprefs(this);
        db=new Database(this);
        button=(Button)findViewById(R.id.button);
        userName_edit=(EditText)findViewById(R.id.editusername);
        password_edit=(EditText)findViewById(R.id.editpassword);
        repassword_edit=(EditText)findViewById(R.id.editpasswordconfirm);
        email_edit=(EditText)findViewById(R.id.editemail);
        model = new ViewModelProvider(this).get(FirebaseViewModel.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable(false);
                if(validation()) {
                    createUser();
                }else{
                    setEnable(true);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setEnable(boolean status) {
        button.setEnabled(status);
        userName_edit.setEnabled(status);
        password_edit.setEnabled(status);
        repassword_edit.setEnabled(status);
    }

    @SuppressLint("NewApi")
    private boolean validation() {
        isValidate = true;
        if (userName_edit.getText().toString().trim().equals("")) {
            userName_edit.setError("please fill this field");
            isValidate = false;
        }
        if (email_edit.getText().toString().trim().equals("")) {
            email_edit.setError("please fill this field");
            isValidate = false;
        } else if (Utils.isValidEmail(email_edit.getText().toString())) {
            email_edit.setError("Email is not in correct format");
            isValidate = false;
        }
        if (password_edit.getText().toString().trim().equals("")) {
            password_edit.setError("please fill this field");
            isValidate = false;
        } else if (!Utils.isPasswordStrong(password_edit.getText().toString())) {
            password_edit.setError("Password should be greater then 8 and has upper and lower characters");
            isValidate = false;
        } else if (!password_edit.getText().toString().equals(repassword_edit.getText().toString())){
            repassword_edit.setError("Confirm password does not match");
            isValidate = false;
        }

        return isValidate;
    }

    private void createUser() {
        try{
            database = FirebaseDatabase.getInstance();
            mAuth.createUserWithEmailAndPassword(email_edit.getText().toString().trim(), password_edit.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "createUserWithName:success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.d(TAG, "createUser: user id is " + user.getUid());

                            User userObj = new User(user.getUid(), userName_edit.getText().toString(), email_edit.getText().toString(),sdf.format(new Date()));
                            model.insertUer(userObj, user.getUid()).observe(SignupActivity.this, aBoolean -> {
                                if (aBoolean) {
                                    sharedprefs.putBoolean("isLogged", true);
                                    sharedprefs.putString("uid", userObj.id);
                                    sharedprefs.putString("user_name", userObj.name);
                                    db.saveLogin(user.getUid(),userName_edit.getText().toString(), password_edit.getText().toString(), email_edit.getText().toString(),sdf.format(new Date()),"logged");
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(this, "Account create Successfully ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Account create Unsuccessfully", Toast.LENGTH_SHORT).show();
                                    setEnable(true);
                                }
                            });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                userName_edit.setError("User is already exist");
                                Toast.makeText(getBaseContext(), "erooor", Toast.LENGTH_LONG).show();
                                setEnable(true);
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            setEnable(true);
        }
    }
}