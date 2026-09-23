<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/api'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const board = ref(null)
const message = ref('')
const loading = ref(true)
const deleting = ref(false)

// 작성자 본인 또는 ADMIN인지 확인
const canEdit = computed(() => {
  if (!board.value) {
    return false
  }

  return (
    board.value.writer === authStore.username ||
    authStore.isAdmin
  )
})

// 게시글 상세 조회
const getBoard = async () => {
  loading.value = true

  try {
    const response = await api.get(
      `/boards/${route.params.id}`
    )

    board.value = response.data
    message.value = ''
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      '게시글을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

// 목록으로 이동
const goList = () => {
  router.push('/boards')
}

// 수정 페이지 이동
const goEdit = () => {
  if (!board.value) return

  router.push(`/boards/${board.value.id}/edit`)
}

// 게시글 삭제
const deleteBoard = async () => {
  if (!board.value || deleting.value) return

  const confirmed = window.confirm(
    '게시글을 삭제하시겠습니까?'
  )

  if (!confirmed) {
    return
  }

  deleting.value = true

  try {
    await api.delete(
      `/boards/${board.value.id}`
    )

    alert('게시글이 삭제되었습니다.')

    router.push('/boards')
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      '게시글 삭제에 실패했습니다.'
  } finally {
    deleting.value = false
  }
}

// 날짜 표시
const formatDateTime = (date) => {
  if (!date) return '-'

  const value = new Date(date)

  if (Number.isNaN(value.getTime())) {
    return date
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(value)
}

onMounted(() => {
  getBoard()
})
</script>

<template>
  <div class="detail-page">

    <!-- =====================================
         HERO
    ====================================== -->
    <section class="detail-hero">
      <div>
        <div class="eyebrow">
          <span class="eyebrow-dot"></span>
          SMART MOBILITY / BOARD
        </div>

        <h1>게시글 상세</h1>

        <p>
          운영 공지 및 게시글의 상세 내용을 확인하세요.
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
         ERROR
    ====================================== -->
    <div
      v-if="message"
      class="error-message"
    >
      <span>!</span>

      {{ message }}
    </div>


    <!-- =====================================
         LOADING
    ====================================== -->
    <section
      v-if="loading"
      class="state-card"
    >
      <div class="spinner"></div>

      <strong>게시글을 불러오는 중입니다.</strong>

      <p>
        잠시만 기다려주세요.
      </p>
    </section>


    <!-- =====================================
         BOARD DETAIL
    ====================================== -->
    <article
      v-else-if="board"
      class="detail-card"
    >
      <!-- 게시글 상단 -->
      <header class="article-header">

        <div class="article-number">
          BOARD
          <strong>#{{ board.id }}</strong>
        </div>

        <h2>
          {{ board.title }}
        </h2>

        <div class="article-meta">

          <!-- 작성자 -->
          <div class="meta-item">
            <div class="writer-avatar">
              {{
                board.writer
                  ?.charAt(0)
                  ?.toUpperCase() || 'U'
              }}
            </div>

            <div class="meta-text">
              <span>작성자</span>
              <strong>{{ board.writer }}</strong>
            </div>
          </div>

          <span class="meta-divider"></span>

          <!-- 조회수 -->
          <div class="meta-item">
            <div class="meta-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Zm9.5 2.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                />
              </svg>
            </div>

            <div class="meta-text">
              <span>조회수</span>
              <strong>
                {{ board.viewCount ?? 0 }}
              </strong>
            </div>
          </div>

          <span class="meta-divider"></span>

          <!-- 작성일 -->
          <div class="meta-item">
            <div class="meta-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  d="M6 3v3M18 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14H4V6a1 1 0 0 1 1-1Z"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  stroke-linecap="round"
                />
              </svg>
            </div>

            <div class="meta-text">
              <span>작성일</span>
              <strong>
                {{ formatDateTime(board.createdAt) }}
              </strong>
            </div>
          </div>

          <!-- 수정일 -->
          <template
            v-if="
              board.updatedAt &&
              board.updatedAt !== board.createdAt
            "
          >
            <span class="meta-divider"></span>

            <div class="meta-item">
              <div class="meta-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path
                    d="M4 20h4l10.5-10.5a2.12 2.12 0 0 0-3-3L5 17v3ZM14 8l3 3"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.5"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </div>

              <div class="meta-text">
                <span>수정일</span>
                <strong>
                  {{ formatDateTime(board.updatedAt) }}
                </strong>
              </div>
            </div>
          </template>

        </div>
      </header>


      <!-- 게시글 내용 -->
      <section class="article-content">
        <div class="content-label">
          CONTENT
        </div>

        <div class="content-body">
          {{ board.content }}
        </div>
      </section>


      <!-- 하단 버튼 -->
      <footer class="article-footer">

        <button
          type="button"
          class="list-button"
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

          목록
        </button>


        <!-- 작성자 또는 관리자 -->
        <div
          v-if="canEdit"
          class="manage-buttons"
        >
          <button
            type="button"
            class="edit-button"
            @click="goEdit"
          >
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

            수정
          </button>

          <button
            type="button"
            class="delete-button"
            :disabled="deleting"
            @click="deleteBoard"
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M4 7h16M9 7V4h6v3M7 7l1 13h8l1-13M10 11v5M14 11v5"
                fill="none"
                stroke="currentColor"
                stroke-width="1.7"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>

            {{ deleting ? '삭제 중...' : '삭제' }}
          </button>
        </div>

      </footer>
    </article>

  </div>
</template>

<style scoped>
/* =========================================
   PAGE
========================================= */

.detail-page {
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

.detail-hero {
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

.detail-hero::after {
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

.detail-hero h1 {
  color: #ffffff;

  font-size: 28px;
  font-weight: 800;

  letter-spacing: -1px;
}

.detail-hero p {
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
   DETAIL CARD
========================================= */

.detail-card {
  overflow: hidden;

  border: 1px solid #e1e8f0;

  border-radius: 15px;

  background: #ffffff;

  box-shadow:
    0 3px 12px rgba(15, 23, 42, 0.035);
}


/* =========================================
   ARTICLE HEADER
========================================= */

.article-header {
  padding: 30px 32px 26px;

  border-bottom: 1px solid #edf1f5;
}

.article-number {
  display: flex;
  align-items: center;

  gap: 6px;

  margin-bottom: 13px;

  color: #94a3b8;

  font-size: 9px;
  font-weight: 800;

  letter-spacing: 1.2px;
}

.article-number strong {
  color: #3b82f6;
}

.article-header h2 {
  color: #17263b;

  font-size: 24px;
  font-weight: 800;

  line-height: 1.45;

  letter-spacing: -0.7px;
}

.article-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;

  gap: 18px;

  margin-top: 25px;
}

.meta-item {
  display: flex;
  align-items: center;

  gap: 9px;
}

.writer-avatar,
.meta-icon {
  width: 32px;
  height: 32px;

  flex: 0 0 32px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 8px;
}

.writer-avatar {
  color: #2563eb;

  background: #eef5ff;

  font-size: 10px;
  font-weight: 800;
}

.meta-icon {
  color: #6b8fb9;

  background: #f4f7fb;
}

.meta-icon svg {
  width: 15px;
  height: 15px;
}

.meta-text {
  display: flex;
  flex-direction: column;

  gap: 2px;
}

.meta-text span {
  color: #9aa7b7;

  font-size: 9px;
  font-weight: 600;
}

.meta-text strong {
  color: #475569;

  font-size: 10px;
  font-weight: 700;
}

.meta-divider {
  width: 1px;
  height: 28px;

  background: #edf1f5;
}


/* =========================================
   CONTENT
========================================= */

.article-content {
  min-height: 350px;

  padding: 31px 32px 50px;
}

.content-label {
  margin-bottom: 23px;

  color: #3b82f6;

  font-size: 9px;
  font-weight: 800;

  letter-spacing: 1.5px;
}

.content-body {
  color: #334155;

  font-size: 13px;

  line-height: 1.9;

  white-space: pre-wrap;

  word-break: break-word;
}


/* =========================================
   FOOTER BUTTONS
========================================= */

.article-footer {
  min-height: 82px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 15px;

  padding: 18px 32px;

  border-top: 1px solid #edf1f5;

  background: #fafbfd;
}

.manage-buttons {
  display: flex;

  gap: 8px;
}

.list-button,
.edit-button,
.delete-button {
  height: 38px;

  display: inline-flex;
  align-items: center;
  justify-content: center;

  gap: 6px;

  padding: 0 15px;

  border-radius: 8px;

  font-size: 10px;
  font-weight: 700;

  cursor: pointer;

  transition:
    transform 0.18s ease,
    background 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease;
}

.list-button svg,
.edit-button svg,
.delete-button svg {
  width: 14px;
  height: 14px;
}

.list-button {
  border: 1px solid #dce4ee;

  color: #64748b;

  background: #ffffff;
}

.list-button:hover {
  color: #2563eb;

  border-color: #b8d3ef;

  background: #f8fbff;

  transform: translateY(-1px);
}

.edit-button {
  color: #ffffff;

  background:
    linear-gradient(
      135deg,
      #278df2,
      #176fc9
    );

  box-shadow:
    0 5px 14px rgba(32, 116, 201, 0.16);
}

.edit-button:hover {
  transform: translateY(-1px);

  box-shadow:
    0 7px 18px rgba(32, 116, 201, 0.23);
}

.delete-button {
  border: 1px solid #fecdd3;

  color: #dc2626;

  background: #fff7f7;
}

.delete-button:hover:not(:disabled) {
  border-color: #fda4af;

  background: #fff1f2;

  transform: translateY(-1px);
}

.delete-button:disabled {
  opacity: 0.55;

  cursor: not-allowed;
}


/* =========================================
   ERROR / LOADING
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
}

.error-message > span {
  width: 20px;
  height: 20px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  color: #ffffff;

  background: #ef4444;

  font-size: 11px;
  font-weight: 800;
}

.state-card {
  min-height: 350px;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  border: 1px solid #e1e8f0;

  border-radius: 15px;

  background: #ffffff;
}

.state-card strong {
  margin-top: 15px;

  color: #334155;

  font-size: 13px;
}

.state-card p {
  margin-top: 5px;

  color: #94a3b8;

  font-size: 10px;
}

.spinner {
  width: 30px;
  height: 30px;

  border: 3px solid #e5edf6;
  border-top-color: #3b82f6;

  border-radius: 50%;

  animation: spinner 0.75s linear infinite;
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
  .detail-page {
    padding: 24px 22px 45px;
  }

  .article-header,
  .article-content {
    padding-left: 24px;
    padding-right: 24px;
  }

  .article-footer {
    padding-left: 24px;
    padding-right: 24px;
  }
}

@media (max-width: 650px) {
  .detail-hero {
    min-height: 165px;

    align-items: flex-start;
    flex-direction: column;

    padding: 24px;
  }

  .hero-list-button {
    align-self: flex-end;
  }

  .article-header h2 {
    font-size: 20px;
  }

  .article-meta {
    align-items: flex-start;
    flex-direction: column;

    gap: 13px;
  }

  .meta-divider {
    display: none;
  }

  .article-footer {
    align-items: stretch;
    flex-direction: column;
  }

  .manage-buttons {
    width: 100%;
  }

  .list-button,
  .manage-buttons button {
    flex: 1;
  }
}

@media (max-width: 560px) {
  .detail-page {
    padding: 18px 14px 35px;
  }

  .detail-hero {
    margin-bottom: 20px;

    border-radius: 14px;
  }

  .detail-hero h1 {
    font-size: 24px;
  }

  .article-header {
    padding: 24px 20px;
  }

  .article-content {
    min-height: 280px;

    padding: 25px 20px 40px;
  }

  .article-footer {
    padding: 16px 20px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .detail-page,
  .spinner {
    animation: none;
  }
}
</style>