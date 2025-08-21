package com.example.common.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptSalt {

    public static String generateSalt() {

        return BCrypt.gensalt();
    }

    public static String hashPasswordWithSalt(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }

    public static Boolean verifyPassword(String password, String hashedPassword,String salt) {
        return hashedPassword.equals(hashPasswordWithSalt(password, salt));
    }

}
