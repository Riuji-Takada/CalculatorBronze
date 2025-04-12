package com.example.calculatorbronze;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Formula {
//    private final String NUMBER_IN_PARENTHESES_AT_END = "\\(-\\d+(?:\\.\\d+)?\\)$";
//    private final String NUMBER_AT_END = "\\d+(?:\\.\\d+)?$";
    private final List<String> tokens;

    public Formula() {
        tokens = new ArrayList<>();
    }

    public void clearFormula() {
        tokens.clear();
    }

    private boolean isNumericToken(String token) {
        // 数値の正規表現（正数：〇 負数：〇 整数：〇 小数：〇）
        final String NUMERIC_TOKEN_REGEX = "^-?\\d+(?:\\.\\d+)?$";

        return token.matches(NUMERIC_TOKEN_REGEX);
    }

    private boolean isOperatorToken(String token) {
        // TODO 自動生成したいんだけどな...
        final String OPERATOR_TOKEN_REGEX = "[+\\-*/]";
        //    private final String OPERATOR_TOKEN_REGEX = String.format("[%s]",
//            Arrays.stream(Operators.values())
//                    .map(Operators::toString)
//                    .collect(Collectors.joining("")));
        return token.matches(OPERATOR_TOKEN_REGEX);
    }

    private boolean isPositiveNumber(String number) {
        // 正数の数値の正規表現（正数：〇 負数：✕ 整数：〇 小数：〇）
        final String POSITIVE_NUMBER_REGEX = "^\\d+(?:\\.\\d+)?$";

        return number.matches(POSITIVE_NUMBER_REGEX);
    }

    private boolean isNegativeNumber(String number) {
        // 負数の数値の正規表現（正数：✕ 負数：〇 整数：〇 小数：〇）
        final String NEGATIVE_NUMBER_REGEX = "^-\\d+(?:\\.\\d+)?$";

        return number.matches(NEGATIVE_NUMBER_REGEX);
    }

    private boolean isPercentageToken(String token) {
        final String PERCENTAGE_REGEX = "^-?\\d+(?:\\.\\d+)?%$";

        return token.matches(PERCENTAGE_REGEX);
    }

    public boolean addToken(String token) {
        if (token.isBlank()) {
            return false;
        }

        if (isNumericToken(token)) {
            return addNumericToken(token);
        }

        if (isOperatorToken(token)) {
            return addOperatorToken(token);
        }

        return false;
    }

    public void removeLastToken() {
        if (!tokens.isEmpty()) {
            tokens.remove(tokens.size() - 1);
        }
    }

    private boolean addNumericToken(String token) {
        if (tokens.isEmpty()) {
            tokens.add(token);
            return true;
        }

        int lastTokenIndex = tokens.size() - 1;
        String previousToken = tokens.get(lastTokenIndex);

        if (isOperatorToken(previousToken)) {
            tokens.add(token);
            return true;
        }

        if (isNumericToken(previousToken)) {
            String newToken = previousToken + token;

            if (previousToken.equals("0")) {
                newToken = token;
            }

            tokens.set(lastTokenIndex, newToken);
            return true;
        }

        return false;
    }

    private boolean addOperatorToken(String token) {

        if (tokens.isEmpty()) {
            return false;
        }

        int lastTokenIndex = tokens.size() - 1;
        String previousToken = tokens.get(lastTokenIndex);

        if (isNumericToken(previousToken) || isPercentageToken(previousToken)) {
            tokens.add(token);
            return true;
        }

        if (isOperatorToken(previousToken)) {
            tokens.set(lastTokenIndex, token);
            return true;
        }

        return false;
    }

    public boolean changeLastTokensSignal() {
        if (tokens.isEmpty()) {
            return false;
        }

        int lastTokenIndex = tokens.size() - 1;
        String lastToken = tokens.get(lastTokenIndex);

        if (isNumericToken(lastToken)) {
            String newToken;
            if (isPositiveNumber(lastToken)) {
                newToken = Operators.SUBTRACTION + lastToken;
            } else {
                newToken = lastToken.substring(1);
            }
            tokens.set(lastTokenIndex, newToken);
            return true;
        }

        return false;
    }

    public boolean changeLastTokenToPercentage() {
        if (tokens.isEmpty()) {
            return false;
        }

        int lastTokenIndex = tokens.size() - 1;
        String lastToken = tokens.get(lastTokenIndex);

        if (isNumericToken(lastToken)) {
            // TODO 定数化か何かをしたい
            String newToken = lastToken + "%";
            tokens.set(lastTokenIndex, newToken);
            return true;
        }

        if (isPercentageToken(lastToken)) {
            String newToken = lastToken.substring(0, lastToken.length() - 1);
            tokens.set(lastTokenIndex, newToken);
            return true;
        }

        return false;
    }

    public boolean addDecimalPoint() {
        return false;
    }

    public boolean evaluateFormula() {
        final int SIGNIFICANT_DIGITS = 15;

        if (tokens.isEmpty()) {
            return false;
        }

        List<String> tempTokens = new ArrayList<>(tokens);

        int lastTokenIndex = tempTokens.size() - 1;
        String lastToken = tempTokens.get(lastTokenIndex);

        if(isOperatorToken(lastToken)) {
            return false;
        }

        for (int i = 0; i < tempTokens.size(); i++) {
            String token = tempTokens.get(i);
            if (isPercentageToken(token)) {
                token = token.replace("%","");
                BigDecimal percentageAsDouble = new BigDecimal(token).divide(new BigDecimal("100.0"), SIGNIFICANT_DIGITS, RoundingMode.HALF_UP);

                if(i > 0){
                    String previousToken = tempTokens.get(i - 1);

                    if(previousToken.equals(Operators.SUBTRACTION.toString()) ||
                            previousToken.equals(Operators.ADDITION.toString())) {
                        tempTokens.set(i - 1, Operators.MULTIPLICATION.toString());


                        if(previousToken.equals(Operators.SUBTRACTION.toString())){
                            percentageAsDouble = new BigDecimal("1").subtract(percentageAsDouble);
                        } else {
                            percentageAsDouble = new BigDecimal("1").add(percentageAsDouble);
                        }
                    }
                }

                tempTokens.set(i, percentageAsDouble.toString());
            }
        }

        List<BigDecimal> numbers = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        for(String token : tempTokens) {
            if(isOperatorToken(token)) {
                operators.add(token);
            } else {
                numbers.add(new BigDecimal(token));
            }
        }

        BigDecimal minusOne = new BigDecimal("-1");

        // マイナスの演算子をプラスに変換する
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).equals(Operators.SUBTRACTION.toString())) {
                operators.set(i, Operators.ADDITION.toString());

                BigDecimal number = numbers.get(i + 1);
                number = number.multiply(minusOne);
                numbers.set(i + 1, number);
            }
        }

        int i = 0;
        while (i < operators.size()) {
            String operator = operators.get(i);

            if (operator.equals(Operators.DIVISION.toString()) || operator.equals(Operators.MULTIPLICATION.toString())) {
                BigDecimal result;

                if(operator.equals(Operators.DIVISION.toString())){
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

        tokens.clear();
        tokens.add(sum.toPlainString());

        return true;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder formula = new StringBuilder();

        for (String token : tokens) {
            if (isNegativeNumber(token)) {
                token = String.format(Locale.getDefault(), "(%s)", token);
//                token = String.format(Locale.getDefault(), "(%,f)", Double.parseDouble(token));
            } else if (isOperatorToken(token)) {
                for (Operators operator : Operators.values()) {
                    if (token.equals(operator.toString())) {
                        token = operator.getDisplaySymbol();
                    }
                }
            }

            formula.append(token);
        }

        return formula.toString();
    }
}
