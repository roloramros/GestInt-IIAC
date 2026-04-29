package com.rfscu.iaacbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.rfscu.iaacbd.adapter.HistorialAdapter;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.HistorialAcceso;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;
import com.rfscu.iaacbd.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialActivity extends ThemeBaseActivity {

    private RecyclerView rvHistorial;
    private HistorialAdapter adapter;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Button btnLogoutDrawer;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Verificar admin o propietario antes de mostrar nada
        String role = TokenManager.getRole(this);
        if (!"admin".equals(role) && !"propietario".equals(role)) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        setupEdgeToEdge();
        initViews();
        setupToolbar();
        setupDrawer();
        setupRecyclerView();
        loadHistorial();
    }

    private void setupEdgeToEdge() {
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View mainContent = findViewById(R.id.main_content);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return androidx.core.view.WindowInsetsCompat.CONSUMED;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvHistorial = findViewById(R.id.rvHistorial);
        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnLogoutDrawer = findViewById(R.id.btnLogoutDrawer);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupDrawer() {
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_list) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_advanced_search) {
                Intent intent = new Intent(this, AdvancedSearchActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_user_management) {
                Intent intent = new Intent(this, UserManagementActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_historial) {
                // Ya estamos aquí
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);
    }

    private void setupRecyclerView() {
        adapter = new HistorialAdapter();
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        rvHistorial.setAdapter(adapter);
    }

    private void loadHistorial() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService(this).getHistorial().enqueue(new Callback<List<HistorialAcceso>>() {
            @Override
            public void onResponse(Call<List<HistorialAcceso>> call, Response<List<HistorialAcceso>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setHistorial(response.body());
                } else {
                    Toast.makeText(HistorialActivity.this, "Error al cargar historial", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HistorialAcceso>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HistorialActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}