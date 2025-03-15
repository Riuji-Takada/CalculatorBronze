package com.example.calculatorbronze;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Formula {
    // 数値の正規表現（正数：〇 負数：〇 整数：〇 小数：〇）
    private final String NUMERIC_TOKEN_REGEX = "^-?\\d+(?:\\.\\d+)?$";
    private final String POSITIVE_NUMBER_REGEX = "^\\d+(?:\\.\\d+)?$";
    // 負数の数値の正規表現（正数：✕ 負数：〇 整数：〇 小数：〇）
    private final String NEGATIVE_NUMBER_REGEX = "^-\\d+(?:\\.\\d+)?$";
    private final String OPERATOR_TOKEN_REGEX = String.format("[%s]",
            Arrays.stream(Operators.values())
                    .map(Operators::toString)
                    .collect(Collectors.joining("")));
    // 正数の数値の正規表現（正数：〇 負数：✕ 整数：〇 小数：〇）

    private final String NUMBER_IN_PARENTHESES_AT_END = "\\(-\\d+(?:\\.\\d+)?\\)$";

    private final String NUMBER_AT_END = "\\d+(?:\\.\\d+)?$";

    private List<String> tokens;

    public Formula() {
        tokens = new ArrayList<>();
    }

    public void clearFormula() {
        tokens.clear();
    }

    private boolean isNumericToken(String token) {
        return token.matches(NUMERIC_TOKEN_REGEX);
    }

    private boolean isOperatorToken(String token) {
        return token.matches(OPERATOR_TOKEN_REGEX);
    }

    private boolean isPositiveNumber(String number) {
        return number.matches(POSITIVE_NUMBER_REGEX);
    }

    private boolean isNegativeNumber(String number) {
        return number.matches(NEGATIVE_NUMBER_REGEX);
    }


    public boolean addToken(String token) {
        if(token.isBlank()) {
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

    private boolean addNumericToken(String token) throws IllegalArgumentException {
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

        if(isNumericToken(previousToken)){
            tokens.add(token);
            return true;
        }

        if(isOperatorToken(previousToken)){
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

        if(isNumericToken(lastToken)) {
            String newToken = "";
            if(isPositiveNumber(lastToken)) {
                newToken = Operators.SUBTRACTION.toString() + lastToken;
            } else {
                newToken = lastToken.substring(1);
            }
            tokens.set(lastTokenIndex, newToken);
            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public String toString(){
        StringBuilder formula = new StringBuilder();

        for(String token: tokens) {
            if(isNegativeNumber(token)) {
                token = String.format("(%s)", token);
            } else if (isOperatorToken(token)) {
                for(Operators operator : Operators.values()) {
                    if(token.equals(operator.toString())){
                        token = operator.getDisplaySymbol();
                    }
                }
            }

            formula.append(token);
        }

        return formula.toString();
    }
}
