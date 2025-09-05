package com.example.common.utils;

public class NumberValidator {
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("-?\\d+");
    }
}
