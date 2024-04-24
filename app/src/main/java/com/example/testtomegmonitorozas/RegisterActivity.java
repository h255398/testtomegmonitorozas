package com.example.testtomegmonitorozas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    EditText kg;
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private static final int SECRET_KEY = 99;

    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private CollectionReference mUserdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }

        passwordEditText = findViewById(R.id.passwordEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        kg = findViewById(R.id.kg);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");
        String password = preferences.getString("password", "");

        String kG = preferences.getString("kg", "");

        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);
        userEmailEditText.setText(userEmail);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");

        mFirestore = FirebaseFirestore.getInstance();
        mUserdb = mFirestore.collection("User");
    }

    public void register(View view) {
        Animation animSlideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
        view.startAnimation(animSlideIn);

        String password = passwordEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();


        String kG = kg.getText().toString();


        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Nem egyezik a két jelszó!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres regisztráció!");
                    Log.i(LOG_TAG, "Sikeresen regisztrált: " + email + " jelszó: " + password);
                    String[] kGArray = kG.split(",");
                    List<Integer> kGList = new ArrayList<>();
                    for (String s : kGArray) {
                        kGList.add(Integer.parseInt(s.trim()));
                    }
                    saveUserData(email, kGList);
                    startProfil();
                } else {
                    Log.d(LOG_TAG, "Sikertelen regisztráció!");
                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void saveUserData(String email, List<Integer> kg) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("suly", kg);

        mUserdb.document(email).set(user)
                .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "Felhasználói adatok sikeresen mentve a Firestore-ban"))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a felhasználói adatok mentése közben", e));
    }

    private void startProfil() {
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }

    public void cancel(View view) {

        Animation animSlideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
        view.startAnimation(animSlideIn);


        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    private Integer[] stringToIntArray(String string) {
        String[] stringArray = string.split(",");
        Integer[] intArray = new Integer[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i].trim());
        }
        return intArray;
    }

}
