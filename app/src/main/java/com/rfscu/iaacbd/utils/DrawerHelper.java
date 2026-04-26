package com.rfscu.iaacbd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.rfscu.iaacbd.Login;
import com.rfscu.iaacbd.R;

public class DrawerHelper {

    public static void setupDrawer(Activity activity, NavigationView navView, Button btnLogoutDrawer) {
        if (navView == null) return;

        // Logout
        if (btnLogoutDrawer != null) {
            btnLogoutDrawer.setOnClickListener(v -> performLogout(activity));
        }

        // Load User Info (Header and Footer)
        loadDrawerUserInfo(activity, navView);

        // Setup Dark Mode Toggle
        setupDarkModeToggle(activity, navView);
    }

    private static void loadDrawerUserInfo(Activity activity, NavigationView navView) {
        String username = TokenManager.getUsername(activity);
        String role = TokenManager.getRole(activity);
        
        String displayText = "Invitado";
        if (username != null && !username.isEmpty()) {
            displayText = (role != null && !role.isEmpty())
                    ? username + " (" + role + ")"
                    : username;
        }

        // 1. Update Header (if exists)
        View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            TextView tvUserNameHeader = headerView.findViewById(R.id.tvUserName);
            if (tvUserNameHeader != null) {
                tvUserNameHeader.setText(displayText);
            }
        }

        // 2. Update Footer (looking in the activity directly since it's an included layout)
        TextView tvUserNameFooter = activity.findViewById(R.id.tvUserName);
        // Note: We check if it's NOT the one from the header by checking its parent or context if necessary, 
        // but activity.findViewById will find the one in the main layout (the footer).
        if (tvUserNameFooter != null) {
            tvUserNameFooter.setText(displayText);
        }

        // 3. Update Menu Visibility
        MenuItem userMgmtItem = navView.getMenu().findItem(R.id.nav_user_management);
        if (userMgmtItem != null) {
            userMgmtItem.setVisible("admin".equalsIgnoreCase(role));
        }
    }

    private static void setupDarkModeToggle(Activity activity, NavigationView navView) {
        View headerView = navView.getHeaderView(0);
        if (headerView == null) return;

        ImageView ivDarkModeToggle = headerView.findViewById(R.id.ivDarkModeToggle);
        if (ivDarkModeToggle == null) return;

        SharedPreferences prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        updateThemeIcon(ivDarkModeToggle, isDarkMode);

        ivDarkModeToggle.setOnClickListener(v -> {
            boolean newDarkModeState = !prefs.getBoolean("dark_mode", false);
            prefs.edit().putBoolean("dark_mode", newDarkModeState).apply();
            updateThemeIcon(ivDarkModeToggle, newDarkModeState);
        });
    }

    private static void updateThemeIcon(ImageView ivDarkModeToggle, boolean isDarkMode) {
        if (isDarkMode) {
            ivDarkModeToggle.setImageResource(R.drawable.ic_moon);
        } else {
            ivDarkModeToggle.setImageResource(R.drawable.ic_sun);
        }
    }

    private static void performLogout(Activity activity) {
        TokenManager.clearToken(activity);
        Intent intent = new Intent(activity, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
