<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const roleLabel = computed(() => {
  if (authStore.role === 'ADMIN') return '관리자'
  if (authStore.role === 'USER') return '일반 사용자'
  return authStore.role || ''
})
</script>

<template>
  <main class="intro-page">

    <!-- BACKGROUND -->
    <div class="background-image"></div>
    <div class="background-overlay"></div>
    <div class="background-glow"></div>
    <div class="panel-background-glow"></div>

    <!-- ========================================
         HEADER
    ========================================= -->
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


    <!-- ========================================
         HERO
    ========================================= -->
    <section class="hero">

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
            스마트 모빌리티 기술을 통해
            더 안전하고 편리한 교통 환경을 만들어갑니다.
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


      <!-- ========================================
           RIGHT SYSTEM PANEL
      ========================================= -->
      <div class="system-panel animation-panel">

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


        <!-- BOTTOM STATUS -->
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

    </section>


    <!-- ========================================
         FOOTER
    ========================================= -->
    <footer class="intro-footer">

      <div class="scroll-guide">

        <span class="mouse">
          <span></span>
        </span>

        <span>
          SMART MOBILITY PLATFORM
        </span>

      </div>

      <p>
        ENSONSOFT · INTEGRATED MOBILITY MANAGEMENT SYSTEM
      </p>

    </footer>

  </main>
</template>


<style scoped>

/* =========================================================
   PAGE
========================================================= */

.intro-page {
  position: relative;

  width: 100%;
  min-height: 100vh;

  overflow: hidden;

  background: #06172c;

  color: #ffffff;
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

  opacity: 0;

  transform: scale(1.12);

  animation:
    backgroundEnter 2.2s
    cubic-bezier(.2, .8, .2, 1)
    forwards,

    backgroundMove 20s
    ease-in-out
    2.2s
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
      rgba(3, 22, 47, 0.86) 31%,
      rgba(4, 27, 55, 0.69) 59%,
      rgba(2, 18, 39, 0.58) 100%
    );

  opacity: 0;

  animation:
    overlayEnter
    2.3s
    ease
    0.25s
    forwards;

  pointer-events: none;
}


.background-overlay::after {
  position: absolute;
  inset: 0;

  background:
    linear-gradient(
      180deg,
      rgba(2, 15, 31, 0.18),
      rgba(2, 15, 31, 0.08) 60%,
      rgba(2, 15, 31, 0.78)
    );

  content: '';
}


.background-glow {
  position: absolute;

  top: 16%;
  left: 29%;

  z-index: 2;

  width: 560px;
  height: 560px;

  background:
    radial-gradient(
      circle,
      rgba(34, 132, 255, 0.14),
      transparent 68%
    );

  filter: blur(28px);

  pointer-events: none;

  opacity: 0;

  animation:
    glowEnter
    2.8s
    ease
    1s
    forwards;
}


.panel-background-glow {
  position: absolute;

  top: 24%;
  right: 2%;

  z-index: 2;

  width: 620px;
  height: 620px;

  background:
    radial-gradient(
      circle,
      rgba(31, 131, 255, 0.12),
      transparent 66%
    );

  filter: blur(35px);

  opacity: 0;

  animation:
    rightGlowEnter
    2s
    ease
    1.2s
    forwards;

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

  width: calc(100% - 120px);
  height: 100px;

  margin: 0 auto;

  border-bottom:
    1px solid rgba(255, 255, 255, 0.12);

  animation:
    headerEnter
    0.8s
    ease
    both;
}


.brand {
  display: flex;
  align-items: center;

  text-decoration: none;
}


.brand-logo {
  display: block;

  width: 168px;
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
    color 0.2s ease;
}


.header-link:hover {
  color: #ffffff;
}


.header-button {
  display: flex;
  align-items: center;
  justify-content: center;

  min-width: 92px;

  padding: 11px 18px;

  background:
    rgba(255, 255, 255, 0.1);

  border:
    1px solid rgba(255, 255, 255, 0.3);

  border-radius: 7px;

  backdrop-filter: blur(12px);

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
  padding: 4px 7px;

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

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 55px;

  width: calc(100% - 120px);

  min-height:
    calc(100vh - 190px);

  margin: 0 auto;
}


.hero-content {
  flex: 1;

  width: auto;

  max-width: 760px;
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
    clamp(45px, 4.2vw, 74px);

  font-weight: 900;

  letter-spacing: -3.7px;

  line-height: 1.12;
}


.title-line {
  display: block;
}


.title-line.highlight {
  position: relative;

  display: inline-block;

  color: #ffffff;
}


.title-line.highlight::after {
  position: absolute;

  right: -8px;
  bottom: 5px;
  left: -4px;

  z-index: -1;

  height: 13px;

  background:
    rgba(37, 137, 255, 0.45);

  content: '';

  transform: scaleX(0);

  transform-origin: left;

  animation:
    lineDraw
    0.7s
    ease
    1.45s
    forwards;
}


/* =========================================================
   DESCRIPTION
========================================================= */

.hero-description {
  margin-top: 32px;
}


.english-copy {
  margin:
    0 0 10px;

  color:
    rgba(255, 255, 255, 0.9);

  font-size: 18px;
  font-weight: 400;

  letter-spacing: 0.2px;
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

  padding: 0 21px;

  background: #1671df;

  border:
    1px solid #1671df;

  box-shadow:
    0 10px 30px
    rgba(13, 101, 211, 0.25);

  color: #ffffff;
}


.primary-button svg {
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

  padding: 0 21px;

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
   BIG SYSTEM PANEL
========================================================= */

.system-panel {
  position: relative;

  flex: 0 0 500px;

  width: 500px;

  padding:
    38px 40px 30px;

  box-sizing: border-box;

  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      rgba(15, 53, 91, 0.91),
      rgba(6, 29, 58, 0.82)
    );

  border:
    1px solid rgba(115, 186, 255, 0.28);

  border-radius: 25px;

  backdrop-filter:
    blur(25px);

  box-shadow:
    0 40px 100px
    rgba(0, 0, 0, 0.35),

    0 0 85px
    rgba(32, 128, 255, 0.09),

    inset 0 1px 0
    rgba(255, 255, 255, 0.09);

  transition:
    transform 0.35s ease,
    border-color 0.35s ease,
    box-shadow 0.35s ease;
}


.system-panel::before {
  position: absolute;

  top: -170px;
  right: -120px;

  width: 420px;
  height: 420px;

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
  left: 40px;

  width: 105px;
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
    rgba(120, 192, 255, 0.42);

  box-shadow:
    0 50px 120px
    rgba(0, 0, 0, 0.42),

    0 0 105px
    rgba(32, 128, 255, 0.15),

    inset 0 1px 0
    rgba(255, 255, 255, 0.12);
}


/* =========================================================
   PANEL SHINE
========================================================= */

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
    3s
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

  font-size: 25px;
  font-weight: 900;

  letter-spacing: -0.7px;
}


.panel-subtitle {
  margin-top: 3px;

  color:
    rgba(204, 222, 241, 0.42);

  font-size: 10px;

  letter-spacing: 0.5px;
}


/* =========================================================
   LIVE
========================================================= */

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


/* =========================================================
   PANEL DIVIDER
========================================================= */

.panel-divider {
  position: relative;

  z-index: 2;

  height: 1px;

  margin:
    28px 0 7px;

  background:
    linear-gradient(
      90deg,
      rgba(255, 255, 255, 0.17),
      rgba(255, 255, 255, 0.04)
    );
}


/* =========================================================
   SYSTEM ROW
========================================================= */

.system-row {
  position: relative;

  z-index: 2;

  display: grid;

  grid-template-columns:
    52px minmax(0, 1fr) auto;

  align-items: center;

  gap: 18px;

  padding:
    23px 0;

  border-bottom:
    1px solid rgba(255, 255, 255, 0.075);

  opacity: 0;

  transform:
    translateX(40px);

  transition:
    padding 0.25s ease,
    background 0.25s ease;
}


.system-row-1 {
  animation:
    rowEnter
    0.7s
    cubic-bezier(.2, .8, .2, 1)
    1.45s
    forwards;
}


.system-row-2 {
  animation:
    rowEnter
    0.7s
    cubic-bezier(.2, .8, .2, 1)
    1.65s
    forwards;
}


.system-row-3 {
  animation:
    rowEnter
    0.7s
    cubic-bezier(.2, .8, .2, 1)
    1.85s
    forwards;
}


.system-row:hover {
  padding-right: 12px;
  padding-left: 12px;

  background:
    linear-gradient(
      90deg,
      rgba(45, 139, 240, 0.09),
      transparent
    );
}


/* =========================================================
   NUMBER ICON
========================================================= */

.system-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 50px;
  height: 50px;

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
    rgba(45, 139, 240, 0.24);

  border-color:
    rgba(100, 181, 255, 0.55);

  transform:
    translateY(-2px)
    scale(1.04);
}


/* =========================================================
   SYSTEM TEXT
========================================================= */

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

  letter-spacing: -0.2px;
}


/* =========================================================
   STATUS
========================================================= */

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
    18px 19px;

  background:
    rgba(1, 18, 38, 0.27);

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
   PANEL INTERNAL ENTER
========================================================= */

.panel-item {
  opacity: 0;

  transform:
    translateY(12px);

  animation:
    panelItemEnter
    0.65s
    ease
    forwards;
}


.panel-item-1 {
  animation-delay: 1.15s;
}


.panel-item-2 {
  animation-delay: 1.3s;
}


.panel-item-3 {
  animation-delay: 2.05s;
}


/* =========================================================
   FOOTER
========================================================= */

.intro-footer {
  position: absolute;

  right: 60px;
  bottom: 27px;
  left: 60px;

  z-index: 6;

  display: flex;
  align-items: center;
  justify-content: space-between;

  color:
    rgba(255, 255, 255, 0.32);

  font-size: 9px;
  font-weight: 700;

  letter-spacing: 1.4px;
}


.intro-footer p {
  margin: 0;
}


.scroll-guide {
  display: flex;
  align-items: center;

  gap: 11px;
}


.mouse {
  position: relative;

  display: block;

  width: 17px;
  height: 26px;

  border:
    1px solid rgba(255, 255, 255, 0.35);

  border-radius: 10px;
}


.mouse span {
  position: absolute;

  top: 6px;
  left: 7px;

  width: 2px;
  height: 5px;

  background:
    rgba(255, 255, 255, 0.6);

  border-radius: 3px;

  animation:
    mouseMove
    1.7s
    infinite;
}


/* =========================================================
   LEFT CONTENT ANIMATIONS
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


.animation-1 {
  animation-delay: 0.25s;
}


.animation-2 {
  animation-delay: 0.42s;
}


.animation-3 {
  animation-delay: 0.58s;
}


.animation-4 {
  animation-delay: 0.74s;
}


.animation-5 {
  animation-delay: 0.93s;
}


.animation-6 {
  animation-delay: 1.1s;
}


/* =========================================================
   BIG PANEL ENTER
========================================================= */

.animation-panel {
  opacity: 0;

  filter:
    blur(12px);

  transform:
    translateX(130px)
    translateY(20px)
    scale(0.88);

  animation:
    bigPanelEnter
    1.25s
    cubic-bezier(.16, 1, .3, 1)
    0.78s
    forwards;
}


/* =========================================================
   KEYFRAMES
========================================================= */

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
      scale(0.88);
  }

  68% {
    opacity: 1;

    filter:
      blur(0);

    transform:
      translateX(-8px)
      translateY(0)
      scale(1.015);
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

  from {
    opacity: 0;

    transform:
      translateX(40px);
  }

  to {
    opacity: 1;

    transform:
      translateX(0);
  }

}


@keyframes panelItemEnter {

  from {
    opacity: 0;

    transform:
      translateY(12px);
  }

  to {
    opacity: 1;

    transform:
      translateY(0);
  }

}


@keyframes headerEnter {

  from {
    opacity: 0;

    transform:
      translateY(-15px);
  }

  to {
    opacity: 1;

    transform:
      translateY(0);
  }

}


@keyframes backgroundEnter {

  0% {
    opacity: 0;

    filter:
      blur(10px);

    transform:
      scale(1.12);
  }

  100% {
    opacity: 1;

    filter:
      blur(0);

    transform:
      scale(1.04);
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
      translateY(7px);
  }

}


@keyframes overlayEnter {

  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }

}


@keyframes glowEnter {

  from {
    opacity: 0;

    transform:
      scale(0.9);
  }

  to {
    opacity: 1;

    transform:
      scale(1);
  }

}


@keyframes rightGlowEnter {

  from {
    opacity: 0;

    transform:
      scale(0.7);
  }

  to {
    opacity: 1;

    transform:
      scale(1);
  }

}


/* =========================================================
   LARGE DISPLAY
========================================================= */

@media (min-width: 1500px) {

  .system-panel {
    flex-basis: 540px;

    width: 540px;

    padding:
      42px 44px 33px;
  }

  .panel-header strong {
    font-size: 27px;
  }

  .system-row {
    padding:
      25px 0;
  }

}


/* =========================================================
   MEDIUM
========================================================= */

@media (max-width: 1250px) {

  .intro-header,
  .hero {
    width:
      calc(100% - 70px);
  }

  .hero {
    gap: 35px;
  }

  .system-panel {
    flex-basis: 430px;

    width: 430px;

    padding:
      31px;
  }

  .hero-title {
    font-size:
      clamp(43px, 4.5vw, 64px);
  }

}


/* =========================================================
   TABLET
========================================================= */

@media (max-width: 980px) {

  .system-panel {
    display: none;
  }

  .hero-content {
    width: 100%;

    max-width: 800px;
  }

  .hero-title {
    font-size:
      clamp(42px, 8vw, 65px);
  }

}


/* =========================================================
   MOBILE
========================================================= */

@media (max-width: 650px) {

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

    min-height:
      calc(100vh - 145px);
  }

  .hero-title {
    font-size: 42px;

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

  .intro-footer {
    right: 18px;
    bottom: 18px;
    left: 18px;
  }

  .intro-footer > p {
    display: none;
  }

}


/* =========================================================
   REDUCED MOTION
========================================================= */

@media (prefers-reduced-motion: reduce) {

  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;

    animation-iteration-count: 1 !important;
  }

}

</style>
