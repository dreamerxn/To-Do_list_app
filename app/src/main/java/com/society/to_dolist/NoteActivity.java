package com.society.to_dolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    EditText openTitle, openDesc;
    FirebaseDatabase database;
    DatabaseReference mData;
    String mTitle, mDesc, mUID;
    FloatingActionButton fab;
    ExtendedFloatingActionButton exFab;
    String URL = "https://to-do-list-37448-default-rtdb.asia-southeast1.firebasedatabase.app/";
    String email, acId, child;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initFind();
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        mDesc = intent.getStringExtra("desc");
        mUID = intent.getStringExtra("uid");
        openTitle.setText(mTitle);
        openDesc.setText(mDesc);
        exFab = findViewById(R.id.saveNote);
        mData = FirebaseDatabase.getInstance(URL).getReference(acId+"/Notes");
        child = mData.getKey();
        exFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account !=null){
            email = account.getEmail();
            acId = account.getId();
        }
    }

    public void initFind(){
        openTitle = findViewById(R.id.openTitle);
        openDesc = findViewById(R.id.openDesc);
    }

    public void updateData(){
        mData = FirebaseDatabase.getInstance(URL).getReference(acId+"/Notes");
        Toast.makeText(this, "GG "+child, Toast.LENGTH_SHORT).show();
        Query query = mData.orderByChild("uid").equalTo(mUID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Notes notes = new Notes(openTitle.getText().toString(), openDesc.getText().toString(), mUID);
//                    mData.child("title").setValue(openTitle.getText());
                    String gg = dataSnapshot.getKey();
                    Toast.makeText(NoteActivity.this, "sukaaaaa" + gg, Toast.LENGTH_SHORT).show();
                    mData.child(gg).setValue(notes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error has ocuired", Toast.LENGTH_SHORT).show();
            }
        });
    }
}