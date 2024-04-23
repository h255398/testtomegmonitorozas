package com.example.testtomegmonitorozas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfilActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private TextView emailPlaceholder;
    private TextView sulyTextView;
    private TextView sulyPlaceholder;
    private Button elkuldomButton;

    private static final String LOG_TAG = ProfilActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        emailPlaceholder = findViewById(R.id.emailPlaceholder);
        sulyTextView = findViewById(R.id.sulyTextView);
        sulyPlaceholder = findViewById(R.id.sulyPlaceholder);
        elkuldomButton = findViewById(R.id.elkuldom);

        if (user != null) {
            String email = user.getEmail();
            emailPlaceholder.setText(email != null ? email : getString(R.string.ures));

            Log.d(LOG_TAG, "Bejelentkezett felhasználó!");
        } else {
            Log.d(LOG_TAG, "Nem bejelentkezett felhasználó");
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<Integer> sulyList = extras.getIntegerArrayList("suly");
            if (sulyList != null && !sulyList.isEmpty()) {
                int lastSuly = sulyList.get(sulyList.size() - 1);
                sulyTextView.setText(String.valueOf(lastSuly));
                sulyPlaceholder.setText(String.valueOf(lastSuly));
            } else {
                sulyTextView.setText(getString(R.string.ures));
                sulyPlaceholder.setText(getString(R.string.ures));
            }
        } else {
            // Ha nincsenek adatok, frissítsük a Placeholder-t az aktuális súllyal
            refreshPlaceholder();
        }
    }

    public void send(View view) {
        EditText sulyEditText = findViewById(R.id.sulyEditText);
        String sulyString = sulyEditText.getText().toString();

        if (!sulyString.isEmpty()) {
            int suly = Integer.parseInt(sulyString);
            addWeight(suly);
            sulyEditText.getText().clear();
        }
    }

    private void addWeight(int weight) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User").document(user.getEmail());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<Integer> sulyList = (ArrayList<Integer>) documentSnapshot.get("suly");
                if (sulyList == null) {
                    sulyList = new ArrayList<>();
                }
                sulyList.add(weight);
                saveUserData(sulyList);
            }
        }).addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a súlyok lekérése közben", e));
    }

    private void saveUserData(ArrayList<Integer> sulyList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User").document(user.getEmail());

        userRef.update("suly", sulyList)
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOG_TAG, "Felhasználói adatok sikeresen frissítve a Firestore-ban");
                    if (!sulyList.isEmpty()) {
                        //  sulyTextView.setText(String.valueOf(sulyList.get(sulyList.size() - 1)));
                        sulyPlaceholder.setText(String.valueOf(sulyList.get(sulyList.size() - 1)));
                    } else {
                        //  sulyTextView.setText(getString(R.string.ures));
                        sulyPlaceholder.setText(getString(R.string.ures));
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a felhasználói adatok frissítése közben", e));
    }

    private void refreshPlaceholder() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User").document(user.getEmail());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<Integer> sulyList = (ArrayList<Integer>) documentSnapshot.get("suly");
                if (sulyList != null && !sulyList.isEmpty()) {
                    sulyPlaceholder.setText(String.valueOf(sulyList.get(sulyList.size() - 1)));
                } else {
                    sulyPlaceholder.setText(getString(R.string.ures));
                }
            }
        }).addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a súlyok lekérése közben", e));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.action_home) {
            // Főoldal menüpont kezelése
            startActivity(new Intent(this, FooldalActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_profile) {
            // Profil menüpont kezelése
            return true;
        } else if (id == R.id.action_logout) {
            // Kijelentkezés menüpont kezelése
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
