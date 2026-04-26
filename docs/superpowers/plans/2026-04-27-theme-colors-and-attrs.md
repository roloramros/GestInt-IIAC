# Theme Overhaul Part 1: Colors and Attributes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Define brand colors and a custom theme attribute for destructive actions in the Android project.

**Architecture:** Update the central `colors.xml` resource file and create a new `attrs.xml` to expose a theme-switchable destructive color attribute.

**Tech Stack:** Android XML Resources

---

### Task 1: Update Brand Colors

**Files:**
- Modify: `app/src/main/res/values/colors.xml`

- [ ] **Step 1: Replace content of colors.xml**

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

- [ ] **Step 2: Verify XML validity**

Run: `powershell.exe -NoProfile -Command "[xml](Get-Content app/src/main/res/values/colors.xml)"`
Expected: No errors.

### Task 2: Create Custom Theme Attribute

**Files:**
- Create: `app/src/main/res/values/attrs.xml`

- [ ] **Step 1: Create attrs.xml with colorDestructive attribute**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <attr name="colorDestructive" format="color" />
</resources>
```

- [ ] **Step 2: Verify XML validity**

Run: `powershell.exe -NoProfile -Command "[xml](Get-Content app/src/main/res/values/attrs.xml)"`
Expected: No errors.

### Task 3: Commit Changes

- [ ] **Step 1: Stage and commit changes**

Run: `git add app/src/main/res/values/colors.xml app/src/main/res/values/attrs.xml`
Run: `git commit -m "style: define brand colors and custom destructive attribute"`
