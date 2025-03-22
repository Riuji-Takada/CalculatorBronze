package com.example.calculatorbronze;

import androidx.annotation.NonNull;

public enum Operators {
    ADDITION("+", "+"),
    SUBTRACTION("-", "-"),
    MULTIPLICATION("*", "×"),
    DIVISION("/", "÷");

    private final String operator;
    private final String displaySymbol;

    Operators(String operator, String displaySymbol) {
        this.operator = operator;
        this.displaySymbol = displaySymbol;
    }

    public String getDisplaySymbol() {
        return displaySymbol;
    }

    @NonNull
    @Override
    public String toString() {
        return this.operator;
    }
}
