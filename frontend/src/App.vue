<script setup>

import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import LanguageSwitcher from './components/LanguageSwitcher.vue'
import SidebarAttendanceCard from './components/SidebarAttendanceCard.vue'
import { setLocale, roleText } from './i18n'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

// 실제 엔슨소프트 흰색 로고
import logoWhite from './assets/logo_w.png'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t, locale } = useI18n()

const languageOpen = ref(false)
const languages = [
  { code: 'ko', label: '한국어', short: 'KO' },
  { code: 'en', label: 'English', short: 'EN' },
  { code: 'ja', label: '日本語', short: 'JP' }
]

const currentLanguage = computed(() =>
  languages.find((language) => language.code === locale.value) || languages[0]
)

const changeLanguage = (code) => {
  setLocale(code)
  languageOpen.value = false
}


/*
 * 홈 / 로그인 / 회원가입은
 * 사이드바가 없는 독립 화면
 */
const isPublicLayout = computed(() => {
  return ['/', '/login', '/signup'].includes(route.path)
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="app-language-control"><LanguageSwitcher /></div>

  <!-- =========================================
       HOME / LOGIN / SIGNUP
       독립 레이아웃
  ========================================== -->

  <RouterView v-if="isPublicLayout" />


  <!-- =========================================
       SMART MOBILITY MANAGEMENT SYSTEM
  ========================================== -->

  <div v-else class="app-shell">

    <!-- =========================================
         SIDEBAR
    ========================================== -->

    <aside class="sidebar">

      <!-- =====================================
           LOGO
           클릭 시 인트로 화면(/)으로 이동
      ====================================== -->

      <div class="sidebar-logo">

        <RouterLink
          to="/"
          class="logo-link"
          :aria-label="t('m500')"
          :title="t('m501')"
        >

          <img
            :src="logoWhite"
            :alt="t('m232')"
            class="ensonsoft-logo"
          />

        </RouterLink>

      </div>


      <!-- =====================================
           NAVIGATION
      ====================================== -->

      <SidebarAttendanceCard v-if="authStore.isLoggedIn && authStore.role === 'USER'" :key="authStore.username" />

      <nav class="sidebar-nav">

        <p class="nav-label">
          {{ t('m239') }}
        </p>


        <!-- =====================================
             대시보드
        ====================================== -->

        <RouterLink
          to="/dashboard"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M3 13h8V3H3v10Zm0 8h8v-6H3v6Zm10 0h8V11h-8v10Zm0-18v6h8V3h-8Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.dashboard') }}
          </span>

        </RouterLink>


        <!-- =====================================
             통합관제
        ====================================== -->

        <RouterLink
          to="/monitoring"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M4 5h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2h-6v2h3v2H7v-2h3v-2H4a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm0 2v10h16V7H4Zm3 7 3-3 2 2 4-5 2 1.5-5.5 7-2.5-2.5-1.5 1.5L7 14Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.monitoring') }}
          </span>

        </RouterLink>


        <!-- =====================================
             현장 관리
        ====================================== -->

        <RouterLink
          to="/sites"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7Zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.sites') }}
          </span>

        </RouterLink>


        <!-- =====================================
             장비 관리
        ====================================== -->

        <RouterLink
          to="/equipments"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M19.14 12.94a7.5 7.5 0 0 0 .05-.94 7.5 7.5 0 0 0-.05-.94l2.03-1.58-1.92-3.32-2.39.96a7.3 7.3 0 0 0-1.62-.94L14.88 3h-3.84l-.36 2.18c-.57.24-1.11.55-1.62.94l-2.39-.96-1.92 3.32 2.03 1.58a7.5 7.5 0 0 0-.05.94c0 .32.02.63.05.94l-2.03 1.58 1.92 3.32 2.39-.96c.5.39 1.05.7 1.62.94l.36 2.18h3.84l.36-2.18c.57-.24 1.11-.55 1.62-.94l2.39.96 1.92-3.32-2.03-1.58ZM13 15.5A3.5 3.5 0 1 1 13 8a3.5 3.5 0 0 1 0 7.5Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.equipments') }}
          </span>

        </RouterLink>


        <!-- =====================================
             유지보수 관리
        ====================================== -->

        <RouterLink
          to="/maintenance"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="m22.7 19-9.1-9.1a6 6 0 0 0-7.5-7.5l3.4 3.4-3.7 3.7-3.4-3.4a6 6 0 0 0 7.5 7.5l9.1 9.1L22.7 19Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.maintenance') }}
          </span>

        </RouterLink>


        <!-- =====================================
             게시판
        ====================================== -->

        <RouterLink
          to="/boards"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M4 3h16a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Zm2 4v2h12V7H6Zm0 4v2h12v-2H6Zm0 4v2h8v-2H6Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.boards') }}
          </span>

        </RouterLink>


        <!-- =====================================
             Q&A 문의
        ====================================== -->

        <RouterLink
          to="/qna"
          class="nav-item"
        >

          <span class="nav-icon">

            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M12 2C6.48 2 2 5.81 2 10.5c0 2.65 1.43 5.02 3.67 6.58L4.5 22l5.06-2.53c.79.2 1.61.3 2.44.3 5.52 0 10-3.81 10-9.27S17.52 2 12 2Zm.05 14.5a1.25 1.25 0 1 1 0-2.5 1.25 1.25 0 0 1 0 2.5Zm1.73-5.66c-.72.52-.78.76-.78 1.66h-2c0-1.64.25-2.29 1.57-3.24.69-.5 1.11-.89 1.11-1.54 0-.88-.69-1.42-1.66-1.42-1.02 0-1.75.57-1.88 1.64l-1.98-.25C8.4 5.7 9.91 4.4 12.08 4.4c2.14 0 3.7 1.24 3.7 3.19 0 1.45-.78 2.34-2 3.25Z"
              />
            </svg>

          </span>

          <span class="nav-text">
            {{ t('menu.qna') }}
          </span>

        </RouterLink>


        <!-- =====================================
             ADMIN
        ====================================== -->

        <RouterLink v-if="authStore.role === 'USER'" to="/attendance" class="nav-item">
          <span class="nav-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm0 18a8 8 0 1 1 0-16 8 8 0 0 1 0 16Zm1-13h-2v6l5 3 1-1.7-4-2.3V7Z" /></svg></span>
          <span class="nav-text">{{ t('attendance.myTitle') }}</span>
        </RouterLink>

        <template v-if="authStore.isAdmin">

          <p class="nav-label admin-label">
            {{ t('m504') }}
          </p>
          <RouterLink to="/admin/attendance" class="nav-item">
            <span class="nav-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm0 18a8 8 0 1 1 0-16 8 8 0 0 1 0 16Zm1-13h-2v6l5 3 1-1.7-4-2.3V7Z" /></svg></span>
            <span class="nav-text">{{ t('attendance.adminTitle') }}</span>
          </RouterLink>

          <RouterLink
            to="/admin"
            class="nav-item"
          >

            <span class="nav-icon">

              <svg
                viewBox="0 0 24 24"
                aria-hidden="true"
              >
                <path
                  d="M12 2 4 5v6c0 5.55 3.84 10.74 8 12 4.16-1.26 8-6.45 8-12V5l-8-3Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 11.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22Z"
                />
              </svg>

            </span>

            <span class="nav-text">
              {{ t('menu.admin') }}
            </span>

          </RouterLink>

        </template>

      </nav>


      <!-- =========================================
           SIDEBAR USER
      ========================================== -->

      <div class="sidebar-bottom">

        <!-- 언어 선택 -->
        <div class="language-selector">
          <button
            type="button"
            class="language-button"
            :class="{ 'is-open': languageOpen }"
            :aria-expanded="languageOpen"
            @click="languageOpen = !languageOpen"
          >
            <span class="language-globe" aria-hidden="true">
              <svg viewBox="0 0 24 24">
                <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm6.92 6h-3.03a15.7 15.7 0 0 0-1.38-3.1A8.05 8.05 0 0 1 18.92 8ZM12 4c.83 1.2 1.48 2.54 1.86 4h-3.72A13.6 13.6 0 0 1 12 4ZM4.26 14a8.3 8.3 0 0 1 0-4h3.4a16.6 16.6 0 0 0 0 4h-3.4Zm.82 2h3.03c.3 1.1.77 2.14 1.38 3.1A8.05 8.05 0 0 1 5.08 16ZM8.11 8H5.08a8.05 8.05 0 0 1 4.41-3.1A15.7 15.7 0 0 0 8.11 8ZM12 20a13.6 13.6 0 0 1-1.86-4h3.72A13.6 13.6 0 0 1 12 20Zm2.24-6H9.76a14.4 14.4 0 0 1 0-4h4.48a14.4 14.4 0 0 1 0 4Zm.27 5.1c.61-.96 1.08-2 1.38-3.1h3.03a8.05 8.05 0 0 1-4.41 3.1ZM16.34 14a16.6 16.6 0 0 0 0-4h3.4a8.3 8.3 0 0 1 0 4h-3.4Z"/>
              </svg>
            </span>
            <span class="language-current">
              <span class="language-caption">{{ t('common.language') }}</span>
              <strong>{{ currentLanguage.label }}</strong>
            </span>
            <span class="language-code">{{ currentLanguage.short }}</span>
            <span class="language-chevron" :class="{ 'is-open': languageOpen }">⌄</span>
          </button>

          <Transition name="language-menu">
            <div v-if="languageOpen" class="language-menu">
              <button
                v-for="language in languages"
                :key="language.code"
                type="button"
                class="language-option"
                :class="{ active: locale === language.code }"
                @click="changeLanguage(language.code)"
              >
                <span class="language-option-check">{{ locale === language.code ? '✓' : '' }}</span>
                <span>{{ language.label }}</span>
                <small>{{ language.short }}</small>
              </button>
            </div>
          </Transition>
        </div>

        <!-- 사용자 정보 -->

        <RouterLink
          to="/user"
          class="user-card"
        >

          <div class="user-avatar">
            {{
              authStore.username
                ?.charAt(0)
                ?.toUpperCase() || 'U'
            }}
          </div>

          <div class="user-info">

            <strong>
              {{ authStore.username }}
            </strong>

            <span>
              {{ roleText(authStore.role) }}
            </span>

          </div>

          <span class="user-arrow">
            ›
          </span>

        </RouterLink>


        <!-- 로그아웃 -->

        <button
          type="button"
          class="logout-button"
          @click="logout"
        >

          <svg
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <path
              d="M10 17v-2h4V9h-4V7l-5 5 5 5Zm9-14H9a2 2 0 0 0-2 2v3h2V5h10v14H9v-3H7v3a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2Z"
            />
          </svg>

          <span>
            {{ t('common.logout') }}
          </span>

        </button>

      </div>

    </aside>


    <!-- =========================================
         PAGE CONTENT
    ========================================== -->

    <main class="app-main">

      <RouterView v-slot="{ Component }">

        <Transition
          name="page"
          mode="out-in"
        >

          <component
            :is="Component"
            :key="route.fullPath"
          />

        </Transition>

      </RouterView>

    </main>

  </div>
</template>


<style scoped>
.app-language-control { position: fixed; right: 16px; bottom: 16px; z-index: 10000; }

/*
 * RouterView 페이지 전환
 * 전체 디자인은 style.css에서 관리
 */

.page-enter-active {
  transition:
    opacity 0.28s ease,
    transform 0.28s ease;
}

.page-leave-active {
  transition:
    opacity 0.15s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(7px);
}

.page-leave-to {
  opacity: 0;
}

</style>
