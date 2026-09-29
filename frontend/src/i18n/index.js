import { computed, ref, watch } from 'vue'
import { createI18n } from 'vue-i18n'
import ko from './locales/ko.js'
import en from './locales/en.js'
import ja from './locales/ja.js'

export const LANGUAGE_STORAGE_KEY = 'language'
export const supportedLocales = ['ko', 'en', 'ja']
const messages = { ko, en, ja }

function savedLocale() {
  try {
    const saved = localStorage.getItem(LANGUAGE_STORAGE_KEY)
    return supportedLocales.includes(saved) ? saved : 'ko'
  } catch {
    return 'ko'
  }
}

export const i18n = createI18n({
  legacy: false,
  locale: savedLocale(),
  fallbackLocale: 'ko',
  messages,
})

watch(i18n.global.locale, (locale) => {
  if (typeof document !== 'undefined') document.documentElement.lang = locale
  try {
    localStorage.setItem(LANGUAGE_STORAGE_KEY, locale)
  } catch {
    // Language switching still works when browser storage is unavailable.
  }
}, { immediate: true, flush: 'sync' })

export function setLocale(locale) {
  if (supportedLocales.includes(locale)) i18n.global.locale.value = locale
}

export function useFormatLocale() {
  return computed(() => ({ ko: 'ko-KR', en: 'en-US', ja: 'ja-JP' })[i18n.global.locale.value])
}

// Only local feedback fields use this helper. Never pass database records or
// editable content through it. Preserve unknown server error messages verbatim.
const feedbackKeys = new Map()
for (const catalog of Object.values(messages)) {
  for (const [key, text] of Object.entries(catalog)) feedbackKeys.set(text, key)
}

export function useUiMessage() {
  const value = ref('')
  return computed({
    get: () => value.value?.key ? i18n.global.t(value.value.key) : value.value,
    set: (text) => {
      const key = feedbackKeys.get(text)
      value.value = key ? { key } : text
    },
  })
}

export function roleText(role) {
  return role === 'ADMIN' ? i18n.global.t('m223')
    : role === 'USER' ? i18n.global.t('m224') : role
}
