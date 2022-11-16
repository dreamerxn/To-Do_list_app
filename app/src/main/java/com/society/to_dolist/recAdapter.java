package com.society.to_dolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class recAdapter extends RecyclerView.Adapter<recAdapter.MyViewClass> {

    List<Notes> notes;
    Context mContext;
    LayoutInflater mLayoutInflater;
    int color;
    String URL = "https://to-do-list-37448-default-rtdb.asia-southeast1.firebasedatabase.app/";
    String email, acId;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    public recAdapter(List<Notes> mnotes, Context context, int mColor){
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.notes = mnotes;
        this.color = mColor;
    }

    public class MyViewClass extends RecyclerView.ViewHolder{
        TextView Title;
        TextView Desc;
        CardView cardView;
        LinearLayout linearLayout, liner;
        CheckBox checkBox;


        public MyViewClass(@NonNull View itemView) {
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
    public recAdapter.MyViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.rec_layout, parent, false);
        MyViewClass myViewClass = new MyViewClass(view);
        return myViewClass;
    }
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    public void onBindViewHolder(@NonNull recAdapter.MyViewClass holder, @SuppressLint("RecyclerView") int position) {
        holder.Desc.setText(notes.get(position).getDesc());
        holder.Title.setText(notes.get(position).getTitle());
        if (color == 0){
            holder.liner.setBackgroundResource(R.color.notes_bg);
            holder.linearLayout.setBackgroundResource(R.color.notes_bg);
        }
        else if(color == 1){
            holder.liner.setBackgroundResource(R.color.tasks_bg);
            holder.linearLayout.setBackgroundResource(R.color.tasks_bg);
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        else if(color == 2){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(false);
            holder.liner.setBackgroundResource(R.color.done_bg);
            holder.linearLayout.setBackgroundResource(R.color.done_bg);
        }
//        holder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onClick(View v) {
//                getAccount();
//                if (v!=null){
//                    if (notes.size()!=0) {
//                        String desc = notes.get(position).getDesc();
//                        String title = notes.get(position).getTitle();
//                        String UID = notes.get(position).getUID();
//                        delFromFireDatabase(UID, "Tasks", holder.getAdapterPosition());
//                        notifyDataSetChanged();
//                        pushData(title, desc, UID, position);
//                        notifyDataSetChanged();
//                    }
//                    else {
//                        Toast.makeText(mContext, "Error ocuired", Toast.LENGTH_SHORT).show();
//                        notifyDataSetChanged();
//                    }
//
//                }
//            }
//        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onLongClick(View v) {
                getAccount();
                String UID = notes.get(position).getUID();
                database = FirebaseDatabase.getInstance(URL);
                delFromFireDatabase(UID, "Notes", holder.getAdapterPosition());
                delFromFireDatabase(UID, "Done", holder.getAdapterPosition());
                notifyDataSetChanged();
                return true;
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccount();
                Intent intent = new Intent(v.getContext(), NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title", notes.get(position).getTitle());
                intent.putExtra("desc", notes.get(position).getDesc());
                intent.putExtra("uid", notes.get(position).getUID());
                v.getContext().startActivity(intent);
            }
        });
    }

    public  void getAccount(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(mContext, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        if(account !=null){
            email = account.getEmail();
            acId = account.getId();
        }
    }

    public void pushData(String Title, String Desc, String UID, int pos){
        database = FirebaseDatabase.getInstance(URL);
        ref = database.getReference(acId+"/Done");
        Notes notes = new Notes(Title, Desc, UID);
        ref.push().setValue(notes);
        notifyItemInserted(pos);

    }

    private void delFromFireDatabase(String UID, String path, int pos){
        ref = FirebaseDatabase.getInstance(URL).getReference(acId+"/Notes");
        Query query = ref.orderByChild("uid").equalTo(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Toast.makeText(mContext, "ll "+ref.getKey(), Toast.LENGTH_SHORT).show();
                    notes.remove(pos);
                    notifyItemRemoved(pos);
                    dataSnapshot.getRef().removeValue();
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error has ocuires", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getParent(String UID, String path, int pos){
        ref = FirebaseDatabase.getInstance(URL).getReference(acId+"/"+path);
        Query query = ref.orderByChild("uid").equalTo(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Toast.makeText(mContext, "ll "+ref.getKey(), Toast.LENGTH_SHORT).show();
                    notes.remove(pos);
                    notifyItemRemoved(pos);
                    dataSnapshot.getRef().removeValue();
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error has ocuires", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
