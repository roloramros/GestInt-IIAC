package com.rfscu.iaacbd;

import android.content.Intent;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rfscu.iaacbd.adapter.UserAdapter;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.User;
import com.rfscu.iaacbd.model.UserRequest;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;
import com.rfscu.iaacbd.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends ThemeBaseActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView rvUsers;
    private MaterialButton btnAddUser;
    private UserAdapter userAdapter;
    private View progressBar;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Button btnLogoutDrawer;

    private Dialog userDialog;
    private TextInputEditText etUsername, etPassword;
    private AutoCompleteTextView etRole;
    private MaterialButton btnSave, btnCancel;
    
    private String editingUserId = null;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);

        setupEdgeToEdge();
        initViews();
        setupToolbar();
        setupDrawer();
        setupRecyclerView();
        setupListeners();
        loadUsers();
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
        rvUsers = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);
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
                // Ya estamos aquí
            } else if (id == R.id.nav_historial) {
                Intent intent = new Intent(this, HistorialActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void setupListeners() {
        String role = TokenManager.getRole(this);
        if ("propietario".equalsIgnoreCase(role)) {
            btnAddUser.setVisibility(View.VISIBLE);
            btnAddUser.setOnClickListener(v -> showUserDialog(null));
        } else {
            btnAddUser.setVisibility(View.GONE);
        }
    }

    private void loadUsers() {
        showProgress(true);
        RetrofitClient.getApiService(UserManagementActivity.this).getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    userAdapter.setUsers(response.body());
                } else {
                    Toast.makeText(UserManagementActivity.this, 
                        "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(UserManagementActivity.this, 
                    "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(User user) {
        showUserDialog(user);
    }

    @Override
    public void onDeleteClick(User user) {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar al usuario " + user.getUsername() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteUser(user))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void showUserDialog(User user) {
        editingUserId = user != null ? user.getId() : null;
        String currentUserRole = TokenManager.getRole(this);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_user_form, null);
        
        etUsername = dialogView.findViewById(R.id.etUsername);
        etPassword = dialogView.findViewById(R.id.etPassword);
        etRole = dialogView.findViewById(R.id.etRole);
        btnSave = dialogView.findViewById(R.id.btnSave);
        btnCancel = dialogView.findViewById(R.id.btnCancel);
        
        TextInputLayout tilPassword = dialogView.findViewById(R.id.tilPassword);
        TextInputLayout tilRole = dialogView.findViewById(R.id.tilRole);
        
        // Configurar dropdown de roles
        String[] roles = {"admin", "usuario", "propietario"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, roles);
        etRole.setAdapter(roleAdapter);
        
        if (user != null) {
            // Modo edición
            etUsername.setText(user.getUsername());
            etUsername.setEnabled(false); // Bloquear nombre de usuario
            
            etRole.setText(user.getRole() != null ? user.getRole() : "", false);
            // Solo propietario puede cambiar el rol
            boolean isPropietario = "propietario".equalsIgnoreCase(currentUserRole);
            etRole.setEnabled(isPropietario);
            if (tilRole != null) {
                tilRole.setEnabled(isPropietario);
            }

            tilPassword.setHint("Nueva contraseña (dejar vacío si no cambia)");
        } else {
            // Modo creación (Solo accesible para propietario por el botón, pero por seguridad...)
            etUsername.setEnabled(true);
            etRole.setEnabled(true);
            tilPassword.setHint("Contraseña");
        }
        
        btnCancel.setOnClickListener(v -> userDialog.dismiss());
        btnSave.setOnClickListener(v -> saveUser());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        userDialog = builder.create();
        userDialog.show();
    }

    private void saveUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        
        if (username.isEmpty()) {
            Toast.makeText(this, "El nombre de usuario es requerido", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (editingUserId == null && password.isEmpty()) {
            Toast.makeText(this, "La contraseña es requerida para nuevos usuarios", Toast.LENGTH_SHORT).show();
            return;
        }
        
        UserRequest request = new UserRequest(username, password, role);
        
        if (editingUserId != null) {
            updateUser(editingUserId, request);
        } else {
            createUser(request);
        }
    }

    private void createUser(UserRequest request) {
        RetrofitClient.getApiService(UserManagementActivity.this).createUser(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (userDialog != null && userDialog.isShowing()) userDialog.dismiss();
                
                if (response.isSuccessful()) {
                    Toast.makeText(UserManagementActivity.this, 
                        "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(UserManagementActivity.this, 
                        "Error al crear usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (userDialog != null && userDialog.isShowing()) userDialog.dismiss();
                Toast.makeText(UserManagementActivity.this, 
                    "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUser(String userId, UserRequest request) {
        RetrofitClient.getApiService(UserManagementActivity.this).updateUser(userId, request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (userDialog != null && userDialog.isShowing()) userDialog.dismiss();
                
                if (response.isSuccessful()) {
                    Toast.makeText(UserManagementActivity.this, 
                        "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(UserManagementActivity.this, 
                        "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (userDialog != null && userDialog.isShowing()) userDialog.dismiss();
                Toast.makeText(UserManagementActivity.this, 
                    "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteUser(User user) {
        RetrofitClient.getApiService(UserManagementActivity.this).deleteUser(user.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserManagementActivity.this, 
                        "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(UserManagementActivity.this, 
                        "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this, 
                    "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
        btnAddUser.setEnabled(!show);
    }
}
