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
        holder.tvFecha.setText(c.getFecha() != null ? c.getFecha().split("T")[0] : "-");
        
        setupRow(holder.rowTag, "TAG", c.getTag());
        setupRow(holder.rowTipo, "Instrumento", c.getInstrumento());
        setupRow(holder.rowSerie, "No. Serie", c.getNoSerie());
        setupRow(holder.rowRango, "Rango", c.getRango());
        setupRow(holder.rowEstado, "Estado Téc.", c.getEstadoTecnico());
    }

    private void setupRow(View row, String label, String value) {
        ((TextView)row.findViewById(R.id.tvLabel)).setText(label);
        ((TextView)row.findViewById(R.id.tvValue)).setText(value != null ? value : "-");
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoCertificado, tvFecha;
        View rowTag, rowTipo, rowSerie, rowRango, rowEstado;
        ViewHolder(View v) {
            super(v);
            tvNoCertificado = v.findViewById(R.id.tvNoCertificado);
            tvFecha = v.findViewById(R.id.tvFecha);
            rowTag = v.findViewById(R.id.rowTag);
            rowTipo = v.findViewById(R.id.rowTipo);
            rowSerie = v.findViewById(R.id.rowSerie);
            rowRango = v.findViewById(R.id.rowRango);
            rowEstado = v.findViewById(R.id.rowEstado);
        }
    }
}
