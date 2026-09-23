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

    <!-- TOP NAV -->
    <header class="intro-header">

      <RouterLink
        to="/"
        class="brand"
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


    <!-- HERO CONTENT -->
    <section class="hero">

      <div class="hero-content">

        <!-- EYEBROW -->
        <div class="eyebrow animation-1">
          <span class="eyebrow-line"></span>

          <span>
            ENSONSOFT SMART MOBILITY
          </span>
        </div>


        <!-- TITLE -->
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


        <!-- DESCRIPTION -->
        <div class="hero-description animation-5">

          <p class="english-copy">
            Innovative creativity for
            <strong>better safety and future.</strong>
          </p>

          <p class="korean-copy">
            스마트 모빌리티 기술을 통해
            더 안전하고 편리한 교통 환경을 만들어갑니다.
          </p>

        </div>


        <!-- BUTTONS -->
        <div class="hero-buttons animation-6">

          <!-- 로그인 전 -->
          <template v-if="!authStore.isLoggedIn">

            <RouterLink
              to="/login"
              class="primary-button"
            >
              <span>시스템 로그인</span>

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
              <span>통합 대시보드</span>

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


      <!-- RIGHT DECORATION -->
      <div class="system-panel animation-panel">

        <div class="panel-header">

          <div>
            <span class="panel-small">
              SMART MOBILITY
            </span>

            <strong>
              Integrated Control
            </strong>
          </div>

          <span class="live">
            <span class="live-dot"></span>
            LIVE
          </span>

        </div>


        <div class="panel-divider"></div>


        <div class="system-row">

          <div class="system-icon">
            01
          </div>

          <div class="system-text">
            <span>Integrated Monitoring</span>
            <strong>통합 관제 시스템</strong>
          </div>

          <span class="system-status">
            NORMAL
          </span>

        </div>


        <div class="system-row">

          <div class="system-icon">
            02
          </div>

          <div class="system-text">
            <span>Traffic Infrastructure</span>
            <strong>교통 인프라 관리</strong>
          </div>

          <span class="system-status">
            ACTIVE
          </span>

        </div>


        <div class="system-row">

          <div class="system-icon">
            03
          </div>

          <div class="system-text">
            <span>Safety Management</span>
            <strong>스마트 안전 관리</strong>
          </div>

          <span class="system-status">
            ONLINE
          </span>

        </div>

      </div>

    </section>


    <!-- BOTTOM -->
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
/* ========================================
   PAGE
======================================== */

.intro-page {
  position: relative;

  width: 100%;
  min-height: 100vh;

  overflow: hidden;

  background: #06172c;

  color: #ffffff;
}


/* ========================================
   BACKGROUND
======================================== */

.background-image {
  position: absolute;
  inset: 0;

  background:
    url('../assets/intro-bg.png')
    center center / cover
    no-repeat;

  opacity: 0;
  transform: scale(1.08);

  animation:
    backgroundEnter 2.2s cubic-bezier(.2, .8, .2, 1) forwards,
    backgroundMove 20s ease-in-out 2.2s infinite alternate;
}

.background-overlay {
  position: absolute;
  inset: 0;

  z-index: 1;

  background:
    linear-gradient(
      90deg,
      rgba(2, 15, 34, 0.94) 0%,
      rgba(3, 22, 47, 0.84) 32%,
      rgba(4, 27, 55, 0.70) 60%,
      rgba(2, 18, 39, 0.58) 100%
    );

  opacity: 0;
  animation: overlayEnter 2.3s ease 0.25s forwards;
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
      rgba(2, 15, 31, 0.75)
    );

  content: '';
}

.background-glow {
  position: absolute;

  top: 17%;
  left: 32%;

  z-index: 2;

  width: 500px;
  height: 500px;

  background:
    radial-gradient(
      circle,
      rgba(34, 132, 255, 0.13),
      transparent 68%
    );

  filter: blur(25px);

  pointer-events: none;
  opacity: 0;
  animation: glowEnter 2.8s ease 1s forwards;
}


/* ========================================
   HEADER
======================================== */

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
    headerEnter 0.8s ease both;
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

  color: rgba(255, 255, 255, 0.8);

  font-size: 13px;
  font-weight: 700;

  text-decoration: none;

  transition: 0.2s ease;
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

  transition: 0.25s ease;
}

.header-button:hover {
  background: #ffffff;

  color: #0c2c52;

  transform: translateY(-1px);
}


/* ========================================
   USER
======================================== */

.user-status {
  display: flex;
  align-items: center;
  gap: 8px;

  padding-right: 10px;

  color: rgba(255, 255, 255, 0.9);

  font-size: 13px;
  font-weight: 700;
}

.online-dot {
  width: 7px;
  height: 7px;

  background: #31d98b;

  border-radius: 50%;

  box-shadow:
    0 0 0 4px rgba(49, 217, 139, 0.13);
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


/* ========================================
   HERO
======================================== */

.hero {
  position: relative;
  z-index: 5;

  display: flex;
  align-items: center;
  justify-content: space-between;

  width: calc(100% - 120px);
  min-height: calc(100vh - 190px);

  margin: 0 auto;
}

.hero-content {
  width: 57%;
  max-width: 820px;
}


/* EYEBROW */

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


/* ========================================
   TITLE
======================================== */

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
    lineDraw 0.7s ease 1.45s forwards;
}


/* ========================================
   DESCRIPTION
======================================== */

.hero-description {
  margin-top: 32px;
}

.english-copy {
  margin: 0 0 10px;

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


/* ========================================
   BUTTONS
======================================== */

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

  transition: 0.25s ease;
}

.primary-button {
  gap: 26px;

  min-width: 190px;

  padding: 0 21px;

  background: #1671df;

  border: 1px solid #1671df;

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

  transition: 0.2s ease;
}

.primary-button:hover {
  background: #2384f2;

  border-color: #2384f2;

  transform: translateY(-2px);
}

.primary-button:hover svg {
  transform: translateX(4px);
}

.secondary-button {
  min-width: 115px;

  padding: 0 21px;

  background:
    rgba(255, 255, 255, 0.05);

  border:
    1px solid rgba(255, 255, 255, 0.28);

  backdrop-filter: blur(8px);

  color: #ffffff;
}

.secondary-button:hover {
  background:
    rgba(255, 255, 255, 0.12);

  border-color:
    rgba(255, 255, 255, 0.5);

  transform: translateY(-2px);
}


/* ========================================
   SYSTEM PANEL
======================================== */

.system-panel {
  width: 350px;

  padding: 25px;

  box-sizing: border-box;

  background:
    linear-gradient(
      145deg,
      rgba(10, 42, 76, 0.67),
      rgba(6, 29, 57, 0.48)
    );

  border:
    1px solid rgba(255, 255, 255, 0.17);

  border-radius: 14px;

  backdrop-filter: blur(15px);

  box-shadow:
    0 25px 60px rgba(0, 0, 0, 0.18);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-header > div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.panel-small {
  color: #65aff9;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 1.7px;
}

.panel-header strong {
  font-size: 16px;
  font-weight: 800;
}

.live {
  display: flex;
  align-items: center;
  gap: 7px;

  color: #7fe1b2;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 1px;
}

.live-dot {
  width: 6px;
  height: 6px;

  background: #35dd91;

  border-radius: 50%;

  box-shadow:
    0 0 0 5px
    rgba(53, 221, 145, 0.1);

  animation:
    pulse 1.8s infinite;
}

.panel-divider {
  height: 1px;

  margin: 21px 0 7px;

  background:
    rgba(255, 255, 255, 0.1);
}

.system-row {
  display: grid;
  grid-template-columns:
    37px minmax(0, 1fr) auto;

  align-items: center;

  gap: 12px;

  padding: 16px 0;

  border-bottom:
    1px solid rgba(255, 255, 255, 0.07);
}

.system-row:last-child {
  border-bottom: 0;
}

.system-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 35px;
  height: 35px;

  background:
    rgba(45, 137, 237, 0.12);

  border:
    1px solid rgba(74, 158, 248, 0.2);

  border-radius: 8px;

  color: #69b4ff;

  font-size: 10px;
  font-weight: 900;
}

.system-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.system-text span {
  color:
    rgba(255, 255, 255, 0.4);

  font-size: 9px;
}

.system-text strong {
  color:
    rgba(255, 255, 255, 0.88);

  font-size: 12px;
}

.system-status {
  color: #5bd89c;

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 0.7px;
}


/* ========================================
   FOOTER
======================================== */

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
    mouseMove 1.7s infinite;
}


/* ========================================
   INTRO ANIMATIONS
======================================== */

.animation-1,
.animation-2,
.animation-3,
.animation-4,
.animation-5,
.animation-6 {
  opacity: 0;

  transform: translateY(25px);

  animation:
    textEnter 0.75s
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

.animation-panel {
  opacity: 0;

  transform:
    translateX(40px)
    scale(0.98);

  animation:
    panelEnter 0.9s
    cubic-bezier(.2, .8, .2, 1)
    1s forwards;
}


/* ========================================
   KEYFRAMES
======================================== */

@keyframes textEnter {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes panelEnter {
  to {
    opacity: 1;

    transform:
      translateX(0)
      scale(1);
  }
}

@keyframes headerEnter {
  from {
    opacity: 0;
    transform: translateY(-15px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes backgroundEnter {
  0% {
    opacity: 0;
    filter: blur(10px);
    transform: scale(1.12);
  }

  100% {
    opacity: 1;
    filter: blur(0);
    transform: scale(1.04);
  }
}

@keyframes backgroundMove {
  from {
    transform: scale(1.04);
  }

  to {
    transform: scale(1.08);
  }
}

@keyframes lineDraw {
  to {
    transform: scaleX(1);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.35;
  }
}

@keyframes mouseMove {
  0% {
    opacity: 0;
    transform: translateY(0);
  }

  40% {
    opacity: 1;
  }

  100% {
    opacity: 0;
    transform: translateY(7px);
  }
}


/* ========================================
   RESPONSIVE
======================================== */

@media (max-width: 1100px) {
  .intro-header,
  .hero {
    width: calc(100% - 70px);
  }

  .system-panel {
    width: 310px;
  }

  .hero-content {
    width: 60%;
  }
}

@media (max-width: 900px) {
  .system-panel {
    display: none;
  }

  .hero-content {
    width: 100%;
  }

  .hero-title {
    font-size:
      clamp(42px, 8vw, 65px);
  }
}

@media (max-width: 650px) {
  .intro-header {
    width: calc(100% - 36px);
    height: 82px;
  }

  .brand-logo {
    width: 135px;
  }

  .header-actions .user-status {
    display: none;
  }

  .hero {
    width: calc(100% - 36px);
    min-height: calc(100vh - 145px);
  }

  .hero-title {
    font-size: 42px;

    letter-spacing: -2.5px;
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


@keyframes overlayEnter {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes glowEnter {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

</style>