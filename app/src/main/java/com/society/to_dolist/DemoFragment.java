package com.society.to_dolist;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DemoFragment extends Fragment {
    String URL = "https://to-do-list-37448-default-rtdb.asia-southeast1.firebasedatabase.app/";
    FirebaseDatabase database;
    ProgressBar progressBar;
    int color;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    recAdapter adapter;
    TaskAdapter taskAdapter;
    DoneAdapter doneAdapter;
    String email;
    String acId;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    public static final String TITLE = "title";
    public DemoFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance(URL);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account !=null){
            email = account.getEmail();
            acId = account.getId();
        }
        return inflater.inflate(R.layout.my_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        progressBar = view.findViewById(R.id.progressBar);
        assert getArguments() != null;
        int i = getArguments().getInt(TITLE);
        String child = "Notes";
        color = i;
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (i == 0){
            getFromDatabase(recyclerView, acId+"/Notes");
        }
        else if(i == 1){
            getTasks(recyclerView, acId+"/Tasks");
        }
        else if(i == 2){
            getDones(recyclerView, acId+"/Done");

        }
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void getDones(RecyclerView recyclerView, String mchild){
        List<Tasks> dones;

        dones = new ArrayList<>();
        DatabaseReference myData = database.getReference(mchild);
        myData.keepSynced(true);

        doneAdapter = new DoneAdapter(dones, getContext());
        recyclerView.setAdapter(doneAdapter);
        myData.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dones.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Tasks dones1 = dataSnapshot.getValue(Tasks.class);
                    dones.add(dones1);
                    doneAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error hac ocuired", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void getTasks(RecyclerView recyclerView, String mchild){
        List<Tasks> tasks;

        tasks = new ArrayList<>();
        DatabaseReference myData = database.getReference(mchild);
        myData.keepSynced(true);

        taskAdapter = new TaskAdapter(tasks, getContext());
        recyclerView.setAdapter(taskAdapter);

        myData.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasks.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Tasks tasks1 = dataSnapshot.getValue(Tasks.class);
                    tasks.add(tasks1);
                    taskAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error hac ocuired", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void getFromDatabase(RecyclerView recyclerView, String mchild){
        List<Notes> nList;

        nList = new ArrayList<>();
        DatabaseReference myData = database.getReference(mchild);
        myData.keepSynced(true);
        adapter = new recAdapter(nList, getContext(), color);
        recyclerView.setAdapter(adapter);
        myData.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Notes notes = dataSnapshot.getValue(Notes.class);
                    nList.add(notes);
                    adapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error hac ocuired", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
