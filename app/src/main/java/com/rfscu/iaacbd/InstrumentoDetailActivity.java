package com.rfscu.iaacbd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.Instrumento;
import com.rfscu.iaacbd.model.InstrumentoCreateRequest;
import com.rfscu.iaacbd.model.InstrumentoUpdateRequest;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.InstrumentoFormDialog;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;
import com.rfscu.iaacbd.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstrumentoDetailActivity extends ThemeBaseActivity implements InstrumentoFormDialog.FormCallback {

    private Instrumento instrumento;
    private TextView tvLastUpdate;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Button btnLogoutDrawer;

    // Floating Action Buttons
    private FloatingActionButton fabBack, fabEdit, fabDelete;
    private boolean isFabMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instrumento_detail);

        instrumento = (Instrumento) getIntent().getSerializableExtra("instrumento");
        if (instrumento == null) {
            finish();
            return;
        }

        initViews();
        setupDrawer();
        displayData();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnLogoutDrawer = findViewById(R.id.btnLogoutDrawer);

        // --- FAB CODE START ---
        fabBack = findViewById(R.id.fabBack);
        fabEdit = findViewById(R.id.fabEdit);
        fabDelete = findViewById(R.id.fabDelete);

        // Click normal: Atrás
        fabBack.setOnClickListener(v -> finish());

        // Pulsación larga: Mostrar/Ocultar menú (Versión Simple)
        fabBack.setOnLongClickListener(v -> {
            String userRole = TokenManager.getRole(this);
            if ("admin".equalsIgnoreCase(userRole) || "propietario".equalsIgnoreCase(userRole)) {
                toggleFabMenu();
            } else {
                Toast.makeText(this, "Solo administradores pueden editar o eliminar", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Acciones de los sub-botones FAB
        fabEdit.setOnClickListener(v -> {
            InstrumentoFormDialog dialog = new InstrumentoFormDialog(this, instrumento, this);
            dialog.show();
            if (isFabMenuOpen) toggleFabMenu(); 
        });

        fabDelete.setOnClickListener(v -> {
            showDeleteConfirmation();
            if (isFabMenuOpen) toggleFabMenu();
        });
        // --- FAB CODE END ---
    }

    private void toggleFabMenu() {
        isFabMenuOpen = !isFabMenuOpen;
        if (isFabMenuOpen) {
            fabEdit.setVisibility(View.VISIBLE);
            fabDelete.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Instrumento")
                .setMessage("¿Estás seguro de que deseas eliminar el instrumento con TAG: " + instrumento.getTag() + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteInstrumento())
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteInstrumento() {
        RetrofitClient.getApiService(this).deleteInstrumento(instrumento.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InstrumentoDetailActivity.this, "Instrumento eliminado correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Notificar a MainActivity para recargar la lista
                    finish();
                } else {
                    Toast.makeText(InstrumentoDetailActivity.this, "Error al eliminar el instrumento", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(InstrumentoDetailActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
                Intent intent = new Intent(this, UserManagementActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_historial) {
                Intent intent = new Intent(this, HistorialActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);
    }

    private void displayData() {
        setRowData(R.id.rowTag, "TAG", instrumento.getTag());
        setRowData(R.id.rowPlanta, "Planta", instrumento.getPlanta());
        setRowData(R.id.rowInstrumento, "Instrumento", instrumento.getInstrumento());
        setRowData(R.id.rowTarjeta, "Tarjeta", instrumento.getTarjeta());
        setRowData(R.id.rowNoSerie, "N° Serie", instrumento.getNoSerie());
        setRowData(R.id.rowRango, "Rango", instrumento.getRango());

        setRowData(R.id.rowDirIm, "Dir IM", instrumento.getDirIm());
        setRowData(R.id.rowDirPa, "Dir PA", instrumento.getDirPa());
        setRowData(R.id.rowVarMedida, "Var. Medida", instrumento.getVarMedida());
        setRowData(R.id.rowComunicacion, "Comunicación", instrumento.getComunicacion());
        setRowData(R.id.rowSeguridad, "Seguridad", instrumento.getSeguridad());
        setRowData(R.id.rowDescripcion, "Descripción", instrumento.getDescripcion());

        setSmallRowData(R.id.rowLowWarning, "Low Warning", instrumento.getLowWarning());
        setSmallRowData(R.id.rowHighWarning, "High Warning", instrumento.getHighWarning());
        setSmallRowData(R.id.rowLowAlarm, "Low Alarm", instrumento.getLowAlarm());
        setSmallRowData(R.id.rowHighAlarm, "High Alarm", instrumento.getHighAlarm());

        setSmallRowData(R.id.rowStartWr, "Start WR", instrumento.getStartWr());
        setSmallRowData(R.id.rowEndWr, "End WR", instrumento.getEndWr());
        setSmallRowData(R.id.rowStartMr, "Start MR", instrumento.getStartMr());
        setSmallRowData(R.id.rowEndMr, "End MR", instrumento.getEndMr());

        tvLastUpdate.setText("Actualizado por: " + (instrumento.getUserUpdate() != null ? instrumento.getUserUpdate() : "-"));
    }

    private void setRowData(int layoutId, String label, String value) {
        View view = findViewById(layoutId);
        ((TextView) view.findViewById(R.id.tvLabel)).setText(label);
        ((TextView) view.findViewById(R.id.tvValue)).setText(value != null && !value.isEmpty() ? value : "-");
    }

    private void setSmallRowData(int layoutId, String label, Integer value) {
        View view = findViewById(layoutId);
        ((TextView) view.findViewById(R.id.tvLabel)).setText(label);
        ((TextView) view.findViewById(R.id.tvValue)).setText(value != null ? String.valueOf(value) : "-");
    }

    @Override
    public void onCreateInstrumento(InstrumentoCreateRequest data) {
        // No se usa aquí
    }

    @Override
    public void onUpdateInstrumento(int id, InstrumentoUpdateRequest data) {
        RetrofitClient.getApiService(this).updateInstrumento(id, data).enqueue(new Callback<Instrumento>() {
            @Override
            public void onResponse(Call<Instrumento> call, Response<Instrumento> response) {
                if (response.isSuccessful() && response.body() != null) {
                    instrumento = response.body();
                    displayData();
                    Toast.makeText(InstrumentoDetailActivity.this, R.string.instrument_updated, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Para que MainActivity sepa que debe recargar
                } else {
                    Toast.makeText(InstrumentoDetailActivity.this, R.string.error_updating_instrument, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Instrumento> call, Throwable t) {
                Toast.makeText(InstrumentoDetailActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
