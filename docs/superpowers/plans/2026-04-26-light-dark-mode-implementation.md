# Light/Dark Mode Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement full Light/Dark mode support across all Activities in the project, driven by a persistent "app_theme" setting in SharedPreferences, independent of system theme.

**Architecture:** Use a `ThemeBaseActivity` to apply `AppCompatDelegate.setDefaultNightMode()` based on SharedPreferences before `setContentView()`. Define custom theme attributes to handle brand colors and destructive actions consistently.

**Tech Stack:** Android (Java), Material Components (M3), SharedPreferences.

---

### Task 1: Color & Attribute Definitions

**Files:**
- Modify: `app/src/main/res/values/colors.xml`
- Create: `app/src/main/res/values/attrs.xml`

- [ ] **Step 1: Update colors.xml with brand hex codes**

```xml
<resources>
    <!-- Brand Colors: Greens -->
    <color name="green_primary">#2E7D32</color> <!-- Light Mode Primary -->
    <color name="green_primary_dark">#388E3C</color> <!-- Dark Mode Primary Suggestion -->
    <color name="green_secondary">#66BB6A</color>
    <color name="green_toolbar_light">#1B5E20</color>
    <color name="green_toolbar_dark">#0A3D0A</color>

    <!-- Destructive Action Colors -->
    <color name="color_delete_light">#D32F2F</color>
    <color name="color_delete_dark">#EF5350</color>

    <!-- Backgrounds & Surfaces -->
    <color name="bg_light">#FAFAFA</color>
    <color name="surface_light">#FFFFFF</color>
    <color name="bg_dark">#121212</color>
    <color name="surface_dark">#1E1E1E</color>

    <!-- Text Colors -->
    <color name="text_on_light">#000000</color>
    <color name="text_on_dark">#E8F5E9</color>
    <color name="text_hint_light">#757575</color>
    <color name="text_hint_dark">#A5D6A7</color>

    <!-- General -->
    <color name="white">#FFFFFFFF</color>
    <color name="black">#FF000000</color>
    <color name="transparent">#00000000</color>
</resources>
```

- [ ] **Step 2: Create attrs.xml for custom attributes**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <attr name="colorDestructive" format="color" />
</resources>
```

- [ ] **Step 3: Commit**
```bash
git add app/src/main/res/values/colors.xml app/src/main/res/values/attrs.xml
git commit -m "style: define brand colors and custom destructive attribute"
```

### Task 2: Theme Definitions

**Files:**
- Modify: `app/src/main/res/values/themes.xml`
- Modify: `app/src/main/res/values-night/themes.xml`

- [ ] **Step 1: Update res/values/themes.xml**

```xml
<resources>
    <!-- Light Theme -->
    <style name="Theme.IAACBD.Light" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/green_primary</item>
        <item name="colorSecondary">@color/green_secondary</item>
        <item name="colorSurface">@color/surface_light</item>
        <item name="android:colorBackground">@color/bg_light</item>
        <item name="android:windowBackground">@color/bg_light</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorOnSurface">@color/text_on_light</item>
        <item name="colorOnBackground">@color/text_on_light</item>
        <item name="android:statusBarColor">@color/green_toolbar_light</item>
        <item name="android:textColorPrimary">@color/text_on_light</item>
        <item name="android:textColorSecondary">@color/text_hint_light</item>
        <item name="colorDestructive">@color/color_delete_light</item>
    </style>

    <!-- Dark Theme -->
    <style name="Theme.IAACBD.Dark" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/green_primary_dark</item>
        <item name="colorSecondary">@color/green_secondary</item>
        <item name="colorSurface">@color/surface_dark</item>
        <item name="android:colorBackground">@color/bg_dark</item>
        <item name="android:windowBackground">@color/bg_dark</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorOnSurface">@color/text_on_dark</item>
        <item name="colorOnBackground">@color/text_on_dark</item>
        <item name="android:statusBarColor">@color/green_toolbar_dark</item>
        <item name="android:textColorPrimary">@color/text_on_dark</item>
        <item name="android:textColorSecondary">@color/text_hint_dark</item>
        <item name="colorDestructive">@color/color_delete_dark</item>
    </style>
    
    <style name="Theme.IAACBD" parent="Theme.IAACBD.Light" />
</resources>
```

- [ ] **Step 2: Update res/values-night/themes.xml to be identical to Light (to prevent auto-switching)**

```xml
<resources>
    <style name="Theme.IAACBD.Dark" parent="Theme.Material3.Dark.NoActionBar">
        <!-- Re-defining to ensure it's available in night qualifier if needed, but we control manually -->
        <item name="colorPrimary">@color/green_primary_dark</item>
        <item name="colorSecondary">@color/green_secondary</item>
        <item name="colorSurface">@color/surface_dark</item>
        <item name="android:colorBackground">@color/bg_dark</item>
        <item name="android:windowBackground">@color/bg_dark</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorOnSurface">@color/text_on_dark</item>
        <item name="colorOnBackground">@color/text_on_dark</item>
        <item name="android:statusBarColor">@color/green_toolbar_dark</item>
        <item name="android:textColorPrimary">@color/text_on_dark</item>
        <item name="android:textColorSecondary">@color/text_hint_dark</item>
        <item name="colorDestructive">@color/color_delete_dark</item>
    </style>
</resources>
```

- [ ] **Step 3: Commit**
```bash
git add app/src/main/res/values/themes.xml app/src/main/res/values-night/themes.xml
git commit -m "style: define light and dark themes with Material3"
```

### Task 3: Base Activity Implementation

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/utils/ThemeBaseActivity.java`

- [ ] **Step 1: Implement theme logic in ThemeBaseActivity**

```java
package com.rfscu.iaacbd.utils;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.rfscu.iaacbd.R;

public class ThemeBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme BEFORE super.onCreate()
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String theme = prefs.getString("app_theme", "light");
        
        if ("dark".equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.Theme_IAACBD_Dark);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_IAACBD_Light);
        }
        
        super.onCreate(savedInstanceState);
    }
}
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/com/rfscu/iaacbd/utils/ThemeBaseActivity.java
git commit -m "feat: implement manual theme switching in ThemeBaseActivity"
```

### Task 4: Theme Toggle Logic

**Files:**
- Modify: `app/src/main/java/com/rfscu/iaacbd/utils/DrawerHelper.java`

- [ ] **Step 1: Update setupDarkModeToggle in DrawerHelper**

```java
    private static void setupDarkModeToggle(Activity activity, NavigationView navView) {
        View headerView = navView.getHeaderView(0);
        if (headerView == null) return;

        ImageView ivDarkModeToggle = headerView.findViewById(R.id.ivDarkModeToggle);
        if (ivDarkModeToggle == null) return;

        SharedPreferences prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String currentTheme = prefs.getString("app_theme", "light");
        boolean isDarkMode = "dark".equals(currentTheme);
        updateThemeIcon(ivDarkModeToggle, isDarkMode);

        ivDarkModeToggle.setOnClickListener(v -> {
            String theme = prefs.getString("app_theme", "light");
            String newTheme = "light".equals(theme) ? "dark" : "light";
            
            prefs.edit().putString("app_theme", newTheme).apply();
            
            // Recreate activity to apply new theme immediately
            activity.recreate();
        });
    }
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/com/rfscu/iaacbd/utils/DrawerHelper.java
git commit -m "feat: update theme toggle to use app_theme key"
```

### Task 5: Layout Migration to Theme Attributes

**Files:**
- Modify: `app/src/main/res/layout/*.xml` (Multiple files)

- [ ] **Step 1: Replace hardcoded colors in activity_main.xml**
- Change `android:textColor="@android:color/white"` to `?attr/colorOnPrimary` for views inside Toolbar.
- Change `app:tint="@android:color/white"` to `?attr/colorOnPrimary` or `?attr/colorOnSecondary`.
- Change FAB `app:backgroundTint="?attr/colorPrimary"` and `app:tint="@android:color/white"`.

- [ ] **Step 2: Replace red buttons with ?attr/colorDestructive**
- Files: `nav_footer.xml`, `activity_instrumento_detail.xml`, `item_user.xml`.
- Change `app:backgroundTint="@color/color_delete_light"` to `app:backgroundTint="?attr/colorDestructive"`.

- [ ] **Step 3: Update Drawer backgrounds**
- Ensure `android:background="?android:attr/windowBackground"` or `?attr/colorSurface`.

- [ ] **Step 4: Commit**
```bash
git add app/src/main/res/layout/*.xml
git commit -m "style: migrate layouts to use theme attributes"
```
