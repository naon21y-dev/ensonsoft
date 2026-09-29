# UI localization

`index.js` registers Vue I18n in Composition API mode. The supported locale codes
are `ko`, `en`, and `ja`; the Japanese button is labeled **JP**. Korean is the
default and fallback language. The selection is stored under the existing `language` key
and synchronized to `<html lang>` immediately.

All fixed UI strings live in `locales/ko.js`, `en.js`, and `ja.js`. The `mNNN`
keys are stable shared message IDs; search the Korean catalog to find a message.
`counts.*` contains parameterized counts with English plural forms.

- Use `useI18n().t` in components and add matching keys to all three catalogs.
- Build translated label arrays/maps in `computed`, so changing languages does
  not require remounting pages or lose filters, dialogs, and unsaved form data.
- Keep API enum values and CSS status classes unchanged. Translate only labels.
- Use `useUiMessage` only for local success/error feedback. It keeps existing
  feedback reactive when the language changes and preserves unknown server text.
- Never translate database titles, contents, site/equipment names, descriptions,
  history text, or form values. These must be rendered verbatim.
- Use `useFormatLocale` for existing locale-aware number/date formatting.
- `LanguageSwitcher` is shared by the app shell and native maintenance dialogs,
  whose modal top layer otherwise makes the global switcher inaccessible.

## Verification

```sh
npm run test:i18n
npm run build
```

The catalog tests check key/parameter parity, message compilation, persistence,
storage failure, reactive feedback, and untranslated template text.

`tests/i18n.browser.cjs` exercises all 18 routes in three languages, persistence,
forms, empty/error states, dialogs, and JWT headers. It intercepts API traffic
with local fixtures and never contacts the backend. Start `npm run dev` first,
then run it with Playwright available in the test environment:

```sh
node tests/i18n.browser.cjs
```

Optional environment variables: `PLAYWRIGHT_MODULE` (package path),
`BROWSER_CHANNEL` (default `msedge`), `TEST_BASE_URL` (default
`http://127.0.0.1:5173`), and `TEST_SCREENSHOT` (mobile screenshot output).

Q&A list, create/edit, question details, and administrator answers are included.
Question/answer contents and user-provided titles remain untranslated.
