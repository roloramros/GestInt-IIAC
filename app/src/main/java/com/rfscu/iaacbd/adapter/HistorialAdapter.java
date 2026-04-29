package com.rfscu.iaacbd.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rfscu.iaacbd.R;
import com.rfscu.iaacbd.model.HistorialAcceso;

import java.util.ArrayList;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<HistorialAcceso> historialList = new ArrayList<>();

    public void setHistorial(List<HistorialAcceso> historial) {
        this.historialList = historial;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        HistorialAcceso item = historialList.get(position);
        holder.tvUsername.setText(item.getUsername());
        holder.tvIpAddress.setText("IP: " + item.getIpAddress());
        
        // Formatear un poco la fecha (el backend envía ISO 8601)
        String rawTime = item.getLoginTime();
        if (rawTime != null && rawTime.contains("T")) {
            String formatted = rawTime.replace("T", " ").split("\\.")[0];
            holder.tvLoginTime.setText(formatted);
        } else {
            holder.tvLoginTime.setText(rawTime);
        }

        holder.tvStatus.setText(item.getStatus().toUpperCase());
        if ("success".equalsIgnoreCase(item.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Rojo
        }
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvIpAddress, tvLoginTime, tvStatus;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvIpAddress = itemView.findViewById(R.id.tvIpAddress);
            tvLoginTime = itemView.findViewById(R.id.tvLoginTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}