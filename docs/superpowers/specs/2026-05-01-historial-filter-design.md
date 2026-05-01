# Design Spec - Historial Activity Filter

## Goal
Implement a filtering mechanism in the `HistorialActivity` to allow administrators to filter access logs by user and/or date.

## User Interface
- **Toolbar Icon**: A filter icon will be added to the top-right of the toolbar.
- **Expandable Filter Header**: Clicking the filter icon will toggle an expandable section above the history list.
  - **User Selector**: A Dropdown (Spinner) populated with all system users.
  - **Date Selector**: A clickable field that opens a `DatePickerDialog` to select a specific date.
  - **Action Buttons**: 
    - `Apply`: Executes the filtered search.
    - `Clear`: Resets filters and shows all history.

## Backend Changes (`api/main.py`)
- Update the `/historial` endpoint:
  - Add optional query parameters: `username` (string) and `fecha` (date).
  - Modify the SQL query to apply filters if they are provided.
  - Change authentication requirement from `get_current_propietario` to `get_current_admin` to allow both admins and owners.

## Android Implementation
- **API Service**: Update `ApiService.java` to support query parameters for `/historial`.
- **Activity**: 
  - Add logic to fetch the list of users to populate the spinner.
  - Implement the expandable header toggle logic.
  - Implement `DatePickerDialog` for date selection.
  - Update `loadHistorial()` to handle filtered requests.
- **Layout**: Modify `activity_historial.xml` to include the filter header and update the toolbar menu.

## Success Criteria
- The user can toggle the filter header.
- Filtering by user only shows logs for that user.
- Filtering by date only shows logs for that specific day.
- Filtering by both works correctly.
- Clearing filters restores the full history list.
