package com.rfscu.iaacbd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String PREF_NAME = "secure_app_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    // 🔹 Nuevas claves para datos del usuario
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";

    private static SharedPreferences getSecurePreferences(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            return EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Fallback a SharedPreferences normales si falla el cifrado (no recomendado pero evita crash total)
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    // ─────────────────────────────────────────────────────
    // TOKEN
    // ─────────────────────────────────────────────────────

    public static void saveToken(Context context, String token) {
        getSecurePreferences(context)
                .edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public static String getToken(Context context) {
        return getSecurePreferences(context)
                .getString(KEY_TOKEN, null);
    }

    // ─────────────────────────────────────────────────────
    // DATOS DEL USUARIO
    // ─────────────────────────────────────────────────────

    public static void saveUserInfo(Context context, String username, String userId, String role) {
        SharedPreferences.Editor editor = getSecurePreferences(context).edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public static String getUsername(Context context) {
        return getSecurePreferences(context)
                .getString(KEY_USERNAME, null);
    }

    public static String getUserId(Context context) {
        return getSecurePreferences(context)
                .getString(KEY_USER_ID, null);
    }

    public static String getRole(Context context) {
        return getSecurePreferences(context)
                .getString(KEY_ROLE, null);
    }

    // ─────────────────────────────────────────────────────
    // LIMPIEZA / LOGOUT
    // ─────────────────────────────────────────────────────

    public static void clearToken(Context context) {
        getSecurePreferences(context)
                .edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USERNAME)
                .remove(KEY_USER_ID)
                .remove(KEY_ROLE)
                .apply();
    }

    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return token != null && !token.isEmpty();
    }
}