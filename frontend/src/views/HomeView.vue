<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../stores/auth'

import logoWhite from '../assets/logo_w.png'
import greetingLogo from '../assets/greeting_logo.png'

const authStore = useAuthStore()
const { t, locale } = useI18n()

const languages = [
  { code: 'ko', label: 'KO' },
  { code: 'en', label: 'EN' },
  { code: 'ja', label: 'JP' }
]

const changeLanguage = (language) => {
  locale.value = language
  localStorage.setItem('language', language)
  document.documentElement.lang = language
}

const showOpening = ref(true)
const openingLeaving = ref(false)
const platformSection = ref(null)
const companySection = ref(null)

let openingLeaveTimer = null
let openingRemoveTimer = null

const roleLabel = computed(() => {
  if (authStore.role === 'ADMIN') return t('home.role.admin')
  if (authStore.role === 'USER') return t('home.role.user')
  return authStore.role || ''
})

const scrollToPlatform = () => {
  platformSection.value?.scrollIntoView({
    behavior: 'smooth',
    block: 'start'
  })
}

const scrollToCompany = () => {
  companySection.value?.scrollIntoView({
    behavior: 'smooth',
    block: 'start'
  })
}

onMounted(() => {
  openingLeaveTimer = window.setTimeout(() => {
    openingLeaving.value = true
  }, 2200)

  openingRemoveTimer = window.setTimeout(() => {
    showOpening.value = false
  }, 2800)
})

onBeforeUnmount(() => {
  if (openingLeaveTimer) clearTimeout(openingLeaveTimer)
  if (openingRemoveTimer) clearTimeout(openingRemoveTimer)
})
</script>

<template>
  <main class="home-page">

    <!-- =====================================================
         OPENING INTRO
    ====================================================== -->

    <Transition name="opening">
      <section
        v-if="showOpening"
        class="brand-opening"
        :class="{ 'is-leaving': openingLeaving }"
      >
        <div class="opening-bg"></div>
        <div class="opening-overlay"></div>
        <div class="opening-grid"></div>

        <div class="opening-content">
          <div class="opening-brand">
            ENSONSOFT
          </div>

          <div class="opening-line"></div>

          <h1 class="opening-title">
            <span class="opening-text opening-text-1">
              {{ t('home.opening.line1') }}
            </span>

            <span class="opening-text opening-text-2">
              {{ t('home.opening.line2') }}
            </span>

            <span class="opening-highlight opening-text-3">
              <span class="opening-highlight-bg"></span>

              <span class="opening-highlight-text">
                {{ t('home.opening.line3') }}
              </span>
            </span>
          </h1>

          <div class="opening-meta">
            <span>SMART MOBILITY</span>
            <i></i>
            <span>INTEGRATED CONTROL</span>
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

      <div class="hero-bg"></div>
      <div class="hero-overlay"></div>
      <div class="hero-glow"></div>


      <!-- HEADER -->

      <header class="intro-header">

        <RouterLink
          to="/"
          class="brand-link"
        >
          <img
            :src="logoWhite"
            alt="엔슨소프트"
            class="brand-logo"
          />
        </RouterLink>


        <div class="header-actions">

          <!-- LANGUAGE -->
          <div class="home-language-switcher">
            <button
              v-for="language in languages"
              :key="language.code"
              type="button"
              class="home-language-button"
              :class="{ active: locale === language.code }"
              @click="changeLanguage(language.code)"
            >
              {{ language.label }}
            </button>
          </div>

          <span class="header-language-divider"></span>

          <template v-if="!authStore.isLoggedIn">
            <RouterLink
              to="/login"
              class="header-text-link"
            >
              {{ t('home.header.login') }}
            </RouterLink>

            <RouterLink
              to="/signup"
              class="header-button"
            >
              {{ t('home.header.signup') }}
            </RouterLink>
          </template>

          <template v-else>
            <div class="user-status">
              <span class="online-dot"></span>

              <strong>
                {{ authStore.username }}
              </strong>

              <span class="role-badge">
                {{ roleLabel }}
              </span>
            </div>

            <RouterLink
              to="/dashboard"
              class="header-button"
            >
              {{ t('home.header.enter') }}
            </RouterLink>
          </template>

        </div>

        </div>

      </header>


      <!-- HERO CONTENT -->

      <div class="hero-layout">

        <!-- LEFT -->

        <div class="hero-content">

          <div class="hero-eyebrow hero-enter hero-delay-1">
            <span></span>
            ENSONSOFT SMART MOBILITY
          </div>


          <h1 class="hero-title">
            <span class="hero-title-line hero-enter hero-delay-2">
              {{ t('home.hero.line1') }}
            </span>

            <span class="hero-title-line hero-enter hero-delay-3">
              {{ t('home.hero.line2') }}
            </span>

            <span class="hero-title-line hero-title-highlight hero-enter hero-delay-4">
              {{ t('home.hero.line3') }}
            </span>
          </h1>


          <div class="hero-description hero-enter hero-delay-5">
            <p class="hero-description-en">
              {{ t('home.hero.description1') }}
              <strong>{{ t('home.hero.description2') }}</strong>
            </p>

          </div>


          <div class="hero-buttons hero-enter hero-delay-6">

            <template v-if="authStore.isLoggedIn">
              <RouterLink
                to="/dashboard"
                class="button-primary"
              >
                {{ t('home.hero.dashboard') }}

                <svg viewBox="0 0 24 24">
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>
              </RouterLink>

              <RouterLink
                to="/user"
                class="button-secondary"
              >
                {{ t('home.hero.myInfo') }}
              </RouterLink>
            </template>


            <template v-else>
              <RouterLink
                to="/login"
                class="button-primary"
              >
                {{ t('home.hero.systemLogin') }}

                <svg viewBox="0 0 24 24">
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>
              </RouterLink>

              <RouterLink
                to="/signup"
                class="button-secondary"
              >
                {{ t('home.header.signup') }}
              </RouterLink>
            </template>

          </div>

        </div>


        <!-- =================================================
             RIGHT SYSTEM PANEL
        ================================================== -->

        <div class="control-panel">

          <div class="panel-glow"></div>

          <div class="control-header">
            <div>
              <span class="control-eyebrow">
                SMART MOBILITY
              </span>

              <h2>
                Integrated Control
              </h2>

              <p>
                Mobility Management System
              </p>
            </div>

            <div class="live-badge">
              <span></span>
              LIVE
            </div>
          </div>


          <div class="control-divider"></div>


          <!-- 01 -->

          <RouterLink
            to="/monitoring"
            class="control-row"
          >
            <div class="control-number">
              01
            </div>

            <div class="control-name">
              <span>Integrated Monitoring</span>
              <strong>{{ t('home.control.monitoring') }}</strong>
            </div>

            <div class="control-status">
              NORMAL
            </div>
          </RouterLink>


          <!-- 02 -->

          <RouterLink
            to="/sites"
            class="control-row"
          >
            <div class="control-number">
              02
            </div>

            <div class="control-name">
              <span>Traffic Infrastructure</span>
              <strong>{{ t('home.control.site') }}</strong>
            </div>

            <div class="control-status">
              ACTIVE
            </div>
          </RouterLink>


          <!-- 03 -->

          <RouterLink
            to="/equipments"
            class="control-row"
          >
            <div class="control-number">
              03
            </div>

            <div class="control-name">
              <span>Safety Management</span>
              <strong>{{ t('home.control.equipment') }}</strong>
            </div>

            <div class="control-status">
              ONLINE
            </div>
          </RouterLink>


          <!-- 04 -->

          <RouterLink
            to="/maintenance"
            class="control-row"
          >
            <div class="control-number">
              04
            </div>

            <div class="control-name">
              <span>Maintenance Management</span>
              <strong>{{ t('home.control.maintenance') }}</strong>
            </div>

            <div class="control-status">
              READY
            </div>
          </RouterLink>


          <div class="control-bottom">

            <div>
              <span>SYSTEM</span>
              <strong>STABLE</strong>
            </div>

            <i></i>

            <div>
              <span>NETWORK</span>
              <strong>CONNECTED</strong>
            </div>

            <i></i>

            <div>
              <span>SECURITY</span>
              <strong>ENABLED</strong>
            </div>

          </div>

        </div>

      </div>


      <!-- SCROLL -->

      <button
        type="button"
        class="scroll-button"
        @click="scrollToPlatform"
      >
        <span class="mouse">
          <i></i>
        </span>

        <span class="scroll-text">
          <small>SMART MOBILITY PLATFORM</small>
          <strong>SCROLL</strong>
        </span>

        <svg viewBox="0 0 24 24">
          <path d="m6 9 6 6 6-6" />
        </svg>
      </button>

    </section>


    <!-- =====================================================
         SMART MOBILITY PLATFORM
    ====================================================== -->

    <section
      ref="platformSection"
      class="platform-section"
    >

      <div class="section-grid"></div>

      <div class="section-container">

        <div class="section-heading">

          <div>
            <div class="section-eyebrow">
              <span></span>
              SMART MOBILITY PLATFORM
            </div>

            <h2>
              {{ t('home.platform.heading1') }}
              <strong>{{ t('home.platform.heading2') }}</strong>
            </h2>
          </div>



        </div>


        <div class="feature-grid">

          <!-- 01 -->

          <RouterLink
            to="/monitoring"
            class="feature-card"
          >
            <span class="feature-number">01</span>

            <div class="feature-icon">
              <svg viewBox="0 0 24 24">
                <path d="M4 5h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2h-6v2h3v2H7v-2h3v-2H4a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm0 2v10h16V7H4Zm3 7 3-3 2 2 4-5 2 1.5-5.5 7-2.5-2.5-1.5 1.5L7 14Z" />
              </svg>
            </div>

            <span class="feature-en">
              INTEGRATED MONITORING
            </span>

            <h3>
              {{ t('home.platform.monitoring.title') }}
            </h3>

            <p>
              {{ t('home.platform.monitoring.description') }}
            </p>

            <div class="feature-link">
              VIEW SYSTEM
              <span>→</span>
            </div>
          </RouterLink>


          <!-- 02 -->

          <RouterLink
            to="/sites"
            class="feature-card"
          >
            <span class="feature-number">02</span>

            <div class="feature-icon">
              <svg viewBox="0 0 24 24">
                <path d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7Zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5Z" />
              </svg>
            </div>

            <span class="feature-en">
              SITE MANAGEMENT
            </span>

            <h3>
              {{ t('home.platform.site.title') }}
            </h3>

            <p>
              {{ t('home.platform.site.description') }}
            </p>

            <div class="feature-link">
              VIEW SYSTEM
              <span>→</span>
            </div>
          </RouterLink>


          <!-- 03 -->

          <RouterLink
            to="/equipments"
            class="feature-card"
          >
            <span class="feature-number">03</span>

            <div class="feature-icon">
              <svg viewBox="0 0 24 24">
                <path d="M19.14 12.94a7.5 7.5 0 0 0 .05-.94 7.5 7.5 0 0 0-.05-.94l2.03-1.58-1.92-3.32-2.39.96a7.3 7.3 0 0 0-1.62-.94L14.88 3h-3.84l-.36 2.18c-.57.24-1.11.55-1.62.94l-2.39-.96-1.92 3.32 2.03 1.58a7.5 7.5 0 0 0-.05.94c0 .32.02.63.05.94l-2.03 1.58 1.92 3.32 2.39-.96c.5.39 1.05.7 1.62.94l.36 2.18h3.84l.36-2.18c.57-.24 1.11-.55 1.62-.94l2.39.96 1.92-3.32-2.03-1.58ZM13 15.5A3.5 3.5 0 1 1 13 8a3.5 3.5 0 0 1 0 7.5Z" />
              </svg>
            </div>

            <span class="feature-en">
              EQUIPMENT MANAGEMENT
            </span>

            <h3>
              {{ t('home.platform.equipment.title') }}
            </h3>

            <p>
              {{ t('home.platform.equipment.description') }}
            </p>

            <div class="feature-link">
              VIEW SYSTEM
              <span>→</span>
            </div>
          </RouterLink>


          <!-- 04 -->

          <RouterLink
            to="/maintenance"
            class="feature-card"
          >
            <span class="feature-number">04</span>

            <div class="feature-icon">
              <svg viewBox="0 0 24 24">
                <path d="m22.7 19-9.1-9.1a6 6 0 0 0-7.5-7.5l3.4 3.4-3.7 3.7-3.4-3.4a6 6 0 0 0 7.5 7.5l9.1 9.1L22.7 19Z" />
              </svg>
            </div>

            <span class="feature-en">
              MAINTENANCE
            </span>

            <h3>
              {{ t('home.platform.maintenance.title') }}
            </h3>

            <p>
              {{ t('home.platform.maintenance.description') }}
            </p>

            <div class="feature-link">
              VIEW SYSTEM
              <span>→</span>
            </div>
          </RouterLink>

        </div>


        <!-- COMPANY SCROLL CTA -->

        <button
          type="button"
          class="company-scroll-button"
          @click="scrollToCompany"
        >
          <span>
            COMPANY INFORMATION
          </span>

          <strong>
            {{ t('home.company.viewInfo') }}
          </strong>

          <svg viewBox="0 0 24 24">
            <path d="m6 9 6 6 6-6" />
          </svg>
        </button>

      </div>

    </section>


    <!-- =====================================================
         COMPANY INFORMATION
    ====================================================== -->

    <section
      ref="companySection"
      class="company-section"
    >

      <div class="section-grid"></div>

      <div class="section-container">

        <!-- TITLE -->

        <div class="company-heading">

          <div>
            <div class="section-eyebrow">
              <span></span>
              COMPANY INFORMATION
            </div>

            <h2>
              {{ t('home.company.heading1') }}
              <strong>{{ t('home.company.heading2') }}</strong>
            </h2>
          </div>

        </div>


        <!-- COMPANY CARD -->

        <div class="company-card">

          <!-- LOGO -->

          <div class="company-logo-area">

            <div class="company-logo-circle company-circle-1"></div>
            <div class="company-logo-circle company-circle-2"></div>
            <div class="company-logo-glow"></div>

            <img
              :src="greetingLogo"
              alt="엔슨소프트 심볼"
              class="company-logo"
            />

            <div class="company-brand-name">
              <strong>ENSONSOFT</strong>
              <span>Ensonsoft Co., Ltd.</span>
            </div>

          </div>


          <!-- INFORMATION -->

          <div class="company-information">

            <div class="company-row">
              <span>{{ t('home.company.nameLabel') }}</span>
              <strong>{{ t('home.company.name') }}</strong>
            </div>

            <div class="company-row">
              <span>{{ t('home.company.ceoLabel') }}</span>
              <strong>차기욱</strong>
            </div>

            <div class="company-row">
              <span>{{ t('home.company.businessLabel') }}</span>
              <strong>{{ t('home.company.business') }}</strong>
            </div>

            <div class="company-row">
              <span>{{ t('home.company.phoneLabel') }}</span>
              <strong>070-4350-7400 ~ 7401</strong>
            </div>

            <div class="company-row">
              <span>FAX</span>
              <strong>0303-3440-7400</strong>
            </div>

            <div class="company-row">
              <span>{{ t('home.company.addressLabel') }}</span>

              <strong>
                {{ t('home.company.address') }}
              </strong>
            </div>

            <div class="company-row">
              <span>{{ t('home.company.labLabel') }}</span>

              <strong>
                {{ t('home.company.lab') }}
              </strong>
            </div>

          </div>

        </div>


        <!-- FOOTER -->

        <div class="company-footer">

          <div>
            <img
              :src="logoWhite"
              alt="엔슨소프트"
            />

            <p>
              Smart Mobility · Integrated Control ·
              Intelligent Video Analysis
            </p>
          </div>

          <div class="company-footer-right">
            <span>ENSONSOFT CO., LTD.</span>
            <strong>SMART MOBILITY SOLUTION</strong>
          </div>

        </div>

      </div>

    </section>

  </main>
</template>


<style scoped>

/* =========================================================
   BASE
========================================================= */

.home-page {
  width: 100%;
  margin: 0;
  overflow-x: hidden;
  background: #06172c;
  color: #fff;
}

* {
  box-sizing: border-box;
}


/* =========================================================
   OPENING
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

.opening-bg {
  position: absolute;
  inset: -5%;

  background:
    url('../assets/intro-bg.png')
    center / cover no-repeat;

  filter:
    brightness(.55)
    saturate(.8);

  animation:
    openingBg 3s ease forwards;
}

.opening-overlay {
  position: absolute;
  inset: 0;

  background:
    linear-gradient(
      90deg,
      rgba(2, 15, 34, .78),
      rgba(4, 28, 55, .53),
      rgba(2, 15, 34, .72)
    );
}

.opening-grid,
.section-grid {
  position: absolute;
  inset: 0;

  background-image:
    linear-gradient(
      rgba(100, 180, 255, .045) 1px,
      transparent 1px
    ),
    linear-gradient(
      90deg,
      rgba(100, 180, 255, .045) 1px,
      transparent 1px
    );

  background-size: 80px 80px;

  opacity: .25;

  pointer-events: none;
}

.opening-content {
  position: relative;
  z-index: 2;

  width:
    min(1300px, calc(100% - 80px));

  text-align: center;
}

.opening-brand {
  margin-bottom: 27px;

  font-size:
    clamp(32px, 3vw, 56px);

  font-weight: 900;
  letter-spacing: -2px;

  opacity: 0;

  animation:
    openingItem .7s ease .1s forwards;
}

.opening-line {
  width: 0;
  height: 1px;

  margin:
    0 auto 34px;

  background:
    linear-gradient(
      90deg,
      transparent,
      #58adff,
      transparent
    );

  animation:
    openingLine .7s ease .3s forwards;
}

.opening-title {
  display: flex;
  flex-direction: column;
  align-items: center;

  margin: 0;

  font-size:
    clamp(42px, 4.7vw, 78px);

  font-weight: 900;
  letter-spacing: -4px;
  line-height: 1.13;
}

.opening-text {
  display: block;

  opacity: 0;
  transform: translateY(35px);

  animation:
    openingItem .65s
    cubic-bezier(.16, 1, .3, 1)
    forwards;
}

.opening-text-1 {
  animation-delay: .4s;
}

.opening-text-2 {
  animation-delay: .62s;
}

.opening-text-3 {
  animation-delay: .82s;
}

.opening-highlight {
  position: relative;

  margin-top: 12px;
  padding: 4px 22px 8px;

  overflow: hidden;
}

.opening-highlight-bg {
  position: absolute;
  inset: 0;

  background:
    linear-gradient(
      90deg,
      #126fdc,
      #2c98ff
    );

  transform: scaleX(0);
  transform-origin: left;

  animation:
    highlightOpen .7s
    cubic-bezier(.16, 1, .3, 1)
    1s forwards;
}

.opening-highlight-text {
  position: relative;
  z-index: 2;
}

.opening-meta {
  display: flex;
  align-items: center;
  justify-content: center;

  gap: 13px;

  margin-top: 43px;

  color:
    rgba(214, 230, 247, .46);

  font-size: 9px;
  font-weight: 900;
  letter-spacing: 2.3px;

  opacity: 0;

  animation:
    openingItem .6s ease 1.3s forwards;
}

.opening-meta i {
  width: 4px;
  height: 4px;

  background: #55b1ff;
  border-radius: 50%;

  box-shadow:
    0 0 13px #55b1ff;
}

.opening-progress {
  position: absolute;

  right: 0;
  bottom: 0;
  left: 0;

  height: 3px;

  background:
    rgba(255, 255, 255, .05);
}

.opening-progress span {
  display: block;

  width: 0;
  height: 100%;

  background:
    linear-gradient(
      90deg,
      #1475e7,
      #6cc0ff
    );

  animation:
    progress 2.3s linear forwards;
}

.brand-opening.is-leaving {
  animation:
    openingExit .65s ease forwards;
}


/* =========================================================
   HERO BACKGROUND
========================================================= */

.intro-page {
  position: relative;

  width: 100%;
  min-height: 100vh;

  overflow: hidden;

  background: #06172c;
}

.hero-bg {
  position: absolute;
  inset: 0;

  background:
    url('../assets/intro-bg.png')
    center / cover no-repeat;

  transform: scale(1.04);

  animation:
    heroBg 18s ease-in-out
    infinite alternate;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;

  background:
    linear-gradient(
      90deg,
      rgba(2, 15, 34, .96) 0%,
      rgba(3, 23, 48, .84) 33%,
      rgba(4, 29, 58, .61) 65%,
      rgba(2, 17, 37, .54) 100%
    );
}

.hero-overlay::after {
  position: absolute;
  inset: 0;

  content: '';

  background:
    linear-gradient(
      180deg,
      rgba(0, 0, 0, .06),
      transparent 55%,
      rgba(1, 11, 24, .7)
    );
}

.hero-glow {
  position: absolute;

  top: 8%;
  right: 0;

  z-index: 2;

  width: 800px;
  height: 800px;

  background:
    radial-gradient(
      circle,
      rgba(30, 131, 245, .15),
      transparent 67%
    );

  filter: blur(30px);
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
    1px solid
    rgba(255, 255, 255, .12);
}

.brand-link {
  display: block;
}

.brand-logo {
  display: block;

  width: 170px;
  height: auto;
}

.header-actions {
  display: flex;
  align-items: center;

  gap: 13px;
}

.header-text-link {
  padding: 10px;

  color:
    rgba(255, 255, 255, .76);

  font-size: 13px;
  font-weight: 700;

  text-decoration: none;
}

.header-button {
  display: flex;
  align-items: center;
  justify-content: center;

  min-width: 98px;
  height: 44px;

  padding: 0 18px;

  background:
    rgba(255, 255, 255, .09);

  border:
    1px solid
    rgba(255, 255, 255, .28);

  border-radius: 7px;

  color: #fff;

  font-size: 13px;
  font-weight: 800;

  text-decoration: none;

  backdrop-filter: blur(12px);

  transition: .25s ease;
}

.header-button:hover {
  background: #fff;
  color: #0c2d52;
}

.user-status {
  display: flex;
  align-items: center;

  gap: 8px;

  font-size: 13px;
}

.online-dot {
  width: 7px;
  height: 7px;

  background: #36dc92;
  border-radius: 50%;

  box-shadow:
    0 0 0 4px
    rgba(54, 220, 146, .12);
}

.role-badge {
  padding: 4px 7px;

  background:
    rgba(255, 255, 255, .1);

  border-radius: 5px;

  color:
    rgba(255, 255, 255, .65);

  font-size: 10px;
}


/* =========================================================
   HERO LAYOUT
========================================================= */

.hero-layout {
  position: relative;
  z-index: 5;

  display: grid;

  grid-template-columns:
    minmax(500px, .86fr)
    minmax(650px, 1.14fr);

  align-items: center;

  gap: 60px;

  width:
    calc(100% - 58px);

  min-height:
    calc(100vh - 145px);

  margin: 0 auto;

  padding:
    35px 0 75px;
}

.hero-content {
  max-width: 720px;
}

.hero-eyebrow {
  display: flex;
  align-items: center;

  gap: 13px;

  margin-bottom: 25px;

  color: #72baff;

  font-size: 12px;
  font-weight: 900;
  letter-spacing: 3px;
}

.hero-eyebrow span,
.section-eyebrow span {
  width: 37px;
  height: 2px;

  background: #3998ff;
}

.hero-title {
  margin: 0;

  font-size:
    clamp(50px, 4.3vw, 78px);

  font-weight: 900;
  letter-spacing: -4px;
  line-height: 1.08;
}

.hero-title-line {
  display: block;
}

.hero-title-highlight {
  position: relative;

  display: inline-block;
}

.hero-title-highlight::after {
  position: absolute;

  right: -8px;
  bottom: 3px;
  left: -4px;

  z-index: -1;

  height: 14px;

  background:
    rgba(34, 135, 252, .5);

  content: '';
}

.hero-description {
  margin-top: 31px;

  color:
    rgba(214, 228, 242, .65);

  font-size: 14px;
  line-height: 1.8;
}

.hero-description-en {
  margin-bottom: 8px;

  color:
    rgba(255, 255, 255, .9);

  font-size: 18px;
}

.hero-description-en strong {
  color: #69b7ff;
}

.hero-buttons {
  display: flex;

  gap: 11px;

  margin-top: 36px;
}

.button-primary,
.button-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  height: 53px;

  border-radius: 7px;

  color: #fff;

  font-size: 14px;
  font-weight: 800;

  text-decoration: none;

  transition: .25s ease;
}

.button-primary {
  gap: 27px;

  min-width: 190px;

  padding: 0 21px;

  background: #1776e5;

  border:
    1px solid #1776e5;

  box-shadow:
    0 12px 32px
    rgba(17, 108, 218, .25);
}

.button-primary svg {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
}

.button-primary:hover {
  background: #268af5;

  transform:
    translateY(-2px);
}

.button-secondary {
  min-width: 115px;

  padding: 0 21px;

  background:
    rgba(255, 255, 255, .05);

  border:
    1px solid
    rgba(255, 255, 255, .28);
}

.button-secondary:hover {
  background:
    rgba(255, 255, 255, .12);

  transform:
    translateY(-2px);
}


/* =========================================================
   CONTROL PANEL
========================================================= */

.control-panel {
  position: relative;

  width: 100%;
  max-width: 720px;

  justify-self: end;

  padding:
    42px 44px 31px;

  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      rgba(17, 58, 99, .94),
      rgba(6, 29, 58, .89)
    );

  border:
    1px solid
    rgba(108, 184, 255, .3);

  border-radius: 25px;

  backdrop-filter: blur(25px);

  box-shadow:
    0 42px 110px
    rgba(0, 0, 0, .36),

    0 0 100px
    rgba(28, 126, 241, .1);

  opacity: 0;

  transform:
    translateX(100px)
    scale(.94);

  animation:
    panelEnter 1.1s
    cubic-bezier(.16, 1, .3, 1)
    2.4s forwards;
}

.control-panel::before {
  position: absolute;

  top: 0;
  left: 44px;

  width: 120px;
  height: 2px;

  background:
    linear-gradient(
      90deg,
      #51aeff,
      transparent
    );

  content: '';
}

.panel-glow {
  position: absolute;

  top: -150px;
  right: -120px;

  width: 430px;
  height: 430px;

  background:
    radial-gradient(
      circle,
      rgba(51, 153, 255, .2),
      transparent 68%
    );
}

.control-header {
  position: relative;

  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.control-eyebrow {
  color: #6bb7ff;

  font-size: 11px;
  font-weight: 900;
  letter-spacing: 2.4px;
}

.control-header h2 {
  margin:
    8px 0 4px;

  font-size: 29px;
  letter-spacing: -1px;
}

.control-header p {
  margin: 0;

  color:
    rgba(204, 222, 240, .4);

  font-size: 10px;
}

.live-badge {
  display: flex;
  align-items: center;

  gap: 7px;

  padding: 8px 11px;

  background:
    rgba(50, 218, 145, .07);

  border:
    1px solid
    rgba(50, 218, 145, .14);

  border-radius: 30px;

  color: #70e4ad;

  font-size: 9px;
  font-weight: 900;
}

.live-badge span {
  width: 7px;
  height: 7px;

  background: #38dd94;
  border-radius: 50%;

  box-shadow:
    0 0 12px #38dd94;

  animation:
    live 1.6s infinite;
}

.control-divider {
  height: 1px;

  margin:
    29px 0 8px;

  background:
    linear-gradient(
      90deg,
      rgba(255, 255, 255, .17),
      rgba(255, 255, 255, .03)
    );
}

.control-row {
  position: relative;

  display: grid;

  grid-template-columns:
    55px minmax(0, 1fr) auto;

  align-items: center;

  gap: 19px;

  padding: 19px 0;

  border-bottom:
    1px solid
    rgba(255, 255, 255, .075);

  color: #fff;

  text-decoration: none;

  transition: .25s ease;
}

.control-row:hover {
  padding:
    19px 13px;

  background:
    linear-gradient(
      90deg,
      rgba(45, 140, 240, .1),
      transparent
    );
}

.control-number {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 52px;
  height: 52px;

  background:
    rgba(39, 135, 235, .14);

  border:
    1px solid
    rgba(83, 170, 255, .3);

  border-radius: 12px;

  color: #72bdff;

  font-size: 12px;
  font-weight: 900;
}

.control-name {
  display: flex;
  flex-direction: column;

  gap: 5px;
}

.control-name span {
  color:
    rgba(210, 226, 243, .42);

  font-size: 10px;
}

.control-name strong {
  font-size: 14px;
  font-weight: 800;
}

.control-status {
  position: relative;

  padding-left: 12px;

  color: #61e5a5;

  font-size: 9px;
  font-weight: 900;
  letter-spacing: 1px;
}

.control-status::before {
  position: absolute;

  top: 50%;
  left: 0;

  width: 5px;
  height: 5px;

  background: #42dc97;

  border-radius: 50%;

  content: '';

  transform:
    translateY(-50%);

  box-shadow:
    0 0 9px #42dc97;
}

.control-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 15px;

  margin-top: 22px;

  padding:
    17px 20px;

  background:
    rgba(1, 17, 37, .3);

  border:
    1px solid
    rgba(255, 255, 255, .06);

  border-radius: 12px;
}

.control-bottom > div {
  display: flex;
  flex-direction: column;

  gap: 4px;
}

.control-bottom span {
  color:
    rgba(205, 224, 242, .34);

  font-size: 7px;
  font-weight: 900;
  letter-spacing: 1px;
}

.control-bottom strong {
  font-size: 9px;
  letter-spacing: .6px;
}

.control-bottom i {
  width: 1px;
  height: 27px;

  background:
    rgba(255, 255, 255, .08);
}


/* =========================================================
   SCROLL
========================================================= */

.scroll-button {
  position: absolute;

  bottom: 23px;
  left: 29px;

  z-index: 10;

  display: flex;
  align-items: center;

  gap: 12px;

  padding: 0;

  background: transparent;
  border: 0;

  color:
    rgba(255, 255, 255, .48);

  cursor: pointer;

  transition: .25s ease;
}

.scroll-button:hover {
  color: #fff;

  transform:
    translateY(-3px);
}

.mouse {
  position: relative;

  width: 18px;
  height: 28px;

  border:
    1px solid
    rgba(255, 255, 255, .4);

  border-radius: 11px;
}

.mouse i {
  position: absolute;

  top: 6px;
  left: 7px;

  width: 2px;
  height: 6px;

  background: #72b9ff;

  border-radius: 4px;

  animation:
    mouse 1.5s infinite;
}

.scroll-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  gap: 2px;
}

.scroll-text small {
  font-size: 8px;
  font-weight: 900;
  letter-spacing: 1.6px;
}

.scroll-text strong {
  color: #69b5ff;

  font-size: 7px;
  letter-spacing: 1.3px;
}

.scroll-button svg,
.company-scroll-button svg {
  width: 15px;
  height: 15px;

  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;

  animation:
    arrowDown 1.4s infinite;
}


/* =========================================================
   COMMON SECTIONS
========================================================= */

.platform-section,
.company-section {
  position: relative;

  min-height: 100vh;

  padding:
    115px 0 100px;

  overflow: hidden;

  background:
    linear-gradient(
      180deg,
      #06172c,
      #081d35 48%,
      #06172c
    );
}

.company-section {
  background:
    linear-gradient(
      180deg,
      #06172c,
      #071a31 45%,
      #041222
    );
}

.platform-section::before,
.company-section::before {
  position: absolute;

  top: -280px;
  left: 50%;

  width: 1000px;
  height: 650px;

  background:
    radial-gradient(
      circle,
      rgba(31, 126, 237, .14),
      transparent 68%
    );

  content: '';

  transform:
    translateX(-50%);
}

.section-container {
  position: relative;
  z-index: 2;

  width:
    min(1500px, calc(100% - 100px));

  margin: 0 auto;
}

.section-heading,
.company-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;

  gap: 60px;

  margin-bottom: 64px;
}

.section-heading > div,
.company-heading > div {
  max-width: 800px;
}

.section-eyebrow {
  display: flex;
  align-items: center;

  gap: 13px;

  margin-bottom: 20px;

  color: #67b3ff;

  font-size: 10px;
  font-weight: 900;
  letter-spacing: 2.5px;
}

.section-heading h2,
.company-heading h2 {
  margin: 0;

  font-size:
    clamp(38px, 3.5vw, 58px);

  font-weight: 900;
  letter-spacing: -3px;
  line-height: 1.15;
}

.section-heading h2 strong,
.company-heading h2 strong {
  display: block;

  color: #69b7ff;
}

.section-heading > p,
.company-heading > p {
  max-width: 430px;

  margin: 0;

  color:
    rgba(207, 225, 242, .52);

  font-size: 14px;
  line-height: 1.9;
}


/* =========================================================
   FEATURE CARDS
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

  min-height: 390px;

  padding: 31px 29px;

  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      rgba(17, 52, 87, .75),
      rgba(7, 29, 55, .75)
    );

  border:
    1px solid
    rgba(255, 255, 255, .08);

  border-radius: 17px;

  color: #fff;

  text-decoration: none;

  transition: .35s ease;
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
      #2c92f7,
      transparent
    );

  content: '';

  opacity: 0;

  transition: .35s;
}

.feature-card:hover {
  border-color:
    rgba(81, 165, 247, .34);

  background:
    linear-gradient(
      145deg,
      rgba(20, 67, 113, .88),
      rgba(8, 35, 66, .84)
    );

  transform:
    translateY(-9px);

  box-shadow:
    0 25px 70px
    rgba(0, 0, 0, .22);
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-number {
  position: absolute;

  top: 27px;
  right: 27px;

  color:
    rgba(113, 186, 255, .23);

  font-size: 39px;
  font-weight: 900;
}

.feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 56px;
  height: 56px;

  margin-bottom: 37px;

  background:
    rgba(43, 137, 238, .12);

  border:
    1px solid
    rgba(79, 162, 249, .2);

  border-radius: 13px;

  color: #68b5ff;
}

.feature-icon svg {
  width: 26px;
  height: 26px;

  fill: currentColor;
}

.feature-en {
  margin-bottom: 9px;

  color: #5eabf5;

  font-size: 8px;
  font-weight: 900;
  letter-spacing: 1.7px;
}

.feature-card h3 {
  margin:
    0 0 17px;

  font-size: 23px;
  font-weight: 900;
}

.feature-card p {
  margin: 0;

  color:
    rgba(210, 226, 241, .5);

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
    rgba(255, 255, 255, .55);

  font-size: 8px;
  font-weight: 900;
  letter-spacing: 1.5px;

  transition: .25s;
}

.feature-card:hover .feature-link {
  gap: 17px;

  color: #69b5ff;
}


/* =========================================================
   COMPANY SCROLL BUTTON
========================================================= */

.company-scroll-button {
  display: flex;
  align-items: center;

  gap: 18px;

  margin:
    55px auto 0;

  padding:
    17px 25px;

  background:
    rgba(18, 72, 126, .28);

  border:
    1px solid
    rgba(85, 168, 249, .18);

  border-radius: 40px;

  color:
    rgba(255, 255, 255, .6);

  cursor: pointer;

  transition: .25s;
}

.company-scroll-button span {
  color: #63b2ff;

  font-size: 8px;
  font-weight: 900;
  letter-spacing: 1.5px;
}

.company-scroll-button strong {
  color: #fff;

  font-size: 12px;
}

.company-scroll-button:hover {
  background:
    rgba(26, 100, 177, .38);

  border-color:
    rgba(97, 179, 255, .35);

  transform:
    translateY(-3px);
}


/* =========================================================
   COMPANY
========================================================= */

.company-card {
  position: relative;

  display: grid;

  grid-template-columns:
    .8fr 1.2fr;

  min-height: 520px;

  overflow: hidden;

  background:
    linear-gradient(
      135deg,
      rgba(17, 57, 96, .82),
      rgba(6, 27, 52, .9)
    );

  border:
    1px solid
    rgba(99, 177, 251, .16);

  border-radius: 25px;

  box-shadow:
    0 38px 100px
    rgba(0, 0, 0, .26);
}

.company-card::before {
  position: absolute;

  top: 0;
  left: 0;

  width: 180px;
  height: 2px;

  background:
    linear-gradient(
      90deg,
      #4aaaff,
      transparent
    );

  content: '';
}

.company-logo-area {
  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;

  min-width: 0;

  padding: 60px;

  overflow: hidden;

  border-right:
    1px solid
    rgba(255, 255, 255, .07);
}

.company-logo-glow {
  position: absolute;

  top: 50%;
  left: 50%;

  width: 420px;
  height: 420px;

  background:
    radial-gradient(
      circle,
      rgba(31, 138, 246, .17),
      transparent 67%
    );

  filter: blur(18px);

  transform:
    translate(-50%, -50%);
}

.company-logo-circle {
  position: absolute;

  top: 50%;
  left: 50%;

  border:
    1px solid
    rgba(85, 171, 255, .07);

  border-radius: 50%;

  transform:
    translate(-50%, -50%);
}

.company-circle-1 {
  width: 390px;
  height: 390px;
}

.company-circle-2 {
  width: 290px;
  height: 290px;
}

.company-logo {
  position: relative;
  z-index: 2;

  display: block;

  width:
    min(300px, 80%);

  height: auto;

  object-fit: contain;

  filter:
    drop-shadow(
      0 25px 40px
      rgba(0, 0, 0, .32)
    );

  transition:
    .4s ease;
}

.company-logo-area:hover .company-logo {
  transform:
    translateY(-9px)
    scale(1.04);
}

.company-brand-name {
  position: relative;
  z-index: 2;

  display: flex;
  align-items: center;
  flex-direction: column;

  gap: 7px;

  margin-top: 32px;
}

.company-brand-name strong {
  font-size: 23px;
  font-weight: 900;
  letter-spacing: 4px;
}

.company-brand-name span {
  color:
    rgba(113, 185, 255, .55);

  font-size: 9px;
  font-weight: 800;
  letter-spacing: 1.5px;
}

.company-information {
  display: flex;
  flex-direction: column;
  justify-content: center;

  padding:
    38px 60px;
}

.company-row {
  display: grid;

  grid-template-columns:
    155px minmax(0, 1fr);

  align-items: center;

  min-height: 62px;

  border-bottom:
    1px solid
    rgba(255, 255, 255, .075);

  transition: .25s ease;
}

.company-row:last-child {
  border-bottom: 0;
}

.company-row:hover {
  padding:
    0 14px;

  background:
    linear-gradient(
      90deg,
      rgba(43, 138, 235, .09),
      transparent
    );
}

.company-row > span {
  color:
    rgba(171, 199, 224, .55);

  font-size: 12px;
}

.company-row strong {
  color:
    rgba(255, 255, 255, .91);

  font-size: 14px;
  font-weight: 700;
  line-height: 1.65;
}


/* =========================================================
   COMPANY FOOTER
========================================================= */

.company-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 30px;

  margin-top: 35px;
  padding-top: 30px;

  border-top:
    1px solid
    rgba(255, 255, 255, .08);
}

.company-footer > div:first-child {
  display: flex;
  align-items: center;

  gap: 25px;
}

.company-footer img {
  width: 145px;
  height: auto;

  opacity: .78;
}

.company-footer p {
  margin: 0;

  color:
    rgba(184, 207, 229, .34);

  font-size: 9px;
  letter-spacing: 1px;
}

.company-footer-right {
  display: flex;
  align-items: flex-end;
  flex-direction: column;

  gap: 4px;
}

.company-footer-right span {
  color:
    rgba(177, 205, 231, .32);

  font-size: 8px;
  font-weight: 800;
  letter-spacing: 1.5px;
}

.company-footer-right strong {
  color:
    rgba(104, 180, 253, .48);

  font-size: 8px;
  letter-spacing: 1.4px;
}


/* =========================================================
   HERO ENTER
========================================================= */

.hero-enter {
  opacity: 0;

  transform:
    translateY(25px);

  animation:
    heroEnter .7s ease forwards;
}

.hero-delay-1 {
  animation-delay: 2.35s;
}

.hero-delay-2 {
  animation-delay: 2.48s;
}

.hero-delay-3 {
  animation-delay: 2.61s;
}

.hero-delay-4 {
  animation-delay: 2.74s;
}

.hero-delay-5 {
  animation-delay: 2.87s;
}

.hero-delay-6 {
  animation-delay: 3s;
}


/* =========================================================
   ANIMATIONS
========================================================= */

@keyframes openingBg {
  from {
    transform: scale(1.1);
    filter:
      brightness(.48)
      saturate(.7)
      blur(5px);
  }

  to {
    transform: scale(1.02);
    filter:
      brightness(.65)
      saturate(.9)
      blur(0);
  }
}

@keyframes openingItem {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes openingLine {
  to {
    width: 150px;
  }
}

@keyframes highlightOpen {
  to {
    transform: scaleX(1);
  }
}

@keyframes progress {
  to {
    width: 100%;
  }
}

@keyframes openingExit {
  to {
    opacity: 0;
    visibility: hidden;
  }
}

@keyframes heroBg {
  from {
    transform: scale(1.04);
  }

  to {
    transform: scale(1.08);
  }
}

@keyframes heroEnter {
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

@keyframes live {
  50% {
    opacity: .45;

    box-shadow:
      0 0 20px #38dd94;
  }
}

@keyframes mouse {
  0% {
    opacity: 0;
    transform: translateY(0);
  }

  35% {
    opacity: 1;
  }

  100% {
    opacity: 0;
    transform: translateY(8px);
  }
}

@keyframes arrowDown {
  50% {
    transform: translateY(4px);
  }
}


/* =========================================================
   RESPONSIVE
========================================================= */

@media (max-width: 1450px) {

  .hero-layout {
    grid-template-columns:
      minmax(470px, .9fr)
      minmax(540px, 1.1fr);

    gap: 40px;
  }

  .control-panel {
    padding:
      35px 36px 28px;
  }

  .feature-grid {
    grid-template-columns:
      repeat(2, 1fr);
  }

}


@media (max-width: 1120px) {

  .intro-header,
  .hero-layout {
    width:
      calc(100% - 60px);
  }

  .hero-layout {
    grid-template-columns: 1fr;

    padding:
      80px 0 130px;
  }

  .hero-content {
    max-width: 800px;
  }

  .control-panel {
    max-width: none;

    justify-self: stretch;
  }

  .section-heading,
  .company-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .company-card {
    grid-template-columns: 1fr;
  }

  .company-logo-area {
    min-height: 430px;

    border-right: 0;

    border-bottom:
      1px solid
      rgba(255, 255, 255, .07);
  }

}


@media (max-width: 760px) {

  .opening-content {
    width:
      calc(100% - 36px);
  }

  .opening-title {
    font-size:
      clamp(35px, 10vw, 52px);

    letter-spacing: -2px;
  }

  .opening-meta {
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

  .user-status {
    display: none;
  }


  .hero-layout {
    width:
      calc(100% - 36px);

    padding:
      60px 0 125px;
  }

  .hero-title {
    font-size:
      clamp(40px, 11vw, 58px);

    letter-spacing: -2.5px;
  }

  .hero-description-en {
    font-size: 15px;
  }

  .hero-buttons {
    flex-direction: column;

    max-width: 290px;
  }

  .button-primary,
  .button-secondary {
    width: 100%;
  }


  .control-panel {
    padding:
      27px 23px 23px;

    border-radius: 18px;
  }

  .control-header h2 {
    font-size: 22px;
  }

  .control-row {
    grid-template-columns:
      46px minmax(0, 1fr);

    gap: 13px;
  }

  .control-number {
    width: 43px;
    height: 43px;
  }

  .control-status {
    display: none;
  }


  .platform-section,
  .company-section {
    padding:
      85px 0 70px;
  }

  .section-container {
    width:
      calc(100% - 36px);
  }

  .section-heading,
  .company-heading {
    gap: 25px;

    margin-bottom: 40px;
  }

  .section-heading h2,
  .company-heading h2 {
    font-size: 37px;
    letter-spacing: -2px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .feature-card {
    min-height: 340px;
  }


  .company-card {
    min-height: auto;
  }

  .company-logo-area {
    min-height: 350px;

    padding: 45px 25px;
  }

  .company-logo {
    width: 215px;
  }

  .company-information {
    padding:
      25px 24px;
  }

  .company-row {
    grid-template-columns: 1fr;

    gap: 7px;

    padding:
      17px 0;
  }

  .company-row:hover {
    padding:
      17px 10px;
  }

  .company-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .company-footer > div:first-child {
    align-items: flex-start;
    flex-direction: column;
  }

  .company-footer-right {
    align-items: flex-start;
  }

}


@media (prefers-reduced-motion: reduce) {

  *,
  *::before,
  *::after {
    animation-duration:
      .01ms !important;

    animation-iteration-count:
      1 !important;

    scroll-behavior:
      auto !important;
  }

}


/* =========================================================
   PLATFORM LIGHT THEME
   플랫폼 섹션만 연회색 + 화이트 카드로 변경
========================================================= */

/* HERO → PLATFORM : 선 없이 안개처럼 자연스럽게 연결 */
.intro-page {
  position: relative;
  z-index: 2;
}

.intro-page::after {
  position: absolute;
  right: 0;
  bottom: -105px;
  left: 0;
  z-index: 10;

  height: 210px;

  pointer-events: none;
  content: '';

  background: linear-gradient(
    180deg,
    rgba(5, 22, 43, 0) 0%,
    rgba(32, 63, 94, .08) 18%,
    rgba(112, 145, 177, .18) 38%,
    rgba(197, 215, 232, .48) 62%,
    rgba(241, 246, 251, .88) 82%,
    rgba(248, 250, 252, 1) 100%
  );

  filter: blur(12px);
  transform: scaleY(1.12);
}

/* 기존 위쪽 띠/경계용 레이어 제거 */
.platform-section::after {
  display: none !important;
  content: none !important;
}

.platform-section {
  background:
    radial-gradient(circle at 50% 8%, rgba(35, 137, 255, .07), transparent 34%),
    linear-gradient(180deg, #fbfcfe 0%, #f5f7fa 52%, #f8fafc 100%);
  color: #10233f;
}

.platform-section::before {
  background: radial-gradient(circle, rgba(31, 126, 237, .07), transparent 68%);
}

.platform-section .section-grid {
  background-image:
    linear-gradient(rgba(21, 91, 166, .025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(21, 91, 166, .025) 1px, transparent 1px);
  opacity: .24;
}

.platform-section .section-heading {
  align-items: flex-start;
}

.platform-section .section-heading > div {
  max-width: 900px;
}

.platform-section .section-eyebrow {
  color: #1684ef;
}

.platform-section .section-eyebrow span {
  background: #1684ef;
}

.platform-section .section-heading h2 {
  color: #10233f;
}

.platform-section .section-heading h2 strong {
  color: #258cf2;
}

.platform-section .feature-card {
  background: linear-gradient(145deg, rgba(255,255,255,.99), rgba(250,252,255,.99));
  border: 1px solid rgba(49, 126, 205, .14);
  color: #10233f;
  box-shadow: 0 18px 55px rgba(30, 73, 119, .07);
}

.platform-section .feature-card::before {
  background: linear-gradient(90deg, #1684ef, #67b7ff, transparent);
}

.platform-section .feature-card:hover {
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  border-color: rgba(22, 132, 239, .3);
  box-shadow: 0 28px 70px rgba(31, 91, 153, .14);
}

.platform-section .feature-number {
  color: rgba(34, 139, 244, .28);
}

.platform-section .feature-icon {
  background: linear-gradient(145deg, rgba(36,145,255,.11), rgba(36,145,255,.04));
  border: 1px solid rgba(36, 145, 255, .18);
  color: #1684ef;
}

.platform-section .feature-en {
  color: #1684ef;
}

.platform-section .feature-card h3 {
  color: #10233f;
}

.platform-section .feature-card p {
  color: #667a92;
}

.platform-section .feature-link {
  color: #1684ef;
  border-top: 1px solid rgba(34, 78, 123, .09);
}

.platform-section .feature-card:hover .feature-link {
  color: #086fd5;
}

.platform-section .company-scroll-button {
  background: rgba(255,255,255,.9);
  border: 1px solid rgba(22,132,239,.28);
  color: #64778d;
  box-shadow: 0 10px 32px rgba(30,79,130,.07);
}

.platform-section .company-scroll-button span {
  color: #1684ef;
}

.platform-section .company-scroll-button strong {
  color: #10233f;
}

.platform-section .company-scroll-button:hover {
  background: #ffffff;
  border-color: rgba(22,132,239,.5);
  box-shadow: 0 16px 38px rgba(30,79,130,.12);
}



/* =========================================================
   HOME LANGUAGE SWITCHER
========================================================= */

.home-language-switcher {
  display: flex;
  align-items: center;
  padding: 4px;
  background: rgba(6, 28, 54, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 9px;
  backdrop-filter: blur(14px);
}

.home-language-button {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 32px;
  padding: 0 10px;
  background: transparent;
  border: 0;
  border-radius: 6px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 10px;
  font-weight: 900;
  letter-spacing: 1px;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease,
    box-shadow 0.2s ease;
}

.home-language-button:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.08);
}

.home-language-button.active {
  color: #ffffff;
  background: linear-gradient(
    135deg,
    rgba(30, 132, 245, 0.95),
    rgba(25, 105, 215, 0.95)
  );
  box-shadow: 0 5px 15px rgba(20, 108, 220, 0.25);
}

.header-language-divider {
  width: 1px;
  height: 23px;
  margin: 0 3px;
  background: rgba(255, 255, 255, 0.14);
}

@media (max-width: 768px) {
  .home-language-switcher {
    padding: 3px;
  }

  .home-language-button {
    min-width: 34px;
    height: 29px;
    padding: 0 7px;
    font-size: 9px;
  }

  .header-language-divider {
    display: none;
  }
}

</style>
