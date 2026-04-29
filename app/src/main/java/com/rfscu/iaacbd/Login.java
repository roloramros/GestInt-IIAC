package com.rfscu.iaacbd;

import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.LoginRequest;
import com.rfscu.iaacbd.model.LoginResponse;
import com.rfscu.iaacbd.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;

public class Login extends ThemeBaseActivity {

    private TextInputEditText etUsuario, etPassword;
    private MaterialButton btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Si el token es válido (menos de 1 hora)...
        if (TokenManager.isLoggedIn(this)) {
            Intent intent = new Intent(Login.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        } else {
            // Si hay algo pero no es válido, limpiamos por seguridad
            TokenManager.clearToken(this);
        }

        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            btnEntrar.setEnabled(false);
            btnEntrar.setText("Conectando...");

            // Llamada asíncrona al backend
            RetrofitClient.getApiService(Login.this).login(new LoginRequest(usuario, password))
                    .enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            btnEntrar.setEnabled(true);
                            btnEntrar.setText("Entrar");

                            if (response.isSuccessful() && response.body() != null) {
                                String token = response.body().getAccessToken();

                                // ✅ Guarda el token de forma segura
                                TokenManager.saveToken(Login.this, token);
                                
                                // Extraer rol del token JWT
                                String role = extractRoleFromToken(token);
                                
                                // ✅ Guarda la info básica del usuario
                                TokenManager.saveUserInfo(Login.this, usuario, null, role);

                                // ✅ Navega a la pantalla principal
                                Intent intent = new Intent(Login.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(Login.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            btnEntrar.setEnabled(true);
                            btnEntrar.setText("Entrar");
                            Toast.makeText(Login.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private String extractRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            
            String payload = parts[1];
            byte[] data = android.util.Base64.decode(payload, android.util.Base64.DEFAULT);
            String json = new String(data, "UTF-8");
            
            org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            return jsonObject.optString("rol", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
