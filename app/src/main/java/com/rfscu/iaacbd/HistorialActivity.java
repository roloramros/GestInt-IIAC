package com.rfscu.iaacbd;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.rfscu.iaacbd.adapter.HistorialAdapter;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.HistorialAcceso;
import com.rfscu.iaacbd.model.User;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;
import com.rfscu.iaacbd.utils.TokenManager;

import java.util.ArrayList;
import java.util.Calendar;
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

    // Filter views
    private MaterialCardView filterContainer;
    private Spinner spinnerUser;
    private Button btnDatePicker, btnApplyFilters, btnClearFilters;
    private String selectedDate = null;

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
        loadHistorial(null, null);
        loadUsersForFilter();
        setupFilterListeners();
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

        filterContainer = findViewById(R.id.filter_container);
        spinnerUser = findViewById(R.id.spinnerUser);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.historial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (filterContainer.getVisibility() == View.VISIBLE) {
                filterContainer.setVisibility(View.GONE);
            } else {
                filterContainer.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFilterListeners() {
        btnDatePicker.setOnClickListener(v -> showDatePicker());

        btnApplyFilters.setOnClickListener(v -> {
            String username = spinnerUser.getSelectedItem().toString();
            if (username.equals(getString(R.string.all_users))) {
                username = null;
            }
            loadHistorial(username, selectedDate);
            filterContainer.setVisibility(View.GONE);
        });

        btnClearFilters.setOnClickListener(v -> {
            spinnerUser.setSelection(0);
            selectedDate = null;
            btnDatePicker.setText(R.string.select_date);
            loadHistorial(null, null);
            filterContainer.setVisibility(View.GONE);
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    btnDatePicker.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadUsersForFilter() {
        RetrofitClient.getApiService(this).getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> userList = new ArrayList<>();
                    userList.add(getString(R.string.all_users));
                    for (User user : response.body()) {
                        userList.add(user.getUsername());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HistorialActivity.this,
                            android.R.layout.simple_spinner_item, userList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUser.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Silently fail or show error
            }
        });
    }

    private void setupDrawer() {
        DrawerHelper.setupNavigationListener(this, navView, drawerLayout);
        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);
    }


    private void setupRecyclerView() {
        adapter = new HistorialAdapter();
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        rvHistorial.setAdapter(adapter);
    }

    private void loadHistorial(String username, String date) {
        progressBar.setVisibility(View.VISIBLE);
        
        Call<List<HistorialAcceso>> call;
        if (username == null && date == null) {
            call = RetrofitClient.getApiService(this).getHistorial();
        } else {
            call = RetrofitClient.getApiService(this).getFilteredHistorial(username, date);
        }

        call.enqueue(new Callback<List<HistorialAcceso>>() {
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