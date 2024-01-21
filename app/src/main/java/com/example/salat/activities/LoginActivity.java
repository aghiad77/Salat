package com.example.salat.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.salat.R;
import com.example.salat.models.Database;
import com.example.salat.models.User;
import com.example.salat.utils.Sharedprefs;
import com.example.salat.utils.Utils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    EditText password_edit , email_edit;
    Button button;
    TextView create_text;
    Database db;
    private FirebaseAuth mAuth;
    private User user;
    private boolean isValidate = true;
    private Sharedprefs sharedprefs;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy HH:mm:ss", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        sharedprefs = new Sharedprefs(this);
        db=new Database(this);

        button=(Button)findViewById(R.id.button);
        create_text=(TextView)findViewById(R.id.create);

        password_edit=(EditText)findViewById(R.id.editpassword);
        email_edit=(EditText)findViewById(R.id.editusername);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable(false);
                if(validation()) {
                    firebaseLogin();
                }else{
                    setEnable(true);
                }
            }
        });

        create_text.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
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
        create_text.setEnabled(status);
        password_edit.setEnabled(status);

    }

    @SuppressLint("NewApi")
    private boolean validation() {
        isValidate = true;
        if (email_edit.getText().toString().trim().equals("")) {
            email_edit.setError("please fill this field");
            isValidate = false;
        }else if (Utils.isValidEmail(email_edit.getText().toString())) {
            email_edit.setError("Email is not in correct format");
            isValidate = false;
        }
        if (password_edit.getText().toString().trim().equals("")) {
            password_edit.setError("please fill this field");
            isValidate = false;
        }else if(password_edit.getText().toString().length() < 8) {
            password_edit.setError("Password should be greater then 8 characters ");
            isValidate = false;
        }

        return isValidate;
    }

    private void firebaseLogin() {
        try {
            mAuth.signInWithEmailAndPassword(email_edit.getText().toString(), password_edit.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser firebaseuser = mAuth.getCurrentUser();
                    assert firebaseuser != null;
                    Log.d(TAG, "_apiLogin: uid is " + firebaseuser.getUid());

                    user = new User();
                    user.id = firebaseuser.getUid();

                    sharedprefs.putBoolean("isLogged",true);
                    sharedprefs.putString("uid",user.id);
                    db.saveLogin(user.id,email_edit.getText().toString(), password_edit.getText().toString(), email_edit.getText().toString(),sdf.format(new Date()),"logged");
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        email_edit.setError("User or password doesn't match");
                    }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        email_edit.setError("User doesn't match");
                    }
                    setEnable(true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
            setEnable(true);
        }
    }
}