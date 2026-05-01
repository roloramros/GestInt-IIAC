# Calibration Module Navigation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the navigation structure for the Calibration module, including an expandable submenu in the drawer and three placeholder activities.

**Architecture:** Convert the existing calibration menu item into a submenu. Create three new activities (`MonthlyPlansActivity`, `UpdateCertsActivity`, `CertsHistoryActivity`) inheriting from `ThemeBaseActivity`. Update navigation logic in all activities to handle new menu items.

**Tech Stack:** Java (Android), XML layouts, Material Components.

---

### Task 1: Navigation Menu Update

**Files:**
- Modify: `app/src/main/res/menu/navigation_menu.xml`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add new strings to `strings.xml`**

```xml
    <string name="menu_calibration_monthly">Planes Mensuales</string>
    <string name="menu_calibration_update">Actualización de Certificados</string>
    <string name="menu_calibration_history">Historial de Certificados</string>
```

- [ ] **Step 2: Update `navigation_menu.xml` with submenu**

Replace `nav_calibration` item with:
```xml
        <item
            android:id="@+id/nav_calibration"
            android:icon="@android:drawable/ic_menu_recent_history"
            android:title="Planes de Calibración">
            <menu>
                <item
                    android:id="@+id/nav_calibration_monthly"
                    android:title="@string/menu_calibration_monthly" />
                <item
                    android:id="@+id/nav_calibration_update"
                    android:title="@string/menu_calibration_update" />
                <item
                    android:id="@+id/nav_calibration_history"
                    android:title="@string/menu_calibration_history" />
            </menu>
        </item>
```

- [ ] **Step 3: Commit menu changes**

```bash
git add app/src/main/res/menu/navigation_menu.xml app/src/main/res/values/strings.xml
git commit -m "feat(ui): add calibration submenu to navigation drawer"
```

---

### Task 2: Create Placeholder Layouts

**Files:**
- Create: `app/src/main/res/layout/activity_monthly_plans.xml`
- Create: `app/src/main/res/layout/activity_update_certs.xml`
- Create: `app/src/main/res/layout/activity_certs_history.xml`

- [ ] **Step 1: Create `activity_monthly_plans.xml`**
(Use `activity_historial.xml` as template, but with a simple TextView "Próximamente: Planes Mensuales")

- [ ] **Step 2: Create `activity_update_certs.xml`**
(Similar template, TextView "Próximamente: Actualización de Certificados")

- [ ] **Step 3: Create `activity_certs_history.xml`**
(Similar template, TextView "Próximamente: Historial de Certificados")

- [ ] **Step 4: Commit layouts**

```bash
git add app/src/main/res/layout/activity_*.xml
git commit -m "feat(ui): create placeholder layouts for calibration activities"
```

---

### Task 3: Create Placeholder Activities

**Files:**
- Create: `app/src/main/java/com/rfscu/iaacbd/MonthlyPlansActivity.java`
- Create: `app/src/main/java/com/rfscu/iaacbd/UpdateCertsActivity.java`
- Create: `app/src/main/java/com/rfscu/iaacbd/CertsHistoryActivity.java`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create `MonthlyPlansActivity.java`**
(Implement standard drawer setup and `NavigationItemSelectedListener`)

- [ ] **Step 2: Create `UpdateCertsActivity.java`**
(Similar implementation)

- [ ] **Step 3: Create `CertsHistoryActivity.java`**
(Similar implementation)

- [ ] **Step 4: Register activities in `AndroidManifest.xml`**

- [ ] **Step 5: Commit activities**

```bash
git add app/src/main/java/com/rfscu/iaacbd/*.java app/src/main/AndroidManifest.xml
git commit -m "feat(ui): create placeholder activities for calibration module"
```

---

### Task 4: Update Navigation Logic Everywhere

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/HomeActivity.java`
- Modify: `app/src/main/java/com/rfscu/iaacbd/MainActivity.java`
- Modify: `app/src/main/java/com/rfscu/iaacbd/AdvancedSearchActivity.java`
- Modify: `app/src/main/java/com/rfscu/iaacbd/UserManagementActivity.java`
- Modify: `app/src/main/java/com/rfscu/iaacbd/HistorialActivity.java`

- [ ] **Step 1: Update `NavigationItemSelectedListener` in each activity**

Add handlers for the three new sub-items:
```java
            } else if (id == R.id.nav_calibration_monthly) {
                startActivity(new Intent(this, MonthlyPlansActivity.class));
            } else if (id == R.id.nav_calibration_update) {
                startActivity(new Intent(this, UpdateCertsActivity.class));
            } else if (id == R.id.nav_calibration_history) {
                startActivity(new Intent(this, CertsHistoryActivity.class));
```

- [ ] **Step 2: Commit navigation updates**

```bash
git add app/src/main/java/com/rfscu/iaacbd/*.java
git commit -m "feat(ui): update navigation logic across all activities"
```
