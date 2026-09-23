<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import logoBlue from '../assets/logo.png'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const message = ref('')
const loading = ref(false)
const showPassword = ref(false)

const login = async () => {
  if (!username.value.trim()) {
    message.value = '아이디를 입력해주세요.'
    return
  }

  if (!password.value) {
    message.value = '비밀번호를 입력해주세요.'
    return
  }

  try {
    loading.value = true
    message.value = ''

    await authStore.login(
      username.value.trim(),
      password.value
    )

    router.push('/dashboard')
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      '아이디 또는 비밀번호를 확인해주세요.'
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/')
}

const goSignup = () => {
  router.push('/signup')
}
</script>


<template>
  <main class="login-page">

    <!-- ========================================
         LEFT VISUAL
    ========================================= -->

    <section class="visual-section">

      <div class="visual-background"></div>
      <div class="visual-overlay"></div>
      <div class="visual-glow"></div>

      <!-- 장식 라인 -->
      <div class="tech-line line-one"></div>
      <div class="tech-line line-two"></div>

      <div class="visual-content">

        <button
          type="button"
          class="back-home"
          @click="goHome"
        >
          <svg viewBox="0 0 24 24">
            <path d="M19 12H5M11 18l-6-6 6-6" />
          </svg>

          <span>INTRO</span>
        </button>


        <div class="visual-copy">

          <div class="eyebrow">
            <span class="eyebrow-line"></span>
            ENSONSOFT SMART MOBILITY
          </div>

          <h1>
            더 안전한 이동을 위한
            <strong>스마트 모빌리티</strong>
          </h1>

          <p>
            교통 인프라와 실시간 데이터를 하나의 플랫폼에서
            통합 관리하는 스마트 모빌리티 운영 시스템입니다.
          </p>

        </div>


        <!-- 하단 상태 -->
        <div class="system-state">

          <div class="state-item">
            <span class="state-dot"></span>

            <div>
              <small>SYSTEM</small>
              <strong>ONLINE</strong>
            </div>
          </div>

          <div class="state-divider"></div>

          <div class="state-item">
            <div>
              <small>PLATFORM</small>
              <strong>SMART MOBILITY</strong>
            </div>
          </div>

        </div>

      </div>

    </section>


    <!-- ========================================
         RIGHT LOGIN
    ========================================= -->

    <section class="login-section">

      <div class="login-wrapper">

        <!-- LOGO -->
        <button
          type="button"
          class="logo-button"
          @click="goHome"
        >
          <img
            :src="logoBlue"
            alt="엔슨소프트"
            class="login-logo"
          />
        </button>


        <!-- TITLE -->
        <div class="login-heading">

          <span class="login-label">
            WELCOME BACK
          </span>

          <h2>
            로그인
          </h2>

          <p>
            스마트 모빌리티 통합 관리 시스템에 접속합니다.
          </p>

        </div>


        <!-- FORM -->
        <form
          class="login-form"
          @submit.prevent="login"
        >

          <!-- USERNAME -->
          <div class="field">

            <label for="username">
              아이디
            </label>

            <div class="input-box">

              <span class="input-icon">

                <svg viewBox="0 0 24 24">
                  <path
                    d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm7 8a7 7 0 0 0-14 0"
                  />
                </svg>

              </span>

              <input
                id="username"
                v-model="username"
                type="text"
                placeholder="아이디를 입력해주세요"
                autocomplete="username"
                :disabled="loading"
              />

            </div>

          </div>


          <!-- PASSWORD -->
          <div class="field">

            <div class="label-row">

              <label for="password">
                비밀번호
              </label>

              <span class="password-help">
                PASSWORD
              </span>

            </div>


            <div class="input-box">

              <span class="input-icon">

                <svg viewBox="0 0 24 24">
                  <path
                    d="M7 10V7a5 5 0 0 1 10 0v3M6 10h12a2 2 0 0 1 2 2v8H4v-8a2 2 0 0 1 2-2Z"
                  />
                </svg>

              </span>


              <input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="비밀번호를 입력해주세요"
                autocomplete="current-password"
                :disabled="loading"
              />


              <button
                type="button"
                class="password-toggle"
                tabindex="-1"
                @click="showPassword = !showPassword"
              >

                <svg
                  v-if="!showPassword"
                  viewBox="0 0 24 24"
                >
                  <path
                    d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z"
                  />
                  <circle
                    cx="12"
                    cy="12"
                    r="2.5"
                  />
                </svg>

                <svg
                  v-else
                  viewBox="0 0 24 24"
                >
                  <path d="m3 3 18 18" />
                  <path
                    d="M10.6 6.1A10.5 10.5 0 0 1 12 6c6.5 0 10 6 10 6a16 16 0 0 1-2.2 2.8M6.2 6.2C3.5 8 2 12 2 12s3.5 6 10 6a10 10 0 0 0 4.2-.9"
                  />
                </svg>

              </button>

            </div>

          </div>


          <!-- ERROR -->
          <Transition name="message">

            <div
              v-if="message"
              class="error-message"
            >
              <span>!</span>

              {{ message }}
            </div>

          </Transition>


          <!-- LOGIN BUTTON -->
          <button
            type="submit"
            class="login-button"
            :disabled="loading"
          >

            <template v-if="loading">

              <span class="spinner"></span>

              로그인 중...

            </template>

            <template v-else>

              <span>
                로그인
              </span>

              <svg viewBox="0 0 24 24">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>

            </template>

          </button>

        </form>


        <!-- SIGNUP -->
        <div class="signup-area">

          <span>
            아직 계정이 없으신가요?
          </span>

          <button
            type="button"
            @click="goSignup"
          >
            회원가입
          </button>

        </div>


        <!-- FOOTER -->
        <div class="login-footer">

          <span class="security-dot"></span>

          <span>
            SECURE ACCESS
          </span>

          <span class="footer-divider"></span>

          <span>
            ENSONSOFT
          </span>

        </div>

      </div>

    </section>

  </main>
</template>


<style scoped>
/* ========================================
   PAGE
======================================== */

.login-page {
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;

  width: 100%;
  min-height: 100vh;

  background: #ffffff;

  overflow: hidden;
}


/* ========================================
   LEFT VISUAL
======================================== */

.visual-section {
  position: relative;

  min-height: 100vh;

  overflow: hidden;

  background: #06172c;
}

.visual-background {
  position: absolute;
  inset: 0;

  background:
    url('../assets/intro-bg.png')
    center center / cover
    no-repeat;

  animation:
    backgroundIntro 7s ease-out forwards;
}

.visual-overlay {
  position: absolute;
  inset: 0;

  background:
    linear-gradient(
      125deg,
      rgba(3, 17, 37, 0.93) 0%,
      rgba(5, 31, 62, 0.82) 45%,
      rgba(6, 40, 78, 0.66) 100%
    );

  opacity: 0;

  animation:
    overlayIntro 1.5s ease forwards;
}

.visual-glow {
  position: absolute;

  top: 15%;
  right: -180px;

  width: 600px;
  height: 600px;

  border-radius: 50%;

  background:
    radial-gradient(
      circle,
      rgba(32, 132, 255, 0.25),
      transparent 65%
    );

  filter: blur(25px);
}


/* ========================================
   TECH DECORATION
======================================== */

.tech-line {
  position: absolute;

  height: 1px;

  background:
    linear-gradient(
      90deg,
      transparent,
      rgba(69, 160, 255, 0.45),
      transparent
    );

  transform: rotate(-18deg);
}

.line-one {
  top: 31%;
  left: -10%;

  width: 80%;
}

.line-two {
  right: -10%;
  bottom: 23%;

  width: 70%;
}


/* ========================================
   VISUAL CONTENT
======================================== */

.visual-content {
  position: relative;
  z-index: 5;

  display: flex;
  flex-direction: column;

  min-height: 100vh;

  padding: 52px 64px 47px;

  box-sizing: border-box;
}

.back-home {
  display: inline-flex;
  align-items: center;
  gap: 10px;

  align-self: flex-start;

  padding: 0;

  background: transparent;
  border: 0;

  color: rgba(255, 255, 255, 0.65);

  cursor: pointer;

  font-family: inherit;
  font-size: 10px;
  font-weight: 900;

  letter-spacing: 2px;

  transition: 0.2s ease;
}

.back-home svg {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;

  stroke-linecap: round;
  stroke-linejoin: round;

  transition: 0.2s ease;
}

.back-home:hover {
  color: #ffffff;
}

.back-home:hover svg {
  transform: translateX(-4px);
}


/* ========================================
   COPY
======================================== */

.visual-copy {
  width: 100%;
  max-width: 650px;

  margin: auto 0;

  animation:
    visualContentIn 0.8s
    cubic-bezier(.2, .8, .2, 1)
    0.2s both;
}

.eyebrow {
  display: flex;
  align-items: center;
  gap: 12px;

  margin-bottom: 25px;

  color: #6db7ff;

  font-size: 11px;
  font-weight: 900;

  letter-spacing: 2.7px;
}

.eyebrow-line {
  display: block;

  width: 32px;
  height: 2px;

  background: #3d9dff;
}

.visual-copy h1 {
  max-width: 590px;

  margin: 0;

  color: #ffffff;

  font-size:
    clamp(40px, 4vw, 65px);

  font-weight: 900;

  letter-spacing: -3.5px;

  line-height: 1.13;
}

.visual-copy h1 strong {
  display: block;

  margin-top: 7px;

  color: #6eb9ff;

  font-weight: 900;
}

.visual-copy p {
  max-width: 500px;

  margin: 28px 0 0;

  color:
    rgba(220, 231, 243, 0.68);

  font-size: 14px;

  line-height: 1.9;
}


/* ========================================
   SYSTEM STATE
======================================== */

.system-state {
  display: flex;
  align-items: center;
  gap: 25px;

  padding-top: 25px;

  border-top:
    1px solid rgba(255, 255, 255, 0.12);
}

.state-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.state-item > div {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.state-item small {
  color:
    rgba(255, 255, 255, 0.35);

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.5px;
}

.state-item strong {
  color:
    rgba(255, 255, 255, 0.78);

  font-size: 10px;
  font-weight: 900;

  letter-spacing: 0.7px;
}

.state-dot {
  width: 7px;
  height: 7px;

  background: #39dd91;

  border-radius: 50%;

  box-shadow:
    0 0 0 5px rgba(57, 221, 145, 0.11);

  animation:
    pulse 1.8s infinite;
}

.state-divider {
  width: 1px;
  height: 29px;

  background:
    rgba(255, 255, 255, 0.12);
}


/* ========================================
   RIGHT LOGIN
======================================== */

.login-section {
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: 100vh;

  padding: 60px;

  box-sizing: border-box;

  background:
    linear-gradient(
      145deg,
      #ffffff 0%,
      #fbfcfe 100%
    );
}

.login-wrapper {
  width: 100%;
  max-width: 410px;

  animation:
    loginCardIn 0.7s
    cubic-bezier(.2, .8, .2, 1)
    both;
}


/* ========================================
   LOGO
======================================== */

.logo-button {
  display: inline-flex;

  padding: 0;

  background: transparent;
  border: 0;

  cursor: pointer;
}

.login-logo {
  display: block;

  width: 165px;
  height: auto;

  object-fit: contain;
}


/* ========================================
   LOGIN HEADING
======================================== */

.login-heading {
  margin-top: 55px;
  margin-bottom: 39px;
}

.login-label {
  display: block;

  margin-bottom: 10px;

  color: #1b75df;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 2.2px;
}

.login-heading h2 {
  margin: 0;

  color: #10233e;

  font-size: 35px;
  font-weight: 900;

  letter-spacing: -1.8px;
}

.login-heading p {
  margin: 12px 0 0;

  color: #8996a8;

  font-size: 13px;

  line-height: 1.6;
}


/* ========================================
   FORM
======================================== */

.login-form {
  display: flex;
  flex-direction: column;

  gap: 22px;
}

.field label {
  display: block;

  margin-bottom: 9px;

  color: #344760;

  font-size: 12px;
  font-weight: 800;
}

.label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.password-help {
  margin-bottom: 9px;

  color: #b0bac7;

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.4px;
}


/* ========================================
   INPUT
======================================== */

.input-box {
  position: relative;

  display: flex;
  align-items: center;

  height: 54px;

  background: #ffffff;

  border: 1px solid #dce3eb;
  border-radius: 9px;

  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.input-box:focus-within {
  border-color: #2a80e7;

  box-shadow:
    0 0 0 4px rgba(42, 128, 231, 0.08);

  transform: translateY(-1px);
}

.input-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 49px;

  flex-shrink: 0;

  color: #a4b0bf;
}

.input-icon svg {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;

  stroke-width: 1.7;

  stroke-linecap: round;
  stroke-linejoin: round;
}

.input-box input {
  width: 100%;
  height: 100%;

  padding: 0 14px 0 0;

  background: transparent;

  border: 0;
  outline: 0;

  color: #172b46;

  font-family: inherit;
  font-size: 13px;
  font-weight: 650;
}

.input-box input::placeholder {
  color: #b4bdc9;

  font-weight: 500;
}

.input-box input:disabled {
  cursor: not-allowed;
}


/* ========================================
   PASSWORD
======================================== */

.password-toggle {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 48px;
  height: 100%;

  flex-shrink: 0;

  background: transparent;
  border: 0;

  color: #9ba8b8;

  cursor: pointer;

  transition: 0.2s ease;
}

.password-toggle:hover {
  color: #247be2;
}

.password-toggle svg {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;

  stroke-width: 1.6;

  stroke-linecap: round;
  stroke-linejoin: round;
}


/* ========================================
   ERROR
======================================== */

.error-message {
  display: flex;
  align-items: center;
  gap: 9px;

  margin-top: -5px;

  padding: 11px 13px;

  background: #fff4f4;

  border:
    1px solid #ffdcdc;

  border-radius: 7px;

  color: #d94a4a;

  font-size: 11px;
  font-weight: 700;
}

.error-message span {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 17px;
  height: 17px;

  background: #e45454;

  border-radius: 50%;

  color: #ffffff;

  font-size: 10px;
  font-weight: 900;
}


/* ========================================
   LOGIN BUTTON
======================================== */

.login-button {
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  height: 55px;

  margin-top: 3px;

  padding: 0 21px;

  background:
    linear-gradient(
      135deg,
      #1168d2,
      #2585ee
    );

  border: 0;
  border-radius: 9px;

  color: #ffffff;

  cursor: pointer;

  font-family: inherit;
  font-size: 13px;
  font-weight: 900;

  box-shadow:
    0 12px 25px rgba(26, 113, 216, 0.2);

  transition: 0.25s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);

  box-shadow:
    0 15px 30px rgba(26, 113, 216, 0.28);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.login-button svg {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;

  stroke-width: 1.8;

  stroke-linecap: round;
  stroke-linejoin: round;

  transition: 0.2s ease;
}

.login-button:hover:not(:disabled) svg {
  transform: translateX(4px);
}


/* ========================================
   LOADING
======================================== */

.spinner {
  width: 15px;
  height: 15px;

  border:
    2px solid rgba(255, 255, 255, 0.35);

  border-top-color: #ffffff;

  border-radius: 50%;

  animation:
    spin 0.7s linear infinite;
}


/* ========================================
   SIGNUP
======================================== */

.signup-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  margin-top: 29px;

  color: #9aa6b5;

  font-size: 11px;
}

.signup-area button {
  padding: 0;

  background: transparent;
  border: 0;

  color: #1c73db;

  cursor: pointer;

  font-family: inherit;
  font-size: 11px;
  font-weight: 900;
}

.signup-area button:hover {
  text-decoration: underline;
}


/* ========================================
   FOOTER
======================================== */

.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;

  margin-top: 52px;

  color: #b0bac7;

  font-size: 8px;
  font-weight: 800;

  letter-spacing: 1px;
}

.security-dot {
  width: 5px;
  height: 5px;

  background: #2ecb80;

  border-radius: 50%;
}

.footer-divider {
  width: 1px;
  height: 10px;

  background: #dfe4ea;
}


/* ========================================
   TRANSITION
======================================== */

.message-enter-active,
.message-leave-active {
  transition: 0.2s ease;
}

.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}


/* ========================================
   ANIMATION
======================================== */

@keyframes backgroundIntro {
  from {
    transform: scale(1.08);
    filter: blur(5px);
  }

  to {
    transform: scale(1);
    filter: blur(0);
  }
}

@keyframes overlayIntro {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes visualContentIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes loginCardIn {
  from {
    opacity: 0;
    transform: translateX(22px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
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

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}


/* ========================================
   RESPONSIVE
======================================== */

@media (max-width: 950px) {
  .login-page {
    grid-template-columns: 0.85fr 1.15fr;
  }

  .visual-content {
    padding: 45px 40px;
  }

  .visual-copy h1 {
    font-size: 45px;
  }

  .login-section {
    padding: 45px;
  }
}


@media (max-width: 760px) {
  .login-page {
    display: block;
  }

  .visual-section {
    display: none;
  }

  .login-section {
    min-height: 100vh;

    padding: 35px 24px;
  }

  .login-wrapper {
    max-width: 430px;
  }

  .login-heading {
    margin-top: 45px;
  }
}
</style>