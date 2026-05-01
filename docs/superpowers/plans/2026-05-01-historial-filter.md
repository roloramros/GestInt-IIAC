# Historial Activity Filter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a filtering mechanism in the `HistorialActivity` to allow administrators to filter access logs by user and/or date.

**Architecture:** Update the backend to support optional query parameters for filtering, update the Android API service, and add an expandable filter header to the history activity with a toggle in the toolbar.

**Tech Stack:** FastAPI (Python), SQLAlchemy, Java (Android), Retrofit, Material Design components.

---

### Task 1: Backend Update

**Files:**
- Modify: `api/main.py`

- [ ] **Step 1: Update `listar_historial` endpoint**

```python
@app.get("/historial", response_model=list[schemas.HistorialAccesoResponse])
async def listar_historial(
    username: Optional[str] = None,
    fecha: Optional[date] = None,
    admin: Usuario = Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    stmt = select(HistorialAcceso)
    if username:
        stmt = stmt.where(HistorialAcceso.username == username)
    if fecha:
        start_dt = datetime.combine(fecha, datetime.min.time())
        end_dt = datetime.combine(fecha, datetime.max.time())
        stmt = stmt.where(HistorialAcceso.login_time.between(start_dt, end_dt))
        
    result = await db.execute(
        stmt.order_by(HistorialAcceso.login_time.desc()).limit(200)
    )
    return result.scalars().all()
```

- [ ] **Step 2: Commit backend changes**

```bash
git add api/main.py
git commit -m "feat(api): add filters to historial endpoint"
```

---

### Task 2: API Service Update

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/api/ApiService.java`

- [ ] **Step 1: Update `getHistorial` to accept filters**

```java
    @GET("/historial")
    Call<List<HistorialAcceso>> getHistorial();

    @GET("/historial")
    Call<List<HistorialAcceso>> getFilteredHistorial(
        @retrofit2.http.Query("username") String username,
        @retrofit2.http.Query("fecha") String fecha
    );
```

- [ ] **Step 2: Commit API changes**

```bash
git add app/src/main/java/com/rfscu/iaacbd/api/ApiService.java
git commit -m "feat(android): update ApiService for filtered history"
```

---

### Task 3: Layout Update

**Files:**
- Modify: `app/src/main/res/layout/activity_historial.xml`
- Create: `app/src/main/res/menu/historial_menu.xml`

- [ ] **Step 1: Create `historial_menu.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_filter"
        android:icon="@android:drawable/ic_menu_search"
        android:title="Filtrar"
        app:showAsAction="ifRoom"
        app:iconTint="?attr/colorOnPrimary" />
</menu>
```

- [ ] **Step 2: Add expandable filter header to `activity_historial.xml`**

Insert above `rvHistorial`:
```xml
        <com.google.android.material.card.MaterialCardView
            android:id="@+id/filter_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            android:visibility="gone"
            app:cardCornerRadius="8dp"
            app:cardElevation="2dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="12dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Filtrar por Usuario"
                    android:textAppearance="?attr/textAppearanceSubtitle2" />

                <Spinner
                    android:id="@+id/spinnerUser"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="12dp"
                    android:text="Filtrar por Fecha"
                    android:textAppearance="?attr/textAppearanceSubtitle2" />

                <Button
                    android:id="@+id/btnDatePicker"
                    style="@style/Widget.MaterialComponents.Button.TextButton"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Seleccionar Fecha"
                    android:textAlignment="viewStart" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="12dp"
                    android:gravity="end"
                    android:orientation="horizontal">

                    <Button
                        android:id="@+id/btnClearFilters"
                        style="@style/Widget.MaterialComponents.Button.TextButton"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Limpiar" />

                    <Button
                        android:id="@+id/btnApplyFilters"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginStart="8dp"
                        android:text="Aplicar" />
                </LinearLayout>

            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>
```

- [ ] **Step 3: Commit layout changes**

```bash
git add app/src/main/res/layout/activity_historial.xml app/src/main/res/menu/historial_menu.xml
git commit -m "feat(android): add filter UI to historial layout"
```

---

### Task 4: Activity Implementation

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/HistorialActivity.java`

- [ ] **Step 1: Add member variables and init views**

```java
    private View filterContainer;
    private Spinner spinnerUser;
    private Button btnDatePicker, btnApplyFilters, btnClearFilters;
    private String selectedDate = null;
    private List<String> userList = new ArrayList<>();
```

- [ ] **Step 2: Implement `onCreateOptionsMenu` to handle filter toggle**

```java
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.historial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            toggleFilter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFilter() {
        if (filterContainer.getVisibility() == View.VISIBLE) {
            filterContainer.setVisibility(View.GONE);
        } else {
            filterContainer.setVisibility(View.VISIBLE);
        }
    }
```

- [ ] **Step 3: Implement user list loading and spinner setup**

```java
    private void loadUsersForFilter() {
        RetrofitClient.getApiService(this).getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.add("Todos los usuarios");
                    for (User user : response.body()) {
                        userList.add(user.getUser_name());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HistorialActivity.this,
                            android.R.layout.simple_spinner_item, userList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUser.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }
```

- [ ] **Step 4: Implement `DatePickerDialog`**

```java
    private void showDatePicker() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            btnDatePicker.setText(selectedDate);
        }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }
```

- [ ] **Step 5: Update `loadHistorial` to use filters**

```java
    private void loadHistorial(String username, String date) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<HistorialAcceso>> call;
        if (username == null && date == null) {
            call = RetrofitClient.getApiService(this).getHistorial();
        } else {
            call = RetrofitClient.getApiService(this).getFilteredHistorial(username, date);
        }

        call.enqueue(new Callback<List<HistorialAcceso>>() {
            // ... same onResponse and onFailure logic ...
        });
    }
```

- [ ] **Step 6: Setup listeners for filter buttons**

```java
    btnApplyFilters.setOnClickListener(v -> {
        String user = spinnerUser.getSelectedItem().toString();
        if ("Todos los usuarios".equals(user)) user = null;
        loadHistorial(user, selectedDate);
        filterContainer.setVisibility(View.GONE);
    });

    btnClearFilters.setOnClickListener(v -> {
        spinnerUser.setSelection(0);
        selectedDate = null;
        btnDatePicker.setText("Seleccionar Fecha");
        loadHistorial(null, null);
        filterContainer.setVisibility(View.GONE);
    });
```

- [ ] **Step 7: Final commit**

```bash
git add app/src/main/java/com/rfscu/iaacbd/HistorialActivity.java
git commit -m "feat(android): implement filter logic in HistorialActivity"
```
