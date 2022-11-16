package com.society.to_dolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends AppCompatActivity {

    RelativeLayout parent;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    FloatingActionButton fab;
    TabLayout tabLayout;
    FirebaseDatabase database;
    String URL = "https://to-do-list-37448-default-rtdb.asia-southeast1.firebasedatabase.app/";
    ViewPager2 viewPager;
    Button saveButton;
    EditText editTitle, editText;
    DatabaseReference mData;
    CheckBox task_check;
    InputMethodManager imm;
    View view;
    String email, acId;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance(URL);
        database.setPersistenceEnabled(true);
        initializeFind();
        bottom();
        AdapterDemo adapterDemo = new AdapterDemo(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapterDemo);
        tableLayout();
        save();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account !=null){
            email = account.getEmail();
            acId = account.getId();
            Toast.makeText(this, "email = "+email, Toast.LENGTH_SHORT).show();
        }


    }

    public void bottom(){
        bottomSheetBehavior = BottomSheetBehavior.from(parent);
        bottomSheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_PEEK_HEIGHT|BottomSheetBehavior.SAVE_FIT_TO_CONTENTS);
        fab.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    view.setVisibility(View.VISIBLE);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    });
                }
                else if(newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) view.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                fab.animate().scaleX(1-slideOffset).scaleY(1-slideOffset).setDuration(0).start();
            }
        });
    }

    public void initializeFind(){
        parent = findViewById(R.id.bottomSheet);
        fab = findViewById(R.id.myFab);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        saveButton = findViewById(R.id.saveButton);
        editTitle = findViewById(R.id.editTitle);
        editText = findViewById(R.id.editText);
        task_check = findViewById(R.id.tasks_check);
        view = findViewById(R.id.backTouch);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void save(){
        saveButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String text = editText.getText().toString();
            if (!title.equals("") || !text.equals("")){
                pushData(title, text);
                editTitle.setText(null);
                editText.setText(null);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            else Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            View view = this.getCurrentFocus();
            if (view != null){
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        });
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetBehavior.setPeekHeight(0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else{
            super.onBackPressed();
        }
    }

    public void tableLayout(){
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0){
                tab.setText(R.string.notes);
            }
            else if(position == 1){
                tab.setText(R.string.tasks);
            }
            else if(position == 2){
                tab.setText(R.string.done);
            }
        }).attach();
    }


    public void pushData(String Title, String Desc){
        if (task_check.isChecked()){
            mData = database.getReference(acId+"/Tasks");
        }
        else {
            mData = database.getReference(acId+"/Notes");
        }
        int UID = ThreadLocalRandom.current().nextInt(0, 100000000);
        String UUID = String.valueOf(UID);
        Notes notes = new Notes(Title, Desc, UUID);
        mData.push().setValue(notes);
        editText.setText(null);

    }

}