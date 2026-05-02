package com.rfscu.iaacbd;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.Certificado;
import com.rfscu.iaacbd.model.CertificadoRequest;
import com.rfscu.iaacbd.model.Instrumento;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCertsFragment extends Fragment {
    private AutoCompleteTextView acNoSerie;
    private Button btnCargar, btnActualizar;
    private View rowTag, rowTipo, rowRango;
    private EditText etNoCertificado, etFecha, etObservaciones;
    private AutoCompleteTextView spinnerEstado;
    private ProgressBar progressBar;

    private List<Instrumento> allInstruments = new ArrayList<>();
    private Instrumento selectedInstrument = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_certs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupEstadoSpinner();
        setupDatePicker();
        loadInstruments();

        btnCargar.setOnClickListener(v -> cargarDetalles());
        btnActualizar.setOnClickListener(v -> guardarCertificado());
    }

    private void initViews(View v) {
        acNoSerie = v.findViewById(R.id.acNoSerie);
        btnCargar = v.findViewById(R.id.btnCargar);
        btnActualizar = v.findViewById(R.id.btnActualizar);
        rowTag = v.findViewById(R.id.rowTag);
        rowTipo = v.findViewById(R.id.rowTipo);
        rowRango = v.findViewById(R.id.rowRango);
        etNoCertificado = v.findViewById(R.id.etNoCertificado);
        etFecha = v.findViewById(R.id.etFecha);
        etObservaciones = v.findViewById(R.id.etObservaciones);
        spinnerEstado = v.findViewById(R.id.spinnerEstado);
        progressBar = v.findViewById(R.id.progressBar);

        setupRowLabel(rowTag, "TAG");
        setupRowLabel(rowTipo, "Instrumento");
        setupRowLabel(rowRango, "Rango");
    }

    private void setupRowLabel(View row, String label) {
        ((TextView) row.findViewById(R.id.tvLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvValue)).setText("-");
    }

    private void setupEstadoSpinner() {
        String[] options = {"Dentro de Límites", "No Apto"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, options);
        spinnerEstado.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String date = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                etFecha.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadInstruments() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService(getContext()).getInstrumentos().enqueue(new Callback<List<Instrumento>>() {
            @Override
            public void onResponse(Call<List<Instrumento>> call, Response<List<Instrumento>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allInstruments = response.body();
                    List<String> series = new ArrayList<>();
                    for (Instrumento i : allInstruments) {
                        if (i.getNoSerie() != null && !i.getNoSerie().isEmpty()) {
                            series.add(i.getNoSerie());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, series);
                    acNoSerie.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Instrumento>> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar instrumentos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDetalles() {
        String serieStr = acNoSerie.getText().toString();
        if (TextUtils.isEmpty(serieStr)) return;

        selectedInstrument = null;
        for (Instrumento i : allInstruments) {
            if (serieStr.equalsIgnoreCase(i.getNoSerie())) {
                selectedInstrument = i;
                break;
            }
        }

        if (selectedInstrument != null) {
            ((TextView) rowTag.findViewById(R.id.tvValue)).setText(selectedInstrument.getTag());
            ((TextView) rowTipo.findViewById(R.id.tvValue)).setText(selectedInstrument.getInstrumento());
            ((TextView) rowRango.findViewById(R.id.tvValue)).setText(selectedInstrument.getRango());
        } else {
            Toast.makeText(getContext(), "Instrumento no encontrado", Toast.LENGTH_SHORT).show();
            clearDetails();
        }
    }

    private void clearDetails() {
        ((TextView) rowTag.findViewById(R.id.tvValue)).setText("-");
        ((TextView) rowTipo.findViewById(R.id.tvValue)).setText("-");
        ((TextView) rowRango.findViewById(R.id.tvValue)).setText("-");
        selectedInstrument = null;
    }

    private void guardarCertificado() {
        if (selectedInstrument == null) {
            Toast.makeText(getContext(), "Debe cargar un instrumento primero", Toast.LENGTH_SHORT).show();
            return;
        }

        String noCert = etNoCertificado.getText().toString();
        String estado = spinnerEstado.getText().toString();
        String fecha = etFecha.getText().toString();
        String obs = etObservaciones.getText().toString();

        if (TextUtils.isEmpty(noCert) || TextUtils.isEmpty(estado) || TextUtils.isEmpty(fecha)) {
            Toast.makeText(getContext(), "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        CertificadoRequest request = new CertificadoRequest(noCert, selectedInstrument.getId(), estado, obs, fecha);

        RetrofitClient.getApiService(getContext()).createCertificado(request).enqueue(new Callback<Certificado>() {
            @Override
            public void onResponse(Call<Certificado> call, Response<Certificado> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Certificado actualizado con éxito", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                } else {
                    String errorMsg = "Error al guardar";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("detail")) {
                                // Simple extraction for now
                                errorMsg = errorBody.split("\"detail\":\"")[1].split("\"")[0];
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Certificado> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarFormulario() {
        acNoSerie.setText("");
        etNoCertificado.setText("");
        etFecha.setText("");
        etObservaciones.setText("");
        spinnerEstado.setText("");
        clearDetails();
    }
}
