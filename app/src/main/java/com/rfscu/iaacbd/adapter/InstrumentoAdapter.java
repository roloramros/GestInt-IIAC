package com.rfscu.iaacbd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rfscu.iaacbd.R;
import com.rfscu.iaacbd.model.Instrumento;

import java.util.ArrayList;
import java.util.List;

public class InstrumentoAdapter extends RecyclerView.Adapter<InstrumentoAdapter.InstrumentoViewHolder> {

    private List<Instrumento> instrumentoList = new ArrayList<>();
    private OnInstrumentoClickListener clickListener;

    public interface OnInstrumentoClickListener {
        void onTagClick(Instrumento instrumento);
    }

    public void setInstrumentos(List<Instrumento> instrumentos) {
        this.instrumentoList = instrumentos != null ? instrumentos : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnInstrumentoClickListener(OnInstrumentoClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public InstrumentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instrumento, parent, false);
        return new InstrumentoViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull InstrumentoViewHolder holder, int position) {
        Instrumento instrumento = instrumentoList.get(position);
        holder.bind(instrumento);
    }

    @Override
    public int getItemCount() {
        return instrumentoList.size();
    }

    static class InstrumentoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlanta, tvTag, tvInstrumento, tvTarjeta;
        private OnInstrumentoClickListener clickListener;

        public InstrumentoViewHolder(@NonNull View itemView, OnInstrumentoClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            tvPlanta = itemView.findViewById(R.id.tvPlanta);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvInstrumento = itemView.findViewById(R.id.tvInstrumento);
            tvTarjeta = itemView.findViewById(R.id.tvTarjeta);
        }

        public void bind(Instrumento instrumento) {
            tvPlanta.setText(instrumento.getPlanta() != null ? instrumento.getPlanta() : "-");
            tvTag.setText(instrumento.getTag() != null ? instrumento.getTag() : "-");
            tvInstrumento.setText(instrumento.getInstrumento() != null ? instrumento.getInstrumento() : "-");
            tvTarjeta.setText(instrumento.getTarjeta() != null ? instrumento.getTarjeta() : "-");

            tvTag.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTagClick(instrumento);
                }
            });
        }
    }
}
