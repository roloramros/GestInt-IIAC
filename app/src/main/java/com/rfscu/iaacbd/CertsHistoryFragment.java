package com.rfscu.iaacbd;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.rfscu.iaacbd.adapter.CertificadoAdapter;
import com.rfscu.iaacbd.api.RetrofitClient;
import com.rfscu.iaacbd.model.Certificado;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertsHistoryFragment extends Fragment {
    private RecyclerView rv;
    private CertificadoAdapter adapter;
    private ProgressBar pb;
    private TextView tvEmpty;
    private EditText etSearch;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_certs_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rvCertificados);
        pb = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        etSearch = view.findViewById(R.id.etSearch);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        adapter = new CertificadoAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        loadCertificados(null);

        swipeRefresh.setOnRefreshListener(() -> {
            loadCertificados(etSearch.getText().toString());
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                loadCertificados(s.toString());
            }
        });
    }

    private void loadCertificados(String query) {
        pb.setVisibility(View.VISIBLE);
        String noCert = query;
        String noSerie = query;

        RetrofitClient.getApiService(getContext()).getCertificados(noCert, noSerie).enqueue(new Callback<List<Certificado>>() {
            @Override
            public void onResponse(Call<List<Certificado>> call, Response<List<Certificado>> response) {
                if (!isAdded()) return;
                pb.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Certificado> list = response.body();
                    adapter.setList(list);
                    tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error al cargar certificados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Certificado>> call, Throwable t) {
                if (!isAdded()) return;
                pb.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
