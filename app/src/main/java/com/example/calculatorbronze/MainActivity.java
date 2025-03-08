package com.example.calculatorbronze;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private final String END_WITH_NUMBER_REGEX = "^.*[0-9]$";
    private final String ONLY_NUMBERS_REGEX = "^[0-9]+$";
    private Toast lastToast;
    private TextView formulaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        formulaText = findViewById(R.id.formula);

        // 数値ボタンのイベントリスナー登録
        findViewById(R.id.zero_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.one_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.two_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.three_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.four_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.five_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.six_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.seven_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.eight_button).setOnClickListener(numberButtonListener);
        findViewById(R.id.nine_button).setOnClickListener(numberButtonListener);

        // 演算子ボタンのイベントリスナー登録
        findViewById(R.id.addition_button).setOnClickListener(operatorButtonListener);
        findViewById(R.id.subtraction_button).setOnClickListener(operatorButtonListener);
        findViewById(R.id.multiplication_button).setOnClickListener(operatorButtonListener);
        findViewById(R.id.division_button).setOnClickListener(operatorButtonListener);

        findViewById(R.id.clear_button).setOnClickListener(clearButtonListener);
        findViewById(R.id.calc_result_button).setOnClickListener(calcResultButtonListener);
    }

    View.OnClickListener numberButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressedButton = (Button) v;
            CharSequence pressedNumber = pressedButton.getText();

            String formula =  formulaText.getText().toString();

            if(formula.equals("0")) {
                formulaText.setText("");
            }

            formulaText.append(pressedNumber);
        }
    };

    View.OnClickListener operatorButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressedButton = (Button) v;
            CharSequence operator = pressedButton.getText();

            String currentFormula = formulaText.getText().toString();

            if(currentFormula.isEmpty() || currentFormula.equals("0")) {
                showInvalidFormulaToast();
                return;
            }

            boolean endsWithNumber= currentFormula.matches(END_WITH_NUMBER_REGEX);

            if(endsWithNumber) {
                formulaText.append(operator);
            } else {
                CharSequence newFormula = currentFormula.substring(0, currentFormula.length() - 1) + operator;
                formulaText.setText(newFormula);
            }
        }
    };

    View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            formulaText.setText("");
        }
    };

    View.OnClickListener calcResultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String currentFormula = formulaText.getText().toString();

            boolean endsWithNumber = currentFormula.matches(END_WITH_NUMBER_REGEX);
            boolean hasOnlyNumbers = currentFormula.matches(ONLY_NUMBERS_REGEX);

            if(currentFormula.isEmpty()
                    || hasOnlyNumbers
                    || !endsWithNumber) {
                showInvalidFormulaToast();
                return;
            }


        }
    };

    private void showInvalidFormulaToast() {
        if(lastToast != null) {
            lastToast.cancel();
        }

        Context context = getApplicationContext();
        CharSequence message = getString(R.string.invalid_formula);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        lastToast = toast;
    }

    private double evaluateFormula(CharSequence formula) {

    }
}