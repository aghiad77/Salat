package com.example.salat.viewModels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.salat.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FirebaseViewModel extends ViewModel {

    private static final String TAG = com.example.salat.viewModels.FirebaseViewModel.class.getSimpleName();
    FirebaseDatabase firebaseDatabase;
    public static final String userRef = "users";
    public static final String userStatusRef = "status";
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy HH:mm:ss", Locale.ENGLISH);
    String uid;
    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<User> currentUserMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();
    MutableLiveData<User> userStatusAlertMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> userExist = new MutableLiveData<>();
    MutableLiveData<Boolean> userStatus = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public FirebaseViewModel(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        uid = FirebaseAuth.getInstance().getUid();
    }

    public MutableLiveData<Boolean> insertUer(User user, String uid){
        firebaseDatabase.getReference().child(userRef).child(uid).setValue(user);
        isLoggedIn.setValue(true);
        return isLoggedIn;
    }

    public void addUser(User user){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child(userRef).child(uid).push().setValue(user);
    }

    public MutableLiveData<Boolean> getUserExist() {
        return userExist;
    }
    public MutableLiveData<User> getUser() {

        firebaseDatabase.getReference().child(userRef).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChildren()){
                    currentUserMutableLiveData.setValue(snapshot.getValue(User.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return currentUserMutableLiveData;
    }
    public MutableLiveData<List<User>> getUsersMutableLiveData() {
        firebaseDatabase.getReference().child(userRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot :
                        snapshot.getChildren()) {
                    userList.add(dataSnapshot.getValue(User.class));
                }
                if (!userList.isEmpty()){
                    usersMutableLiveData.setValue(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return usersMutableLiveData;
    }
}


