<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { useUiMessage, useFormatLocale } from '../i18n'
const formatLocale = useFormatLocale()

import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  createQnaAnswer,
  deleteQna,
  deleteQnaAnswer,
  getQna
} from '../api/Qna'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const qna = ref(null)
const loading = ref(false)
const message = useUiMessage()
const answer = ref('')
const answerSaving = ref(false)

const categoryLabels = computed(() => ({
  GENERAL: t('m551'),
  SITE: t('m552'),
  EQUIPMENT: t('m553'),
  VEHICLE: t('m342'),
  VIDEO_ANALYSIS: t('m554'),
  MAINTENANCE: t('m555'),
  ETC: t('m096')
}))

const isOwner = computed(() =>
  qna.value?.username === authStore.username
)

const canEdit = computed(() =>
  isOwner.value || authStore.isAdmin
)

const loadQna = async () => {
  loading.value = true
  message.value = ''

  try {
    const response = await getQna(route.params.id)
    qna.value = response.data
    answer.value = response.data.answer || ''
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      t('m591')
  } finally {
    loading.value = false
  }
}

const submitAnswer = async () => {
  if (!answer.value.trim()) {
    message.value = t('m592')
    return
  }

  answerSaving.value = true
  message.value = ''

  try {
    await createQnaAnswer(
      qna.value.id,
      answer.value.trim()
    )

    await loadQna()
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      t('m593')
  } finally {
    answerSaving.value = false
  }
}

const removeAnswer = async () => {
  if (!confirm(t('m594'))) return

  try {
    await deleteQnaAnswer(qna.value.id)
    answer.value = ''
    await loadQna()
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      t('m595')
  }
}

const removeQna = async () => {
  if (!confirm(t('m596'))) return

  try {
    await deleteQna(qna.value.id)
    router.push('/qna')
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      t('m597')
  }
}

const formatDateTime = (value) => {
  if (!value) return '-'

  return new Intl.DateTimeFormat(formatLocale.value, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}

onMounted(loadQna)
</script>

<template>
  <div class="detail-page">

    <button class="back" @click="router.push('/qna')">
      {{ t('m598') }}
    </button>

    <div v-if="message" class="error">
      {{ message }}
    </div>

    <div v-if="loading" class="loading">
      {{ t('m599') }}
    </div>

    <template v-else-if="qna">

      <article class="question-card">

        <div class="badges">
          <span
            class="status"
            :class="qna.status.toLowerCase()"
          >
            {{
              qna.status === 'ANSWERED'
                ? t('m575')
                : t('m576')
            }}
          </span>

          <span class="category">
            {{ categoryLabels[qna.category] }}
          </span>

          <span v-if="qna.secret" class="secret">
            {{ t('m600') }}
          </span>
        </div>

        <h1>{{ qna.title }}</h1>

        <div class="meta">
          <span>
            {{ t('m024') }} <strong>{{ qna.username }}</strong>
          </span>

          <span>
            {{ formatDateTime(qna.createdAt) }}
          </span>
        </div>

        <div class="content">
          {{ qna.content }}
        </div>

        <div v-if="canEdit" class="question-actions">
          <button
            class="edit"
            @click="router.push(`/qna/${qna.id}/edit`)"
          >
            {{ t('m030') }}
          </button>

          <button
            class="delete"
            @click="removeQna"
          >
            {{ t('m032') }}
          </button>
        </div>
      </article>

      <!-- 관리자 답변 -->
      <section class="answer-section">

        <div class="answer-title">
          <div>
            <span>{{ t('m601') }}</span>
            <h2>{{ t('m602') }}</h2>
          </div>

          <span
            v-if="qna.status === 'ANSWERED'"
            class="complete"
          >
            {{ t('m603') }}
          </span>
        </div>

        <!-- 답변이 있는 경우 -->
        <div v-if="qna.answer" class="answer-content">
          <div class="admin-profile">
            <div class="admin-avatar">{{ t('m604') }}</div>

            <div>
              <strong>{{ qna.answeredBy || t('m223') }}</strong>
              <span>
                {{ formatDateTime(qna.answeredAt) }}
              </span>
            </div>
          </div>

          <p>{{ qna.answer }}</p>
        </div>

        <!-- 일반 사용자 + 답변 없음 -->
        <div
          v-else-if="!authStore.isAdmin"
          class="waiting-answer"
        >
          <div>⏳</div>
          <strong>{{ t('m605') }}</strong>
          <span>
            {{ t('m606') }}
          </span>
        </div>

        <!-- 관리자 답변 작성 -->
        <div
          v-if="authStore.isAdmin"
          class="admin-answer-form"
        >
          <label>
            {{ qna.answer ? t('m607') : t('m608') }}
          </label>

          <textarea
            v-model="answer"
            rows="7"
            :placeholder="t('m609')"
          ></textarea>

          <div class="answer-actions">
            <button
              v-if="qna.answer"
              class="remove-answer"
              @click="removeAnswer"
            >
              {{ t('m610') }}
            </button>

            <button
              class="save-answer"
              :disabled="answerSaving"
              @click="submitAnswer"
            >
              {{
                answerSaving
                  ? t('m051')
                  : qna.answer
                    ? t('m607')
                    : t('m611')
              }}
            </button>
          </div>
        </div>

      </section>

    </template>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.detail-page {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 36px 40px 60px;
}

.back {
  margin-bottom: 22px;
  padding: 0;
  border: 0;
  background: none;
  color: #6e798b;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.question-card,
.answer-section {
  border: 1px solid #e3e8ef;
  border-radius: 15px;
  background: white;
}

.question-card {
  padding: 28px;
}

.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 18px;
}

.badges span {
  padding: 5px 8px;
  border-radius: 6px;
  font-size: 9px;
  font-weight: 800;
}

.status.waiting {
  background: #fff7e8;
  color: #b87300;
}

.status.answered {
  background: #edf9f3;
  color: #21865a;
}

.category {
  background: #eef4ff;
  color: #3977dc;
}

.secret {
  background: #f3f1ff;
  color: #6d59c9;
}

h1 {
  margin: 0;
  color: #202b3d;
  font-size: 24px;
  letter-spacing: -.03em;
}

.meta {
  display: flex;
  gap: 18px;
  margin-top: 13px;
  padding-bottom: 21px;
  border-bottom: 1px solid #edf0f4;
  color: #949dab;
  font-size: 10px;
}

.meta strong {
  color: #657084;
}

.content {
  min-height: 180px;
  padding: 28px 3px;
  color: #4a5669;
  font-size: 13px;
  line-height: 1.9;
  white-space: pre-wrap;
}

.question-actions {
  display: flex;
  justify-content: flex-end;
  gap: 7px;
  padding-top: 17px;
  border-top: 1px solid #edf0f4;
}

.question-actions button {
  height: 35px;
  padding: 0 14px;
  border-radius: 7px;
  font-size: 10px;
  font-weight: 700;
  cursor: pointer;
}

.edit {
  border: 1px solid #dfe4eb;
  background: white;
  color: #5c687b;
}

.delete {
  border: 1px solid #fee2e2;
  background: #fff8f8;
  color: #c64c4c;
}

.answer-section {
  margin-top: 16px;
  padding: 25px 28px;
}

.answer-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.answer-title > div > span {
  color: #3b82f6;
  font-size: 9px;
  font-weight: 800;
  letter-spacing: .15em;
}

.answer-title h2 {
  margin: 5px 0 0;
  color: #263145;
  font-size: 16px;
}

.complete {
  padding: 6px 9px;
  border-radius: 6px;
  background: #edf9f3;
  color: #21865a;
  font-size: 9px;
  font-weight: 800;
}

.answer-content {
  padding: 20px;
  border-radius: 11px;
  background: #f8faff;
}

.admin-profile {
  display: flex;
  align-items: center;
  gap: 9px;
}

.admin-avatar {
  display: flex;
  width: 34px;
  height: 34px;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: #e8f0ff;
  color: #3775df;
  font-size: 12px;
  font-weight: 800;
}

.admin-profile > div:last-child {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.admin-profile strong {
  color: #3c485b;
  font-size: 11px;
}

.admin-profile span {
  color: #9ba4b2;
  font-size: 9px;
}

.answer-content p {
  margin: 18px 0 0;
  color: #505d70;
  font-size: 12px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.waiting-answer {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  min-height: 160px;
  border-radius: 10px;
  background: #fafbfd;
}

.waiting-answer div {
  margin-bottom: 9px;
}

.waiting-answer strong {
  color: #596579;
  font-size: 11px;
}

.waiting-answer span {
  margin-top: 5px;
  color: #9aa3b1;
  font-size: 9px;
}

.admin-answer-form {
  margin-top: 18px;
  padding-top: 19px;
  border-top: 1px solid #edf0f4;
}

.admin-answer-form label {
  display: block;
  margin-bottom: 8px;
  color: #465267;
  font-size: 11px;
  font-weight: 750;
}

.admin-answer-form textarea {
  width: 100%;
  padding: 13px;
  border: 1px solid #dfe4eb;
  border-radius: 9px;
  outline: none;
  font-family: inherit;
  font-size: 12px;
  line-height: 1.7;
  resize: vertical;
}

.admin-answer-form textarea:focus {
  border-color: #8eb6fa;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, .07);
}

.answer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 7px;
  margin-top: 10px;
}

.answer-actions button {
  height: 37px;
  padding: 0 15px;
  border-radius: 7px;
  font-size: 10px;
  font-weight: 750;
  cursor: pointer;
}

.remove-answer {
  border: 1px solid #fee2e2;
  background: white;
  color: #c64c4c;
}

.save-answer {
  border: 0;
  background: #2563eb;
  color: white;
}

.error {
  margin-bottom: 15px;
  padding: 13px;
  border-radius: 9px;
  background: #fff1f1;
  color: #b42318;
  font-size: 11px;
}

.loading {
  padding: 70px;
  text-align: center;
  color: #8c96a6;
}

@media (max-width: 700px) {
  .detail-page {
    padding: 22px 15px 40px;
  }

  .question-card,
  .answer-section {
    padding: 19px 16px;
  }

  h1 {
    font-size: 20px;
  }

  .meta {
    flex-direction: column;
    gap: 5px;
  }

  .content {
    min-height: 130px;
  }

  .question-actions button {
    flex: 1;
  }

  .answer-title {
    align-items: flex-start;
    flex-direction: column;
    gap: 10px;
  }

  .answer-actions {
    flex-direction: column-reverse;
  }

  .answer-actions button {
    width: 100%;
  }
}
</style>
