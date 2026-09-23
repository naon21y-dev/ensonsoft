<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/api'

import logoBlue from '../assets/logo.png'

const router = useRouter()

const username = ref('')
const password = ref('')
const name = ref('')
const email = ref('')

const message = ref('')
const messageType = ref('')
const loading = ref(false)
const showPassword = ref(false)

const signup = async () => {
  if (!username.value.trim()) {
    message.value = '아이디를 입력해주세요.'
    messageType.value = 'error'
    return
  }

  if (!password.value) {
    message.value = '비밀번호를 입력해주세요.'
    messageType.value = 'error'
    return
  }

  if (!name.value.trim()) {
    message.value = '이름을 입력해주세요.'
    messageType.value = 'error'
    return
  }

  if (!email.value.trim()) {
    message.value = '이메일을 입력해주세요.'
    messageType.value = 'error'
    return
  }

  try {
    loading.value = true
    message.value = ''
    messageType.value = ''

    await api.post('/auth/signup', {
      username: username.value.trim(),
      password: password.value,
      name: name.value.trim(),
      email: email.value.trim()
    })

    message.value = '회원가입이 완료되었습니다. 로그인 화면으로 이동합니다.'
    messageType.value = 'success'

    username.value = ''
    password.value = ''
    name.value = ''
    email.value = ''

    setTimeout(() => {
      router.push('/login')
    }, 1200)
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      '회원가입에 실패했습니다.'

    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/')
}

const goLogin = () => {
  router.push('/login')
}
</script>

<template>
  <main class="signup-page">

    <!-- ================================
         LEFT VISUAL
    ================================= -->
    <section class="visual-section">

      <div class="visual-background"></div>
      <div class="visual-overlay"></div>
      <div class="visual-glow"></div>

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
            스마트한 이동의 시작,
            <strong>하나의 플랫폼으로</strong>
          </h1>

          <p>
            실시간 교통 데이터와 현장·장비·유지보수 정보를
            통합하여 더 안전하고 효율적인 모빌리티 환경을 제공합니다.
          </p>

        </div>

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


    <!-- ================================
         RIGHT SIGNUP
    ================================= -->
    <section class="signup-section">

      <div class="signup-wrapper">

        <!-- LOGO -->
        <button
          type="button"
          class="logo-button"
          @click="goHome"
        >
          <img
            :src="logoBlue"
            alt="엔슨소프트"
            class="signup-logo"
          />
        </button>


        <!-- TITLE -->
        <div class="signup-heading">

          <span class="signup-label">
            CREATE ACCOUNT
          </span>

          <h2>회원가입</h2>

          <p>
            스마트 모빌리티 통합 관리 시스템 계정을 생성합니다.
          </p>

        </div>


        <!-- FORM -->
        <form
          class="signup-form"
          @submit.prevent="signup"
        >

          <!-- 아이디 -->
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
                placeholder="사용할 아이디를 입력해주세요"
                autocomplete="username"
                :disabled="loading"
              />

            </div>
          </div>


          <!-- 비밀번호 -->
          <div class="field">

            <label for="password">
              비밀번호
            </label>

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
                autocomplete="new-password"
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


          <!-- 이름 -->
          <div class="field">

            <label for="name">
              이름
            </label>

            <div class="input-box">

              <span class="input-icon">
                <svg viewBox="0 0 24 24">
                  <circle
                    cx="12"
                    cy="8"
                    r="4"
                  />
                  <path d="M4 21a8 8 0 0 1 16 0" />
                </svg>
              </span>

              <input
                id="name"
                v-model="name"
                type="text"
                placeholder="이름을 입력해주세요"
                autocomplete="name"
                :disabled="loading"
              />

            </div>
          </div>


          <!-- 이메일 -->
          <div class="field">

            <label for="email">
              이메일
            </label>

            <div class="input-box">

              <span class="input-icon">
                <svg viewBox="0 0 24 24">
                  <rect
                    x="3"
                    y="5"
                    width="18"
                    height="14"
                    rx="2"
                  />
                  <path d="m4 7 8 6 8-6" />
                </svg>
              </span>

              <input
                id="email"
                v-model="email"
                type="email"
                placeholder="example@ensonsoft.com"
                autocomplete="email"
                :disabled="loading"
              />

            </div>
          </div>


          <!-- 메시지 -->
          <Transition name="message">

            <div
              v-if="message"
              :class="[
                'form-message',
                messageType === 'success'
                  ? 'success-message'
                  : 'error-message'
              ]"
            >
              <span class="message-icon">
                {{ messageType === 'success' ? '✓' : '!' }}
              </span>

              {{ message }}
            </div>

          </Transition>


          <!-- 회원가입 -->
          <button
            type="submit"
            class="signup-button"
            :disabled="loading"
          >

            <template v-if="loading">
              <span class="spinner"></span>
              가입 처리 중...
            </template>

            <template v-else>
              <span>회원가입</span>

              <svg viewBox="0 0 24 24">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>
            </template>

          </button>

        </form>


        <!-- LOGIN -->
        <div class="login-area">

          <span>
            이미 계정이 있으신가요?
          </span>

          <button
            type="button"
            @click="goLogin"
          >
            로그인
          </button>

        </div>


        <div class="signup-footer">

          <span class="security-dot"></span>

          <span>SECURE ACCOUNT</span>

          <span class="footer-divider"></span>

          <span>ENSONSOFT</span>

        </div>

      </div>
    </section>

  </main>
</template>


<style scoped>
/* ========================================
   PAGE
======================================== */

.signup-page {
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;

  width: 100%;
  min-height: 100vh;

  background: #ffffff;
  overflow: hidden;
}


/* ========================================
   LEFT
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
      rgba(3, 17, 37, 0.94) 0%,
      rgba(5, 31, 62, 0.84) 45%,
      rgba(6, 40, 78, 0.67) 100%
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
   LEFT COPY
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
  width: 32px;
  height: 2px;

  background: #3d9dff;
}

.visual-copy h1 {
  max-width: 620px;

  margin: 0;

  color: #ffffff;

  font-size: clamp(40px, 4vw, 65px);
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
  max-width: 520px;

  margin: 28px 0 0;

  color: rgba(220, 231, 243, 0.68);

  font-size: 14px;

  line-height: 1.9;
}


/* ========================================
   STATUS
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
  color: rgba(255, 255, 255, 0.35);

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 1.5px;
}

.state-item strong {
  color: rgba(255, 255, 255, 0.78);

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

  background: rgba(255, 255, 255, 0.12);
}


/* ========================================
   RIGHT SIGNUP
======================================== */

.signup-section {
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: 100vh;

  padding: 38px 60px;

  box-sizing: border-box;

  background:
    linear-gradient(
      145deg,
      #ffffff 0%,
      #fbfcfe 100%
    );
}

.signup-wrapper {
  width: 100%;
  max-width: 410px;

  animation:
    signupCardIn 0.7s
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

.signup-logo {
  display: block;

  width: 155px;
  height: auto;

  object-fit: contain;
}


/* ========================================
   HEADING
======================================== */

.signup-heading {
  margin-top: 30px;
  margin-bottom: 25px;
}

.signup-label {
  display: block;

  margin-bottom: 8px;

  color: #1b75df;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 2.2px;
}

.signup-heading h2 {
  margin: 0;

  color: #10233e;

  font-size: 32px;
  font-weight: 900;

  letter-spacing: -1.6px;
}

.signup-heading p {
  margin: 9px 0 0;

  color: #8996a8;

  font-size: 12px;

  line-height: 1.6;
}


/* ========================================
   FORM
======================================== */

.signup-form {
  display: flex;
  flex-direction: column;

  gap: 14px;
}

.field label {
  display: block;

  margin-bottom: 7px;

  color: #344760;

  font-size: 11px;
  font-weight: 800;
}


/* ========================================
   INPUT
======================================== */

.input-box {
  position: relative;

  display: flex;
  align-items: center;

  height: 49px;

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

  width: 47px;

  flex-shrink: 0;

  color: #a4b0bf;
}

.input-icon svg {
  width: 17px;
  height: 17px;

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
  font-size: 12px;
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
   PASSWORD BUTTON
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
   MESSAGE
======================================== */

.form-message {
  display: flex;
  align-items: center;
  gap: 8px;

  padding: 10px 12px;

  border-radius: 7px;

  font-size: 10px;
  font-weight: 700;
}

.error-message {
  background: #fff4f4;

  border: 1px solid #ffdcdc;

  color: #d94a4a;
}

.success-message {
  background: #effbf5;

  border: 1px solid #c9f0db;

  color: #188754;
}

.message-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 17px;
  height: 17px;

  border-radius: 50%;

  color: #ffffff;

  font-size: 9px;
  font-weight: 900;
}

.error-message .message-icon {
  background: #e45454;
}

.success-message .message-icon {
  background: #26a86b;
}


/* ========================================
   BUTTON
======================================== */

.signup-button {
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  height: 52px;

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

.signup-button:hover:not(:disabled) {
  transform: translateY(-2px);

  box-shadow:
    0 15px 30px rgba(26, 113, 216, 0.28);
}

.signup-button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.signup-button svg {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;

  stroke-width: 1.8;

  stroke-linecap: round;
  stroke-linejoin: round;

  transition: 0.2s ease;
}

.signup-button:hover:not(:disabled) svg {
  transform: translateX(4px);
}

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
   LOGIN LINK
======================================== */

.login-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  margin-top: 20px;

  color: #9aa6b5;

  font-size: 11px;
}

.login-area button {
  padding: 0;

  background: transparent;
  border: 0;

  color: #1c73db;

  cursor: pointer;

  font-family: inherit;
  font-size: 11px;
  font-weight: 900;
}

.login-area button:hover {
  text-decoration: underline;
}


/* ========================================
   FOOTER
======================================== */

.signup-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;

  margin-top: 25px;

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

@keyframes signupCardIn {
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
  .signup-page {
    grid-template-columns: 0.85fr 1.15fr;
  }

  .visual-content {
    padding: 45px 40px;
  }

  .visual-copy h1 {
    font-size: 44px;
  }

  .signup-section {
    padding: 35px 45px;
  }
}

@media (max-width: 760px) {
  .signup-page {
    display: block;
  }

  .visual-section {
    display: none;
  }

  .signup-section {
    min-height: 100vh;

    padding: 30px 24px;
  }

  .signup-wrapper {
    max-width: 430px;
  }
}
</style>