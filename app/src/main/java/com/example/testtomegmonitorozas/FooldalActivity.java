package com.example.testtomegmonitorozas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FooldalActivity extends AppCompatActivity {
    private TableLayout weightTable;
    private ArrayList<Long> weights;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooldal);
        weightTable = findViewById(R.id.weight_table);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Súlyok táblázatba való betöltése
        loadWeightsFromDatabase();
    }

    private void loadWeightsFromDatabase() {
        db.collection("User").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                weights = (ArrayList<Long>) document.get("suly");
                                loadWeights(weights);
                            } else {
                                Toast.makeText(FooldalActivity.this, "Nincsenek súlyok az adatbázisban!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FooldalActivity.this, "Hiba történt az adatbázis elérésénél!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadWeights(ArrayList<Long> weights) {
        TableRow headerRow = new TableRow(this);
        TableRow.LayoutParams lpHeader = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(lpHeader);

        TextView headerWeight = new TextView(this);
        headerWeight.setText("Súlyok");
        headerWeight.setPadding(5, 5, 5, 5);
        headerWeight.setTextColor(getResources().getColor(android.R.color.white));
        headerWeight.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        headerWeight.setGravity(Gravity.CENTER);
        headerRow.addView(headerWeight);

        TextView headerDelete = new TextView(this);
        headerDelete.setText("Törlés");
        headerDelete.setPadding(5, 5, 5, 5);
        headerDelete.setTextColor(getResources().getColor(android.R.color.white));
        headerDelete.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        headerDelete.setGravity(Gravity.CENTER);
        headerRow.addView(headerDelete);

        weightTable.addView(headerRow);

        for (Long weight : weights) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView weightView = new TextView(this);
            weightView.setText(String.valueOf(weight));
            weightView.setPadding(5, 5, 5, 5);
            weightView.setGravity(Gravity.CENTER);
            row.addView(weightView);

            TextView deleteView = new TextView(this);
            deleteView.setText("Törlés");
            deleteView.setPadding(5, 5, 5, 5);
            deleteView.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Változtasd a színt az igényeid szerint
            deleteView.setGravity(Gravity.CENTER);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Törlési művelet
                    TableRow parentRow = (TableRow) v.getParent();
                    TextView weightTextView = (TextView) parentRow.getChildAt(0);
                    long weightToDelete = Long.parseLong(weightTextView.getText().toString());
                    weights.remove(weightToDelete);
                    refreshWeightTable();
                    removeFromDatabase(weightToDelete); // Adatbázisból való törlés
                }
            });
            row.addView(deleteView);

            weightTable.addView(row);
        }
    }

    private void refreshWeightTable() {
        weightTable.removeAllViews();
        loadWeights(weights);
    }

    private void removeFromDatabase(long weightToDelete) {
        db.collection("User").document(mAuth.getCurrentUser().getEmail()).update("suly", weights)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FooldalActivity.this, "Súly sikeresen törölve az adatbázisból!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FooldalActivity.this, "Hiba történt a súly törlésekor az adatbázisból!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            return true;
        } else if (id == R.id.action_profile) {
            // Profil menüpont kezelése
            startActivity(new Intent(this, ProfilActivity.class));
            finish();
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
