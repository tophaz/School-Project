package com.example.schoolproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputNumber;
    private Spinner fromBaseSpinner, toBaseSpinner;
    private TextView conversionResult;
    private Button convertButton, copyButton;

    private int fromBase = 10; // Default base is Decimal
    private int toBase = 10;   // Default base is Decimal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNumber = findViewById(R.id.inputNumber);
        fromBaseSpinner = findViewById(R.id.fromBase);
        toBaseSpinner = findViewById(R.id.toBase);
        conversionResult = findViewById(R.id.conversionResult);
        convertButton = findViewById(R.id.convertButton);
        copyButton = findViewById(R.id.copyButton);

        // Set up the base spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bases_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromBaseSpinner.setAdapter(adapter);
        toBaseSpinner.setAdapter(adapter);


        fromBaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fromBase = Integer.parseInt(parentView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        toBaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                toBase = Integer.parseInt(parentView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        convertButton.setOnClickListener(v -> convertNumber());
        copyButton.setOnClickListener(v -> copyToClipboard());
    }

    private void convertNumber() {
        String input = inputNumber.getText().toString().trim();
        if (input.isEmpty()) {
            conversionResult.setText("Please enter a number.");
            return;
        }

        if (fromBase == toBase) {
            conversionResult.setText("Please select different bases for conversion.");
            return;
        }

        try {
            String result = convert(input, fromBase, toBase);
            conversionResult.setText(result);
        } catch (Exception e) {
            conversionResult.setText("Conversion failed. Please check your input.");
        }
    }

    private String convert(String number, int fromBase, int toBase) {
        // Conversion logic for both integer and fractional parts
        if (number.contains(".")) {
            String[] parts = number.split("\\.");
            String integerPart = convertInteger(parts[0], fromBase, toBase);
            String fractionalPart = convertFraction(parts[1], fromBase, toBase);
            return integerPart + "." + fractionalPart;
        } else {
            return convertInteger(number, fromBase, toBase);
        }
    }

    private String convertInteger(String integer, int fromBase, int toBase) {
        return Integer.toString(Integer.parseInt(integer, fromBase), toBase).toUpperCase();
    }

    private String convertFraction(String fraction, int fromBase, int toBase) {
        double decimalFraction = 0;
        for (int i = 0; i < fraction.length(); i++) {
            decimalFraction += Integer.parseInt(String.valueOf(fraction.charAt(i)), fromBase) / Math.pow(fromBase, i + 1);
        }

        StringBuilder result = new StringBuilder();
        while (decimalFraction > 0 && result.length() < 10) {
            decimalFraction *= toBase;
            int digit = (int) decimalFraction;
            result.append(Integer.toString(digit, toBase).toUpperCase());
            decimalFraction -= digit;
        }
        return result.length() > 0 ? result.toString() : "0";
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Conversion Result", conversionResult.getText());
        clipboard.setPrimaryClip(clip);
        // Optionally show a Toast message here
    }
}
