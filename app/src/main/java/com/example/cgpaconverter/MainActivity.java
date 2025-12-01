package com.example.cgpaconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etCgpa;
    private Button btnConvert;
    private TextView tvResult;
    private PieChartView pieChart;
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;

    private ImageButton btnLogout;  // ✅ Correct type

    private SharedPreferences sharedPref;
    private final String historyKey = "cgpa_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI initialization
        etCgpa = findViewById(R.id.etCgpa);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);
        pieChart = findViewById(R.id.pieChart);
        rvHistory = findViewById(R.id.rvHistory);
        btnLogout = findViewById(R.id.btnLogout);   // ✅ Correct initialization

        // SharedPreferences setup
        sharedPref = getSharedPreferences("CGPAApp", Context.MODE_PRIVATE);
        List<Conversion> history = loadHistory();

        // RecyclerView setup
        adapter = new HistoryAdapter(new ArrayList<>(history));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        // Convert Button
        btnConvert.setOnClickListener(v -> convertCGPA());

        // Logout Button
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        // Firebase Sign Out
        FirebaseAuth.getInstance().signOut();

        // Google Sign Out
        GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        // Redirect to Login Page
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void convertCGPA() {
        String cgpaStr = etCgpa.getText().toString();

        if (cgpaStr.isEmpty()) {
            Toast.makeText(this, "Please enter CGPA", Toast.LENGTH_SHORT).show();
            return;
        }

        Double cgpa;
        try {
            cgpa = Double.parseDouble(cgpaStr);
        } catch (Exception e) {
            Toast.makeText(this, "Enter valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cgpa < 0 || cgpa > 10) {
            Toast.makeText(this, "Enter valid CGPA (0-10)", Toast.LENGTH_SHORT).show();
            return;
        }

        double percentage = cgpa * 9.5;
        tvResult.setText("Your Percentage: " + String.format("%.2f", percentage) + "%");
        tvResult.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.VISIBLE);

        pieChart.setPercentage(percentage);

        String date = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        Conversion conversion = new Conversion(cgpa, percentage, date);
        adapter.addConversion(conversion);

        saveHistory(adapter.getConversions());
        etCgpa.setText("");
    }

    private void saveHistory(List<Conversion> history) {
        SharedPreferences.Editor editor = sharedPref.edit();
        StringBuilder sb = new StringBuilder();

        for (Conversion c : history) {
            sb.append(c.cgpa)
                    .append("|")
                    .append(c.percentage)
                    .append("|")
                    .append(c.date)
                    .append(",");
        }

        editor.putString(historyKey, sb.toString());
        editor.apply();
    }

    private List<Conversion> loadHistory() {
        String json = sharedPref.getString(historyKey, "");
        List<Conversion> list = new ArrayList<>();

        if (json == null || json.isEmpty()) return list;

        String[] items = json.split(",");

        for (String item : items) {
            String[] parts = item.split("\\|");

            if (parts.length == 3) {
                try {
                    double cgpa = Double.parseDouble(parts[0]);
                    double percent = Double.parseDouble(parts[1]);
                    String date = parts[2];
                    list.add(new Conversion(cgpa, percent, date));
                } catch (Exception ignored) {}
            }
        }

        return list;
    }
}
