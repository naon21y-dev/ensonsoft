<script setup>
import { useI18n } from 'vue-i18n'
import { setLocale } from '../i18n'

const { locale, t } = useI18n()
const languages = [
  { code: 'ko', label: 'KO', name: '한국어' },
  { code: 'en', label: 'EN', name: 'English' },
  { code: 'ja', label: 'JP', name: '日本語' },
]
</script>

<template>
  <div class="language-switcher" role="group" :aria-label="t('language.select')">
    <button v-for="language in languages" :key="language.code"
      type="button" :lang="language.code" :title="language.name"
      :aria-label="language.name" :aria-pressed="locale === language.code"
      @click="setLocale(language.code)">
      {{ language.label }}
    </button>
  </div>
</template>

<style scoped>
.language-switcher { display: inline-flex; align-items: center; gap: 3px; padding: 4px; border: 1px solid #cbd5e1; border-radius: 9px; background: #fff; flex-shrink: 0; }
button { border: 0; border-radius: 5px; padding: 5px 9px; color: #475569; background: transparent; font: inherit; font-size: 12px; font-weight: 700; line-height: 1.3; cursor: pointer; }
button[aria-pressed="true"] { color: #fff; background: #2563eb; }
button:hover { background: #e2e8f0; color: #1e3a8a; }
button:focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; }
</style>
