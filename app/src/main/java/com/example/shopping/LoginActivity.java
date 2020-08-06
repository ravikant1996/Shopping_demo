package com.example.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 7117;
    public static String TAG = "MainActivity";
    FirebaseUser user;
    List<AuthUI.IdpConfig> providers;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        logintype_check();

    }

    public void logintype_check() {
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            showSignInOptions();
        }
    }

    public void signUp(View view) {
        providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        showSignInOptions();
    }

    private void showSignInOptions() {
        startActivityForResult(AuthUI
                .getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                .setTheme(R.style.AppThemeFirebaseAuth)
                .setTosAndPrivacyPolicyUrls(
                        "https://www.google.com/",
                        "https://www.google.com/")
                .setLogo(R.drawable.smartphone)
                .build(), MY_REQUEST_CODE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checking for request code
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(this, "Welcome! " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();

                    try {
                        String name = user.getDisplayName();
                        String email = user.getEmail();
                        String mobile = user.getPhoneNumber();
                        String Uid = user.getUid();
                        sessionManager.createDetailsSession(name, email, mobile, Uid);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                if (response != null) {
                    Toast.makeText(this, "" + Objects.requireNonNull(response.getError()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (response == null) {
                    // User pressed back button
//                    Toast.makeText(LoginActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Toast.makeText(LoginActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

}