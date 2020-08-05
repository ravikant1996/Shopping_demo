package com.example.shopping;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements ItemsAdapter.Adapter {
    final Context context = this;
    int money = 0;
    TextView Balance;
    EditText Addbalance;
    Button Addbtn;
    String date;
    Menu menu;
    //double tap of exit
    private static final int TIME_INTERVAL = 4000;
    private long mBackPressed;
    LinearLayoutManager layoutManager;

    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;
    ArrayList<items> itemsList = new ArrayList<>();
    SessionManager session;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    TextView Name, EmailId;
    ImageView Profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.e("Test1", "Test1");
        session = new SessionManager(getApplicationContext());
        session.isLoggedIn();
        session.checkLogin();
        Log.e("Test2", "Test2");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Log.e("Test3", "Test3");

        navigationView = findViewById(R.id.enduser_navigation);
        recyclerView = findViewById(R.id.recyclerView);
        itemsListfunc();
        recyclerView();

        View headerView = navigationView.inflateHeaderView(R.layout.layout_header);
        Profile_image = headerView.findViewById(R.id.profile_image);
        Name = (TextView) headerView.findViewById(R.id.name);
        EmailId = (TextView) headerView.findViewById(R.id.email);
        Log.e("Test4", "Test4");


        try {
            Log.e("Test5", "Test5");

            FirebaseUser user;
            user = FirebaseAuth.getInstance().getCurrentUser();
            try {
                getMoney();
                Log.e("Test6", "Test6");

            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }

            HashMap<String, String> details = session.getUserDetails();
            String mob = details.get(SessionManager.KEY_PHONE);
            String name = details.get(SessionManager.KEY_NAME);
            if (details != null) {
                if (mob != null) {
                    if (name != null) {
                        Name.setText(details.get(SessionManager.KEY_NAME) + "" + (details.get(SessionManager.KEY_PHONE)));
                    } else {
                        Name.setText(details.get(SessionManager.KEY_PHONE));
                    }
                } else {
                    Name.setText(details.get(SessionManager.KEY_NAME));
                }
                EmailId.setText(details.get(SessionManager.KEY_EMAIL));
                if (EmailId.getText().toString().length() == 0) {
                    EmailId.setVisibility(View.GONE);
                }
                if (Name.getText().toString().length() == 0) {
                    Name.setText("Guest");
                }
            } else {
                if (user != null) {
                    String names = user.getDisplayName();
                    String email = user.getEmail();
                    String mobile = user.getPhoneNumber();
                    if (mobile != null) {
                        if (names != null) {
                            Name.setText(names + " " + mobile);
                        } else {
                            Name.setText(mobile);
                        }
                    } else {
                        Name.setText(names);
                    }
                    EmailId.setText(email);
                    if (EmailId.getText().toString().length() == 0) {
                        EmailId.setVisibility(View.GONE);
                    }
                    if (Name.getText().toString().length() == 0) {
                        Name.setText("Guest");
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.wallet_recharge:
                        addbalance();
                        break;
                    case R.id.logout:
                        AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //User is now signed out
                                session.logoutUser();
                                Toast.makeText(getApplicationContext(), "User is signed out", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.transMenu:
                        Intent intent1 = new Intent(MainActivity.this, TransactionHistory.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("MONEY", money);
                        bundle1.putString("UID", FirebaseAuth.getInstance().getUid());
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    private void getMoney() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        Log.e("Test7", "Test7");

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Transaction_History").child(userId);
        Query query = mRef.child("totalMoney");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        money = snapshot.getValue(Integer.class);
                        Log.e("Test8", "Test8");
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

    private void recyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemsAdapter = new ItemsAdapter(itemsList, this);
        recyclerView.setAdapter(itemsAdapter);
    }

    private void itemsListfunc() {
        items data = new items("9499", "Infinix Hot 9 (Violet, 64 GB)  (4 GB RAM)", "https://rukminim1.flixcart.com/image/416/416/k8g8knk0/mobile/m/b/z/infinix-hot-9-x655d-original-imafqgr9j7gh32zq.jpeg?q=70");
        itemsList.add(data);
        data = new items("9999", "Motorola G8 Power Lite (Royal Blue, 64 GB)  (4 GB RAM)", "https://rukminim1.flixcart.com/image/416/416/kae95e80/mobile/2/z/r/motorola-g8-power-lite-pajc0008in-original-imafrz85cmz2ycx8.jpeg?q=70");
        itemsList.add(data);
        data = new items("8999", "Realme Narzo 10A (So White, 32 GB)  (3 GB RAM)", "https://rukminim1.flixcart.com/image/416/416/k8ddoy80/mobile/u/g/w/realme-narzo-10a-rmx2020-original-imafqechxsprgfgr.jpeg?q=70");
        itemsList.add(data);
        data = new items("10000", "Motorola Edge+ (Thunder Grey, 256 GB)  (12 GB RAM)", "https://rukminim1.flixcart.com/image/416/416/k9loccw0/mobile/h/f/h/motorola-racer-turbo-edge-xt2061-3-original-imafrcvgpqx23mcq.jpeg?q=70");
        itemsList.add(data);
        data = new items("15000", "Samsung Galaxy A31 (Prism Crush White, 128 GB)  (6 GB RAM)", "https://rukminim1.flixcart.com/image/416/416/kamtsi80/mobile/4/a/6/samsung-galaxy-a31-sm-a315fzwwins-original-imafs5p5y3gf4cej.jpeg?q=70");
        itemsList.add(data);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_btn, menu);
//        menu.findItem(R.id.logout).setIcon(R.mipmap.ic_logout);
//        final MenuItem item = menu.findItem(R.id.wallet);
//        menu.findItem(R.id.wallet)
//                .setTitle("₹ " + showWallet);
//        MenuItemCompat.getActionView(item).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addbalance();
//            }
//        });
//        MenuItem walletItem=menu.getItem(R.id.wallet); // here itemIndex is int
//        item.setTitle("" + money);
        return true;
    }

//    public void showProfileMenuPopup(View v) {
//        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_btn, popup.getMenu());
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Toast.makeText(MainActivity.this, "Wallet clicked", Toast.LENGTH_SHORT).show();
//
//                return false;
//            }
//        });
//        popup.show();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.wallet) {
            addbalance();
//            menu.findItem(R.id.wallet)
//                    .setTitle("₹ " + money);
            return true;
        } else if (id == R.id.logout) {
            logout();
            return true;
        } else if (id == R.id.cart) {
            Intent intent1 = new Intent(MainActivity.this, TransactionHistory.class);
            Bundle bundle1 = new Bundle();
            bundle1.putInt("MONEY", money);
            bundle1.putString("UID", FirebaseAuth.getInstance().getUid());
            intent1.putExtras(bundle1);
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            menu.findItem(R.id.wallet)
                    .setTitle("₹ " + money);
            session.CreateWalletBalanceSession(String.valueOf(money));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void addbalance() {
        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        dialog.setContentView(R.layout.layout_wallet_recharge);
        // Set dialog title
        dialog.setTitle("Wallet");

        Balance = dialog.findViewById(R.id.balance);
        Addbalance = dialog.findViewById(R.id.addbalance);
        Addbtn = dialog.findViewById(R.id.addbtn);
        Balance.setText("₹ " + money);

        Addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Addbalance.getText().toString().trim().length() != 0) {
                        money = Integer.parseInt(Addbalance.getText().toString().trim()) + Integer.parseInt(Balance.getText().toString().replace("₹ ", ""));
                        Balance.setText("₹ " + money);
                        String name = "Wallet Recharge";
                        String image = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRP8WkcwI-oskEzQQS9doRiInnm4b2kOST_GA&usqp=CAU";
                        String status = "Credit";
                        String addedmoney = Addbalance.getText().toString().trim();
                        Addbalance.setText("");
                        dateoftransaction(status, name, addedmoney, image);
                    } else {
                        Toast.makeText(MainActivity.this, "Add balance", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
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
//                session.addFavorite(trans);
                Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

  /*  private void getCount() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Transaction_History");
        Query query = mRef.child(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    count = snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
*/

    private void logout() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //User is now signed out
                session.logoutUser();
                Toast.makeText(getApplicationContext(), "User is signed out", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                finishAffinity();
            } else {
                Toast.makeText(getBaseContext(), "Press back if you want to exit", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public void onServiceItemClicked(View view, int position, String price, String name, String image) {
        try {
            if (money >= Integer.parseInt(price)) {
                money = money - Integer.parseInt(price);
                String status = "Debited";
                dateoftransaction(status, name, price, image);
                Balance.setText("₹ " + money);

//            showWallet = money;
                Toast.makeText(context, "Bought", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Recharge your wallet", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}