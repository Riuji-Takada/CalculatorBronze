package com.example.calculatorbronze;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private Toast lastToast;
    private TextView formulaText;
    private Formula formula;

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

        formula = new Formula();

        formulaText = findViewById(R.id.formula);

        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
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

        findViewById(R.id.signal_button).setOnClickListener(signalButtonListener);
        findViewById(R.id.percent_button).setOnClickListener(percentButtonListener);
        findViewById(R.id.clear_button).setOnClickListener(clearButtonListener);
        findViewById(R.id.all_clear_button).setOnClickListener(allClearButtonListener);
        findViewById(R.id.calc_result_button).setOnClickListener(calcResultButtonListener);
    }

    View.OnClickListener numberButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressedButton = (Button) v;
            String pressedNumber = pressedButton.getText().toString();

            boolean isSuccess = formula.addToken(pressedNumber);

            formulaText.setText(formula.toString());
        }
    };

    View.OnClickListener operatorButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressedButton = (Button) v;
            String operator = "";

            int id = pressedButton.getId();

            if(id == R.id.addition_button) {
                operator = Operators.ADDITION.toString();
            } else if(id == R.id.subtraction_button) {
                operator = Operators.SUBTRACTION.toString();
            } else if(id == R.id.multiplication_button) {
                operator = Operators.MULTIPLICATION.toString();
            } else if(id == R.id.division_button) {
                operator = Operators.DIVISION.toString();
            }

            boolean isSuccess = formula.addToken(operator);

            if(isSuccess) {
                formulaText.setText(formula.toString());
            } else {
                showInvalidFormulaToast();
            }
        }
    };

    View.OnClickListener signalButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isSuccess = formula.changeLastTokensSignal();

            if(isSuccess) {
                formulaText.setText(formula.toString());
            } else {
                showInvalidFormulaToast();
            }
        }
    };

    View.OnClickListener percentButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isSuccess = formula.changeLastTokenToPercentage();

            if(isSuccess) {
                formulaText.setText(formula.toString());
            } else {
                showInvalidFormulaToast();
            }
        }
    };

    View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            formula.removeLastToken();
            formulaText.setText(formula.toString());
        }
    };

    View.OnClickListener allClearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            formula.clearFormula();
            formulaText.setText("");
        }
    };

    View.OnClickListener calcResultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isSuccess = formula.evaluateFormula();

            if(isSuccess) {
                formulaText.setText(formula.toString());
            } else {
                showInvalidFormulaToast();
            }
        }
    };

    private List<String> splitFormula(String formula, String regex) {
        String[] splitedString = formula.split(regex);
        return new ArrayList<>(Arrays.asList(splitedString));
    }

    private void showInvalidFormulaToast() {
        if (lastToast != null) {
            lastToast.cancel();
        }

        Context context = getApplicationContext();
        CharSequence message = getString(R.string.invalid_formula);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        lastToast = toast;
    }
}