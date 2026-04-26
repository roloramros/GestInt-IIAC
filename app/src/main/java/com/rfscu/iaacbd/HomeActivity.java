package com.rfscu.iaacbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.rfscu.iaacbd.utils.TokenManager;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private TextView tvUserName, tvWelcomeGreeting;
    private Button btnLogoutDrawer;
    private MaterialToolbar toolbar;
    private MaterialCardView cardListado, cardBusqueda, cardCalibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!TokenManager.isLoggedIn(this)) {
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupEdgeToEdge();
        initViews();
        setupToolbar();
        setupDrawer();
        setupDashboard();
        updateGreeting();
    }

    private void setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View mainContent = findViewById(R.id.main_content);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        tvUserName = findViewById(R.id.tvUserName);
        tvWelcomeGreeting = findViewById(R.id.tvWelcomeGreeting);
        btnLogoutDrawer = findViewById(R.id.btnLogoutDrawer);
        cardListado = findViewById(R.id.cardListado);
        cardBusqueda = findViewById(R.id.cardBusqueda);
        cardCalibration = findViewById(R.id.cardCalibration);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupDashboard() {
        cardListado.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        cardBusqueda.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdvancedSearchActivity.class);
            startActivity(intent);
        });

        cardCalibration.setOnClickListener(v -> {
            Toast.makeText(this, "Módulo de Calibración en desarrollo", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) greeting = "¡Buen día!";
        else if (hour >= 12 && hour < 19) greeting = "¡Buenas tardes!";
        else greeting = "¡Buenas noches!";
        
        tvWelcomeGreeting.setText(greeting);
    }

    private void setupDrawer() {
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Ya estamos aquí
            } else if (id == R.id.nav_list) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_advanced_search) {
                Intent intent = new Intent(this, AdvancedSearchActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_calibration) {
                Toast.makeText(this, "Módulo de Calibración en desarrollo", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_user_management) {
                Intent intent = new Intent(this, UserManagementActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        btnLogoutDrawer.setOnClickListener(v -> performLogout());
        loadDrawerUserInfo();
    }

    private void loadDrawerUserInfo() {
        String username = TokenManager.getUsername(this);
        String role = TokenManager.getRole(this);

        if (username != null && !username.isEmpty()) {
            String displayText = (role != null && !role.isEmpty())
                    ? username + " (" + role + ")"
                    : username;
            tvUserName.setText(displayText);
        } else {
            tvUserName.setText("Invitado");
        }

        if (navView != null) {
            android.view.MenuItem userMgmtItem = navView.getMenu().findItem(R.id.nav_user_management);
            if (userMgmtItem != null) {
                userMgmtItem.setVisible("admin".equalsIgnoreCase(role));
            }
        }
    }

    private void performLogout() {
        TokenManager.clearToken(this);
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
