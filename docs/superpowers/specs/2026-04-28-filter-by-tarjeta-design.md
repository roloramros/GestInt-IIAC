# Design Doc: Filter by Tarjeta in Instrument List

This design document outlines the implementation of a new filtering feature in the general instrument list. Clicking on a "Tarjeta" value will trigger an API search to show all instruments with that specific card.

## Goals
- Allow users to quickly filter instruments by "Tarjeta" directly from the main list.
- Use the backend search API to ensure accurate and up-to-date results.
- Provide a silent filtering experience (UI updates list without changing search bar).
- Provide a way to restore the full list (Refresh).

## Architecture Changes

### 1. `InstrumentoAdapter`
- Add a new method `onTarjetaClick(Instrumento instrumento)` to `OnInstrumentoClickListener` interface.
- In `InstrumentoViewHolder`, set an `OnClickListener` on `tvTarjeta`.
- When `tvTarjeta` is clicked, invoke `clickListener.onTarjetaClick(instrumento)`.

### 2. `MainActivity`
- Update the `instrumentoAdapter` listener implementation to handle both `onTagClick` and `onTarjetaClick`.
  - `onTagClick`: Open `InstrumentoDetailActivity` (current behavior).
  - `onTarjetaClick`: Trigger a search for the selected tarjeta.
- Add a new method `filterByTarjeta(String tarjeta)`:
  - Show progress bar.
  - Call `ApiService.searchInstrumentos` with `{"tarjeta": tarjeta}`.
  - On success, update the adapter with the results.
  - On failure, show an error Toast.
- Implement "Refresh to Restore":
  - Add `SwipeRefreshLayout` to `activity_main.xml` wrapping the `RecyclerView`.
  - Set a listener to call `loadInstrumentos()` (which fetches the full list) when swiped.

## UI/UX
- **Interaction**: Clicking the "Tarjeta" text in any list item.
- **Feedback**: List updates with filtered results. Progress bar shows during network call.
- **Restoration**: Swipe down to refresh will reload the full list of instruments.

## Error Handling
- If the API call fails, a Toast will notify the user.
- If no instruments are found for a tarjeta, the list will be empty (consider showing an empty state message).

## Testing Strategy
- **Manual Test 1**: Click on a "Tarjeta" value. Verify that only instruments with that tarjeta are displayed.
- **Manual Test 2**: Verify that the progress bar appears while loading.
- **Manual Test 3**: Swipe down to refresh. Verify that the full list of instruments is restored.
- **Manual Test 4**: Click on a "Tag" value. Verify it still opens the details activity.
