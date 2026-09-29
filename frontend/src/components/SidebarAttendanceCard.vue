<script setup>
import { onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useAttendanceStore } from '../stores/attendance'
import { useAttendance, workTypes } from '../composables/useAttendance'

const store = useAttendanceStore()

const {
  today,
  record,
  loading,
  busy,
  error,
  canCheckIn,
  workType,
  overnight
} = storeToRefs(store)

const {
  t,
  clockTime,
  duration,
  synchronize
} = useAttendance()

const expanded = ref(false)

watch(
  () => today.value?.serverTime,
  (value) => {
    if (value) synchronize(value)
  },
  { immediate: true }
)

onMounted(() => store.refresh())
</script>

<template>
  <div
    class="sidebar-attendance"
    @keydown.esc="expanded = false"
  >

    <!-- 모바일 미니 버튼 -->
    <button
      class="attendance-mini"
      :aria-label="t('attendance.today')"
      :title="t('attendance.today')"
      :aria-expanded="expanded"
      aria-controls="sidebar-attendance-panel"
      @click="expanded = !expanded"
    >
      <svg
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <circle
          cx="12"
          cy="12"
          r="9"
        />

        <path d="M12 6v6l4 2" />
      </svg>

      <span
        class="mini-dot"
        :class="record?.status"
      ></span>
    </button>

    <!-- 출퇴근 카드 -->
    <section
      id="sidebar-attendance-panel"
      class="shift-card"
      :class="{ expanded }"
      :aria-label="t('attendance.today')"
      :aria-busy="loading || busy"
    >

      <header>
        <span class="shift-kicker">
          {{ t('attendance.quickTitle') }}
        </span>

        <button
          class="shift-close"
          :aria-label="t('attendance.close')"
          @click="expanded = false"
        >
          ×
        </button>
      </header>

      <template v-if="today">

        <!-- 근무 상태 -->
        <div class="shift-status-row">

          <h2
            class="shift-status"
            :class="record.status"
          >
            <span></span>

            {{ t(`attendance.states.${record.status}`) }}
          </h2>

          <p class="shift-date">
            {{ today.date }}
          </p>

        </div>

        <!-- 야간 근무 안내 -->
        <p
          v-if="overnight"
          class="shift-feedback"
        >
          {{ t('attendance.overnight', { date: record.workDate }) }}
        </p>

        <!-- 출퇴근 시간 -->
        <dl class="shift-times">

          <div>
            <dt>
              {{ t('attendance.checkInAt') }}
            </dt>

            <dd>
              {{ clockTime(record.checkInAt, today.zone) }}
            </dd>
          </div>

          <div>
            <dt>
              {{ t('attendance.checkOutAt') }}
            </dt>

            <dd>
              {{ clockTime(record.checkOutAt, today.zone) }}
            </dd>
          </div>

        </dl>

        <!-- 근무 시간 -->
        <div
          v-if="record.id"
          class="shift-duration"
        >
          <span>
            {{ t('attendance.worked') }}
          </span>

          <strong>
            {{ duration(record) }}
          </strong>
        </div>

        <!-- 출근 전 근무구분 -->
        <label
          v-if="canCheckIn"
          class="shift-type"
        >
          {{ t('attendance.workType') }}

          <select
            v-model="workType"
            :disabled="busy || loading"
            :aria-label="t('attendance.workType')"
          >
            <option
              v-for="type in workTypes"
              :key="type"
              :value="type"
            >
              {{ t(`attendance.types.${type}`) }}
            </option>
          </select>
        </label>

        <!-- 출근 후 근무구분 -->
        <p
          v-else
          class="shift-type-name"
        >
          {{ t(`attendance.types.${record.workType}`) }}
        </p>

        <!-- 출근 버튼 -->
        <button
          v-if="canCheckIn"
          class="shift-action"
          :disabled="busy || loading"
          @click="store.act('in')"
        >
          <span>
            {{ t('attendance.checkIn') }}
          </span>

          <span aria-hidden="true">
            →
          </span>
        </button>

        <!-- 퇴근 버튼 -->
        <button
          v-else-if="today.activeAttendance"
          class="shift-action"
          :disabled="busy || loading"
          @click="store.act('out')"
        >
          <span>
            {{ t('attendance.checkOut') }}
          </span>

          <span aria-hidden="true">
            →
          </span>
        </button>

        <!-- 근무 완료 -->
        <p
          v-else
          class="shift-complete"
        >
          ✓ {{ t('attendance.workComplete') }}
        </p>

      </template>

      <!-- 로딩 / 사용불가 -->
      <p
        v-else
        class="shift-feedback"
      >
        {{ t(loading ? 'attendance.loading' : 'attendance.unavailable') }}
      </p>

      <!-- 오류 -->
      <p
        v-if="error"
        class="shift-feedback shift-error"
        role="alert"
      >
        {{ t(error) }}
      </p>

      <!-- 새로고침 -->
      <button
        v-if="error"
        class="shift-retry"
        :disabled="loading || busy"
        @click="store.refresh()"
      >
        {{ t('attendance.refresh') }}
      </button>

    </section>

  </div>
</template>

<style scoped>

/* =========================================
   SIDEBAR ATTENDANCE
========================================= */

.sidebar-attendance {
  flex-shrink: 0;
  margin: 14px 14px 0;
  position: relative;
}


/* =========================================
   CARD
   기존 민트 → WHITE
========================================= */

.shift-card {
  padding: 15px 15px 14px;

  border: 1px solid #dbe3ef;
  border-radius: 14px;

  background: #ffffff;
  color: #1e293b;

  box-shadow:
    0 6px 18px rgba(0, 0, 0, .12);

  max-height: 48vh;
  overflow-y: auto;
}


/* =========================================
   HEADER
========================================= */

.shift-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.shift-kicker {
  font-size: 10px;
  font-weight: 800;
  letter-spacing: .8px;

  color: #64748b;
}


/* =========================================
   STATUS
========================================= */

.shift-status-row {
  margin-top: 7px;
}

.shift-status {
  display: flex;
  align-items: center;

  gap: 7px;

  font-size: 17px;
  line-height: 1.3;

  margin: 0 0 3px;

  color: #172033;
}

.shift-status > span {
  width: 7px;
  height: 7px;

  flex-shrink: 0;

  border-radius: 50%;

  background: #94a3b8;
}


/* 근무중 */

.shift-status.WORKING > span {
  background: #2563eb;

  box-shadow:
    0 0 0 4px rgba(37, 99, 235, .12);
}


/* =========================================
   DATE
========================================= */

.shift-date {
  font-size: 10px;

  color: #94a3b8;

  margin: 0 0 10px;
}


/* =========================================
   CLOCK TIME
========================================= */

.shift-times {
  margin: 0;

  padding: 8px 0;

  border-top: 1px solid #edf1f6;
  border-bottom: 1px solid #edf1f6;

  font-size: 11px;
}

.shift-times div {
  display: flex;
  justify-content: space-between;
  align-items: baseline;

  gap: 8px;

  padding: 4px 0;
}

.shift-times dt {
  flex-shrink: 0;

  color: #64748b;
}

.shift-times dd {
  margin: 0;

  text-align: right;

  font-weight: 700;

  color: #1e293b;

  font-variant-numeric: tabular-nums;

  overflow-wrap: anywhere;
}


/* =========================================
   WORK DURATION
========================================= */

.shift-duration {
  display: flex;
  justify-content: space-between;
  align-items: center;

  gap: 8px;

  font-size: 11px;

  padding-top: 8px;
}

.shift-duration span {
  color: #64748b;
}

.shift-duration strong {
  font-size: 15px;

  color: #2563eb;

  font-variant-numeric: tabular-nums;
}


/* =========================================
   WORK TYPE
========================================= */

.shift-type {
  display: flex;
  flex-direction: column;

  gap: 5px;

  font-size: 10px;

  color: #64748b;

  margin: 10px 0;
}

.shift-type select {
  width: 100%;

  min-height: 34px;

  border: 1px solid #d7e0eb;
  border-radius: 7px;

  color: #1e293b;

  background: #ffffff;

  padding: 5px 8px;

  font: inherit;
}

.shift-type select:focus {
  outline: none;

  border-color: #93b4e8;

  box-shadow:
    0 0 0 3px rgba(37, 99, 235, .08);
}

.shift-type-name {
  display: inline-flex;

  width: fit-content;

  font-size: 10px;
  font-weight: 700;

  margin: 8px 0 0;

  padding: 4px 8px;

  border-radius: 20px;

  color: #2563eb;

  background: #eff6ff;
}


/* =========================================
   ACTION BUTTON
========================================= */

.shift-action {
  width: 100%;

  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-top: 10px;

  padding: 10px 12px;

  border: 0;
  border-radius: 8px;

  background: #2563eb;

  color: #ffffff;

  font: inherit;

  font-size: 12px;
  font-weight: 750;

  cursor: pointer;

  transition:
    background .18s ease,
    transform .18s ease;
}

.shift-action:hover {
  background: #1d4ed8;
}

.shift-action:active {
  transform: translateY(1px);
}

.shift-action:disabled,
.shift-retry:disabled {
  opacity: .5;

  cursor: wait;
}


/* =========================================
   COMPLETE
========================================= */

.shift-complete {
  text-align: center;

  background: #eff6ff;

  color: #2563eb;

  border: 1px solid #dbeafe;

  border-radius: 8px;

  padding: 8px;

  margin: 10px 0 0;

  font-size: 11px;
  font-weight: 700;
}


/* =========================================
   FEEDBACK / ERROR
========================================= */

.shift-feedback {
  font-size: 11px;

  line-height: 1.6;

  color: #64748b;

  margin: 8px 0;
}

.shift-error {
  color: #be123c;
}

.shift-retry {
  color: #2563eb;

  border: 1px solid #bfdbfe;

  border-radius: 6px;

  background: #ffffff;

  padding: 6px 10px;

  cursor: pointer;
}


/* =========================================
   CLOSE
========================================= */

.shift-close,
.attendance-mini {
  display: none;
}


/* =========================================
   FOCUS
========================================= */

.shift-card :focus-visible,
.attendance-mini:focus-visible {
  outline: 2px solid #2563eb;
  outline-offset: 3px;
}


/* =========================================
   MOBILE
========================================= */

@media (max-width: 820px) {

  .sidebar-attendance {
    margin: 12px 10px 0;
  }

  .attendance-mini {
    position: relative;

    display: flex;
    align-items: center;
    justify-content: center;

    width: 100%;
    height: 44px;

    border: 1px solid rgba(255, 255, 255, .22);

    border-radius: 11px;

    background: rgba(255, 255, 255, .1);

    color: #ffffff;

    cursor: pointer;
  }

  .attendance-mini:hover {
    background: rgba(255, 255, 255, .16);
  }

  .attendance-mini svg {
    width: 23px;

    fill: none;

    stroke: currentColor;

    stroke-width: 1.8;
  }

  .mini-dot {
    position: absolute;

    right: 5px;
    bottom: 5px;

    width: 6px;
    height: 6px;

    border-radius: 50%;

    background: #94a3b8;
  }

  .mini-dot.WORKING {
    background: #60a5fa;
  }

  .shift-card {
    display: none;
  }

  .shift-card.expanded {
    display: block;

    position: fixed;

    left: calc(var(--sidebar-width) + 8px);
    top: 88px;

    width:
      min(
        280px,
        calc(100vw - var(--sidebar-width) - 16px)
      );

    max-height:
      calc(100dvh - 106px);
  }

  .shift-close {
    display: block;

    border: 0;

    background: transparent;

    color: #64748b;

    font-size: 22px;

    cursor: pointer;
  }
}

</style>
