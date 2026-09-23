<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/api'

const router = useRouter()

const title = ref('')
const content = ref('')
const message = ref('')
const submitting = ref(false)

const titleLength = computed(() => title.value.length)
const contentLength = computed(() => content.value.length)

// 게시글 등록
const createBoard = async () => {
  if (submitting.value) return

  message.value = ''

  if (!title.value.trim()) {
    message.value = '제목을 입력해주세요.'
    return
  }

  if (!content.value.trim()) {
    message.value = '내용을 입력해주세요.'
    return
  }

  submitting.value = true

  try {
    const response = await api.post('/boards', {
      title: title.value.trim(),
      content: content.value.trim()
    })

    alert('게시글이 등록되었습니다.')

    router.push(`/boards/${response.data.id}`)
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      '게시글 등록에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}

// 목록으로 돌아가기
const goList = () => {
  router.push('/boards')
}
</script>

<template>
  <div class="write-page">

    <!-- =====================================
         HERO
    ====================================== -->
    <section class="write-hero">
      <div>
        <div class="eyebrow">
          <span class="eyebrow-dot"></span>
          SMART MOBILITY / BOARD
        </div>

        <h1>게시글 작성</h1>

        <p>
          운영 공지 및 시스템 관련 게시글을 등록하세요.
        </p>
      </div>

      <button
        type="button"
        class="hero-list-button"
        @click="goList"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>

        목록으로
      </button>
    </section>


    <!-- =====================================
         ERROR MESSAGE
    ====================================== -->
    <div
      v-if="message"
      class="error-message"
    >
      <span>!</span>

      {{ message }}
    </div>


    <!-- =====================================
         WRITE CARD
    ====================================== -->
    <section class="write-card">

      <!-- 카드 상단 -->
      <header class="card-header">
        <div>
          <div class="header-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M4 20h4L19 9a2.12 2.12 0 0 0-3-3L5 17v3ZM14.5 7.5l3 3"
                fill="none"
                stroke="currentColor"
                stroke-width="1.7"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>

          <div>
            <h2>새 게시글</h2>
            <p>게시글의 제목과 내용을 입력해주세요.</p>
          </div>
        </div>

        <div class="required-guide">
          <span>*</span>
          필수 입력 항목
        </div>
      </header>


      <!-- =====================================
           FORM
      ====================================== -->
      <div class="form-area">

        <!-- 제목 -->
        <div class="form-group">
          <div class="label-row">
            <label for="board-title">
              제목
              <span>*</span>
            </label>

            <small>
              {{ titleLength }}자
            </small>
          </div>

          <input
            id="board-title"
            v-model="title"
            type="text"
            maxlength="200"
            placeholder="게시글 제목을 입력하세요."
            @keyup.enter.prevent
          />

          <p class="field-help">
            게시글의 내용을 쉽게 확인할 수 있는 제목을 입력해주세요.
          </p>
        </div>


        <!-- 구분선 -->
        <div class="form-divider"></div>


        <!-- 내용 -->
        <div class="form-group">
          <div class="label-row">
            <label for="board-content">
              내용
              <span>*</span>
            </label>

            <small>
              {{ contentLength.toLocaleString('ko-KR') }}자
            </small>
          </div>

          <textarea
            id="board-content"
            v-model="content"
            rows="14"
            placeholder="게시글 내용을 입력하세요."
          ></textarea>

          <p class="field-help">
            필요한 내용을 자세하게 작성해주세요.
          </p>
        </div>

      </div>


      <!-- =====================================
           FOOTER
      ====================================== -->
      <footer class="form-footer">
        <div class="footer-guide">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20Zm0-11v6M12 7.5v.01"
              fill="none"
              stroke="currentColor"
              stroke-width="1.6"
              stroke-linecap="round"
            />
          </svg>

          등록 후 게시판에서 바로 확인할 수 있습니다.
        </div>

        <div class="button-group">
          <button
            type="button"
            class="cancel-button"
            :disabled="submitting"
            @click="goList"
          >
            취소
          </button>

          <button
            type="button"
            class="submit-button"
            :disabled="submitting"
            @click="createBoard"
          >
            <svg
              v-if="!submitting"
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                d="M12 5v14M5 12h14"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
              />
            </svg>

            <span
              v-else
              class="button-spinner"
            ></span>

            {{ submitting ? '등록 중...' : '게시글 등록' }}
          </button>
        </div>
      </footer>

    </section>

  </div>
</template>

<style scoped>
/* =========================================
   PAGE
========================================= */

.write-page {
  width: 100%;
  min-height: 100vh;

  padding: 30px 34px 60px;

  color: #0f172a;

  animation: pageEnter 0.45s ease both;
}

@keyframes pageEnter {
  from {
    opacity: 0;
    transform: translateY(8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}


/* =========================================
   HERO
========================================= */

.write-hero {
  min-height: 142px;

  position: relative;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 30px;

  margin-bottom: 30px;
  padding: 27px 30px;

  overflow: hidden;

  border-radius: 18px;

  color: #ffffff;

  background:
    radial-gradient(
      circle at 85% 15%,
      rgba(62, 142, 236, 0.14) 0,
      rgba(62, 142, 236, 0.14) 105px,
      transparent 106px
    ),
    radial-gradient(
      circle at 85% 15%,
      transparent 0,
      transparent 135px,
      rgba(82, 157, 243, 0.06) 136px,
      rgba(82, 157, 243, 0.06) 175px,
      transparent 176px
    ),
    linear-gradient(
      120deg,
      #102946,
      #17375d
    );

  box-shadow:
    0 12px 35px rgba(13, 42, 75, 0.08);
}

.write-hero::after {
  content: "";

  position: absolute;

  right: 12%;
  bottom: -90px;

  width: 220px;
  height: 220px;

  border: 1px solid rgba(94, 183, 255, 0.07);

  border-radius: 50%;
}

.eyebrow {
  display: flex;
  align-items: center;

  gap: 8px;

  margin-bottom: 11px;

  color: #79bfff;

  font-size: 9px;
  font-weight: 800;

  letter-spacing: 1.7px;
}

.eyebrow-dot {
  width: 5px;
  height: 5px;

  border-radius: 50%;

  background: #53d5d2;

  box-shadow:
    0 0 10px rgba(83, 213, 210, 0.55);
}

.write-hero h1 {
  color: #ffffff;

  font-size: 28px;
  font-weight: 800;

  letter-spacing: -1px;
}

.write-hero p {
  margin-top: 8px;

  color: rgba(225, 237, 250, 0.72);

  font-size: 12px;
}

.hero-list-button {
  position: relative;
  z-index: 2;

  height: 42px;

  display: inline-flex;
  align-items: center;
  justify-content: center;

  gap: 7px;

  padding: 0 17px;

  border: 1px solid rgba(255, 255, 255, 0.18);

  border-radius: 9px;

  color: #ffffff;

  background: rgba(255, 255, 255, 0.07);

  backdrop-filter: blur(10px);

  font-size: 11px;
  font-weight: 700;

  cursor: pointer;

  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    transform 0.2s ease;
}

.hero-list-button svg {
  width: 16px;
  height: 16px;
}

.hero-list-button:hover {
  background: rgba(255, 255, 255, 0.13);

  border-color: rgba(255, 255, 255, 0.3);

  transform: translateY(-1px);
}


/* =========================================
   ERROR
========================================= */

.error-message {
  display: flex;
  align-items: center;

  gap: 9px;

  margin-bottom: 20px;
  padding: 12px 15px;

  border: 1px solid #fecdd3;

  border-radius: 10px;

  color: #be123c;

  background: #fff1f2;

  font-size: 11px;
  font-weight: 600;

  animation: errorEnter 0.25s ease both;
}

.error-message > span {
  width: 20px;
  height: 20px;

  flex: 0 0 20px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  color: #ffffff;

  background: #ef4444;

  font-size: 11px;
  font-weight: 800;
}

@keyframes errorEnter {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}


/* =========================================
   WRITE CARD
========================================= */

.write-card {
  overflow: hidden;

  border: 1px solid #e1e8f0;

  border-radius: 15px;

  background: #ffffff;

  box-shadow:
    0 3px 12px rgba(15, 23, 42, 0.035);
}


/* =========================================
   CARD HEADER
========================================= */

.card-header {
  min-height: 82px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 20px;

  padding: 18px 24px;

  border-bottom: 1px solid #edf1f5;
}

.card-header > div:first-child {
  display: flex;
  align-items: center;

  gap: 12px;
}

.header-icon {
  width: 38px;
  height: 38px;

  flex: 0 0 38px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 10px;

  color: #3b82f6;

  background: #eff6ff;
}

.header-icon svg {
  width: 17px;
  height: 17px;
}

.card-header h2 {
  color: #1e293b;

  font-size: 14px;
  font-weight: 800;
}

.card-header p {
  margin-top: 4px;

  color: #94a3b8;

  font-size: 10px;
}

.required-guide {
  color: #94a3b8;

  font-size: 9px;
  font-weight: 600;
}

.required-guide span {
  color: #ef4444;
}


/* =========================================
   FORM
========================================= */

.form-area {
  padding: 32px 34px 38px;
}

.form-group {
  width: 100%;
}

.label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 20px;

  margin-bottom: 10px;
}

.label-row label {
  color: #334155;

  font-size: 12px;
  font-weight: 800;
}

.label-row label span {
  margin-left: 2px;

  color: #ef4444;
}

.label-row small {
  color: #94a3b8;

  font-size: 9px;

  font-variant-numeric: tabular-nums;
}

.form-group input,
.form-group textarea {
  width: 100%;

  border: 1px solid #dce4ee;

  border-radius: 9px;

  color: #334155;

  background: #ffffff;

  font-size: 12px;

  outline: none;

  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.form-group input {
  height: 44px;

  padding: 0 14px;
}

.form-group textarea {
  min-height: 310px;

  padding: 15px 16px;

  line-height: 1.8;

  resize: vertical;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #a7b2c1;
}

.form-group input:hover,
.form-group textarea:hover {
  border-color: #c9d5e2;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #6baef3;

  background: #ffffff;

  box-shadow:
    0 0 0 3px rgba(37, 132, 235, 0.09);
}

.field-help {
  margin-top: 7px;

  color: #9aa7b7;

  font-size: 9px;
}

.form-divider {
  height: 1px;

  margin: 27px 0;

  background: #edf1f5;
}


/* =========================================
   FOOTER
========================================= */

.form-footer {
  min-height: 78px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 20px;

  padding: 17px 24px;

  border-top: 1px solid #edf1f5;

  background: #fafbfd;
}

.footer-guide {
  display: flex;
  align-items: center;

  gap: 7px;

  color: #94a3b8;

  font-size: 9px;
}

.footer-guide svg {
  width: 14px;
  height: 14px;

  color: #7e9fc4;
}

.button-group {
  display: flex;

  gap: 8px;
}

.cancel-button,
.submit-button {
  height: 40px;

  display: inline-flex;
  align-items: center;
  justify-content: center;

  gap: 7px;

  padding: 0 17px;

  border-radius: 9px;

  font-size: 11px;
  font-weight: 700;

  cursor: pointer;

  transition:
    transform 0.18s ease,
    border-color 0.18s ease,
    background 0.18s ease,
    box-shadow 0.18s ease;
}

.cancel-button {
  border: 1px solid #dce4ee;

  color: #64748b;

  background: #ffffff;
}

.cancel-button:hover:not(:disabled) {
  color: #2563eb;

  border-color: #b8d3ef;

  background: #f8fbff;

  transform: translateY(-1px);
}

.submit-button {
  min-width: 120px;

  color: #ffffff;

  background:
    linear-gradient(
      135deg,
      #278df2,
      #176fc9
    );

  box-shadow:
    0 6px 16px rgba(32, 116, 201, 0.18);
}

.submit-button svg {
  width: 15px;
  height: 15px;
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-1px);

  box-shadow:
    0 8px 21px rgba(32, 116, 201, 0.25);
}

.cancel-button:disabled,
.submit-button:disabled {
  opacity: 0.55;

  cursor: not-allowed;
}


/* =========================================
   BUTTON LOADING
========================================= */

.button-spinner {
  width: 13px;
  height: 13px;

  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #ffffff;

  border-radius: 50%;

  animation: spinner 0.7s linear infinite;
}

@keyframes spinner {
  to {
    transform: rotate(360deg);
  }
}


/* =========================================
   RESPONSIVE
========================================= */

@media (max-width: 900px) {
  .write-page {
    padding: 24px 22px 45px;
  }

  .form-area {
    padding: 28px 25px 34px;
  }
}

@media (max-width: 650px) {
  .write-hero {
    min-height: 165px;

    align-items: flex-start;
    flex-direction: column;

    padding: 24px;
  }

  .hero-list-button {
    align-self: flex-end;
  }

  .form-footer {
    align-items: stretch;
    flex-direction: column;
  }

  .footer-guide {
    justify-content: center;
  }

  .button-group {
    width: 100%;
  }

  .cancel-button,
  .submit-button {
    flex: 1;
  }
}

@media (max-width: 560px) {
  .write-page {
    padding: 18px 14px 35px;
  }

  .write-hero {
    margin-bottom: 20px;

    border-radius: 14px;
  }

  .write-hero h1 {
    font-size: 24px;
  }

  .card-header {
    padding: 17px 19px;
  }

  .required-guide {
    display: none;
  }

  .form-area {
    padding: 24px 19px 30px;
  }

  .form-group textarea {
    min-height: 260px;
  }

  .form-footer {
    padding: 16px 19px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .write-page,
  .error-message,
  .button-spinner {
    animation: none;
  }
}
</style>