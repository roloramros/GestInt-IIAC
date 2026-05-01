# Design Spec - Calibration Module Navigation

## Goal
Implement the navigation structure for the Calibration module, including an expandable submenu in the navigation drawer and three placeholder activities for the sub-options.

## Navigation Drawer (`navigation_menu.xml`)
- The existing `nav_calibration` item will be converted into a submenu.
- **Sub-options**:
  - `nav_calibration_monthly`: "Planes Mensuales"
  - `nav_calibration_update`: "Actualización de Certificados"
  - `nav_calibration_history`: "Historial de Certificados"

## Android Activities
Three new activities will be created as placeholders (using `ThemeBaseActivity` for consistent styling):
1. **MonthlyPlansActivity**: Handles monthly calibration plans.
2. **UpdateCertsActivity**: Handles uploading/updating calibration certificates.
3. **CertsHistoryActivity**: Displays the history of all calibration certificates.

## Activity logic
- Each activity will implement the navigation drawer logic to allow switching between modules.
- The `DrawerHelper` will be updated if necessary to handle any common submenu logic (though `NavigationView` handles the expansion automatically).
- The `navView.setNavigationItemSelectedListener` in each activity will be updated to handle the three new sub-items.

## Layouts
- Three basic layouts (`activity_monthly_plans.xml`, `activity_update_certs.xml`, `activity_certs_history.xml`) with a `DrawerLayout`, `Toolbar`, and a placeholder "Próximamente" message.

## Success Criteria
- Clicking "Planes de Calibración" in the sidebar expands the submenu.
- Clicking "Planes Mensuales" opens `MonthlyPlansActivity`.
- Clicking "Actualización de Certificados" opens `UpdateCertsActivity`.
- Clicking "Historial de Certificados" opens `CertsHistoryActivity`.
- Navigation between these activities and existing ones works seamlessly via the drawer.
