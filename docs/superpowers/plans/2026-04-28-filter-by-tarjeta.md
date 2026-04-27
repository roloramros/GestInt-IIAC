# Filter by Tarjeta Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a quick filter by "Tarjeta" in the instrument list that triggers an API search and provides a way to refresh the list.

**Architecture:** Update `InstrumentoAdapter` to support card clicks, modify `MainActivity` to perform API-based searches when a card is clicked, and add `SwipeRefreshLayout` for list restoration.

**Tech Stack:** Java (Android), Retrofit, Material Design Components.

---

### Task 1: Update InstrumentoAdapter for Tarjeta Clicks

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/adapter/InstrumentoAdapter.java`

- [ ] **Step 1: Update OnInstrumentoClickListener interface**

```java
    public interface OnInstrumentoClickListener {
        void onTagClick(Instrumento instrumento);
        void onTarjetaClick(Instrumento instrumento); // New method
    }
```

- [ ] **Step 2: Update InstrumentoViewHolder to handle tvTarjeta clicks**

```java
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

            tvTarjeta.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTarjetaClick(instrumento);
                }
            });
        }
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/rfscu/iaacbd/adapter/InstrumentoAdapter.java
git commit -m "feat: add onTarjetaClick to InstrumentoAdapter"
```

### Task 2: Add SwipeRefreshLayout to activity_main.xml

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: Wrap RecyclerView with SwipeRefreshLayout**

```xml
        <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
            android:id="@+id/swipeRefreshLayout"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintTop_toBottomOf="@id/header"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rvInstrumentos"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                tools:listitem="@layout/item_instrumento"/>

        </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/res/layout/activity_main.xml
git commit -m "ui: add SwipeRefreshLayout to activity_main"
```

### Task 3: Implement Tarjeta Filtering and Refresh in MainActivity

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/MainActivity.java`

- [ ] **Step 1: Add SwipeRefreshLayout member and update setupRecyclerView**

```java
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    // ... in initViews()
    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

    // ... in setupRecyclerView()
    instrumentoAdapter.setOnInstrumentoClickListener(new InstrumentoAdapter.OnInstrumentoClickListener() {
        @Override
        public void onTagClick(Instrumento instrumento) {
            Intent intent = new Intent(MainActivity.this, InstrumentoDetailActivity.class);
            intent.putExtra("instrumento", instrumento);
            startActivityForResult(intent, REQUEST_DETAIL);
        }

        @Override
        public void onTarjetaClick(Instrumento instrumento) {
            if (instrumento.getTarjeta() != null && !instrumento.getTarjeta().isEmpty()) {
                filterByTarjeta(instrumento.getTarjeta());
            }
        }
    });
```

- [ ] **Step 2: Implement filterByTarjeta method**

```java
    private void filterByTarjeta(String tarjeta) {
        showProgress(true);
        java.util.Map<String, String> filters = new java.util.HashMap<>();
        filters.put("tarjeta", tarjeta);

        RetrofitClient.getApiService(this).searchInstrumentos(filters).enqueue(new Callback<List<Instrumento>>() {
            @Override
            public void onResponse(Call<List<Instrumento>> call, Response<List<Instrumento>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    instrumentoAdapter.setInstrumentos(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Error al filtrar por tarjeta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Instrumento>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
```

- [ ] **Step 3: Setup SwipeRefreshLayout listener**

```java
    // ... in setupListeners()
    swipeRefreshLayout.setOnRefreshListener(() -> {
        loadInstrumentos();
    });

    // ... in loadInstrumentos() onResponse and onFailure
    swipeRefreshLayout.setRefreshing(false);
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/rfscu/iaacbd/MainActivity.java
git commit -m "feat: implement tarjeta filtering and refresh logic in MainActivity"
```
