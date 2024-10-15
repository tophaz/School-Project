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

        // Handle fromBaseSpinner selection
        fromBaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBase = parentView.getItemAtPosition(position).toString();
                fromBase = getBaseFromSelection(selectedBase); // Map the selection to a base number
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // Handle toBaseSpinner selection
        toBaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBase = parentView.getItemAtPosition(position).toString();
                toBase = getBaseFromSelection(selectedBase); // Map the selection to a base number
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        convertButton.setOnClickListener(v -> convertNumber());
        copyButton.setOnClickListener(v -> copyToClipboard());
    }

    // Method to map the selected string from the spinner to its corresponding base
    private int getBaseFromSelection(String base) {
        switch (base) {
            case "Binary":
                return 2;
            case "Octal":
                return 8;
            case "Decimal":
                return 10;
            case "Hexadecimal":
                return 16;
            default:
                return 10; // Default to Decimal in case of unknown
        }
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
    }
}
