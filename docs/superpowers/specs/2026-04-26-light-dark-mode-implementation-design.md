# Design Doc: Full Light/Dark Mode Support

This document outlines the implementation plan for independent Light/Dark mode support in the IAAC_BD Android application, driven by user preference rather than system settings.

## 1. Architectural Overview
The application will use a manual theme switching mechanism. A `ThemeBaseActivity` will intercept the creation of every Activity to apply the user's selected theme from `SharedPreferences`.

## 2. Component Design

### 2.1 Theme Persistence
- **Storage**: `SharedPreferences`
- **Key**: `"app_theme"`
- **Values**: `"light"` (default) or `"dark"`

### 2.2 Base Activity (`ThemeBaseActivity.java`)
- **Responsibility**: Reading the theme preference and applying it via `AppCompatDelegate.setDefaultNightMode()`.
- **Implementation**:
  ```java
  // In onCreate, before super.onCreate()
  SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
  String theme = prefs.getString("app_theme", "light");
  if ("dark".equals(theme)) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
  } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
  }
  ```

### 2.3 Color System (`res/values/colors.xml`)
- **Primary Green**: `#2E7D32` (Light), `#388E3C` (Dark recommendation)
- **Background Light**: `#FAFAFA`
- **Background Dark**: `#121212`
- **Surface Dark**: `#1E1E1E`
- **Destructive Red (Light)**: `#D32F2F`
- **Destructive Red (Dark)**: `#EF5350`
- **Toolbar Green (Light)**: `#1B5E20`
- **Toolbar Green (Dark)**: `#0A3D0A`

### 2.4 Theme Definitions
Two distinct themes extending `Theme.Material3`:
- `Theme.IAACBD.Light`
- `Theme.IAACBD.Dark`

A custom attribute `colorDestructive` will be defined in `res/values/attrs.xml` to handle the red buttons across themes.

## 3. Implementation Details

### 3.1 Custom Attributes
```xml
<resources>
    <attr name="colorDestructive" format="color" />
</resources>
```

### 3.2 Layout Updates
- Replace all hardcoded hex colors with theme attributes (`?attr/colorPrimary`, `?attr/colorSurface`, `?attr/colorOnPrimary`, `?attr/colorDestructive`).
- Ensure the theme toggle in the Navigation Drawer updates `SharedPreferences` and calls `recreate()`.

## 4. Verification Plan
1. **Manual Toggle**: Verify that clicking the sun/moon icon in the drawer flips the theme immediately across all open activities.
2. **Persistence**: Close and reopen the app to ensure the theme choice is remembered.
3. **Destructive Actions**: Verify that delete buttons remain red in both modes, using the correct shade for each.
4. **Contrast Check**: Ensure text accessibility on dark surfaces (using the slight green tint `#E8F5E9` for text on dark).
