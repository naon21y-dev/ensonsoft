<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { useUiMessage } from '../i18n'

import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  createQna,
  getQna,
  updateQna
} from '../api/Qna'

const route = useRoute()
const router = useRouter()

const category = ref('GENERAL')
const title = ref('')
const content = ref('')
const secret = ref(false)

const loading = ref(false)
const saving = ref(false)
const message = useUiMessage()

const id = computed(() => route.params.id)
const isEdit = computed(() => !!id.value)

const categories = computed(() => ([
  ['GENERAL', t('m551')],
  ['SITE', t('m552')],
  ['EQUIPMENT', t('m553')],
  ['VEHICLE', t('m342')],
  ['VIDEO_ANALYSIS', t('m554')],
  ['MAINTENANCE', t('m555')],
  ['ETC', t('m096')]
]))

const loadQna = async () => {
  if (!isEdit.value) return

  loading.value = true

  try {
    const response = await getQna(id.value)
    const qna = response.data

    category.value = qna.category
    title.value = qna.title
    content.value = qna.content
    secret.value = qna.secret
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      t('m577')
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  if (!title.value.trim()) {
    message.value = t('m033')
    return
  }

  if (!content.value.trim()) {
    message.value = t('m578')
    return
  }

  saving.value = true
  message.value = ''

  const data = {
    category: category.value,
    title: title.value.trim(),
    content: content.value.trim(),
    secret: secret.value
  }

  try {
    let response

    if (isEdit.value) {
      response = await updateQna(id.value, data)
    } else {
      response = await createQna(data)
    }

    router.push(`/qna/${response.data.id}`)
  } catch (error) {
    console.error(error)

    message.value =
      error.response?.data?.message ||
      t('m579')
  } finally {
    saving.value = false
  }
}

onMounted(loadQna)
</script>

<template>
  <div class="form-page">

    <header>
      <button class="back" @click="router.push('/qna')">
        {{ t('m580') }}
      </button>

      <span class="eyebrow">{{ t('m557') }}</span>

      <h1>
        {{ isEdit ? t('m581') : t('m560') }}
      </h1>

      <p>
        {{ t('m582') }}
      </p>
    </header>

    <div v-if="message" class="error">
      {{ message }}
    </div>

    <div v-if="loading" class="loading">
      {{ t('m583') }}
    </div>

    <form v-else class="form-card" @submit.prevent="submit">

      <div class="field">
        <label>{{ t('m584') }} <strong>*</strong></label>

        <select v-model="category">
          <option
            v-for="[value, label] in categories"
            :key="value"
            :value="value"
          >
            {{ label }}
          </option>
        </select>
      </div>

      <div class="field">
        <label>{{ t('m042') }} <strong>*</strong></label>

        <input
          v-model="title"
          maxlength="200"
          :placeholder="t('m585')"
        />

        <small>{{ title.length }} / 200</small>
      </div>

      <div class="field">
        <label>{{ t('m586') }} <strong>*</strong></label>

        <textarea
          v-model="content"
          rows="12"
          :placeholder="t('m587')"
        ></textarea>
      </div>

      <label class="secret-box">
        <input v-model="secret" type="checkbox" />

        <div>
          <strong>{{ t('m567') }}</strong>
          <span>
            {{ t('m588') }}
          </span>
        </div>
      </label>

      <div class="actions">
        <button
          type="button"
          class="cancel"
          @click="router.back()"
        >
          {{ t('m050') }}
        </button>

        <button
          type="submit"
          class="submit"
          :disabled="saving"
        >
          {{
            saving
              ? t('m051')
              : isEdit
                ? t('m589')
                : t('m590')
          }}
        </button>
      </div>

    </form>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.form-page {
  width: 100%;
  max-width: 920px;
  margin: 0 auto;
  padding: 36px 40px 60px;
}

header {
  margin-bottom: 25px;
}

.back {
  margin-bottom: 24px;
  padding: 0;
  border: 0;
  background: none;
  color: #6f7a8d;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.eyebrow {
  display: block;
  margin-bottom: 8px;
  color: #3b82f6;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: .17em;
}

h1 {
  margin: 0;
  color: #172033;
  font-size: 29px;
  letter-spacing: -.04em;
}

header p {
  margin: 8px 0 0;
  color: #8790a0;
  font-size: 12px;
}

.form-card {
  padding: 27px;
  border: 1px solid #e3e8ef;
  border-radius: 15px;
  background: white;
}

.field {
  position: relative;
  margin-bottom: 23px;
}

.field label {
  display: block;
  margin-bottom: 9px;
  color: #3e495c;
  font-size: 12px;
  font-weight: 750;
}

.field label strong {
  color: #3b82f6;
}

.field input,
.field select,
.field textarea {
  width: 100%;
  border: 1px solid #dfe4eb;
  border-radius: 9px;
  outline: none;
  background: #fbfcfd;
  color: #344054;
  font-family: inherit;
  font-size: 12px;
  transition: .2s;
}

.field input,
.field select {
  height: 43px;
  padding: 0 13px;
}

.field textarea {
  min-height: 220px;
  padding: 13px;
  line-height: 1.7;
  resize: vertical;
}

.field input:focus,
.field select:focus,
.field textarea:focus {
  border-color: #8eb6fa;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, .07);
}

.field small {
  position: absolute;
  right: 3px;
  bottom: -17px;
  color: #a2aab7;
  font-size: 9px;
}

.secret-box {
  display: flex;
  align-items: flex-start;
  gap: 11px;
  padding: 15px;
  border: 1px solid #e4e8ef;
  border-radius: 10px;
  background: #f9fafc;
  cursor: pointer;
}

.secret-box input {
  margin-top: 3px;
}

.secret-box div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.secret-box strong {
  color: #455064;
  font-size: 11px;
}

.secret-box span {
  color: #9099a8;
  font-size: 10px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 27px;
  padding-top: 20px;
  border-top: 1px solid #edf0f4;
}

.actions button {
  height: 40px;
  padding: 0 19px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 750;
  cursor: pointer;
}

.cancel {
  border: 1px solid #dfe4eb;
  background: white;
  color: #687386;
}

.submit {
  border: 0;
  background: #2563eb;
  color: white;
}

.submit:disabled {
  opacity: .6;
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
  padding: 60px;
  text-align: center;
  color: #8b95a5;
}

@media (max-width: 700px) {
  .form-page {
    padding: 22px 15px 40px;
  }

  h1 {
    font-size: 25px;
  }

  .form-card {
    padding: 19px 16px;
  }

  .actions {
    flex-direction: column-reverse;
  }

  .actions button {
    width: 100%;
  }
}
</style>
