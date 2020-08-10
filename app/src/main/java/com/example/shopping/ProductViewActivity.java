package com.example.shopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ProductViewActivity extends AppCompatActivity implements ItemsAdapter.Adapter {
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;
    String cname, date;
    ArrayList<items> itemsList;
    long money = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getMoney();

        try {
            Bundle bundle = getIntent().getExtras();
            cname = bundle.getString("CNAME");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        recyclerView = findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();

        itemListView();

    }

    private void itemListView() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ProductDetail");
        Query query = reference.orderByChild("company").equalTo(cname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot areaSnapshot : snapshot.getChildren()) {
                        items details = areaSnapshot.getValue(items.class);
                        itemsList.add(details);
                    }
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    itemsAdapter = new ItemsAdapter(itemsList, ProductViewActivity.this);
                    recyclerView.setAdapter(itemsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getMoney() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Transaction_History").child(userId);
        Query query = mRef.child("totalMoney");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        money = snapshot.getValue(Long.class);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (RuntimeExecutionException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onServiceItemClicked(View view, int position, long price, String name, String image) {
        try {
            if (money >= price) {
                money = money - price;
                String status = "Debited";
                dateoftransaction(status, name, String.valueOf(price), image);
//                Balance.setText("₹ " + money);
                Toast.makeText(ProductViewActivity.this, "Bought", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(ProductViewActivity.this, "Recharge your wallet", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void dateoftransaction(final String status, final String name, final String addedMoney, final String image) {
        Calendar now = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(now.getTime());
        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        date = date + " " + dateFormat1.format(now.getTime());

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Transaction_History").child(userId);
        final String keyId = mRef.push().getKey();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final transaction trans = new transaction(date, addedMoney, name, image, status);

                mRef.child("totalMoney").setValue(money);
                mRef.child(keyId).setValue(trans);
                Toast.makeText(ProductViewActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductViewActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}