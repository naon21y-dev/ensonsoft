import { createI18n } from 'vue-i18n'

import ko from './locales/ko'
import en from './locales/en'
import ja from './locales/ja'

const savedLanguage = localStorage.getItem('language') || 'ko'

const i18n = createI18n({
  legacy: false,
  locale: savedLanguage,
  fallbackLocale: 'ko',

  messages: {
    ko,
    en,
    ja
  }
})

export default i18n
