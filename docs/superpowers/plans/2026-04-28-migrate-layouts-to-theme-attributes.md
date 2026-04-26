# Layout Migration to Theme Attributes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ensure all layouts correctly adapt to Light and Dark modes using theme attributes instead of hardcoded colors or direct color references.

**Architecture:** Use `?attr/` for theme attributes and `?android:attr/` for system attributes. Replace specific hardcoded colors with their theme-aware equivalents defined in `themes.xml`.

**Tech Stack:** Android XML Layouts, Material Design 3.

---

### Task 1: Migrate Nav Header and Footer

**Files:**
- Modify: `app/src/main/res/layout/nav_header.xml`
- Modify: `app/src/main/res/layout/nav_footer.xml`

- [ ] **Step 1: Update nav_header.xml**
    - Replace `android:textColor="@android:color/white"` with `android:textColor="?attr/colorOnPrimary"`.

- [ ] **Step 2: Update nav_footer.xml**
    - Replace `app:backgroundTint="@color/color_delete_light"` with `app:backgroundTint="?attr/colorDestructive"`.
    - Replace `android:textColor="@color/white"` with `android:textColor="?attr/colorOnPrimary"`.
    - Replace `app:iconTint="@color/white"` with `app:iconTint="?attr/colorOnPrimary"`.

### Task 2: Migrate Main and Home Activities

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/res/layout/activity_home.xml`

- [ ] **Step 1: Update activity_main.xml**
    - Replace all occurrences of `@android:color/white` for text and tints with `?attr/colorOnPrimary`.

- [ ] **Step 2: Update activity_home.xml**
    - Replace `app:titleTextColor="@android:color/white"` with `app:titleTextColor="?attr/colorOnPrimary"`.
    - Replace `android:textColor="@android:color/white"` with `android:textColor="?attr/colorOnPrimary"`.
    - Replace `@color/green_primary` tints with `?attr/colorPrimary`.

### Task 3: Migrate User Management and Item User

**Files:**
- Modify: `app/src/main/res/layout/activity_user_management.xml`
- Modify: `app/src/main/res/layout/item_user.xml`

- [ ] **Step 1: Update activity_user_management.xml**
    - Replace `app:titleTextColor="@android:color/white"` with `app:titleTextColor="?attr/colorOnPrimary"`.
    - Replace `app:iconTint="@android:color/white"` with `app:iconTint="?attr/colorOnPrimary"`.

- [ ] **Step 2: Update item_user.xml**
    - Replace `app:backgroundTint="@color/color_delete_light"` with `app:backgroundTint="?attr/colorDestructive"`.
    - Replace all `app:iconTint="@android:color/white"` with `app:iconTint="?attr/colorOnPrimary"`.

### Task 4: Migrate Instrumento Detail and Advanced Search

**Files:**
- Modify: `app/src/main/res/layout/activity_instrumento_detail.xml`
- Modify: `app/src/main/res/layout/activity_advanced_search.xml`

- [ ] **Step 1: Update activity_instrumento_detail.xml**
    - Replace `app:titleTextColor="@color/white"` with `app:titleTextColor="?attr/colorOnPrimary"`.
    - Replace `app:backgroundTint="@color/color_delete_light"` with `app:backgroundTint="?attr/colorDestructive"`.
    - Replace `app:tint="@android:color/white"` with `app:tint="?attr/colorOnPrimary"`.

- [ ] **Step 2: Update activity_advanced_search.xml**
    - Replace `app:titleTextColor="@android:color/white"` with `app:titleTextColor="?attr/colorOnPrimary"`.

### Task 5: Final Review and Commit

- [ ] **Step 1: Verify XML validity**
    - Check that no `@` remains for attributes like `?attr/colorDestructive`.
- [ ] **Step 2: Commit changes**
    - `git add app/src/main/res/layout/*.xml`
    - `git commit -m "style: migrate layouts to use theme attributes"`
