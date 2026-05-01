package com.rfscu.iaacbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.rfscu.iaacbd.adapter.InstrumentoAdapter;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.Instrumento;
import com.rfscu.iaacbd.model.InstrumentoCreateRequest;
import com.rfscu.iaacbd.model.InstrumentoUpdateRequest;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.InstrumentoFormDialog;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;
import com.rfscu.iaacbd.utils.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ThemeBaseActivity implements InstrumentoFormDialog.FormCallback {

    private static final int REQUEST_DETAIL = 101;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Button btnLogoutDrawer;

    private RecyclerView rvInstrumentos;
    private InstrumentoAdapter instrumentoAdapter;
    private FloatingActionButton fabAddInstrumento;
    private ProgressBar progressBar;

    // Búsqueda
    private ViewGroup layoutTopBar;
    private LinearLayout layoutSearch;
    private EditText etSearch;
    private ImageButton btnSearchToggle, btnSearchClose;
    private boolean isSearchVisible = false;
    private List<Instrumento> fullInstrumentoList = new ArrayList<>();

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupEdgeToEdge();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadInstrumentos();
    }

    private void setupEdgeToEdge() {
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View mainContent = findViewById(R.id.main);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return androidx.core.view.WindowInsetsCompat.CONSUMED;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnLogoutDrawer = findViewById(R.id.btnLogoutDrawer);

        rvInstrumentos = findViewById(R.id.rvInstrumentos);
        fabAddInstrumento = findViewById(R.id.fabAddInstrumento);
        progressBar = findViewById(R.id.progressBar);

        layoutTopBar = findViewById(R.id.layoutTopBar);
        layoutSearch = findViewById(R.id.layoutSearch);
        etSearch = findViewById(R.id.etSearch);
        btnSearchToggle = findViewById(R.id.btnSearchToggle);
        btnSearchClose = findViewById(R.id.btnSearchClose);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupRecyclerView() {
        instrumentoAdapter = new InstrumentoAdapter();
        rvInstrumentos.setLayoutManager(new LinearLayoutManager(this));
        rvInstrumentos.setAdapter(instrumentoAdapter);

        instrumentoAdapter.setOnInstrumentoClickListener(new InstrumentoAdapter.OnInstrumentoClickListener() {
            @Override
            public void onTagClick(Instrumento instrumento) {
                Intent intent = new Intent(MainActivity.this, InstrumentoDetailActivity.class);
                intent.putExtra("instrumento", instrumento);
                startActivityForResult(intent, REQUEST_DETAIL);
            }

            @Override
            public void onTarjetaClick(Instrumento instrumento) {
                if (instrumento.getTarjeta() != null && !instrumento.getTarjeta().isEmpty()) {
                    filterByTarjeta(instrumento.getTarjeta());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_OK) {
            loadInstrumentos(); // Recargar si hubo cambios
        }
    }

    private void setupListeners() {
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_list) {
                // Ya estamos aquí
            } else if (id == R.id.nav_advanced_search) {
                Intent intent = new Intent(MainActivity.this, AdvancedSearchActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_calibration_monthly) {
                Intent intent = new Intent(this, MonthlyPlansActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_calibration_update) {
                Intent intent = new Intent(this, UpdateCertsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_calibration_history) {
                Intent intent = new Intent(this, CertsHistoryActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_user_management) {
                Intent intent = new Intent(MainActivity.this, UserManagementActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_historial) {
                Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);

        fabAddInstrumento.setOnClickListener(v -> {
            InstrumentoFormDialog dialog = new InstrumentoFormDialog(this, this);
            dialog.show();
        });

        btnSearchToggle.setOnClickListener(v -> toggleSearch());

        btnSearchClose.setOnClickListener(v -> closeSearch());
    }

    private void closeSearch() {
        etSearch.setText("");
        filterInstruments("");
        TransitionManager.beginDelayedTransition(toolbar);
        isSearchVisible = false;
        layoutSearch.setVisibility(View.GONE);
        btnSearchClose.setVisibility(View.GONE);
        btnSearchToggle.setImageResource(android.R.drawable.ic_menu_search);
        showKeyboard(false);
        etSearch.clearFocus();
    }

    private void filterByTarjeta(String tarjeta) {
        showProgress(true);
        Map<String, String> filters = new HashMap<>();
        filters.put("tarjeta", tarjeta);

        RetrofitClient.getApiService(this).searchInstrumentos(filters).enqueue(new Callback<List<Instrumento>>() {
            @Override
            public void onResponse(Call<List<Instrumento>> call, Response<List<Instrumento>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    instrumentoAdapter.setInstrumentos(response.body());
                    Toast.makeText(MainActivity.this, "Filtrado por tarjeta: " + tarjeta, Toast.LENGTH_SHORT).show();
                    // Al filtrar por tarjeta, mostramos el botón de cerrar si no está visible
                    if (!isSearchVisible) {
                        TransitionManager.beginDelayedTransition(toolbar);
                        btnSearchClose.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al filtrar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Instrumento>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleSearch() {
        if (!isSearchVisible) {
            // Animación al abrir
            TransitionManager.beginDelayedTransition(toolbar);
            isSearchVisible = true;
            layoutSearch.setVisibility(View.VISIBLE);
            btnSearchClose.setVisibility(View.VISIBLE);
            btnSearchToggle.setImageResource(android.R.drawable.ic_menu_send);
            etSearch.requestFocus();
            showKeyboard(true);
        } else {
            String query = etSearch.getText().toString().trim();
            filterInstruments(query);
            
            if (query.isEmpty()) {
                // Si está vacío, ocultamos la barra y volvemos a la lupa con animación
                TransitionManager.beginDelayedTransition(toolbar);
                isSearchVisible = false;
                layoutSearch.setVisibility(View.GONE);
                btnSearchClose.setVisibility(View.GONE);
                btnSearchToggle.setImageResource(android.R.drawable.ic_menu_search);
                showKeyboard(false);
            } else {
                // Si hay texto, simplemente cerramos el teclado pero dejamos la barra abierta
                showKeyboard(false);
                etSearch.clearFocus();
            }
        }
    }

    private void filterInstruments(String query) {
        List<Instrumento> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(fullInstrumentoList);
        } else {
            for (Instrumento item : fullInstrumentoList) {
                if (item.getTag() != null && item.getTag().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        instrumentoAdapter.setInstrumentos(filteredList);
    }

    private void showKeyboard(boolean show) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    private void loadInstrumentos() {
        showProgress(true);
        RetrofitClient.getApiService(this).getInstrumentos().enqueue(new Callback<List<Instrumento>>() {
            @Override
            public void onResponse(Call<List<Instrumento>> call, Response<List<Instrumento>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    fullInstrumentoList = response.body();
                    // Al recargar, respetamos el filtro si existe
                    String query = etSearch.getText().toString().trim();
                    filterInstruments(query);
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_loading_instruments, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Instrumento>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateInstrumento(InstrumentoCreateRequest data) {
        showProgress(true);
        RetrofitClient.getApiService(this).createInstrumento(data).enqueue(new Callback<Instrumento>() {
            @Override
            public void onResponse(Call<Instrumento> call, Response<Instrumento> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, R.string.instrument_created, Toast.LENGTH_SHORT).show();
                    loadInstrumentos();
                } else {
                    String errorMsg = getString(R.string.error_creating_instrument);
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) { /* ignore */ }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Instrumento> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUpdateInstrumento(int id, InstrumentoUpdateRequest data) {
        // Implementado en InstrumentoDetailActivity
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvInstrumentos != null) {
            rvInstrumentos.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
