package com.example.cgpaconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private SharedPreferences sharedPref;
    private final String historyKey = "cgpa_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCgpa = findViewById(R.id.etCgpa);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);
        pieChart = findViewById(R.id.pieChart);
        rvHistory = findViewById(R.id.rvHistory);

        sharedPref = getSharedPreferences("CGPAApp", Context.MODE_PRIVATE);
        List<Conversion> history = loadHistory();

        adapter = new HistoryAdapter(new ArrayList<>(history));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        btnConvert.setOnClickListener(v -> convertCGPA());
    }

    private void convertCGPA() {
        String cgpaStr = etCgpa.getText().toString();

        if (cgpaStr.isEmpty()) {
            Toast.makeText(this, "Please enter CGPA", Toast.LENGTH_SHORT).show();
            return;
        }

        Double cgpa = null;
        try {
            cgpa = Double.parseDouble(cgpaStr);
        } catch (Exception e) {
            cgpa = null;
        }

        if (cgpa == null || cgpa < 0 || cgpa > 10) {
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
