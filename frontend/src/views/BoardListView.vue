<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/api'

const router = useRouter()

const boards = ref([])
const keyword = ref('')
const message = ref('')
const loading = ref(false)

// 게시글 목록 조회
const getBoards = async () => {
  loading.value = true

  try {
    const response = await api.get('/boards', {
      params: {
        keyword: keyword.value || undefined
      }
    })

    boards.value = response.data
    message.value = ''
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message || '게시글 조회에 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 검색
const searchBoards = () => {
  getBoards()
}

// 검색 초기화
const resetSearch = () => {
  keyword.value = ''
  getBoards()
}

// 상세 페이지 이동
const goDetail = (id) => {
  router.push(`/boards/${id}`)
}

// 글쓰기 이동
const goWrite = () => {
  router.push('/boards/write')
}

// 날짜 표시
const formatDate = (date) => {
  if (!date) return '-'

  const value = new Date(date)

  if (Number.isNaN(value.getTime())) {
    return date
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(value)
}

// 화면 처음 열릴 때 목록 조회
onMounted(() => {
  getBoards()
})
</script>

<template>
  <div class="board-page">

    <!-- =====================================
         HEADER
    ====================================== -->
    <section class="board-header">
      <div>
        <div class="eyebrow">
          <span class="eyebrow-dot"></span>
          SMART MOBILITY / BOARD
        </div>

        <h1>게시판</h1>

        <p>
          운영 공지 및 시스템 관련 게시글을 확인하세요.
        </p>
      </div>

      <button
        type="button"
        class="write-button"
        @click="goWrite"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M12 5v14M5 12h14"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>

        글쓰기
      </button>
    </section>


    <!-- =====================================
         SEARCH
    ====================================== -->
    <section class="search-card">
      <div class="search-info">
        <div class="search-icon-box">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="m21 21-4.35-4.35m2.35-5.15A7.5 7.5 0 1 1 4 11.5a7.5 7.5 0 0 1 15 0Z"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
            />
          </svg>
        </div>

        <div>
          <strong>게시글 검색</strong>
          <span>제목으로 게시글을 검색할 수 있습니다.</span>
        </div>
      </div>

      <div class="search-area">
        <div class="search-input-wrap">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="m21 21-4.35-4.35m2.35-5.15A7.5 7.5 0 1 1 4 11.5a7.5 7.5 0 0 1 15 0Z"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
            />
          </svg>

          <input
            v-model="keyword"
            type="text"
            placeholder="검색할 제목을 입력하세요."
            @keyup.enter="searchBoards"
          />
        </div>

        <button
          type="button"
          class="search-button"
          @click="searchBoards"
        >
          검색
        </button>

        <button
          type="button"
          class="reset-button"
          @click="resetSearch"
        >
          초기화
        </button>
      </div>
    </section>


    <!-- 오류 메시지 -->
    <div
      v-if="message"
      class="error-message"
    >
      <span>!</span>
      {{ message }}
    </div>


    <!-- =====================================
         BOARD LIST
    ====================================== -->
    <section class="board-card">

      <!-- 목록 헤더 -->
      <div class="board-card-header">
        <div>
          <h2>게시글 목록</h2>

          <p>
            등록된 게시글을 확인할 수 있습니다.
          </p>
        </div>

        <div class="board-count">
          전체
          <strong>{{ boards.length }}</strong>
          건
        </div>
      </div>


      <!-- 로딩 -->
      <div
        v-if="loading"
        class="board-state"
      >
        <div class="loading-spinner"></div>

        <strong>게시글을 불러오는 중입니다.</strong>
        <span>잠시만 기다려주세요.</span>
      </div>


      <!-- 게시글 없음 -->
      <div
        v-else-if="boards.length === 0"
        class="board-state"
      >
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="M4 4h16v16H4V4Zm4 5h8M8 13h8M8 17h5"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </div>

        <strong>등록된 게시글이 없습니다.</strong>

        <span>
          새로운 게시글을 작성해보세요.
        </span>

        <button
          type="button"
          class="empty-write-button"
          @click="goWrite"
        >
          게시글 작성
        </button>
      </div>


      <!-- 게시판 테이블 -->
      <div
        v-else
        class="table-wrap"
      >
        <table class="board-table">
          <thead>
            <tr>
              <th class="number-column">번호</th>
              <th>제목</th>
              <th class="writer-column">작성자</th>
              <th class="view-column">조회수</th>
              <th class="date-column">작성일</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="(board, index) in boards"
              :key="board.id"
              class="board-row"
              :style="{ animationDelay: `${index * 45}ms` }"
              @click="goDetail(board.id)"
            >
              <td class="board-number">
                {{ board.id }}
              </td>

              <td>
                <button
                  type="button"
                  class="board-title"
                  @click.stop="goDetail(board.id)"
                >
                  <span class="document-icon">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                      <path
                        d="M6 3h8l4 4v14H6V3Zm8 0v5h5M9 12h6M9 16h6"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="1.5"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                  </span>

                  <span>
                    {{ board.title }}
                  </span>
                </button>
              </td>

              <td>
                <div class="writer">
                  <span class="writer-avatar">
                    {{
                      board.writer
                        ?.charAt(0)
                        ?.toUpperCase() || 'U'
                    }}
                  </span>

                  {{ board.writer }}
                </div>
              </td>

              <td>
                <div class="view-count">
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path
                      d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Zm9.5 2.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                  </svg>

                  {{ board.viewCount ?? 0 }}
                </div>
              </td>

              <td class="board-date">
                {{ formatDate(board.createdAt) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </section>

  </div>
</template>

<style scoped>
/* =========================================
   BOARD PAGE
========================================= */

.board-page {
  width: 100%;
  min-height: 100vh;

  padding: 30px 34px 60px;

  color: #0f172a;

  animation: boardPageEnter 0.45s ease both;
}

@keyframes boardPageEnter {
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
   HEADER
========================================= */

.board-header {
  min-height: 142px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 30px;

  margin-bottom: 30px;
  padding: 27px 30px;

  position: relative;

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

.board-header::after {
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

.board-header h1 {
  color: #ffffff;

  font-size: 28px;
  font-weight: 800;

  letter-spacing: -1px;
}

.board-header p {
  margin-top: 8px;

  color: rgba(225, 237, 250, 0.72);

  font-size: 12px;
}

.write-button {
  position: relative;
  z-index: 2;

  min-width: 108px;
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

  font-size: 12px;
  font-weight: 700;

  cursor: pointer;

  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    transform 0.2s ease;
}

.write-button svg {
  width: 16px;
  height: 16px;
}

.write-button:hover {
  background: rgba(255, 255, 255, 0.13);

  border-color: rgba(255, 255, 255, 0.3);

  transform: translateY(-1px);
}


/* =========================================
   SEARCH CARD
========================================= */

.search-card {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 25px;

  margin-bottom: 22px;
  padding: 20px 22px;

  border: 1px solid #e2e8f0;

  border-radius: 14px;

  background: #ffffff;

  box-shadow:
    0 2px 8px rgba(15, 23, 42, 0.025);
}

.search-info {
  display: flex;
  align-items: center;

  gap: 12px;

  flex-shrink: 0;
}

.search-icon-box {
  width: 38px;
  height: 38px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 10px;

  color: #3b82f6;

  background: #eff6ff;
}

.search-icon-box svg {
  width: 18px;
  height: 18px;
}

.search-info > div:last-child {
  display: flex;
  flex-direction: column;

  gap: 3px;
}

.search-info strong {
  color: #1e293b;

  font-size: 13px;
}

.search-info span {
  color: #94a3b8;

  font-size: 10px;
}

.search-area {
  width: min(620px, 100%);

  display: flex;
  align-items: center;

  gap: 8px;
}

.search-input-wrap {
  position: relative;

  flex: 1;
}

.search-input-wrap > svg {
  position: absolute;

  top: 50%;
  left: 13px;

  width: 16px;
  height: 16px;

  color: #94a3b8;

  transform: translateY(-50%);

  pointer-events: none;
}

.search-input-wrap input {
  width: 100%;
  height: 40px;

  padding:
    0
    14px
    0
    39px;

  border: 1px solid #dce4ee;

  border-radius: 9px;

  color: #334155;

  background: #ffffff;

  font-size: 12px;

  outline: none;

  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.search-input-wrap input::placeholder {
  color: #a7b2c1;
}

.search-input-wrap input:focus {
  border-color: #74b4f9;

  box-shadow:
    0 0 0 3px rgba(37, 132, 235, 0.09);
}

.search-button,
.reset-button {
  height: 40px;

  padding: 0 17px;

  border-radius: 9px;

  font-size: 11px;
  font-weight: 700;

  cursor: pointer;

  transition:
    transform 0.2s ease,
    background 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.search-button {
  color: #ffffff;

  background:
    linear-gradient(
      135deg,
      #278df2,
      #176fc9
    );

  box-shadow:
    0 6px 16px rgba(32, 116, 201, 0.17);
}

.search-button:hover {
  transform: translateY(-1px);

  box-shadow:
    0 8px 20px rgba(32, 116, 201, 0.23);
}

.reset-button {
  border: 1px solid #dce4ee;

  color: #64748b;

  background: #ffffff;
}

.reset-button:hover {
  color: #2563eb;

  border-color: #b8d3ef;

  background: #f8fbff;
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
}

.error-message span {
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


/* =========================================
   BOARD CARD
========================================= */

.board-card {
  overflow: hidden;

  border: 1px solid #e1e8f0;

  border-radius: 15px;

  background: #ffffff;

  box-shadow:
    0 3px 12px rgba(15, 23, 42, 0.035);
}

.board-card-header {
  min-height: 82px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 20px;

  padding: 19px 22px;

  border-bottom: 1px solid #edf1f5;
}

.board-card-header h2 {
  color: #1e293b;

  font-size: 15px;
  font-weight: 800;
}

.board-card-header p {
  margin-top: 5px;

  color: #94a3b8;

  font-size: 10px;
}

.board-count {
  display: flex;
  align-items: center;

  gap: 4px;

  color: #94a3b8;

  font-size: 10px;
}

.board-count strong {
  color: #2563eb;

  font-size: 14px;
}


/* =========================================
   TABLE
========================================= */

.table-wrap {
  width: 100%;

  overflow-x: auto;
}

.board-table {
  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

.board-table thead {
  background: #f8fafc;
}

.board-table th {
  height: 43px;

  padding: 0 18px;

  border-bottom: 1px solid #e7edf4;

  color: #718096;

  font-size: 10px;
  font-weight: 800;

  text-align: left;

  white-space: nowrap;
}

.board-table td {
  height: 64px;

  padding: 0 18px;

  border-bottom: 1px solid #edf1f5;

  color: #475569;

  font-size: 11px;

  vertical-align: middle;
}

.board-table tbody tr:last-child td {
  border-bottom: 0;
}

.number-column {
  width: 85px;
}

.writer-column {
  width: 160px;
}

.view-column {
  width: 110px;
}

.date-column {
  width: 150px;
}

.board-row {
  cursor: pointer;

  opacity: 0;

  animation:
    rowEnter 0.38s ease forwards;

  transition:
    background 0.18s ease,
    box-shadow 0.18s ease;
}

@keyframes rowEnter {
  from {
    opacity: 0;
    transform: translateY(5px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.board-row:hover {
  background: #f8fbff;

  box-shadow:
    inset 3px 0 #3b82f6;
}

.board-number {
  color: #94a3b8 !important;

  font-variant-numeric: tabular-nums;
}

.board-title {
  max-width: 100%;

  display: inline-flex;
  align-items: center;

  gap: 10px;

  padding: 0;

  border: 0;

  color: #24364d;

  background: transparent;

  font-size: 12px;
  font-weight: 700;

  text-align: left;

  cursor: pointer;

  transition:
    color 0.18s ease;
}

.board-title > span:last-child {
  overflow: hidden;

  text-overflow: ellipsis;
  white-space: nowrap;
}

.board-row:hover .board-title {
  color: #176fc9;
}

.document-icon {
  width: 28px;
  height: 28px;

  flex: 0 0 28px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 8px;

  color: #3b82f6;

  background: #eff6ff;
}

.document-icon svg {
  width: 14px;
  height: 14px;
}

.writer {
  display: flex;
  align-items: center;

  gap: 8px;
}

.writer-avatar {
  width: 26px;
  height: 26px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 7px;

  color: #2563eb;

  background: #eef5ff;

  font-size: 9px;
  font-weight: 800;
}

.view-count {
  display: flex;
  align-items: center;

  gap: 6px;

  color: #64748b;
}

.view-count svg {
  width: 14px;
  height: 14px;

  color: #94a3b8;
}

.board-date {
  color: #718096 !important;

  font-variant-numeric: tabular-nums;
}


/* =========================================
   LOADING / EMPTY
========================================= */

.board-state {
  min-height: 300px;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  padding: 50px 20px;

  text-align: center;
}

.board-state strong {
  margin-top: 14px;

  color: #334155;

  font-size: 13px;
}

.board-state > span {
  margin-top: 5px;

  color: #94a3b8;

  font-size: 10px;
}

.empty-icon {
  width: 52px;
  height: 52px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 14px;

  color: #5a94d6;

  background: #f0f6fd;
}

.empty-icon svg {
  width: 24px;
  height: 24px;
}

.empty-write-button {
  margin-top: 18px;
  padding: 9px 14px;

  border-radius: 8px;

  color: #2563eb;

  background: #eff6ff;

  font-size: 10px;
  font-weight: 700;

  cursor: pointer;
}

.loading-spinner {
  width: 30px;
  height: 30px;

  border: 3px solid #e5edf6;
  border-top-color: #3b82f6;

  border-radius: 50%;

  animation:
    spinner 0.75s linear infinite;
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
  .board-page {
    padding:
      24px
      22px
      45px;
  }

  .search-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-area {
    width: 100%;
  }

  .writer-column {
    width: 130px;
  }
}

@media (max-width: 700px) {
  .board-header {
    min-height: 165px;

    align-items: flex-start;
    flex-direction: column;

    padding: 24px;
  }

  .write-button {
    align-self: flex-end;
  }

  .search-area {
    align-items: stretch;
    flex-wrap: wrap;
  }

  .search-input-wrap {
    flex-basis: 100%;
  }

  .search-button,
  .reset-button {
    flex: 1;
  }

  .table-wrap {
    overflow-x: auto;
  }

  .board-table {
    min-width: 720px;
  }
}

@media (max-width: 560px) {
  .board-page {
    padding:
      18px
      14px
      35px;
  }

  .board-header {
    margin-bottom: 20px;

    border-radius: 14px;
  }

  .board-header h1 {
    font-size: 24px;
  }

  .search-card {
    padding: 17px;
  }

  .board-card-header {
    padding: 17px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .board-page,
  .board-row,
  .loading-spinner {
    animation: none;
    opacity: 1;
  }
}
</style>