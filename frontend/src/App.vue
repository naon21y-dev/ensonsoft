<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

// 실제 엔슨소프트 흰색 로고
import logoWhite from './assets/logo_w.png'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

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
          aria-label="엔슨소프트 인트로 홈"
          title="홈으로"
        >

          <img
            :src="logoWhite"
            alt="엔슨소프트"
            class="ensonsoft-logo"
          />

        </RouterLink>

      </div>


      <!-- =====================================
           NAVIGATION
      ====================================== -->

      <nav class="sidebar-nav">

        <p class="nav-label">
          SMART MOBILITY
        </p>


        <!-- 대시보드 -->

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
            대시보드
          </span>

        </RouterLink>


        <!-- 통합관제 -->

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
            통합관제
          </span>

        </RouterLink>


        <!-- 현장 관리 -->

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
            현장 관리
          </span>

        </RouterLink>


        <!-- 장비 관리 -->

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
            장비 관리
          </span>

        </RouterLink>


        <!-- 유지보수 관리 -->

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
            유지보수 관리
          </span>

        </RouterLink>


        <!-- 게시판 -->

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
            게시판
          </span>

        </RouterLink>


        <!-- =====================================
             ADMIN
        ====================================== -->

        <template v-if="authStore.isAdmin">

          <p class="nav-label admin-label">
            ADMINISTRATION
          </p>

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
              관리자
            </span>

          </RouterLink>

        </template>

      </nav>


      <!-- =========================================
           SIDEBAR USER
      ========================================== -->

      <div class="sidebar-bottom">

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
              {{ authStore.role }}
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
            로그아웃
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