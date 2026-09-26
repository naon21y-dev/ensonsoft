<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api/api'

const members = ref([])
const message = ref('')
const loading = ref(false)
const searchKeyword = ref('')
const roleFilter = ref('ALL')

// ========================================
// 회원 목록 조회
// ========================================

const getMembers = async () => {
  loading.value = true
  message.value = ''

  try {
    const response = await api.get('/admin/members')

    members.value = response.data
  } catch (error) {
    console.error(error)

    if (error.response?.status === 403) {
      message.value = '관리자만 접근할 수 있습니다.'
    } else if (error.response?.status === 401) {
      message.value = '로그인이 필요합니다.'
    } else {
      message.value =
        error.response?.data?.message || '회원 목록을 불러오지 못했습니다.'
    }
  } finally {
    loading.value = false
  }
}

// ========================================
// 통계
// ========================================

const totalMembers = computed(() => members.value.length)

const adminCount = computed(() =>
  members.value.filter(member => member.role === 'ADMIN').length
)

const userCount = computed(() =>
  members.value.filter(member => member.role === 'USER').length
)

const enabledCount = computed(() =>
  members.value.filter(member => member.enabled).length
)

// ========================================
// 검색 + 필터
// ========================================

const filteredMembers = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()

  return members.value.filter(member => {
    const matchesKeyword =
      !keyword ||
      String(member.id ?? '').includes(keyword) ||
      member.username?.toLowerCase().includes(keyword) ||
      member.name?.toLowerCase().includes(keyword) ||
      member.email?.toLowerCase().includes(keyword)

    const matchesRole =
      roleFilter.value === 'ALL' ||
      member.role === roleFilter.value

    return matchesKeyword && matchesRole
  })
})

onMounted(() => {
  getMembers()
})
</script>

<template>
  <div class="admin-page">

    <!-- =========================
         HEADER
    ========================== -->
    <header class="page-header">
      <div>
        <div class="eyebrow">
          ADMINISTRATION
        </div>

        <h1>회원 관리</h1>

        <p>
          시스템 사용자와 계정 권한을 관리합니다.
        </p>
      </div>

      <button
        class="refresh-button"
        :disabled="loading"
        @click="getMembers"
      >
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path
            d="M20 11a8.1 8.1 0 0 0-15.5-2M4 4v5h5"
          />
          <path
            d="M4 13a8.1 8.1 0 0 0 15.5 2M20 20v-5h-5"
          />
        </svg>

        {{ loading ? '불러오는 중' : '새로고침' }}
      </button>
    </header>

    <!-- =========================
         ERROR
    ========================== -->
    <div
      v-if="message"
      class="alert"
    >
      <div class="alert-icon">!</div>

      <span>{{ message }}</span>
    </div>

    <!-- =========================
         KPI
    ========================== -->
    <section class="stats-grid">

      <article class="stat-card">
        <div class="stat-top">
          <span class="stat-label">
            전체 회원
          </span>

          <div class="stat-icon blue">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
        </div>

        <strong>{{ totalMembers }}</strong>
        <span class="stat-caption">등록된 전체 계정</span>
      </article>

      <article class="stat-card">
        <div class="stat-top">
          <span class="stat-label">
            관리자
          </span>

          <div class="stat-icon purple">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              <path d="M9 12l2 2 4-4"/>
            </svg>
          </div>
        </div>

        <strong>{{ adminCount }}</strong>
        <span class="stat-caption">ADMIN 권한 계정</span>
      </article>

      <article class="stat-card">
        <div class="stat-top">
          <span class="stat-label">
            일반 회원
          </span>

          <div class="stat-icon cyan">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M20 21a8 8 0 0 0-16 0"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
        </div>

        <strong>{{ userCount }}</strong>
        <span class="stat-caption">USER 권한 계정</span>
      </article>

      <article class="stat-card">
        <div class="stat-top">
          <span class="stat-label">
            활성 계정
          </span>

          <div class="stat-icon green">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M20 6L9 17l-5-5"/>
            </svg>
          </div>
        </div>

        <strong>{{ enabledCount }}</strong>
        <span class="stat-caption">현재 사용 가능한 계정</span>
      </article>

    </section>

    <!-- =========================
         MEMBER PANEL
    ========================== -->
    <section class="member-panel">

      <div class="panel-header">

        <div>
          <h2>회원 목록</h2>

          <p>
            총 {{ totalMembers }}명의 사용자가 등록되어 있습니다.
          </p>
        </div>

        <div class="panel-actions">

          <!-- 검색 -->
          <div class="search-box">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.35-4.35"/>
            </svg>

            <input
              v-model="searchKeyword"
              type="text"
              placeholder="이름, 아이디, 이메일 검색"
            />
          </div>

          <!-- 권한 필터 -->
          <select
            v-model="roleFilter"
            class="role-select"
          >
            <option value="ALL">
              전체 권한
            </option>

            <option value="ADMIN">
              관리자
            </option>

            <option value="USER">
              일반 회원
            </option>
          </select>

        </div>
      </div>

      <!-- =========================
           LOADING
      ========================== -->
      <div
        v-if="loading"
        class="state-container"
      >
        <div class="spinner"></div>

        <strong>회원 정보를 불러오고 있습니다.</strong>

        <span>
          잠시만 기다려주세요.
        </span>
      </div>

      <!-- =========================
           EMPTY
      ========================== -->
      <div
        v-else-if="filteredMembers.length === 0"
        class="state-container"
      >
        <div class="empty-icon">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.35-4.35"/>
          </svg>
        </div>

        <strong>검색 결과가 없습니다.</strong>

        <span>
          다른 검색어나 권한 조건을 선택해주세요.
        </span>
      </div>

      <!-- =========================
           DESKTOP TABLE
      ========================== -->
      <div
        v-else
        class="table-wrapper"
      >
        <table>
          <thead>
            <tr>
              <th>사용자</th>
              <th>아이디</th>
              <th>이메일</th>
              <th>권한</th>
              <th>계정 상태</th>
              <th class="id-column">ID</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="member in filteredMembers"
              :key="member.id"
            >
              <td>
                <div class="member-profile">

                  <div
                    class="avatar"
                    :class="{ admin: member.role === 'ADMIN' }"
                  >
                    {{
                      member.name
                        ? member.name.charAt(0)
                        : member.username?.charAt(0)
                    }}
                  </div>

                  <div class="member-name">
                    <strong>
                      {{ member.name || '-' }}
                    </strong>

                    <span>
                      {{ member.role === 'ADMIN'
                        ? '시스템 관리자'
                        : '일반 사용자'
                      }}
                    </span>
                  </div>

                </div>
              </td>

              <td>
                <span class="username">
                  {{ member.username }}
                </span>
              </td>

              <td>
                <span class="email">
                  {{ member.email }}
                </span>
              </td>

              <td>
                <span
                  class="role-badge"
                  :class="member.role?.toLowerCase()"
                >
                  <span class="role-dot"></span>
                  {{ member.role }}
                </span>
              </td>

              <td>
                <span
                  class="status-badge"
                  :class="{ disabled: !member.enabled }"
                >
                  <span class="status-dot"></span>

                  {{ member.enabled ? '활성' : '비활성' }}
                </span>
              </td>

              <td class="id-column">
                #{{ member.id }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- =========================
           MOBILE CARDS
      ========================== -->
      <div
        v-if="!loading && filteredMembers.length"
        class="mobile-members"
      >
        <article
          v-for="member in filteredMembers"
          :key="`mobile-${member.id}`"
          class="mobile-member-card"
        >
          <div class="mobile-member-header">

            <div class="member-profile">

              <div
                class="avatar"
                :class="{ admin: member.role === 'ADMIN' }"
              >
                {{
                  member.name
                    ? member.name.charAt(0)
                    : member.username?.charAt(0)
                }}
              </div>

              <div class="member-name">
                <strong>
                  {{ member.name || '-' }}
                </strong>

                <span>
                  @{{ member.username }}
                </span>
              </div>

            </div>

            <span
              class="status-badge"
              :class="{ disabled: !member.enabled }"
            >
              <span class="status-dot"></span>

              {{ member.enabled ? '활성' : '비활성' }}
            </span>

          </div>

          <div class="mobile-info-row">
            <span>이메일</span>
            <strong>{{ member.email }}</strong>
          </div>

          <div class="mobile-info-row">
            <span>권한</span>

            <span
              class="role-badge"
              :class="member.role?.toLowerCase()"
            >
              <span class="role-dot"></span>
              {{ member.role }}
            </span>
          </div>

          <div class="mobile-info-row">
            <span>회원 ID</span>
            <strong>#{{ member.id }}</strong>
          </div>

        </article>
      </div>

      <!-- =========================
           FOOTER
      ========================== -->
      <div
        v-if="!loading && members.length"
        class="panel-footer"
      >
        <span>
          총 {{ filteredMembers.length }}개 결과
        </span>

        <span>
          전체 {{ totalMembers }}명
        </span>
      </div>

    </section>

  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.admin-page {
  width: 100%;
  min-height: 100%;
  padding: 36px 40px 60px;
  color: #172033;
}

/* ========================================
   HEADER
======================================== */

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 30px;
}

.eyebrow {
  margin-bottom: 8px;
  color: #3b82f6;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.18em;
}

.page-header h1 {
  margin: 0;
  color: #172033;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.page-header p {
  margin: 9px 0 0;
  color: #7b8496;
  font-size: 14px;
}

.refresh-button {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  padding: 0 17px;
  border: 1px solid #dfe4ec;
  border-radius: 10px;
  background: #ffffff;
  color: #354052;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s ease;
}

.refresh-button:hover {
  border-color: #b9c7dc;
  box-shadow: 0 5px 16px rgba(25, 42, 70, 0.07);
}

.refresh-button:disabled {
  opacity: 0.55;
  cursor: default;
}

.refresh-button svg {
  width: 16px;
  height: 16px;
}

/* ========================================
   ALERT
======================================== */

.alert {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 13px 16px;
  border: 1px solid #fecaca;
  border-radius: 10px;
  background: #fff7f7;
  color: #b42318;
  font-size: 13px;
  font-weight: 600;
}

.alert-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #fee2e2;
  font-size: 12px;
  font-weight: 900;
}

/* ========================================
   STATS
======================================== */

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 22px;
}

.stat-card {
  min-width: 0;
  padding: 21px;
  border: 1px solid #e5e9f0;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 3px 12px rgba(25, 42, 70, 0.035);
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.stat-label {
  color: #7a8495;
  font-size: 12px;
  font-weight: 700;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 35px;
  height: 35px;
  border-radius: 10px;
}

.stat-icon svg {
  width: 17px;
  height: 17px;
}

.stat-icon.blue {
  background: #eef5ff;
  color: #377cf6;
}

.stat-icon.purple {
  background: #f3f0ff;
  color: #7657e8;
}

.stat-icon.cyan {
  background: #ecf9fc;
  color: #1d9ab5;
}

.stat-icon.green {
  background: #edf9f3;
  color: #239563;
}

.stat-card strong {
  display: block;
  margin-bottom: 5px;
  color: #182234;
  font-size: 27px;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.stat-caption {
  color: #a0a8b6;
  font-size: 11px;
}

/* ========================================
   PANEL
======================================== */

.member-panel {
  overflow: hidden;
  border: 1px solid #e4e8ef;
  border-radius: 15px;
  background: #ffffff;
  box-shadow: 0 4px 18px rgba(28, 44, 70, 0.04);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 21px 23px;
  border-bottom: 1px solid #edf0f4;
}

.panel-header h2 {
  margin: 0;
  color: #1d2738;
  font-size: 16px;
  font-weight: 800;
}

.panel-header p {
  margin: 5px 0 0;
  color: #98a1af;
  font-size: 11px;
}

.panel-actions {
  display: flex;
  align-items: center;
  gap: 9px;
}

.search-box {
  position: relative;
  width: 260px;
}

.search-box svg {
  position: absolute;
  top: 50%;
  left: 13px;
  width: 15px;
  height: 15px;
  color: #a3acba;
  transform: translateY(-50%);
  pointer-events: none;
}

.search-box input {
  width: 100%;
  height: 39px;
  padding: 0 13px 0 37px;
  border: 1px solid #dfe4eb;
  border-radius: 9px;
  outline: none;
  background: #fafbfd;
  color: #293447;
  font-size: 12px;
  transition: 0.2s ease;
}

.search-box input:focus {
  border-color: #8db5fa;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
}

.search-box input::placeholder {
  color: #aab2bf;
}

.role-select {
  height: 39px;
  padding: 0 30px 0 12px;
  border: 1px solid #dfe4eb;
  border-radius: 9px;
  outline: none;
  background: #fafbfd;
  color: #4d586a;
  font-size: 12px;
  font-weight: 600;
}

/* ========================================
   TABLE
======================================== */

.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #fafbfd;
}

th {
  padding: 12px 20px;
  border-bottom: 1px solid #edf0f4;
  color: #8b94a4;
  font-size: 10px;
  font-weight: 800;
  text-align: left;
  letter-spacing: 0.03em;
}

td {
  padding: 15px 20px;
  border-bottom: 1px solid #f0f2f5;
  color: #536074;
  font-size: 12px;
}

tbody tr {
  transition: background 0.15s ease;
}

tbody tr:hover {
  background: #fbfcfe;
}

tbody tr:last-child td {
  border-bottom: none;
}

.member-profile {
  display: flex;
  align-items: center;
  gap: 11px;
}

.avatar {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 35px;
  height: 35px;
  border-radius: 10px;
  background: #edf4ff;
  color: #3978e8;
  font-size: 13px;
  font-weight: 800;
}

.avatar.admin {
  background: #f0edff;
  color: #6d55d9;
}

.member-name {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 3px;
}

.member-name strong {
  overflow: hidden;
  max-width: 170px;
  color: #263145;
  font-size: 12px;
  font-weight: 750;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-name span {
  color: #a0a8b5;
  font-size: 10px;
}

.username {
  color: #465267;
  font-weight: 650;
}

.email {
  color: #697589;
}

.role-badge,
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 8px;
  border-radius: 6px;
  font-size: 9px;
  font-weight: 800;
  white-space: nowrap;
}

.role-badge.user {
  background: #eef5ff;
  color: #3978df;
}

.role-badge.admin {
  background: #f2efff;
  color: #6952d2;
}

.role-dot,
.status-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
}

.status-badge {
  background: #edf9f3;
  color: #23885c;
}

.status-badge.disabled {
  background: #f3f4f6;
  color: #8b93a0;
}

.id-column {
  color: #9ca5b3;
  text-align: right;
}

/* ========================================
   STATE
======================================== */

.state-container {
  display: flex;
  min-height: 270px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 40px;
  text-align: center;
}

.state-container strong {
  margin-top: 13px;
  color: #3b4658;
  font-size: 13px;
}

.state-container span {
  margin-top: 6px;
  color: #9aa3b1;
  font-size: 11px;
}

.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid #e9edf3;
  border-top-color: #4384f4;
  border-radius: 50%;
  animation: spin 0.75s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #f4f6f9;
  color: #8f99a8;
}

.empty-icon svg {
  width: 19px;
  height: 19px;
}

/* ========================================
   FOOTER
======================================== */

.panel-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 22px;
  border-top: 1px solid #edf0f4;
  background: #fafbfd;
  color: #929ba9;
  font-size: 10px;
}

/* ========================================
   MOBILE
======================================== */

.mobile-members {
  display: none;
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .panel-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .panel-actions {
    width: 100%;
  }

  .search-box {
    flex: 1;
    width: auto;
  }
}

@media (max-width: 700px) {
  .admin-page {
    padding: 22px 16px 40px;
  }

  .page-header {
    align-items: stretch;
    flex-direction: column;
    margin-bottom: 22px;
  }

  .page-header h1 {
    font-size: 25px;
  }

  .refresh-button {
    justify-content: center;
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-card strong {
    font-size: 23px;
  }

  .stat-caption {
    display: none;
  }

  .panel-header {
    padding: 18px;
  }

  .panel-actions {
    align-items: stretch;
    flex-direction: column;
  }

  .search-box {
    width: 100%;
  }

  .role-select {
    width: 100%;
  }

  .table-wrapper {
    display: none;
  }

  .mobile-members {
    display: flex;
    flex-direction: column;
    padding: 12px;
    gap: 10px;
    background: #f7f9fc;
  }

  .mobile-member-card {
    padding: 16px;
    border: 1px solid #e6eaf0;
    border-radius: 12px;
    background: #ffffff;
  }

  .mobile-member-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 15px;
    padding-bottom: 14px;
    border-bottom: 1px solid #eef1f5;
  }

  .mobile-info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 15px;
    padding: 7px 0;
  }

  .mobile-info-row > span:first-child {
    color: #9aa3b1;
    font-size: 10px;
    font-weight: 650;
  }

  .mobile-info-row > strong {
    overflow: hidden;
    max-width: 68%;
    color: #4d596c;
    font-size: 11px;
    font-weight: 650;
    text-align: right;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

@media (max-width: 420px) {
  .admin-page {
    padding: 18px 12px 35px;
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
  }

  .stat-icon {
    width: 31px;
    height: 31px;
  }

  .stat-label {
    font-size: 10px;
  }
}
</style>
