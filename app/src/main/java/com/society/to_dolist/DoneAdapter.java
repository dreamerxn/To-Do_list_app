package com.society.to_dolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DoneAdapter extends RecyclerView.Adapter<DoneAdapter.MyDoneClass> {
    List<Tasks> dones;
    Context context;
    LayoutInflater layoutInflater;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String acId;
    String URL = "https://to-do-list-37448-default-rtdb.asia-southeast1.firebasedatabase.app/";
    DoneAdapter(List<Tasks> mDones, Context mContext){
        this.dones = mDones;
        this.context = mContext;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public class MyDoneClass extends RecyclerView.ViewHolder{
        TextView Title;
        TextView Desc;
        CardView cardView;
        LinearLayout linearLayout, liner;
        CheckBox checkBox;

        public MyDoneClass(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            Desc = itemView.findViewById(R.id.desc);
            cardView = itemView.findViewById(R.id.recCard);
            linearLayout = itemView.findViewById(R.id.parentCard);
            liner = itemView.findViewById(R.id.liner);
            checkBox = itemView.findViewById(R.id.isTaskCheck);
        }
    }
    @NonNull
    @Override
    public DoneAdapter.MyDoneClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.rec_layout, parent, false);
        MyDoneClass myDoneClass = new MyDoneClass(view);
        return myDoneClass;
    }

    @Override
    public void onBindViewHolder(@NonNull DoneAdapter.MyDoneClass holder, int position) {
        holder.Title.setText(dones.get(position).getTitle());
        holder.Desc.setText(dones.get(position).getDesc());
        holder.linearLayout.setBackgroundResource(R.color.done_bg);
        holder.liner.setBackgroundResource(R.color.done_bg);
        holder.checkBox.setChecked(true);
        holder.checkBox.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return dones.size();
    }
}
