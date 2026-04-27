package com.rfscu.iaacbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.rfscu.iaacbd.adapter.InstrumentoAdapter;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.Instrumento;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;
import com.rfscu.iaacbd.utils.TokenManager;

import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvancedSearchActivity extends ThemeBaseActivity {

    private CheckBox cbPlanta, cbInstrumento, cbTarjeta, cbDirIm, cbDirPa, cbVarMedida, cbComunicacion;
    private Spinner spPlanta, spInstrumento, spVarMedida, spComunicacion;
    private TextInputEditText etTarjeta, etDirIm, etDirPa;
    private RecyclerView rvResultados;
    private InstrumentoAdapter adapter;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Button btnLogoutDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_search);

        setupEdgeToEdge();
        initViews();
        setupRecyclerView();
        setupCheckboxLogic();
        setupDrawer();
        loadDistinctValues();

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                performSearch();
                return true;
            }
            return false;
        });
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
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
        cbPlanta = findViewById(R.id.cbPlanta);
        cbInstrumento = findViewById(R.id.cbInstrumento);
        cbTarjeta = findViewById(R.id.cbTarjeta);
        cbDirIm = findViewById(R.id.cbDirIm);
        cbDirPa = findViewById(R.id.cbDirPa);
        cbVarMedida = findViewById(R.id.cbVarMedida);
        cbComunicacion = findViewById(R.id.cbComunicacion);

        spPlanta = findViewById(R.id.spPlanta);
        spInstrumento = findViewById(R.id.spInstrumento);
        spVarMedida = findViewById(R.id.spVarMedida);
        spComunicacion = findViewById(R.id.spComunicacion);

        etTarjeta = findViewById(R.id.etTarjeta);
        etDirIm = findViewById(R.id.etDirIm);
        etDirPa = findViewById(R.id.etDirPa);

        rvResultados = findViewById(R.id.rvResultados);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnLogoutDrawer = findViewById(R.id.btnLogoutDrawer);

        // Forzar estado desactivado inicial
        disableAllInputs();
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
                // Ya estamos aquí
            } else if (id == R.id.nav_user_management) {
                Intent intent = new Intent(this, UserManagementActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);
    }

    private void disableAllInputs() {
        spPlanta.setEnabled(false); spPlanta.setAlpha(0.5f);
        spInstrumento.setEnabled(false); spInstrumento.setAlpha(0.5f);
        spVarMedida.setEnabled(false); spVarMedida.setAlpha(0.5f);
        spComunicacion.setEnabled(false); spComunicacion.setAlpha(0.5f);

        etTarjeta.setEnabled(false); etTarjeta.setAlpha(0.5f);
        etDirIm.setEnabled(false); etDirIm.setAlpha(0.5f);
        etDirPa.setEnabled(false); etDirPa.setAlpha(0.5f);
    }

    private void setupRecyclerView() {
        adapter = new InstrumentoAdapter();
        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        rvResultados.setAdapter(adapter);

        adapter.setOnInstrumentoClickListener(new InstrumentoAdapter.OnInstrumentoClickListener() {
            @Override
            public void onTagClick(Instrumento instrumento) {
                Intent intent = new Intent(AdvancedSearchActivity.this, InstrumentoDetailActivity.class);
                intent.putExtra("instrumento", instrumento);
                startActivity(intent);
            }

            @Override
            public void onTarjetaClick(Instrumento instrumento) {
                if (instrumento.getTarjeta() != null && !instrumento.getTarjeta().isEmpty()) {
                    // Si hacen click en una tarjeta en los resultados de búsqueda avanzada, 
                    // simplemente forzamos una nueva búsqueda con esa tarjeta.
                    cbTarjeta.setChecked(true);
                    etTarjeta.setText(instrumento.getTarjeta());
                    performSearch();
                }
            }
        });
    }

    private void setupCheckboxLogic() {
        cbPlanta.setOnCheckedChangeListener((b, isChecked) -> {
            spPlanta.setEnabled(isChecked);
            spPlanta.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        cbInstrumento.setOnCheckedChangeListener((b, isChecked) -> {
            spInstrumento.setEnabled(isChecked);
            spInstrumento.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        cbTarjeta.setOnCheckedChangeListener((b, isChecked) -> {
            etTarjeta.setEnabled(isChecked);
            etTarjeta.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        cbDirIm.setOnCheckedChangeListener((b, isChecked) -> {
            etDirIm.setEnabled(isChecked);
            etDirIm.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        cbDirPa.setOnCheckedChangeListener((b, isChecked) -> {
            etDirPa.setEnabled(isChecked);
            etDirPa.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        cbVarMedida.setOnCheckedChangeListener((b, isChecked) -> {
            spVarMedida.setEnabled(isChecked);
            spVarMedida.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        cbComunicacion.setOnCheckedChangeListener((b, isChecked) -> {
            spComunicacion.setEnabled(isChecked);
            spComunicacion.setAlpha(isChecked ? 1.0f : 0.5f);
        });
    }

    private void loadDistinctValues() {
        RetrofitClient.getApiService(this).getDistinctValues().enqueue(new Callback<Map<String, List<String>>>() {
            @Override
            public void onResponse(Call<Map<String, List<String>>> call, Response<Map<String, List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<String>> data = response.body();
                    populateSpinner(spPlanta, data.get("planta"));
                    populateSpinner(spInstrumento, data.get("instrumento"));
                    populateSpinner(spVarMedida, data.get("var_medida"));
                    populateSpinner(spComunicacion, data.get("comunicacion"));
                } else {
                    String errorMsg = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) { /* ignore */ }
                    Toast.makeText(AdvancedSearchActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<String>>> call, Throwable t) {
                Toast.makeText(AdvancedSearchActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinner(Spinner spinner, List<String> values) {
        if (values == null || values.isEmpty()) {
            values = new ArrayList<>();
            values.add("No hay datos");
        }
        
        // Usamos un layout más estándar para asegurar visibilidad
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void performSearch() {
        Map<String, String> queryParams = new HashMap<>();

        if (cbPlanta.isChecked() && spPlanta.getSelectedItem() != null) {
            queryParams.put("planta", spPlanta.getSelectedItem().toString());
        }
        if (cbInstrumento.isChecked() && spInstrumento.getSelectedItem() != null) {
            queryParams.put("instrumento", spInstrumento.getSelectedItem().toString());
        }
        if (cbTarjeta.isChecked() && etTarjeta.getText() != null && !etTarjeta.getText().toString().isEmpty()) {
            queryParams.put("tarjeta", etTarjeta.getText().toString());
        }
        if (cbDirIm.isChecked() && etDirIm.getText() != null && !etDirIm.getText().toString().isEmpty()) {
            queryParams.put("dir_im", etDirIm.getText().toString());
        }
        if (cbDirPa.isChecked() && etDirPa.getText() != null && !etDirPa.getText().toString().isEmpty()) {
            queryParams.put("dir_pa", etDirPa.getText().toString());
        }
        if (cbVarMedida.isChecked() && spVarMedida.getSelectedItem() != null) {
            queryParams.put("var_medida", spVarMedida.getSelectedItem().toString());
        }
        if (cbComunicacion.isChecked() && spComunicacion.getSelectedItem() != null) {
            queryParams.put("comunicacion", spComunicacion.getSelectedItem().toString());
        }

        showLoading(true);
        RetrofitClient.getApiService(this).searchInstrumentos(queryParams).enqueue(new Callback<List<Instrumento>>() {
            @Override
            public void onResponse(Call<List<Instrumento>> call, Response<List<Instrumento>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Instrumento> results = response.body();
                    adapter.setInstrumentos(results);
                    updateEmptyState(results.isEmpty(), "No se encontraron resultados");
                } else {
                    Toast.makeText(AdvancedSearchActivity.this, "Error en la búsqueda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Instrumento>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AdvancedSearchActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvResultados.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (loading) tvEmptyState.setVisibility(View.GONE);
    }

    private void updateEmptyState(boolean empty, String message) {
        tvEmptyState.setText(message);
        tvEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvResultados.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
