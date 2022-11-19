package com.society.to_dolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyTaskViewClass> {
    List<Tasks> tasks;
    Context context;
    LayoutInflater layoutInflater;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String acId;
    String URL = "https://to-do-list-37448-default-rtdb.asia-southeast1.firebasedatabase.app/";
    TaskAdapter(List<Tasks> mTasks, Context mContext){
        this.tasks = mTasks;
        this.context = mContext;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public class MyTaskViewClass extends RecyclerView.ViewHolder{
        TextView Title;
        TextView Desc;
        CardView cardView;
        LinearLayout linearLayout, liner;
        CheckBox checkBox;

        @SuppressLint("NotifyDataSetChanged")
        public MyTaskViewClass(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            Desc = itemView.findViewById(R.id.desc);
            cardView = itemView.findViewById(R.id.recCard);
            linearLayout = itemView.findViewById(R.id.parentCard);
            liner = itemView.findViewById(R.id.liner);
            checkBox = itemView.findViewById(R.id.isTaskCheck);
            getAccount();

            checkBox.setOnClickListener(v -> {
                String sTitle, sDesc, sUID;

                try {
                    sTitle = Title.getText().toString();
                    sDesc = Desc.getText().toString();
                    sUID = tasks.get(getAdapterPosition()).getUID();
                    delFromFireDatabase(sUID);
                    pushData(sTitle, sDesc, sUID, getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    if (tasks.size() == 1){
                        tasks.clear();
                    }
                }catch (Exception e){
                    notifyDataSetChanged();
                    Toast.makeText(context, "Error has occurred "+e, Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "GG  "+e);
                }
            });

        }
    }
    @NonNull
    @Override
    public TaskAdapter.MyTaskViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.rec_layout, parent, false);
        MyTaskViewClass myTaskViewClass = new MyTaskViewClass(view);
        return myTaskViewClass;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.MyTaskViewClass holder, @SuppressLint("RecyclerView") int position) {
        holder.Desc.setText(tasks.get(position).getDesc());
        holder.Title.setText(tasks.get(position).getTitle());
        holder.liner.setBackgroundResource(R.color.tasks_bg);
        holder.linearLayout.setBackgroundResource(R.color.tasks_bg);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public  void getAccount(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(context, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account !=null){
            acId = account.getId();
        }
    }
    private void delFromFireDatabase(String UID){
        ref = FirebaseDatabase.getInstance(URL).getReference(acId+"/Tasks");
        Query query = ref.orderByChild("uid").equalTo(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error has occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pushData(String Title, String Desc, String UID, int pos){
        database = FirebaseDatabase.getInstance(URL);
        ref = database.getReference(acId+"/Done");
        Notes notes = new Notes(Title, Desc, UID);
        ref.push().setValue(notes);

    }
}
