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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final String END_WITH_NUMBER_REGEX = "^.*[0-9]$";
    private final String CONTAINS_ONLY_NUMBERS_REGEX = "^[0-9]+$";

    private Toast lastToast;
    private TextView formulaText;

    private String additionOperator;
    private String subtractionOperator;
    private String multiplicationOperator;
    private String divisionOperator;

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

        initializeButtonListeners();

        additionOperator = getString(R.string.addition_operator);
        subtractionOperator = getString(R.string.subtraction_operator);
        multiplicationOperator = getString(R.string.multiplication_operator);
        divisionOperator = getString(R.string.division_operator);
    }

    public void initializeButtonListeners() {
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

            String formula = formulaText.getText().toString();

            if (formula.equals("0")) {
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

            if (currentFormula.isEmpty()) {
                showInvalidFormulaToast();
                return;
            }

            boolean endsWithNumber = currentFormula.matches(END_WITH_NUMBER_REGEX);

            if (endsWithNumber) {
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
            boolean hasOnlyNumbers = currentFormula.matches(CONTAINS_ONLY_NUMBERS_REGEX);

            if (currentFormula.isEmpty()
                    || hasOnlyNumbers
                    || !endsWithNumber) {
                showInvalidFormulaToast();
                return;
            }

            String result = evaluateFormula(currentFormula);

            formulaText.setText(result);
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

    private String evaluateFormula(String formula) {
        final int SIGNIFICANT_DIGITS = 15;
        final String NUMBER_REGEX = "[0-9]+";
        final String NON_NUMBER_REGEX = "[^0-9]";

        List<String> numbersAsString = splitFormula(formula, NON_NUMBER_REGEX);
        List<BigDecimal> numbers = numbersAsString.stream().map(s -> new BigDecimal(s.trim())).collect(Collectors.toList());
        List<String> operators = splitFormula(formula, NUMBER_REGEX);

        // 演算子リストの先頭には空要素が入るため、削除
        if (!operators.isEmpty() && operators.get(0).isEmpty()) {
            operators.remove(0);
        }

        BigDecimal minusOne = new BigDecimal("-1");

        // マイナスの演算子をプラスに変換する
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).equals(subtractionOperator)) {
                operators.set(i, additionOperator);

                BigDecimal number = numbers.get(i + 1);
                number = number.multiply(minusOne);
                numbers.set(i + 1, number);
            }
        }

        int i = 0;
        while (i < operators.size()) {
            String operator = operators.get(i);

            if (operator.equals(divisionOperator) || operator.equals(multiplicationOperator)) {
                BigDecimal result;

                if(operator.equals(divisionOperator)){
                    // ChatGPT曰く、一般的な電卓の有効桁数は10桁、制度の良いもので15桁とのこと
                    result = numbers.get(i).divide(numbers.get(i + 1), SIGNIFICANT_DIGITS, RoundingMode.HALF_UP);
                }else{
                    result = numbers.get(i).multiply(numbers.get(i + 1));
                }

                numbers.set(i, result);

                operators.remove(i);
                numbers.remove(i + 1);
            } else {
                i++;
            }
        }

        BigDecimal sum = new BigDecimal("0.0");
        for(BigDecimal number : numbers) {
            sum = sum.add(number);
        }

        // 小数点以下の0を削除
        sum = sum.stripTrailingZeros();

        return sum.toString();
    }
}