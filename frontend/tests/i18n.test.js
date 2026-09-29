import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync, readdirSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { parse as parseSfc } from '@vue/compiler-sfc'
import { baseParse } from '@vue/compiler-dom'
import ko from '../src/i18n/locales/ko.js'
import en from '../src/i18n/locales/en.js'
import ja from '../src/i18n/locales/ja.js'
import 'vue'

const storage = new Map()
Object.defineProperty(globalThis, 'localStorage', { configurable: true, value: {
  getItem: key => storage.get(key) ?? null,
  setItem: (key, value) => storage.set(key, value),
} })
globalThis.document = { documentElement: { lang: '' } }
const { i18n, setLocale, useUiMessage, useFormatLocale, LANGUAGE_STORAGE_KEY } = await import('../src/i18n/index.js')
const flatten = (obj, prefix = '') => Object.fromEntries(Object.entries(obj).flatMap(([k,v]) => typeof v === 'string' ? [[prefix+k,v]] : Object.entries(flatten(v,prefix+k+'.'))))
const catalogs = { ko: flatten(ko), en: flatten(en), ja: flatten(ja) }

test('all languages have identical keys, parameters, and valid message syntax', () => {
  const keys = Object.keys(catalogs.ko).sort()
  for (const [locale, catalog] of Object.entries(catalogs)) {
    assert.deepEqual(Object.keys(catalog).sort(), keys)
    setLocale(locale)
    for (const [key, text] of Object.entries(catalog)) {
      assert(text.trim(), `${locale}:${key} is empty`)
      assert.deepEqual([...new Set(text.match(/\{\w+\}/g) ?? [])].sort(), [...new Set(catalogs.ko[key].match(/\{\w+\}/g) ?? [])].sort(), `${locale}:${key} parameters`)
      const result = i18n.global.t(key, { p0: 'DATA 원문', count: 3 }, 3)
      assert.notEqual(result, key)
      assert(!result.includes('{p0}'))
    }
    assert.equal(i18n.global.t('m399'), 'example@ensonsoft.com')
  }
})

test('locale changes persist and synchronize document language and formatting', () => {
  const formatting = useFormatLocale()
  for (const [locale, expected] of [['ko','ko-KR'],['en','en-US'],['ja','ja-JP']]) {
    setLocale(locale)
    assert.equal(localStorage.getItem(LANGUAGE_STORAGE_KEY), locale)
    assert.equal(document.documentElement.lang, locale)
    assert.equal(formatting.value, expected)
  }
  setLocale('jp')
  assert.equal(i18n.global.locale.value, 'ja', 'JP is a UI label; ja is the locale code')
})

test('already-visible feedback follows locale while unknown server messages stay intact', () => {
  setLocale('ko')
  const feedback = useUiMessage()
  feedback.value = i18n.global.t('m033')
  setLocale('en')
  assert.equal(feedback.value, en.m033)
  setLocale('ja')
  assert.equal(feedback.value, ja.m033)
  feedback.value = '서버에서 받은 원문 메시지'
  setLocale('ko')
  assert.equal(feedback.value, '서버에서 받은 원문 메시지')
  feedback.value = ''
  assert.equal(feedback.value, '')
})

test('storage restrictions do not prevent switching languages', () => {
  const original = localStorage.setItem
  localStorage.setItem = () => { throw new Error('storage disabled') }
  try { assert.doesNotThrow(() => setLocale('en')); assert.equal(document.documentElement.lang, 'en') }
  finally { localStorage.setItem = original }
})

test('saved language is restored and invalid stored values fall back to Korean', async () => {
  storage.set(LANGUAGE_STORAGE_KEY, 'ja')
  const restored = await import('../src/i18n/index.js?restore')
  assert.equal(restored.i18n.global.locale.value, 'ja')
  storage.set(LANGUAGE_STORAGE_KEY, 'invalid')
  const fallback = await import('../src/i18n/index.js?fallback')
  assert.equal(fallback.i18n.global.locale.value, 'ko')
})

test('all Vue templates use catalog keys for fixed text and accessibility attributes', () => {
  const root = fileURLToPath(new URL('../src/', import.meta.url))
  const files = ['App.vue', ...['views','components'].flatMap(folder => readdirSync(root+folder).filter(f=>f.endsWith('.vue')).map(f=>folder+'/'+f))]
  for (const file of files) {
    const code = readFileSync(root+file,'utf8')
    for (const match of code.matchAll(/\bt\('([^']+)'/g)) assert(match[1] in catalogs.ko, `${file}: missing ${match[1]}`)
    const { descriptor } = parseSfc(code)
    const ast = baseParse(descriptor.template.content)
    function visit(node) {
      if (node.type === 2) assert(!/[A-Za-z가-힣]/.test(node.content), `${file}: raw UI text ${node.content}`)
      for (const prop of node.props || []) {
        if (prop.type === 6 && ['placeholder','title','alt','aria-label'].includes(prop.name)) assert(!/[A-Za-z가-힣]/.test(prop.value?.content ?? ''), `${file}: raw ${prop.name}`)
      }
      for (const child of node.children || []) visit(child)
    }
    visit(ast)
  }
})
