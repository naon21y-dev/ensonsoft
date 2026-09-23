<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import api from '../api/api'

const member = ref(null)
const loading = ref(true)
const errorMessage = ref('')

const getMyInfo = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await api.get('/user/me')
    member.value = response.data
  } catch (error) {
    console.error('내 정보 조회 실패:', error)

    errorMessage.value =
      error.response?.data?.message ||
      '회원 정보를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const roleLabel = computed(() => {
  if (!member.value) return '-'

  if (member.value.role === 'ADMIN') return '관리자'
  if (member.value.role === 'USER') return '일반 사용자'

  return member.value.role || '-'
})

const accountStatus = computed(() => {
  return member.value?.enabled ? '활성화' : '비활성화'
})

const initial = computed(() => {
  const name =
    member.value?.name ||
    member.value?.username ||
    'U'

  return name.charAt(0).toUpperCase()
})

const passwordNotice = () => {
  alert('비밀번호 변경 기능은 준비 중입니다.')
}

onMounted(() => {
  getMyInfo()
})
</script>

<template>
  <div class="profile-page">

    <!-- HEADER -->
    <header class="page-header">
      <div>
        <p class="eyebrow">USER PROFILE</p>

        <h1>내 정보</h1>

        <p class="page-description">
          회원 정보를 확인하고 계정 상태를 관리할 수 있습니다.
        </p>
      </div>

      <div class="breadcrumb">
        <RouterLink
          to="/"
          class="home-link"
        >
          Home
        </RouterLink>

        <span class="breadcrumb-arrow">›</span>

        <strong>내 정보</strong>
      </div>
    </header>


    <!-- LOADING -->
    <div
      v-if="loading"
      class="state-card"
    >
      <div class="spinner"></div>

      <strong>회원 정보를 불러오는 중입니다.</strong>

      <p>잠시만 기다려 주세요.</p>
    </div>


    <!-- ERROR -->
    <div
      v-else-if="errorMessage"
      class="state-card"
    >
      <div class="state-icon">!</div>

      <strong>회원 정보를 불러오지 못했습니다.</strong>

      <p>{{ errorMessage }}</p>

      <button
        type="button"
        class="retry-button"
        @click="getMyInfo"
      >
        다시 시도
      </button>
    </div>


    <!-- CONTENT -->
    <div
      v-else-if="member"
      class="profile-layout"
    >

      <!-- LEFT PROFILE -->
      <aside class="profile-card">

        <div class="avatar-wrap">
          <div class="avatar">
            {{ initial }}
          </div>

          <span
            class="online-dot"
            :class="{ inactive: !member.enabled }"
          ></span>
        </div>

        <h2 class="profile-name">
          {{ member.name || member.username }}
        </h2>

        <span
          class="role-badge"
          :class="{ admin: member.role === 'ADMIN' }"
        >
          {{ roleLabel }}
        </span>


        <div class="profile-divider"></div>


        <div class="profile-info-row">
          <div class="info-icon">
            <svg viewBox="0 0 24 24">
              <path
                d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm7 8a7 7 0 0 0-14 0"
              />
            </svg>
          </div>

          <div>
            <span>아이디</span>
            <strong>{{ member.username }}</strong>
          </div>
        </div>


        <div class="profile-info-row">
          <div class="info-icon">
            <svg viewBox="0 0 24 24">
              <path
                d="M3 6h18v12H3V6Zm0 1 9 6 9-6"
              />
            </svg>
          </div>

          <div>
            <span>이메일</span>
            <strong>{{ member.email || '-' }}</strong>
          </div>
        </div>


        <div class="profile-info-row">
          <div class="info-icon number-icon">
            #
          </div>

          <div>
            <span>회원번호</span>
            <strong>{{ member.id }}</strong>
          </div>
        </div>


        <div class="profile-divider bottom"></div>


        <button
          type="button"
          class="security-button"
          @click="passwordNotice"
        >
          <svg
            class="lock-icon"
            viewBox="0 0 24 24"
          >
            <path
              d="M7 10V7a5 5 0 0 1 10 0v3M5 10h14v10H5V10Z"
            />
          </svg>

          <span>비밀번호 변경</span>

          <span class="security-arrow">›</span>
        </button>
      </aside>


      <!-- RIGHT INFORMATION -->
      <section class="information-card">

        <div class="information-body">

          <div class="section-heading">
            <div>
              <p class="section-label">
                ACCOUNT INFORMATION
              </p>

              <h2>기본 정보</h2>

              <p class="section-description">
                회원님의 기본 계정 정보를 확인할 수 있습니다.
              </p>
            </div>

            <button
              type="button"
              class="refresh-button"
              @click="getMyInfo"
            >
              <span class="refresh-icon">↻</span>
              새로고침
            </button>
          </div>


          <div class="section-line"></div>


          <div class="info-grid">

            <div class="field">
              <label>아이디</label>

              <div class="field-value readonly">
                {{ member.username }}
              </div>
            </div>


            <div class="field">
              <label>이름</label>

              <div class="field-value">
                {{ member.name || '-' }}
              </div>
            </div>


            <div class="field">
              <label>이메일</label>

              <div class="field-value">
                {{ member.email || '-' }}
              </div>
            </div>


            <div class="field">
              <label>회원번호</label>

              <div class="field-value readonly">
                {{ member.id }}
              </div>
            </div>


            <div class="field field-full">
              <label>권한</label>

              <div class="field-value readonly role-value">
                <span
                  class="role-dot"
                  :class="{ admin: member.role === 'ADMIN' }"
                ></span>

                {{ member.role }}
              </div>
            </div>


            <div class="field field-full">
              <label>계정 상태</label>

              <div class="field-value status-value">
                <span
                  class="status-dot"
                  :class="{ inactive: !member.enabled }"
                ></span>

                <strong>
                  {{ accountStatus }}
                </strong>

                <span class="status-description">
                  {{
                    member.enabled
                      ? '현재 정상적으로 이용 가능한 계정입니다.'
                      : '현재 비활성화된 계정입니다.'
                  }}
                </span>
              </div>
            </div>

          </div>


          <div class="notice-box">
            <div class="notice-icon">i</div>

            <div>
              <strong>계정 정보 안내</strong>

              <p>
                아이디, 회원번호 및 권한은 사용자가 직접 변경할 수 없습니다.
                정보 변경이 필요한 경우 관리자에게 문의해 주세요.
              </p>
            </div>
          </div>

        </div>
      </section>
    </div>
  </div>
</template>


<style scoped>
.profile-page {
  width: 100%;
  min-height: 100vh;
  padding: 44px 48px 70px;
  box-sizing: border-box;

  background:
    radial-gradient(
      circle at 90% 0%,
      rgba(41, 112, 224, 0.06),
      transparent 30%
    ),
    #f4f7fb;

  color: #10233f;

  animation: pageEnter 0.4s ease;
}


/* HEADER */

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  max-width: 1400px;
  margin: 0 auto 34px;
}

.eyebrow {
  margin: 0 0 9px;

  color: #71839d;

  font-size: 12px;
  font-weight: 800;

  letter-spacing: 2px;
}

.page-header h1 {
  margin: 0;

  color: #0c213e;

  font-size: 34px;
  font-weight: 900;

  letter-spacing: -1.5px;
}

.page-description {
  margin: 10px 0 0;

  color: #8492a6;

  font-size: 14px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 10px;

  padding-top: 14px;

  font-size: 13px;
}

.home-link {
  position: relative;

  color: #718096;

  font-weight: 700;

  text-decoration: none;

  transition: 0.2s ease;
}

.home-link::after {
  position: absolute;

  right: 0;
  bottom: -4px;
  left: 0;

  height: 1px;

  background: #1769df;

  content: '';

  opacity: 0;

  transform: scaleX(0);

  transition: 0.2s ease;
}

.home-link:hover {
  color: #1769df;
}

.home-link:hover::after {
  opacity: 1;
  transform: scaleX(1);
}

.breadcrumb-arrow {
  color: #b2bdca;

  font-size: 17px;
}

.breadcrumb strong {
  color: #35445c;
}


/* LAYOUT */

.profile-layout {
  display: grid;
  grid-template-columns: 330px minmax(0, 1fr);
  gap: 22px;

  max-width: 1400px;
  margin: 0 auto;
}


/* LEFT CARD */

.profile-card {
  padding: 38px 30px 30px;

  background: #ffffff;

  border: 1px solid #e5ebf3;
  border-radius: 18px;

  box-shadow:
    0 12px 35px rgba(15, 40, 75, 0.06);

  text-align: center;
}

.avatar-wrap {
  position: relative;

  width: 112px;
  height: 112px;

  margin: 0 auto 20px;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 112px;
  height: 112px;

  background:
    linear-gradient(
      145deg,
      #e7f0ff,
      #f5f8ff
    );

  border-radius: 50%;

  color: #4e7fd0;

  font-size: 42px;
  font-weight: 900;

  box-shadow:
    inset 0 0 0 1px #e2ebf8;
}

.online-dot {
  position: absolute;

  right: 5px;
  bottom: 9px;

  width: 15px;
  height: 15px;

  background: #20c77a;

  border: 4px solid #ffffff;
  border-radius: 50%;
}

.online-dot.inactive {
  background: #a4afbd;
}

.profile-name {
  margin: 0 0 10px;

  color: #0d213c;

  font-size: 25px;
  font-weight: 900;

  letter-spacing: -0.8px;
}

.role-badge {
  display: inline-flex;

  padding: 6px 12px;

  background: #eaf2ff;

  border-radius: 999px;

  color: #2870da;

  font-size: 12px;
  font-weight: 800;
}

.role-badge.admin {
  background: #fff1e7;

  color: #e26e1b;
}

.profile-divider {
  height: 1px;

  margin: 27px 0;

  background: #edf1f6;
}

.profile-divider.bottom {
  margin-top: 30px;
  margin-bottom: 22px;
}


/* PROFILE ROW */

.profile-info-row {
  display: flex;
  align-items: center;
  gap: 15px;

  margin-bottom: 23px;

  text-align: left;
}

.info-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  flex: 0 0 40px;

  width: 40px;
  height: 40px;

  background: #f4f7fb;

  border-radius: 11px;

  color: #164679;
}

.info-icon svg {
  width: 19px;
  height: 19px;

  fill: none;
  stroke: currentColor;

  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

.number-icon {
  font-size: 17px;
  font-weight: 900;
}

.profile-info-row span {
  display: block;

  margin-bottom: 4px;

  color: #8a98ab;

  font-size: 12px;
}

.profile-info-row strong {
  display: block;

  max-width: 205px;

  overflow: hidden;

  color: #263b57;

  font-size: 14px;
  font-weight: 700;

  text-overflow: ellipsis;
  white-space: nowrap;
}


/* PASSWORD BUTTON */

.security-button {
  display: flex;
  align-items: center;
  gap: 10px;

  width: 100%;

  padding: 14px 15px;

  background: #ffffff;

  border: 1px solid #cddcf3;
  border-radius: 10px;

  color: #1767d2;

  cursor: pointer;

  font-family: inherit;
  font-size: 13px;
  font-weight: 800;

  transition: 0.2s ease;
}

.security-button:hover {
  background: #f4f8ff;

  border-color: #8fb7ed;

  box-shadow:
    0 5px 15px rgba(26, 94, 185, 0.08);

  transform: translateY(-1px);
}

.lock-icon {
  width: 18px;
  height: 18px;

  fill: none;
  stroke: currentColor;

  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

.security-arrow {
  margin-left: auto;

  font-size: 22px;
  font-weight: 500;
}


/* RIGHT CARD */

.information-card {
  overflow: hidden;

  background: #ffffff;

  border: 1px solid #e5ebf3;
  border-radius: 18px;

  box-shadow:
    0 12px 35px rgba(15, 40, 75, 0.06);
}

.information-body {
  padding: 36px 34px;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 20px;
}

.section-label {
  margin: 0 0 8px;

  color: #1769df;

  font-size: 10px;
  font-weight: 900;

  letter-spacing: 1.7px;
}

.section-heading h2 {
  margin: 0 0 7px;

  color: #10233e;

  font-size: 21px;
  font-weight: 900;
}

.section-description {
  margin: 0;

  color: #8b98aa;

  font-size: 13px;
}

.section-line {
  height: 1px;

  margin: 28px 0;

  background: #edf1f6;
}


/* REFRESH */

.refresh-button {
  display: inline-flex;
  align-items: center;
  gap: 7px;

  padding: 11px 16px;

  background: #1769df;

  border: 0;
  border-radius: 9px;

  box-shadow:
    0 7px 18px rgba(23, 105, 223, 0.18);

  color: #ffffff;

  cursor: pointer;

  font-family: inherit;
  font-size: 13px;
  font-weight: 800;

  transition: 0.2s ease;
}

.refresh-button:hover {
  background: #0e5cc9;

  transform: translateY(-1px);
}

.refresh-icon {
  font-size: 17px;
}


/* INFORMATION */

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));

  gap: 20px;
}

.field {
  min-width: 0;
}

.field-full {
  grid-column: 1 / -1;
}

.field label {
  display: block;

  margin-bottom: 8px;

  color: #30435d;

  font-size: 13px;
  font-weight: 800;
}

.field-value {
  display: flex;
  align-items: center;

  min-height: 47px;

  padding: 0 15px;

  box-sizing: border-box;

  background: #ffffff;

  border: 1px solid #dfe6ef;
  border-radius: 9px;

  color: #263a55;

  font-size: 14px;
  font-weight: 650;
}

.field-value.readonly {
  background: #f5f7fa;

  color: #758397;
}

.role-value {
  gap: 9px;
}

.role-dot {
  width: 8px;
  height: 8px;

  background: #2878e6;

  border-radius: 50%;
}

.role-dot.admin {
  background: #f08a3e;
}


/* STATUS */

.status-value {
  gap: 10px;
}

.status-dot {
  flex: 0 0 9px;

  width: 9px;
  height: 9px;

  background: #20c77a;

  border-radius: 50%;

  box-shadow:
    0 0 0 4px rgba(32, 199, 122, 0.11);
}

.status-dot.inactive {
  background: #9aa6b6;

  box-shadow:
    0 0 0 4px rgba(154, 166, 182, 0.12);
}

.status-description {
  margin-left: 4px;

  color: #8996a7;

  font-size: 12px;
}


/* NOTICE */

.notice-box {
  display: flex;
  gap: 13px;

  margin-top: 28px;

  padding: 18px;

  background:
    linear-gradient(
      90deg,
      #eef5ff,
      #f5f8ff
    );

  border: 1px solid #e1ecfb;
  border-radius: 11px;
}

.notice-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  flex: 0 0 26px;

  width: 26px;
  height: 26px;

  background: #2475df;

  border-radius: 50%;

  color: #ffffff;

  font-family: Georgia, serif;
  font-size: 15px;
  font-weight: 900;
}

.notice-box strong {
  display: block;

  margin-bottom: 5px;

  color: #1c67c8;

  font-size: 13px;
}

.notice-box p {
  margin: 0;

  color: #60718a;

  font-size: 12px;
  line-height: 1.7;
}


/* LOADING / ERROR */

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  max-width: 560px;
  min-height: 260px;

  margin: 70px auto;

  padding: 30px;

  box-sizing: border-box;

  background: #ffffff;

  border: 1px solid #e1e8f1;
  border-radius: 18px;

  box-shadow:
    0 12px 35px rgba(15, 40, 75, 0.06);

  text-align: center;
}

.state-card strong {
  margin-bottom: 7px;

  color: #19314f;

  font-size: 16px;
}

.state-card p {
  margin: 0;

  color: #8391a4;

  font-size: 13px;
}

.spinner {
  width: 31px;
  height: 31px;

  margin-bottom: 20px;

  border: 3px solid #e5ebf3;
  border-top-color: #1c6fd9;
  border-radius: 50%;

  animation: spin 0.8s linear infinite;
}

.state-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 45px;
  height: 45px;

  margin-bottom: 15px;

  background: #fff0f0;

  border-radius: 50%;

  color: #e54a4a;

  font-size: 22px;
  font-weight: 900;
}

.retry-button {
  margin-top: 18px;

  padding: 10px 18px;

  background: #173b68;

  border: 0;
  border-radius: 8px;

  color: #ffffff;

  cursor: pointer;

  font-weight: 800;
}


/* ANIMATION */

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

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}


/* RESPONSIVE */

@media (max-width: 1050px) {
  .profile-page {
    padding: 32px 25px 55px;
  }

  .profile-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .profile-page {
    padding: 25px 18px 45px;
  }

  .page-header {
    flex-direction: column;

    gap: 12px;
  }

  .page-header h1 {
    font-size: 29px;
  }

  .breadcrumb {
    padding-top: 0;
  }

  .information-body {
    padding: 27px 19px;
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;

    gap: 18px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .field-full {
    grid-column: auto;
  }

  .status-value {
    align-items: flex-start;
    flex-wrap: wrap;

    padding-top: 13px;
    padding-bottom: 13px;
  }

  .status-description {
    width: 100%;

    margin: 3px 0 0 19px;
  }
}
</style>