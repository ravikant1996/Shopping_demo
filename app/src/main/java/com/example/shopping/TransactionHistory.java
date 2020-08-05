package com.example.shopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<transaction> arrayList;
    LinearLayoutManager layoutManager;
    transactionAdapter dataAdapter;
    SharedPreferences sharedPreferences;
    List<transaction> favorites;
    SessionManager session;
    String money;
    String UID;
    TextView Balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerView);
        Balance = findViewById(R.id.balance);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            money = String.valueOf(bundle.getInt("MONEY"));
            UID = bundle.getString("UID");
            Balance.setText("₹ "+money);
            //     Log.e(TAG, usertype);
            //     Log.e(TAG, UID);
        }catch (Exception e){
            e.printStackTrace();
        }


        session = new SessionManager(getApplicationContext());
        session.getFavorites();


        arrayList = new ArrayList<transaction>();



        recycler1();
    }

    public void recycler1() {
        // layout visibility
        // Adding items to RecyclerView.
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        Log.e("UID", userId);
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Transaction_History");
        Query query = mRef.child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    for (DataSnapshot areaSnapshot : snapshot.getChildren()) {
                        transaction details = areaSnapshot.getValue(transaction.class);
                        arrayList.add(details);
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                dataAdapter = new transactionAdapter(arrayList, getApplicationContext());
                recyclerView.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
