package com.rfscu.iaacbd.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rfscu.iaacbd.R;
import com.rfscu.iaacbd.model.Instrumento;
import com.rfscu.iaacbd.model.InstrumentoCreateRequest;
import com.rfscu.iaacbd.model.InstrumentoUpdateRequest;

public class InstrumentoFormDialog {

    private final Context context;
    private final FormCallback callback;
    private Dialog dialog;
    private Instrumento instrumentoToEdit;

    // Campos Básicos
    private TextInputLayout tilTag;
    private TextInputEditText etTag, etPlanta, etInstrumento, etTarjeta;

    // Campos Avanzados
    private TextInputEditText etDirIm, etDirPa, etVarMedida, etComunicacion, etSeguridad, etDescripcion;
    private TextInputEditText etLowWarning, etHighWarning, etLowAlarm, etHighAlarm;
    private TextInputEditText etStartWr, etEndWr, etStartMr, etEndMr;
    private TextInputEditText etNoSerie, etRango, etUserUpdate;

    private LinearLayout layoutAdvancedFields;
    private TextView tvToggleText;
    private boolean isAdvancedVisible = false;

    public interface FormCallback {
        void onCreateInstrumento(InstrumentoCreateRequest data);
        void onUpdateInstrumento(int id, InstrumentoUpdateRequest data);
    }

    public InstrumentoFormDialog(Context context, FormCallback callback) {
        this(context, null, callback);
    }

    public InstrumentoFormDialog(Context context, Instrumento instrumento, FormCallback callback) {
        this.context = context;
        this.instrumentoToEdit = instrumento;
        this.callback = callback;
        initDialog();
    }

    private void initDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_instrumento_form, null);

        // Bind Básicos
        tilTag = view.findViewById(R.id.tilTag);
        etTag = view.findViewById(R.id.etTag);
        etPlanta = view.findViewById(R.id.etPlanta);
        etInstrumento = view.findViewById(R.id.etInstrumento);
        etTarjeta = view.findViewById(R.id.etTarjeta);

        // Bind Toggle
        LinearLayout layoutToggleAdvanced = view.findViewById(R.id.layoutToggleAdvanced);
        layoutAdvancedFields = view.findViewById(R.id.layoutAdvancedFields);
        tvToggleText = view.findViewById(R.id.tvToggleText);

        // Bind Avanzados
        etDirIm = view.findViewById(R.id.etDirIm);
        etDirPa = view.findViewById(R.id.etDirPa);
        etVarMedida = view.findViewById(R.id.etVarMedida);
        etComunicacion = view.findViewById(R.id.etComunicacion);
        etSeguridad = view.findViewById(R.id.etSeguridad);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etLowWarning = view.findViewById(R.id.etLowWarning);
        etHighWarning = view.findViewById(R.id.etHighWarning);
        etLowAlarm = view.findViewById(R.id.etLowAlarm);
        etHighAlarm = view.findViewById(R.id.etHighAlarm);
        etStartWr = view.findViewById(R.id.etStartWr);
        etEndWr = view.findViewById(R.id.etEndWr);
        etStartMr = view.findViewById(R.id.etStartMr);
        etEndMr = view.findViewById(R.id.etEndMr);
        etNoSerie = view.findViewById(R.id.etNoSerie);
        etRango = view.findViewById(R.id.etRango);
        etUserUpdate = view.findViewById(R.id.etUserUpdate);

        // Si estamos editando, pre-llenar campos
        if (instrumentoToEdit != null) {
            etTag.setText(instrumentoToEdit.getTag());
            etTag.setEnabled(false); // Tag es clave primaria, no se edita
            etPlanta.setText(instrumentoToEdit.getPlanta());
            etInstrumento.setText(instrumentoToEdit.getInstrumento());
            etTarjeta.setText(instrumentoToEdit.getTarjeta());

            etDirIm.setText(instrumentoToEdit.getDirIm());
            etDirPa.setText(instrumentoToEdit.getDirPa());
            etVarMedida.setText(instrumentoToEdit.getVarMedida());
            etComunicacion.setText(instrumentoToEdit.getComunicacion());
            etSeguridad.setText(instrumentoToEdit.getSeguridad());
            etDescripcion.setText(instrumentoToEdit.getDescripcion());

            etLowWarning.setText(instrumentoToEdit.getLowWarning() != null ? String.valueOf(instrumentoToEdit.getLowWarning()) : "");
            etHighWarning.setText(instrumentoToEdit.getHighWarning() != null ? String.valueOf(instrumentoToEdit.getHighWarning()) : "");
            etLowAlarm.setText(instrumentoToEdit.getLowAlarm() != null ? String.valueOf(instrumentoToEdit.getLowAlarm()) : "");
            etHighAlarm.setText(instrumentoToEdit.getHighAlarm() != null ? String.valueOf(instrumentoToEdit.getHighAlarm()) : "");

            etStartWr.setText(instrumentoToEdit.getStartWr() != null ? String.valueOf(instrumentoToEdit.getStartWr()) : "");
            etEndWr.setText(instrumentoToEdit.getEndWr() != null ? String.valueOf(instrumentoToEdit.getEndWr()) : "");
            etStartMr.setText(instrumentoToEdit.getStartMr() != null ? String.valueOf(instrumentoToEdit.getStartMr()) : "");
            etEndMr.setText(instrumentoToEdit.getEndMr() != null ? String.valueOf(instrumentoToEdit.getEndMr()) : "");

            etNoSerie.setText(instrumentoToEdit.getNoSerie());
            etRango.setText(instrumentoToEdit.getRango());
        }

        // Auto-rellenar username
        String currentUser = TokenManager.getUsername(context);
        etUserUpdate.setText(currentUser != null ? currentUser : "");
        etUserUpdate.setEnabled(false); // No se edita manualmente

        // Toggle Logic
        layoutToggleAdvanced.setOnClickListener(v -> toggleAdvanced());

        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> validateAndSave());

        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();
    }

    private void toggleAdvanced() {
        isAdvancedVisible = !isAdvancedVisible;
        layoutAdvancedFields.setVisibility(isAdvancedVisible ? View.VISIBLE : View.GONE);
        tvToggleText.setText(isAdvancedVisible ? R.string.hide_advanced : R.string.show_advanced);
    }

    private void validateAndSave() {
        String tag = etTag.getText().toString().trim();
        
        if (tag.isEmpty()) {
            tilTag.setError(context.getString(R.string.error_tag_required));
            return;
        } else {
            tilTag.setError(null);
        }

        if (instrumentoToEdit != null) {
            // MODO EDITAR
            InstrumentoUpdateRequest updateRequest = new InstrumentoUpdateRequest();
            updateRequest.setTag(tag);
            updateRequest.setPlanta(getText(etPlanta));
            updateRequest.setInstrumento(getText(etInstrumento));
            updateRequest.setTarjeta(getText(etTarjeta));
            updateRequest.setDirIm(getText(etDirIm));
            updateRequest.setDirPa(getText(etDirPa));
            updateRequest.setVarMedida(getText(etVarMedida));
            updateRequest.setComunicacion(getText(etComunicacion));
            updateRequest.setSeguridad(getText(etSeguridad));
            updateRequest.setDescripcion(getText(etDescripcion));
            updateRequest.setLowWarning(getInt(etLowWarning));
            updateRequest.setHighWarning(getInt(etHighWarning));
            updateRequest.setLowAlarm(getInt(etLowAlarm));
            updateRequest.setHighAlarm(getInt(etHighAlarm));
            updateRequest.setStartWr(getInt(etStartWr));
            updateRequest.setEndWr(getInt(etEndWr));
            updateRequest.setStartMr(getInt(etStartMr));
            updateRequest.setEndMr(getInt(etEndMr));
            updateRequest.setNoSerie(getText(etNoSerie));
            updateRequest.setRango(getText(etRango));
            updateRequest.setUserUpdate(etUserUpdate.getText().toString());

            if (callback != null) {
                callback.onUpdateInstrumento(instrumentoToEdit.getId(), updateRequest);
            }
        } else {
            // MODO CREAR
            InstrumentoCreateRequest createRequest = new InstrumentoCreateRequest();
            createRequest.setTag(tag);
            createRequest.setPlanta(getText(etPlanta));
            createRequest.setInstrumento(getText(etInstrumento));
            createRequest.setTarjeta(getText(etTarjeta));
            createRequest.setDirIm(getText(etDirIm));
            createRequest.setDirPa(getText(etDirPa));
            createRequest.setVarMedida(getText(etVarMedida));
            createRequest.setComunicacion(getText(etComunicacion));
            createRequest.setSeguridad(getText(etSeguridad));
            createRequest.setDescripcion(getText(etDescripcion));
            createRequest.setLowWarning(getInt(etLowWarning));
            createRequest.setHighWarning(getInt(etHighWarning));
            createRequest.setLowAlarm(getInt(etLowAlarm));
            createRequest.setHighAlarm(getInt(etHighAlarm));
            createRequest.setStartWr(getInt(etStartWr));
            createRequest.setEndWr(getInt(etEndWr));
            createRequest.setStartMr(getInt(etStartMr));
            createRequest.setEndMr(getInt(etEndMr));
            createRequest.setNoSerie(getText(etNoSerie));
            createRequest.setRango(getText(etRango));
            createRequest.setUserUpdate(etUserUpdate.getText().toString());

            if (callback != null) {
                callback.onCreateInstrumento(createRequest);
            }
        }
        dialog.dismiss();
    }

    private String getText(TextInputEditText editText) {
        String text = editText.getText().toString().trim();
        return text.isEmpty() ? null : text;
    }

    private Integer getInt(TextInputEditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }
}
