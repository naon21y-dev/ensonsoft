<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const showOpening = ref(true)
const openingLeaving = ref(false)
const platformSection = ref(null)

let openingLeaveTimer = null
let openingRemoveTimer = null

const roleLabel = computed(() => {
  if (authStore.role === 'ADMIN') return '관리자'
  if (authStore.role === 'USER') return '일반 사용자'
  return authStore.role || ''
})

const scrollToPlatform = () => {
  platformSection.value?.scrollIntoView({
    behavior: 'smooth',
    block: 'start'
  })
}

onMounted(() => {
  // 2.15초부터 오프닝 퇴장
  openingLeaveTimer = window.setTimeout(() => {
    openingLeaving.value = true
  }, 2150)

  // 약 2.75초에 완전히 제거
  openingRemoveTimer = window.setTimeout(() => {
    showOpening.value = false
  }, 2750)
})

onBeforeUnmount(() => {
  if (openingLeaveTimer) {
    clearTimeout(openingLeaveTimer)
  }

  if (openingRemoveTimer) {
    clearTimeout(openingRemoveTimer)
  }
})
</script>


<template>
  <main class="home-page">

    <!-- =====================================================
         2.7 SECOND BRAND OPENING
    ====================================================== -->

    <Transition name="opening">
      <section
        v-if="showOpening"
        class="brand-opening"
        :class="{ 'is-leaving': openingLeaving }"
      >
        <div class="opening-background"></div>
        <div class="opening-overlay"></div>
        <div class="opening-grid"></div>

        <div class="opening-content">

          <p class="opening-brand">
            ENSONSOFT
          </p>

          <div class="opening-line"></div>

          <h1 class="opening-title">

            <span class="opening-text opening-text-1">
              차별화 된 첨단 기술력으로
            </span>

            <span class="opening-text opening-text-2">
              이끌어나갈
            </span>

            <span class="opening-highlight opening-text-3">
              <span class="opening-highlight-bg"></span>

              <span class="opening-highlight-text">
                안전하고 새로운 미래
              </span>
            </span>

          </h1>

          <div class="opening-bottom">

            <span>
              SMART MOBILITY
            </span>

            <span class="opening-dot"></span>

            <span>
              INTEGRATED CONTROL
            </span>

          </div>

        </div>

        <div class="opening-progress">
          <span></span>
        </div>

      </section>
    </Transition>


    <!-- =====================================================
         MAIN HERO
    ====================================================== -->

    <section class="intro-page">

      <!-- BACKGROUND -->

      <div class="background-image"></div>
      <div class="background-overlay"></div>
      <div class="background-glow"></div>
      <div class="panel-background-glow"></div>


      <!-- =================================================
           HEADER
      ================================================== -->

      <header class="intro-header">

        <RouterLink
          to="/"
          class="brand"
          aria-label="엔슨소프트 인트로 홈"
        >
          <img
            src="../assets/logo_w.png"
            alt="Ensonsoft"
            class="brand-logo"
          />
        </RouterLink>


        <div class="header-actions">

          <!-- 로그인 전 -->

          <template v-if="!authStore.isLoggedIn">

            <RouterLink
              to="/login"
              class="header-link"
            >
              로그인
            </RouterLink>

            <RouterLink
              to="/signup"
              class="header-button"
            >
              회원가입
            </RouterLink>

          </template>


          <!-- 로그인 후 -->

          <template v-else>

            <div class="user-status">

              <span class="online-dot"></span>

              <span>
                {{ authStore.username }}
              </span>

              <span class="user-role">
                {{ roleLabel }}
              </span>

            </div>

            <RouterLink
              to="/dashboard"
              class="header-button"
            >
              시스템 접속
            </RouterLink>

          </template>

        </div>

      </header>


      <!-- =================================================
           HERO
      ================================================== -->

      <div class="hero">

        <!-- LEFT -->

        <div class="hero-content">

          <div class="eyebrow animation-1">

            <span class="eyebrow-line"></span>

            <span>
              ENSONSOFT SMART MOBILITY
            </span>

          </div>


          <h1 class="hero-title">

            <span class="title-line animation-2">
              더 나은 세상을 만드는
            </span>

            <span class="title-line animation-3">
              첨단 기술의 중심
            </span>

            <span class="title-line highlight animation-4">
              엔슨소프트
            </span>

          </h1>


          <div class="hero-description animation-5">

            <p class="english-copy">
              Innovative creativity for
              <strong>
                better safety and future.
              </strong>
            </p>

            <p class="korean-copy">
              스마트 모빌리티 기술을 통해 더 안전하고 편리한
              교통 환경을 만들어갑니다.
            </p>

          </div>


          <div class="hero-buttons animation-6">

            <!-- 로그인 전 -->

            <template v-if="!authStore.isLoggedIn">

              <RouterLink
                to="/login"
                class="primary-button"
              >

                <span>
                  시스템 로그인
                </span>

                <svg viewBox="0 0 24 24">
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>

              </RouterLink>


              <RouterLink
                to="/signup"
                class="secondary-button"
              >
                회원가입
              </RouterLink>

            </template>


            <!-- 로그인 후 -->

            <template v-else>

              <RouterLink
                to="/dashboard"
                class="primary-button"
              >

                <span>
                  통합 대시보드
                </span>

                <svg viewBox="0 0 24 24">
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>

              </RouterLink>


              <RouterLink
                to="/user"
                class="secondary-button"
              >
                내 정보
              </RouterLink>

            </template>

          </div>

        </div>


        <!-- =================================================
             RIGHT CONTROL PANEL
        ================================================== -->

        <div class="system-panel animation-panel">

          <div class="panel-light"></div>
          <div class="panel-shine"></div>


          <!-- HEADER -->

          <div class="panel-header panel-item panel-item-1">

            <div class="panel-title-area">

              <span class="panel-small">
                SMART MOBILITY
              </span>

              <strong>
                Integrated Control
              </strong>

              <span class="panel-subtitle">
                Mobility Management System
              </span>

            </div>


            <span class="live">

              <span class="live-dot"></span>

              LIVE

            </span>

          </div>


          <div class="panel-divider panel-item panel-item-2"></div>


          <!-- 01 -->

          <div class="system-row system-row-1">

            <div class="system-icon">
              01
            </div>

            <div class="system-text">

              <span>
                Integrated Monitoring
              </span>

              <strong>
                통합 관제 시스템
              </strong>

            </div>

            <span class="system-status">
              NORMAL
            </span>

          </div>


          <!-- 02 -->

          <div class="system-row system-row-2">

            <div class="system-icon">
              02
            </div>

            <div class="system-text">

              <span>
                Traffic Infrastructure
              </span>

              <strong>
                교통 인프라 관리
              </strong>

            </div>

            <span class="system-status">
              ACTIVE
            </span>

          </div>


          <!-- 03 -->

          <div class="system-row system-row-3">

            <div class="system-icon">
              03
            </div>

            <div class="system-text">

              <span>
                Safety Management
              </span>

              <strong>
                스마트 안전 관리
              </strong>

            </div>

            <span class="system-status">
              ONLINE
            </span>

          </div>


          <!-- 04 -->

          <div class="system-row system-row-4">

            <div class="system-icon">
              04
            </div>

            <div class="system-text">

              <span>
                Maintenance Management
              </span>

              <strong>
                유지보수 관리
              </strong>

            </div>

            <span class="system-status">
              READY
            </span>

          </div>


          <!-- PANEL BOTTOM -->

          <div class="panel-bottom panel-item panel-item-3">

            <div class="panel-bottom-item">

              <span>
                SYSTEM
              </span>

              <strong>
                STABLE
              </strong>

            </div>

            <span class="panel-bottom-line"></span>

            <div class="panel-bottom-item">

              <span>
                NETWORK
              </span>

              <strong>
                CONNECTED
              </strong>

            </div>

            <span class="panel-bottom-line"></span>

            <div class="panel-bottom-item">

              <span>
                SECURITY
              </span>

              <strong>
                ENABLED
              </strong>

            </div>

          </div>

        </div>

      </div>


      <!-- =================================================
           SCROLL
      ================================================== -->

      <button
        type="button"
        class="scroll-guide"
        aria-label="스마트 모빌리티 플랫폼 소개로 이동"
        @click="scrollToPlatform"
      >

        <span class="mouse">
          <span></span>
        </span>

        <span class="scroll-copy">

          <span>
            SMART MOBILITY PLATFORM
          </span>

          <strong>
            SCROLL
          </strong>

        </span>

        <svg viewBox="0 0 24 24">
          <path d="m6 9 6 6 6-6" />
        </svg>

      </button>


      <p class="hero-footer-copy">
        ENSONSOFT · INTEGRATED MOBILITY MANAGEMENT SYSTEM
      </p>

    </section>


    <!-- =====================================================
         PLATFORM SECTION
    ====================================================== -->

    <section
      ref="platformSection"
      class="platform-section"
    >

      <div class="platform-background-grid"></div>

      <div class="platform-container">

        <!-- SECTION HEADER -->

        <div class="section-heading">

          <div>

            <div class="section-eyebrow">
              <span></span>

              SMART MOBILITY PLATFORM
            </div>

            <h2>
              하나의 플랫폼으로 연결되는
              <strong>통합 모빌리티 관리</strong>
            </h2>

          </div>

          <p>
            현장부터 장비, 관제 이벤트와 유지보수까지
            스마트 모빌리티 운영에 필요한 정보를
            하나의 시스템에서 관리합니다.
          </p>

        </div>


        <!-- FEATURE GRID -->

        <div class="feature-grid">


          <!-- 01 -->

          <RouterLink
            to="/monitoring"
            class="feature-card"
          >

            <div class="feature-number">
              01
            </div>

            <div class="feature-icon">

              <svg viewBox="0 0 24 24">
                <path
                  d="M4 5h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2h-6v2h3v2H7v-2h3v-2H4a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm0 2v10h16V7H4Zm3 7 3-3 2 2 4-5 2 1.5-5.5 7-2.5-2.5-1.5 1.5L7 14Z"
                />
              </svg>

            </div>

            <span class="feature-en">
              INTEGRATED MONITORING
            </span>

            <h3>
              통합관제
            </h3>

            <p>
              차량번호 인식과 영상분석 이벤트를
              통합하여 실시간 관제 상황을 확인합니다.
            </p>

            <span class="feature-link">

              VIEW SYSTEM

              <svg viewBox="0 0 24 24">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>

            </span>

          </RouterLink>


          <!-- 02 -->

          <RouterLink
            to="/sites"
            class="feature-card"
          >

            <div class="feature-number">
              02
            </div>

            <div class="feature-icon">

              <svg viewBox="0 0 24 24">
                <path
                  d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7Zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5Z"
                />
              </svg>

            </div>

            <span class="feature-en">
              SITE MANAGEMENT
            </span>

            <h3>
              현장 관리
            </h3>

            <p>
              설치 현장과 운영 상태를 관리하고
              각 현장에 연결된 장비 정보를 확인합니다.
            </p>

            <span class="feature-link">

              VIEW SYSTEM

              <svg viewBox="0 0 24 24">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>

            </span>

          </RouterLink>


          <!-- 03 -->

          <RouterLink
            to="/equipments"
            class="feature-card"
          >

            <div class="feature-number">
              03
            </div>

            <div class="feature-icon">

              <svg viewBox="0 0 24 24">
                <path
                  d="M19.14 12.94a7.5 7.5 0 0 0 .05-.94 7.5 7.5 0 0 0-.05-.94l2.03-1.58-1.92-3.32-2.39.96a7.3 7.3 0 0 0-1.62-.94L14.88 3h-3.84l-.36 2.18c-.57.24-1.11.55-1.62.94l-2.39-.96-1.92 3.32 2.03 1.58a7.5 7.5 0 0 0-.05.94c0 .32.02.63.05.94l-2.03 1.58 1.92 3.32 2.39-.96c.5.39 1.05.7 1.62.94l.36 2.18h3.84l.36-2.18c.57-.24 1.11-.55 1.62-.94l2.39.96 1.92-3.32-2.03-1.58ZM13 15.5A3.5 3.5 0 1 1 13 8a3.5 3.5 0 0 1 0 7.5Z"
                />
              </svg>

            </div>

            <span class="feature-en">
              EQUIPMENT MANAGEMENT
            </span>

            <h3>
              장비 관리
            </h3>

            <p>
              카메라와 차량번호 인식 장비 등
              주요 관제 장비의 상태와 이력을 관리합니다.
            </p>

            <span class="feature-link">

              VIEW SYSTEM

              <svg viewBox="0 0 24 24">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>

            </span>

          </RouterLink>


          <!-- 04 -->

          <RouterLink
            to="/maintenance"
            class="feature-card"
          >

            <div class="feature-number">
              04
            </div>

            <div class="feature-icon">

              <svg viewBox="0 0 24 24">
                <path
                  d="m22.7 19-9.1-9.1a6 6 0 0 0-7.5-7.5l3.4 3.4-3.7 3.7-3.4-3.4a6 6 0 0 0 7.5 7.5l9.1 9.1L22.7 19Z"
                />
              </svg>

            </div>

            <span class="feature-en">
              MAINTENANCE
            </span>

            <h3>
              유지보수 관리
            </h3>

            <p>
              장애 발생부터 처리와 완료까지
              장비 유지보수 전 과정을 이력으로 관리합니다.
            </p>

            <span class="feature-link">

              VIEW SYSTEM

              <svg viewBox="0 0 24 24">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>

            </span>

          </RouterLink>

        </div>


        <!-- BOTTOM CTA -->

        <div class="platform-cta">

          <div>

            <span>
              ENSONSOFT SMART MOBILITY
            </span>

            <h3>
              통합 시스템을 시작하세요.
            </h3>

          </div>


          <RouterLink
            v-if="authStore.isLoggedIn"
            to="/dashboard"
            class="cta-button"
          >

            통합 대시보드

            <svg viewBox="0 0 24 24">
              <path d="M5 12h14M13 6l6 6-6 6" />
            </svg>

          </RouterLink>


          <RouterLink
            v-else
            to="/login"
            class="cta-button"
          >

            시스템 로그인

            <svg viewBox="0 0 24 24">
              <path d="M5 12h14M13 6l6 6-6 6" />
            </svg>

          </RouterLink>

        </div>

      </div>

    </section>

  </main>
</template>


<style scoped>

/* =========================================================
   GLOBAL
========================================================= */

.home-page {
  width: 100%;

  background: #06172c;

  color: #ffffff;

  overflow-x: hidden;
}


/* =========================================================
   BRAND OPENING
========================================================= */

.brand-opening {
  position: fixed;

  inset: 0;

  z-index: 99999;

  display: flex;
  align-items: center;
  justify-content: center;

  overflow: hidden;

  background: #06172c;
}


.opening-background {
  position: absolute;

  inset: -3%;

  background:
    url('../assets/intro-bg.png')
    center center / cover
    no-repeat;

  filter:
    saturate(0.8)
    brightness(0.68);

  transform:
    scale(1.09);

  animation:
    openingBackground
    2.8s
    cubic-bezier(.2, .8, .2, 1)
    forwards;
}


.opening-overlay {
  position: absolute;

  inset: 0;

  background:
    linear-gradient(
      90deg,
      rgba(2, 15, 34, 0.74),
      rgba(4, 24, 48, 0.54),
      rgba(2, 17, 35, 0.67)
    );
}


.opening-overlay::after {
  position: absolute;

  inset: 0;

  background:
    radial-gradient(
      circle at center,
      rgba(42, 139, 246, 0.09),
      transparent 55%
    );

  content: '';
}


.opening-grid {
  position: absolute;

  inset: 0;

  opacity: 0.14;

  background-image:
    linear-gradient(
      rgba(107, 183, 255, 0.08) 1px,
      transparent 1px
    ),
    linear-gradient(
      90deg,
      rgba(107, 183, 255, 0.08) 1px,
      transparent 1px
    );

  background-size:
    80px 80px;

  mask-image:
    linear-gradient(
      to bottom,
      transparent,
      black 35%,
      black 65%,
      transparent
    );
}


.opening-content {
  position: relative;

  z-index: 4;

  width:
    min(1180px, calc(100% - 100px));

  text-align: center;
}


.opening-brand {
  margin:
    0 0 34px;

  color:
    rgba(255, 255, 255, 0.96);

  font-size:
    clamp(34px, 3.1vw, 56px);

  font-weight: 900;

  letter-spacing:
    -1.8px;

  opacity: 0;

  transform:
    translateY(-20px);

  animation:
    openingBrandEnter
    0.7s
    cubic-bezier(.2, .8, .2, 1)
    0.1s
    forwards;
}


.opening-line {
  width: 0;
  height: 1px;

  margin:
    0 auto 37px;

  background:
    linear-gradient(
      90deg,
      transparent,
      rgba(103, 181, 255, 0.8),
      transparent
    );

  animation:
    openingLine
    0.8s
    ease
    0.25s
    forwards;
}


.opening-title {
  display: flex;

  flex-direction: column;

  align-items: center;

  margin: 0;

  font-size:
    clamp(42px, 4.8vw, 78px);

  font-weight: 900;

  letter-spacing:
    -3.6px;

  line-height: 1.13;
}


.opening-text {
  display: block;

  opacity: 0;

  transform:
    translateY(35px);

  animation:
    openingTextEnter
    0.65s
    cubic-bezier(.16, 1, .3, 1)
    forwards;
}


.opening-text-1 {
  animation-delay: 0.42s;
}


.opening-text-2 {
  margin-top: 6px;

  animation-delay: 0.63s;
}


.opening-highlight {
  position: relative;

  display: inline-flex;

  margin-top: 13px;

  padding:
    4px 21px 8px;

  overflow: hidden;

  opacity: 0;

  transform:
    translateY(35px);

  animation:
    openingTextEnter
    0.65s
    cubic-bezier(.16, 1, .3, 1)
    0.84s
    forwards;
}


.opening-highlight-bg {
  position: absolute;

  inset: 0;

  background:
    linear-gradient(
      90deg,
      #126fdc,
      #258df5
    );

  transform:
    scaleX(0);

  transform-origin:
    left center;

  animation:
    highlightOpen
    0.7s
    cubic-bezier(.16, 1, .3, 1)
    1s
    forwards;
}


.opening-highlight-text {
  position: relative;

  z-index: 2;
}


.opening-bottom {
  display: flex;
  align-items: center;
  justify-content: center;

  gap: 13px;

  margin-top: 45px;

  color:
    rgba(205, 225, 244, 0.47);

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 2.4px;

  opacity: 0;

  animation:
    openingBottomEnter
    0.6s
    ease
    1.3s
    forwards;
}


.opening-dot {
  width: 4px;
  height: 4px;

  background: #51b1ff;

  border-radius: 50%;

  box-shadow:
    0 0 12px
    rgba(81, 177, 255, 0.8);
}


.opening-progress {
  position: absolute;

  right: 0;
  bottom: 0;
  left: 0;

  z-index: 5;

  height: 3px;

  background:
    rgba(255, 255, 255, 0.06);
}


.opening-progress span {
  display: block;

  width: 0;
  height: 100%;

  background:
    linear-gradient(
      90deg,
      #1777e6,
      #61b7ff
    );

  box-shadow:
    0 0 15px
    rgba(50, 153, 255, 0.5);

  animation:
    openingProgress
    2.25s
    linear
    forwards;
}


.brand-opening.is-leaving {
  pointer-events: none;

  animation:
    openingExit
    0.65s
    cubic-bezier(.4, 0, .2, 1)
    forwards;
}


.brand-opening.is-leaving .opening-content {
  animation:
    openingContentExit
    0.55s
    ease
    forwards;
}


/* =========================================================
   MAIN INTRO
========================================================= */

.intro-page {
  position: relative;

  width: 100%;
  min-height: 100vh;

  overflow: hidden;

  background: #06172c;
}


/* =========================================================
   BACKGROUND
========================================================= */

.background-image {
  position: absolute;

  inset: 0;

  background:
    url('../assets/intro-bg.png')
    center center / cover
    no-repeat;

  transform:
    scale(1.04);

  animation:
    backgroundMove
    20s
    ease-in-out
    infinite
    alternate;
}


.background-overlay {
  position: absolute;

  inset: 0;

  z-index: 1;

  background:
    linear-gradient(
      90deg,
      rgba(2, 15, 34, 0.95) 0%,
      rgba(3, 22, 47, 0.86) 30%,
      rgba(4, 27, 55, 0.69) 59%,
      rgba(2, 18, 39, 0.56) 100%
    );

  pointer-events: none;
}


.background-overlay::after {
  position: absolute;

  inset: 0;

  background:
    linear-gradient(
      180deg,
      rgba(2, 15, 31, 0.15),
      rgba(2, 15, 31, 0.05) 60%,
      rgba(2, 15, 31, 0.78)
    );

  content: '';
}


.background-glow {
  position: absolute;

  top: 15%;
  left: 28%;

  z-index: 2;

  width: 560px;
  height: 560px;

  background:
    radial-gradient(
      circle,
      rgba(34, 132, 255, 0.14),
      transparent 68%
    );

  filter:
    blur(28px);

  pointer-events: none;
}


.panel-background-glow {
  position: absolute;

  top: 18%;
  right: -2%;

  z-index: 2;

  width: 720px;
  height: 720px;

  background:
    radial-gradient(
      circle,
      rgba(31, 131, 255, 0.15),
      transparent 66%
    );

  filter:
    blur(40px);

  pointer-events: none;
}


/* =========================================================
   HEADER
========================================================= */

.intro-header {
  position: relative;

  z-index: 10;

  display: flex;
  align-items: center;
  justify-content: space-between;

  width:
    calc(100% - 58px);

  height: 104px;

  margin: 0 auto;

  border-bottom:
    1px solid rgba(255, 255, 255, 0.12);
}


.brand {
  display: flex;
  align-items: center;

  text-decoration: none;
}


.brand-logo {
  display: block;

  width: 170px;
  height: auto;

  object-fit: contain;
}


.header-actions {
  display: flex;
  align-items: center;

  gap: 13px;
}


.header-link {
  padding: 11px 15px;

  color:
    rgba(255, 255, 255, 0.8);

  font-size: 13px;
  font-weight: 700;

  text-decoration: none;

  transition:
    0.2s ease;
}


.header-link:hover {
  color: #ffffff;
}


.header-button {
  display: flex;
  align-items: center;
  justify-content: center;

  min-width: 92px;

  padding:
    11px 18px;

  background:
    rgba(255, 255, 255, 0.1);

  border:
    1px solid rgba(255, 255, 255, 0.3);

  border-radius: 7px;

  backdrop-filter:
    blur(12px);

  color: #ffffff;

  font-size: 13px;
  font-weight: 800;

  text-decoration: none;

  transition:
    0.25s ease;
}


.header-button:hover {
  background: #ffffff;

  color: #0c2c52;

  transform:
    translateY(-1px);
}


/* =========================================================
   USER
========================================================= */

.user-status {
  display: flex;
  align-items: center;

  gap: 8px;

  padding-right: 10px;

  color:
    rgba(255, 255, 255, 0.9);

  font-size: 13px;
  font-weight: 700;
}


.online-dot {
  width: 7px;
  height: 7px;

  background: #31d98b;

  border-radius: 50%;

  box-shadow:
    0 0 0 4px
    rgba(49, 217, 139, 0.13);
}


.user-role {
  padding:
    4px 7px;

  background:
    rgba(255, 255, 255, 0.1);

  border-radius: 5px;

  color:
    rgba(255, 255, 255, 0.65);

  font-size: 10px;
}


/* =========================================================
   HERO
========================================================= */

.hero {
  position: relative;

  z-index: 5;

  display: grid;

  grid-template-columns:
    minmax(500px, 0.88fr)
    minmax(600px, 1.12fr);

  align-items: center;

  gap: 58px;

  width:
    calc(100% - 58px);

  min-height:
    calc(100vh - 160px);

  margin:
    0 auto;

  padding-bottom: 50px;

  box-sizing: border-box;
}


.hero-content {
  width: 100%;

  max-width: 720px;
}


/* =========================================================
   EYEBROW
========================================================= */

.eyebrow {
  display: flex;
  align-items: center;

  gap: 13px;

  margin-bottom: 25px;

  color: #75b9ff;

  font-size: 12px;
  font-weight: 900;

  letter-spacing: 3px;
}


.eyebrow-line {
  width: 37px;
  height: 2px;

  background: #3998ff;
}


/* =========================================================
   TITLE
========================================================= */

.hero-title {
  margin: 0;

  color: #ffffff;

  font-size:
    clamp(48px, 4.3vw, 77px);

  font-weight: 900;

  letter-spacing:
    -4px;

  line-height: 1.08;
}


.title-line {
  display: block;
}


.title-line.highlight {
  position: relative;

  display: inline-block;
}


.title-line.highlight::after {
  position: absolute;

  right: -8px;
  bottom: 4px;
  left: -4px;

  z-index: -1;

  height: 14px;

  background:
    rgba(37, 137, 255, 0.48);

  content: '';

  transform:
    scaleX(0);

  transform-origin:
    left;

  animation:
    lineDraw
    0.7s
    ease
    3.55s
    forwards;
}


/* =========================================================
   DESCRIPTION
========================================================= */

.hero-description {
  margin-top: 31px;
}


.english-copy {
  margin:
    0 0 10px;

  color:
    rgba(255, 255, 255, 0.9);

  font-size: 18px;
}


.english-copy strong {
  color: #6bb6ff;

  font-weight: 800;
}


.korean-copy {
  margin: 0;

  color:
    rgba(214, 226, 240, 0.68);

  font-size: 14px;

  line-height: 1.8;
}


/* =========================================================
   BUTTONS
========================================================= */

.hero-buttons {
  display: flex;

  gap: 11px;

  margin-top: 37px;
}


.primary-button,
.secondary-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  height: 52px;

  box-sizing: border-box;

  border-radius: 7px;

  font-size: 14px;
  font-weight: 800;

  text-decoration: none;

  transition:
    0.25s ease;
}


.primary-button {
  gap: 26px;

  min-width: 190px;

  padding:
    0 21px;

  background: #1671df;

  border:
    1px solid #1671df;

  box-shadow:
    0 10px 30px
    rgba(13, 101, 211, 0.25);

  color: #ffffff;
}


.primary-button svg,
.feature-link svg,
.cta-button svg {
  width: 18px;
  height: 18px;

  fill: none;

  stroke: currentColor;

  stroke-linecap: round;
  stroke-linejoin: round;

  stroke-width: 1.7;

  transition:
    0.2s ease;
}


.primary-button:hover {
  background: #2384f2;

  border-color: #2384f2;

  transform:
    translateY(-2px);
}


.primary-button:hover svg {
  transform:
    translateX(4px);
}


.secondary-button {
  min-width: 115px;

  padding:
    0 21px;

  background:
    rgba(255, 255, 255, 0.05);

  border:
    1px solid rgba(255, 255, 255, 0.28);

  backdrop-filter:
    blur(8px);

  color: #ffffff;
}


.secondary-button:hover {
  background:
    rgba(255, 255, 255, 0.12);

  border-color:
    rgba(255, 255, 255, 0.5);

  transform:
    translateY(-2px);
}


/* =========================================================
   LARGE SYSTEM PANEL
========================================================= */

.system-panel {
  position: relative;

  width: 100%;

  max-width: 700px;

  justify-self: end;

  padding:
    42px 44px 32px;

  box-sizing: border-box;

  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      rgba(15, 53, 91, 0.94),
      rgba(6, 29, 58, 0.86)
    );

  border:
    1px solid rgba(115, 186, 255, 0.3);

  border-radius: 25px;

  backdrop-filter:
    blur(25px);

  box-shadow:
    0 40px 100px
    rgba(0, 0, 0, 0.35),

    0 0 90px
    rgba(32, 128, 255, 0.1),

    inset 0 1px 0
    rgba(255, 255, 255, 0.09);

  transition:
    transform 0.35s ease,
    border-color 0.35s ease,
    box-shadow 0.35s ease;
}


.system-panel::before {
  position: absolute;

  top: -180px;
  right: -120px;

  width: 450px;
  height: 450px;

  background:
    radial-gradient(
      circle,
      rgba(49, 149, 255, 0.22),
      transparent 68%
    );

  content: '';

  pointer-events: none;

  animation:
    panelGlow
    5s
    ease-in-out
    infinite
    alternate;
}


.system-panel::after {
  position: absolute;

  top: 0;
  left: 44px;

  width: 115px;
  height: 2px;

  background:
    linear-gradient(
      90deg,
      #4ca9ff,
      rgba(76, 169, 255, 0)
    );

  content: '';
}


.system-panel:hover {
  transform:
    translateY(-7px);

  border-color:
    rgba(120, 192, 255, 0.45);

  box-shadow:
    0 52px 125px
    rgba(0, 0, 0, 0.42),

    0 0 110px
    rgba(32, 128, 255, 0.16);
}


.panel-light {
  position: absolute;

  top: -120px;
  right: -70px;

  width: 300px;
  height: 300px;

  border:
    1px solid rgba(74, 166, 255, 0.07);

  border-radius: 50%;

  box-shadow:
    0 0 100px
    rgba(40, 143, 255, 0.08);

  pointer-events: none;
}


.panel-shine {
  position: absolute;

  top: -50%;
  left: -65%;

  width: 45%;
  height: 200%;

  background:
    linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.045),
      transparent
    );

  transform:
    rotate(20deg);

  animation:
    panelShine
    7s
    ease-in-out
    4s
    infinite;

  pointer-events: none;
}


/* =========================================================
   PANEL HEADER
========================================================= */

.panel-header {
  position: relative;

  z-index: 2;

  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}


.panel-title-area {
  display: flex;

  flex-direction: column;

  gap: 5px;
}


.panel-small {
  color: #6db9ff;

  font-size: 11px;
  font-weight: 900;

  letter-spacing: 2.5px;
}


.panel-header strong {
  margin-top: 2px;

  color: #ffffff;

  font-size: 27px;
  font-weight: 900;

  letter-spacing:
    -0.8px;
}


.panel-subtitle {
  margin-top: 4px;

  color:
    rgba(204, 222, 241, 0.42);

  font-size: 10px;

  letter-spacing: 0.5px;
}


.live {
  display: flex;
  align-items: center;

  gap: 8px;

  padding:
    8px 11px;

  background:
    rgba(49, 217, 139, 0.07);

  border:
    1px solid rgba(49, 217, 139, 0.14);

  border-radius: 30px;

  color: #72e7af;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 1.1px;
}


.live-dot {
  width: 8px;
  height: 8px;

  background: #35dd91;

  border-radius: 50%;

  animation:
    livePulse
    1.7s
    ease-in-out
    infinite;
}


.panel-divider {
  position: relative;

  z-index: 2;

  height: 1px;

  margin:
    30px 0 8px;

  background:
    linear-gradient(
      90deg,
      rgba(255, 255, 255, 0.17),
      rgba(255, 255, 255, 0.04)
    );
}


/* =========================================================
   SYSTEM ROWS
========================================================= */

.system-row {
  position: relative;

  z-index: 2;

  display: grid;

  grid-template-columns:
    55px minmax(0, 1fr) auto;

  align-items: center;

  gap: 19px;

  padding:
    20px 0;

  border-bottom:
    1px solid rgba(255, 255, 255, 0.075);

  transition:
    padding 0.25s ease,
    background 0.25s ease;
}


.system-row:hover {
  padding-right: 13px;
  padding-left: 13px;

  background:
    linear-gradient(
      90deg,
      rgba(45, 139, 240, 0.1),
      transparent
    );
}


.system-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 52px;
  height: 52px;

  background:
    linear-gradient(
      145deg,
      rgba(45, 139, 240, 0.19),
      rgba(45, 139, 240, 0.07)
    );

  border:
    1px solid rgba(83, 169, 255, 0.29);

  border-radius: 12px;

  color: #74bdff;

  font-size: 12px;
  font-weight: 900;

  transition:
    0.25s ease;
}


.system-row:hover .system-icon {
  background:
    rgba(45, 139, 240, 0.25);

  border-color:
    rgba(100, 181, 255, 0.55);

  transform:
    translateY(-2px)
    scale(1.04);
}


.system-text {
  display: flex;

  flex-direction: column;

  gap: 5px;
}


.system-text span {
  color:
    rgba(210, 226, 243, 0.43);

  font-size: 10px;
}


.system-text strong {
  color:
    rgba(255, 255, 255, 0.95);

  font-size: 14px;
  font-weight: 800;
}


.system-status {
  position: relative;

  padding-left: 12px;

  color: #62e6a5;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 1px;
}


.system-status::before {
  position: absolute;

  top: 50%;
  left: 0;

  width: 5px;
  height: 5px;

  background: #43dc96;

  border-radius: 50%;

  content: '';

  transform:
    translateY(-50%);

  box-shadow:
    0 0 9px
    rgba(67, 220, 150, 0.65);
}


/* =========================================================
   PANEL BOTTOM
========================================================= */

.panel-bottom {
  position: relative;

  z-index: 2;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 15px;

  margin-top: 23px;

  padding:
    18px 20px;

  background:
    rgba(1, 18, 38, 0.28);

  border:
    1px solid rgba(255, 255, 255, 0.065);

  border-radius: 12px;
}


.panel-bottom-item {
  display: flex;

  flex-direction: column;

  gap: 4px;
}


.panel-bottom-item span {
  color:
    rgba(207, 224, 242, 0.35);

  font-size: 7px;
  font-weight: 800;

  letter-spacing: 1px;
}


.panel-bottom-item strong {
  color:
    rgba(255, 255, 255, 0.78);

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 0.6px;
}


.panel-bottom-line {
  width: 1px;
  height: 27px;

  background:
    rgba(255, 255, 255, 0.08);
}


/* =========================================================
   SCROLL GUIDE
========================================================= */

.scroll-guide {
  position: absolute;

  bottom: 24px;
  left: 29px;

  z-index: 10;

  display: flex;
  align-items: center;

  gap: 12px;

  padding: 0;

  background: none;

  border: 0;

  color:
    rgba(255, 255, 255, 0.48);

  cursor: pointer;

  font-family: inherit;

  text-align: left;

  transition:
    0.25s ease;
}


.scroll-guide:hover {
  color: #ffffff;

  transform:
    translateY(-2px);
}


.mouse {
  position: relative;

  display: block;

  width: 18px;
  height: 28px;

  border:
    1px solid rgba(255, 255, 255, 0.4);

  border-radius: 11px;
}


.mouse span {
  position: absolute;

  top: 6px;
  left: 7px;

  width: 2px;
  height: 6px;

  background: #75b9ff;

  border-radius: 3px;

  animation:
    mouseMove
    1.6s
    infinite;
}


.scroll-copy {
  display: flex;

  flex-direction: column;

  gap: 2px;
}


.scroll-copy > span {
  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.8px;
}


.scroll-copy strong {
  color: #6eb7ff;

  font-size: 7px;
  font-weight: 900;

  letter-spacing: 1.3px;
}


.scroll-guide > svg {
  width: 15px;
  height: 15px;

  margin-left: 3px;

  fill: none;

  stroke: currentColor;

  stroke-width: 1.8;

  animation:
    scrollArrow
    1.5s
    ease-in-out
    infinite;
}


.hero-footer-copy {
  position: absolute;

  right: 29px;
  bottom: 27px;

  z-index: 8;

  margin: 0;

  color:
    rgba(255, 255, 255, 0.28);

  font-size: 8px;
  font-weight: 800;

  letter-spacing: 1.5px;
}


/* =========================================================
   PLATFORM SECTION
========================================================= */

.platform-section {
  position: relative;

  min-height: 100vh;

  padding:
    115px 0 100px;

  box-sizing: border-box;

  overflow: hidden;

  background:
    linear-gradient(
      180deg,
      #06172c 0%,
      #071c34 45%,
      #06172c 100%
    );

  scroll-margin-top: 0;
}


.platform-section::before {
  position: absolute;

  top: -300px;
  left: 50%;

  width: 900px;
  height: 600px;

  background:
    radial-gradient(
      circle,
      rgba(27, 120, 230, 0.14),
      transparent 68%
    );

  content: '';

  transform:
    translateX(-50%);

  pointer-events: none;
}


.platform-background-grid {
  position: absolute;

  inset: 0;

  opacity: 0.17;

  background-image:
    linear-gradient(
      rgba(100, 171, 240, 0.05) 1px,
      transparent 1px
    ),
    linear-gradient(
      90deg,
      rgba(100, 171, 240, 0.05) 1px,
      transparent 1px
    );

  background-size:
    80px 80px;

  mask-image:
    linear-gradient(
      to bottom,
      transparent,
      black 15%,
      black 85%,
      transparent
    );

  pointer-events: none;
}


.platform-container {
  position: relative;

  z-index: 2;

  width:
    min(1500px, calc(100% - 100px));

  margin:
    0 auto;
}


/* =========================================================
   PLATFORM HEADING
========================================================= */

.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;

  gap: 60px;

  margin-bottom: 65px;
}


.section-heading > div {
  max-width: 760px;
}


.section-eyebrow {
  display: flex;
  align-items: center;

  gap: 13px;

  margin-bottom: 20px;

  color: #69b5ff;

  font-size: 10px;
  font-weight: 900;

  letter-spacing: 2.5px;
}


.section-eyebrow span {
  width: 35px;
  height: 2px;

  background: #2f94f6;
}


.section-heading h2 {
  margin: 0;

  color: #ffffff;

  font-size:
    clamp(37px, 3.4vw, 57px);

  font-weight: 900;

  letter-spacing:
    -2.8px;

  line-height: 1.15;
}


.section-heading h2 strong {
  display: block;

  color: #6bb6ff;
}


.section-heading > p {
  max-width: 430px;

  margin:
    0 0 7px;

  color:
    rgba(208, 224, 241, 0.52);

  font-size: 14px;

  line-height: 1.9;
}


/* =========================================================
   FEATURE GRID
========================================================= */

.feature-grid {
  display: grid;

  grid-template-columns:
    repeat(4, 1fr);

  gap: 15px;
}


.feature-card {
  position: relative;

  display: flex;

  flex-direction: column;

  min-height: 380px;

  padding:
    31px 29px;

  box-sizing: border-box;

  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      rgba(17, 51, 86, 0.72),
      rgba(7, 29, 55, 0.72)
    );

  border:
    1px solid rgba(255, 255, 255, 0.08);

  border-radius: 17px;

  color: #ffffff;

  text-decoration: none;

  transition:
    transform 0.35s ease,
    border-color 0.35s ease,
    background 0.35s ease,
    box-shadow 0.35s ease;
}


.feature-card::before {
  position: absolute;

  top: 0;
  left: 0;

  width: 100%;
  height: 2px;

  background:
    linear-gradient(
      90deg,
      #268cf1,
      transparent
    );

  content: '';

  opacity: 0;

  transition:
    0.35s ease;
}


.feature-card:hover {
  background:
    linear-gradient(
      145deg,
      rgba(20, 65, 110, 0.86),
      rgba(8, 35, 66, 0.82)
    );

  border-color:
    rgba(81, 164, 246, 0.32);

  box-shadow:
    0 25px 70px
    rgba(0, 0, 0, 0.2);

  transform:
    translateY(-9px);
}


.feature-card:hover::before {
  opacity: 1;
}


.feature-number {
  position: absolute;

  top: 28px;
  right: 28px;

  color:
    rgba(116, 187, 255, 0.24);

  font-size: 38px;
  font-weight: 900;

  letter-spacing:
    -2px;
}


.feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 55px;
  height: 55px;

  margin-bottom: 37px;

  background:
    rgba(44, 137, 237, 0.12);

  border:
    1px solid rgba(79, 162, 249, 0.2);

  border-radius: 13px;

  color: #67b4ff;
}


.feature-icon svg {
  width: 25px;
  height: 25px;

  fill: currentColor;
}


.feature-en {
  margin-bottom: 9px;

  color: #5da9f4;

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.7px;
}


.feature-card h3 {
  margin:
    0 0 17px;

  font-size: 22px;
  font-weight: 900;

  letter-spacing:
    -0.8px;
}


.feature-card p {
  margin: 0;

  color:
    rgba(210, 225, 241, 0.49);

  font-size: 13px;

  line-height: 1.85;
}


.feature-link {
  display: flex;
  align-items: center;

  gap: 11px;

  margin-top: auto;
  padding-top: 30px;

  color:
    rgba(255, 255, 255, 0.58);

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.5px;

  transition:
    0.25s ease;
}


.feature-link svg {
  width: 15px;
  height: 15px;
}


.feature-card:hover .feature-link {
  gap: 17px;

  color: #69b5ff;
}


/* =========================================================
   PLATFORM CTA
========================================================= */

.platform-cta {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 30px;

  margin-top: 30px;

  padding:
    31px 36px;

  background:
    linear-gradient(
      90deg,
      rgba(20, 76, 134, 0.45),
      rgba(9, 39, 72, 0.42)
    );

  border:
    1px solid rgba(86, 165, 244, 0.14);

  border-radius: 16px;
}


.platform-cta span {
  display: block;

  margin-bottom: 6px;

  color: #5eacf7;

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.8px;
}


.platform-cta h3 {
  margin: 0;

  color: #ffffff;

  font-size: 21px;
  font-weight: 900;
}


.cta-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  gap: 25px;

  min-width: 180px;
  height: 50px;

  padding:
    0 20px;

  background: #1979e7;

  border:
    1px solid #1979e7;

  border-radius: 7px;

  color: #ffffff;

  font-size: 13px;
  font-weight: 900;

  text-decoration: none;

  transition:
    0.25s ease;
}


.cta-button:hover {
  background: #2589f5;

  transform:
    translateY(-2px);
}


.cta-button:hover svg {
  transform:
    translateX(4px);
}


/* =========================================================
   MAIN HERO ENTER
========================================================= */

.animation-1,
.animation-2,
.animation-3,
.animation-4,
.animation-5,
.animation-6 {
  opacity: 0;

  transform:
    translateY(25px);

  animation:
    textEnter
    0.75s
    cubic-bezier(.2, .8, .2, 1)
    forwards;
}


/*
 * 오프닝이 끝나갈 때쯤
 * 메인 화면이 등장
 */

.animation-1 {
  animation-delay: 2.25s;
}

.animation-2 {
  animation-delay: 2.4s;
}

.animation-3 {
  animation-delay: 2.55s;
}

.animation-4 {
  animation-delay: 2.7s;
}

.animation-5 {
  animation-delay: 2.85s;
}

.animation-6 {
  animation-delay: 3s;
}


.animation-panel {
  opacity: 0;

  filter:
    blur(12px);

  transform:
    translateX(130px)
    translateY(20px)
    scale(0.9);

  animation:
    bigPanelEnter
    1.2s
    cubic-bezier(.16, 1, .3, 1)
    2.35s
    forwards;
}


.system-row {
  opacity: 0;

  transform:
    translateX(35px);
}


.system-row-1 {
  animation:
    rowEnter
    0.65s
    ease
    2.9s
    forwards;
}


.system-row-2 {
  animation:
    rowEnter
    0.65s
    ease
    3.05s
    forwards;
}


.system-row-3 {
  animation:
    rowEnter
    0.65s
    ease
    3.2s
    forwards;
}


.system-row-4 {
  animation:
    rowEnter
    0.65s
    ease
    3.35s
    forwards;
}


.panel-item {
  opacity: 0;

  transform:
    translateY(12px);

  animation:
    panelItemEnter
    0.6s
    ease
    forwards;
}


.panel-item-1 {
  animation-delay: 2.75s;
}


.panel-item-2 {
  animation-delay: 2.85s;
}


.panel-item-3 {
  animation-delay: 3.55s;
}


/* =========================================================
   KEYFRAMES
========================================================= */

@keyframes openingBackground {
  from {
    transform:
      scale(1.09);

    filter:
      saturate(0.8)
      brightness(0.58)
      blur(5px);
  }

  to {
    transform:
      scale(1.02);

    filter:
      saturate(0.9)
      brightness(0.72)
      blur(0);
  }
}


@keyframes openingBrandEnter {
  to {
    opacity: 1;

    transform:
      translateY(0);
  }
}


@keyframes openingLine {
  to {
    width: 130px;
  }
}


@keyframes openingTextEnter {
  to {
    opacity: 1;

    transform:
      translateY(0);
  }
}


@keyframes highlightOpen {
  to {
    transform:
      scaleX(1);
  }
}


@keyframes openingBottomEnter {
  to {
    opacity: 1;
  }
}


@keyframes openingProgress {
  to {
    width: 100%;
  }
}


@keyframes openingExit {
  from {
    opacity: 1;
  }

  to {
    opacity: 0;

    visibility: hidden;
  }
}


@keyframes openingContentExit {
  to {
    opacity: 0;

    filter:
      blur(5px);

    transform:
      scale(1.04);
  }
}


@keyframes textEnter {
  to {
    opacity: 1;

    transform:
      translateY(0);
  }
}


@keyframes bigPanelEnter {
  0% {
    opacity: 0;

    filter:
      blur(12px);

    transform:
      translateX(130px)
      translateY(20px)
      scale(0.9);
  }

  68% {
    opacity: 1;

    filter:
      blur(0);

    transform:
      translateX(-7px)
      translateY(0)
      scale(1.01);
  }

  100% {
    opacity: 1;

    filter:
      blur(0);

    transform:
      translateX(0)
      translateY(0)
      scale(1);
  }
}


@keyframes rowEnter {
  to {
    opacity: 1;

    transform:
      translateX(0);
  }
}


@keyframes panelItemEnter {
  to {
    opacity: 1;

    transform:
      translateY(0);
  }
}


@keyframes backgroundMove {
  from {
    transform:
      scale(1.04);
  }

  to {
    transform:
      scale(1.08);
  }
}


@keyframes lineDraw {
  to {
    transform:
      scaleX(1);
  }
}


@keyframes livePulse {
  0%,
  100% {
    opacity: 1;

    box-shadow:
      0 0 0 4px
      rgba(53, 221, 145, 0.12),

      0 0 13px
      rgba(53, 221, 145, 0.65);
  }

  50% {
    opacity: 0.55;

    box-shadow:
      0 0 0 8px
      rgba(53, 221, 145, 0.03),

      0 0 25px
      rgba(53, 221, 145, 0.85);
  }
}


@keyframes panelGlow {
  from {
    opacity: 0.45;

    transform:
      scale(0.92);
  }

  to {
    opacity: 1;

    transform:
      scale(1.12);
  }
}


@keyframes panelShine {
  0%,
  70% {
    left: -65%;
  }

  100% {
    left: 140%;
  }
}


@keyframes mouseMove {
  0% {
    opacity: 0;

    transform:
      translateY(0);
  }

  40% {
    opacity: 1;
  }

  100% {
    opacity: 0;

    transform:
      translateY(8px);
  }
}


@keyframes scrollArrow {
  0%,
  100% {
    transform:
      translateY(0);
  }

  50% {
    transform:
      translateY(4px);
  }
}


/* =========================================================
   RESPONSIVE
========================================================= */

@media (max-width: 1450px) {

  .hero {
    grid-template-columns:
      minmax(470px, 0.9fr)
      minmax(540px, 1.1fr);

    gap: 40px;
  }

  .system-panel {
    padding:
      35px 36px 28px;
  }

  .system-row {
    padding:
      17px 0;
  }

  .feature-grid {
    grid-template-columns:
      repeat(2, 1fr);
  }

}


@media (max-width: 1120px) {

  .intro-header,
  .hero {
    width:
      calc(100% - 60px);
  }

  .hero {
    grid-template-columns:
      1fr;

    padding:
      80px 0 120px;
  }

  .hero-content {
    max-width: 800px;
  }

  .system-panel {
    width: 100%;

    max-width: none;

    justify-self: stretch;
  }

  .intro-page {
    min-height: auto;
  }

  .scroll-guide {
    bottom: 35px;
  }

  .hero-footer-copy {
    bottom: 38px;
  }

  .section-heading {
    align-items: flex-start;

    flex-direction: column;
  }

}


@media (max-width: 760px) {

  .opening-content {
    width:
      calc(100% - 36px);
  }

  .opening-brand {
    font-size: 31px;
  }

  .opening-title {
    font-size:
      clamp(36px, 10vw, 54px);

    letter-spacing:
      -2.2px;
  }

  .opening-bottom {
    display: none;
  }


  .intro-header {
    width:
      calc(100% - 36px);

    height: 82px;
  }

  .brand-logo {
    width: 135px;
  }

  .header-actions .user-status {
    display: none;
  }


  .hero {
    width:
      calc(100% - 36px);

    padding:
      60px 0 130px;
  }

  .hero-title {
    font-size:
      clamp(40px, 11vw, 58px);

    letter-spacing:
      -2.5px;
  }

  .english-copy {
    font-size: 15px;
  }

  .hero-buttons {
    flex-direction: column;

    max-width: 280px;
  }

  .primary-button,
  .secondary-button {
    width: 100%;
  }


  .system-panel {
    padding:
      27px 23px 23px;

    border-radius: 18px;
  }

  .panel-header strong {
    font-size: 22px;
  }

  .system-row {
    grid-template-columns:
      46px minmax(0, 1fr);

    gap: 13px;
  }

  .system-icon {
    width: 43px;
    height: 43px;
  }

  .system-status {
    display: none;
  }

  .panel-bottom {
    gap: 8px;

    padding:
      15px;
  }


  .hero-footer-copy {
    display: none;
  }


  .platform-section {
    padding:
      85px 0 70px;
  }

  .platform-container {
    width:
      calc(100% - 36px);
  }

  .section-heading {
    gap: 25px;

    margin-bottom: 40px;
  }

  .section-heading h2 {
    font-size: 37px;

    letter-spacing:
      -2px;
  }

  .feature-grid {
    grid-template-columns:
      1fr;
  }

  .feature-card {
    min-height: 330px;
  }

  .platform-cta {
    align-items: flex-start;

    flex-direction: column;
  }

  .cta-button {
    width: 100%;

    box-sizing: border-box;
  }

}


@media (prefers-reduced-motion: reduce) {

  *,
  *::before,
  *::after {
    scroll-behavior: auto !important;

    animation-duration:
      0.01ms !important;

    animation-iteration-count:
      1 !important;
  }

}

</style>
