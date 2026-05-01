package com.rfscu.iaacbd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rfscu.iaacbd.R;
import com.rfscu.iaacbd.model.Certificado;
import java.util.ArrayList;
import java.util.List;

public class CertificadoAdapter extends RecyclerView.Adapter<CertificadoAdapter.ViewHolder> {
    private List<Certificado> list = new ArrayList<>();

    public void setList(List<Certificado> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificado, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificado c = list.get(position);
        holder.tvNoCertificado.setText(c.getNoCertificado());
        
        setupRow(holder.rowSerie, "No. Serie", c.getNoSerie());
        setupRow(holder.rowTipo, "Instrumento", c.getInstrumento());
        setupRow(holder.rowDesc, "Descripción", c.getDescripcion());
        setupRow(holder.rowRango, "Rango", c.getRango());
        setupRow(holder.rowEstado, "Estado Téc.", c.getEstadoTecnico());
        setupRow(holder.rowFecha, "Fecha", c.getFecha() != null ? c.getFecha().split("T")[0] : "-");
    }

    private void setupRow(View row, String label, String value) {
        ((TextView)row.findViewById(R.id.tvLabel)).setText(label);
        ((TextView)row.findViewById(R.id.tvValue)).setText(value != null ? value : "-");
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoCertificado;
        View rowSerie, rowTipo, rowDesc, rowRango, rowEstado, rowFecha;
        ViewHolder(View v) {
            super(v);
            tvNoCertificado = v.findViewById(R.id.tvNoCertificado);
            rowSerie = v.findViewById(R.id.rowSerie);
            rowTipo = v.findViewById(R.id.rowTipo);
            rowDesc = v.findViewById(R.id.rowDesc);
            rowRango = v.findViewById(R.id.rowRango);
            rowEstado = v.findViewById(R.id.rowEstado);
            rowFecha = v.findViewById(R.id.rowFecha);
        }
    }
}
