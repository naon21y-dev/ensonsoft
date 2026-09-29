<script setup>
import { useI18n } from 'vue-i18n'
import LanguageSwitcher from '../components/LanguageSwitcher.vue'
import { useUiMessage } from '../i18n'
const { t } = useI18n()

import { computed, onMounted, ref, watch } from 'vue'
import { getSites } from '../api/Site'
import { getEquipmentsBySite } from '../api/Equipment'
import { getMaintenances, getMaintenance, createMaintenance, startMaintenanceProcessing, completeMaintenance, getMaintenanceHistories } from '../api/Maintenance'

const statuses = computed(() => ({ REPORTED: t('m087'), IN_PROGRESS: t('m088'), COMPLETED: t('m089') }))
const maintenances = ref([])
const loading = ref(false)
const busy = ref(false)
const errorMessage = useUiMessage()
const notice = useUiMessage()
const statusFilter = ref('')
const createDialog = ref(null)
const detailDialog = ref(null)
const sites = ref([])
const equipments = ref([])
const sitesLoading = ref(false)
const equipmentsLoading = ref(false)
const choicesError = useUiMessage()
const formError = useUiMessage()
const form = ref({ siteId: '', equipmentId: '', title: '', content: '', occurredAt: '' })
const selected = ref(null)
const selectedId = ref(null)
const detailLoading = ref(false)
const detailError = useUiMessage()
const histories = ref([])
const historyLoading = ref(false)
const historyError = useUiMessage()
const result = ref('')
const arrayData = (response) => Array.isArray(response.data) ? response.data : []
const message = (error, fallback) => error.response?.data?.message || fallback
const date = (value) => value ? value.replace('T', ' ').slice(0, 19) : '-'
const visibleRows = computed(() => maintenances.value.filter(item => !statusFilter.value || item.status === statusFilter.value))
const kpis = computed(() => [
  { label: t('m273'), count: maintenances.value.length },
  ...Object.entries(statuses.value).map(([key, label]) => ({ label, count: maintenances.value.filter(item => item.status === key).length }))
])

async function loadList() {
  if (loading.value) return
  loading.value = true
  errorMessage.value = ''
  try { maintenances.value = arrayData(await getMaintenances()) }
  catch (error) { errorMessage.value = message(error, t('m274')) }
  finally { loading.value = false }
}

async function loadSites() {
  sitesLoading.value = true
  choicesError.value = ''
  try { sites.value = arrayData(await getSites()) }
  catch (error) { choicesError.value = message(error, t('m275')) }
  finally { sitesLoading.value = false }
}

let equipmentRequest = 0
async function loadEquipments() {
  const request = ++equipmentRequest
  const siteId = form.value.siteId
  form.value.equipmentId = ''
  equipments.value = []
  choicesError.value = ''
  equipmentsLoading.value = !!siteId
  if (!siteId) return
  try {
    const response = await getEquipmentsBySite(siteId)
    if (request === equipmentRequest) equipments.value = arrayData(response)
  } catch (error) {
    if (request === equipmentRequest) choicesError.value = message(error, t('m276'))
  } finally {
    if (request === equipmentRequest) equipmentsLoading.value = false
  }
}
watch(() => form.value.siteId, loadEquipments)

function openCreate() {
  ++equipmentRequest
  equipments.value = []
  equipmentsLoading.value = false
  sites.value = []
  const now = new Date()
  const localTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 19)
  form.value = { siteId: '', equipmentId: '', title: '', content: '', occurredAt: localTime }
  formError.value = ''
  createDialog.value.showModal()
  loadSites()
}
function closeCreate() {
  if (busy.value) return
  ++equipmentRequest
  createDialog.value.close()
}
async function submitCreate() {
  if (busy.value || equipmentsLoading.value || sitesLoading.value) return
  if (!equipments.value.some(item => item.id === Number(form.value.equipmentId))) {
    formError.value = t('m277')
    return
  }
  if (!form.value.title.trim() || !form.value.content.trim()) {
    formError.value = t('m278')
    return
  }
  busy.value = true
  formError.value = ''
  try {
    const response = await createMaintenance({ equipmentId: Number(form.value.equipmentId), title: form.value.title.trim(), content: form.value.content.trim(), occurredAt: form.value.occurredAt })
    maintenances.value.unshift(response.data)
    statusFilter.value = ''
    notice.value = t('m279')
    createDialog.value.close()
  } catch (error) { formError.value = message(error, t('m280')) }
  finally { busy.value = false }
}

let detailRequest = 0
async function loadHistories() {
  const id = selectedId.value
  const request = detailRequest
  historyLoading.value = true
  historyError.value = ''
  try {
    const response = await getMaintenanceHistories(id)
    if (request === detailRequest) histories.value = arrayData(response)
  } catch (error) {
    if (request === detailRequest) historyError.value = message(error, t('m281'))
  } finally {
    if (request === detailRequest) historyLoading.value = false
  }
}
async function loadDetail() {
  const request = ++detailRequest
  detailLoading.value = true
  detailError.value = ''
  selected.value = null
  histories.value = []
  try {
    const response = await getMaintenance(selectedId.value)
    if (request !== detailRequest) return
    selected.value = response.data
    await loadHistories()
  } catch (error) {
    if (request === detailRequest) detailError.value = message(error, t('m282'))
  } finally {
    if (request === detailRequest) detailLoading.value = false
  }
}
function openDetail(item) {
  selectedId.value = item.id
  result.value = ''
  detailDialog.value.showModal()
  loadDetail()
}
function closeDetail() {
  if (busy.value) return
  ++detailRequest
  detailDialog.value.close()
}
async function processMaintenance(complete = false) {
  if (busy.value || !selected.value) return
  if (complete && !result.value.trim()) {
    detailError.value = t('m283')
    return
  }
  busy.value = true
  detailError.value = ''
  try {
    const response = complete
      ? await completeMaintenance(selected.value.id, result.value.trim())
      : await startMaintenanceProcessing(selected.value.id)
    selected.value = response.data
    const index = maintenances.value.findIndex(item => item.id === response.data.id)
    if (index !== -1) maintenances.value[index] = response.data
    result.value = ''
    notice.value = complete ? t('m284') : t('m285')
    await loadHistories()
  } catch (error) { detailError.value = message(error, t('m286')) }
  finally { busy.value = false }
}
onMounted(loadList)
</script>

<template>
  <div class="maintenance-page">
    <header class="page-header">
      <div><p class="eyebrow">{{ t('m287') }}</p><h1>{{ t('m288') }}</h1><p>{{ t('m289') }}</p></div>
      <button class="primary" :disabled="loading || busy" @click="openCreate">{{ t('m290') }}</button>
    </header>
    <p v-if="notice" class="notice" role="status">{{ notice }}</p>
    <p v-if="errorMessage" class="error" role="alert">{{ errorMessage }} <button :disabled="loading" @click="loadList">{{ t('m179') }}</button></p>
    <div class="kpi-grid"><div v-for="kpi in kpis" :key="kpi.label" class="kpi-card"><span>{{ kpi.label }}</span><strong>{{ kpi.count }}</strong></div></div>
    <section class="list-panel">
      <div class="list-header"><h2>{{ t('m291') }} <small>{{ t('counts.items', visibleRows.length) }}</small></h2><div class="list-controls"><label>{{ t('m183') }}<select v-model="statusFilter"><option value="">{{ t('m184') }}</option><option v-for="(label, key) in statuses" :key="key" :value="key">{{ label }}</option></select></label><button :disabled="loading || busy" @click="loadList">{{ t('m128') }}</button></div></div>
      <p v-if="loading" class="empty" role="status">{{ t('m292') }}</p>
      <div v-else class="table-scroll"><table><thead><tr><th>{{ t('m293') }}</th><th>{{ t('m042') }}</th><th>{{ t('m294') }}</th><th>{{ t('m183') }}</th><th>{{ t('m295') }}</th><th>{{ t('m147') }}</th><th>{{ t('m192') }}</th></tr></thead><tbody>
        <tr v-for="item in visibleRows" :key="item.id"><td class="code-cell">{{ item.maintenanceCode }}</td><td>{{ item.title }}</td><td>{{ item.siteName }}<br />{{ item.equipmentName }}</td><td><span class="badge" :class="item.status">{{ statuses[item.status] }}</span></td><td>{{ date(item.occurredAt) }}</td><td>{{ item.reportedBy }}</td><td><button @click="openDetail(item)">{{ t('m192') }}</button></td></tr>
        <tr v-if="!visibleRows.length"><td colspan="7" class="empty">{{ t('m296') }}</td></tr>
      </tbody></table></div>
    </section>

    <dialog ref="createDialog" class="modal" aria-labelledby="create-title" @cancel.prevent="closeCreate">
      <header class="modal-header"><h2 id="create-title">{{ t('m297') }}</h2><LanguageSwitcher /><button :disabled="busy" @click="closeCreate">{{ t('m196') }}</button></header>
      <p class="hint">{{ t('m298') }}</p>
      <p v-if="formError" class="error" role="alert">{{ formError }}</p>
      <p v-if="choicesError" class="error" role="alert">{{ choicesError }} <button type="button" :disabled="sitesLoading || equipmentsLoading" @click="form.siteId ? loadEquipments() : loadSites()">{{ t('m179') }}</button></p>
      <form @submit.prevent="submitCreate"><fieldset :disabled="busy"><div class="form-grid">
        <label>{{ t('m299') }}<select v-model="form.siteId" required :disabled="sitesLoading"><option value="">{{ sitesLoading ? t('m300') : t('m217') }}</option><option v-for="site in sites" :key="site.id" :value="site.id">{{ site.name }} ({{ site.siteCode }})</option></select></label>
        <label>{{ t('m301') }}<select v-model="form.equipmentId" required :disabled="!form.siteId || equipmentsLoading"><option value="">{{ equipmentsLoading ? t('m302') : t('m303') }}</option><option v-for="equipment in equipments" :key="equipment.id" :value="equipment.id">{{ equipment.name }} ({{ equipment.equipmentCode }})</option></select></label>
        <p v-if="!sitesLoading && !choicesError && !sites.length" class="wide hint">{{ t('m304') }}</p>
        <p v-if="form.siteId && !equipmentsLoading && !choicesError && !equipments.length" class="wide hint">{{ t('m305') }}</p>
        <label class="wide">{{ t('m306') }}<input v-model="form.title" required maxlength="100" :placeholder="t('m307')" /></label>
        <label class="wide">{{ t('m308') }}<textarea v-model="form.content" required maxlength="2000" rows="4" :placeholder="t('m309')"></textarea></label>
        <label>{{ t('m310') }}<input v-model="form.occurredAt" required type="datetime-local" step="1" /></label>
      </div><div class="actions"><button type="button" @click="closeCreate">{{ t('m050') }}</button><button class="primary" :disabled="sitesLoading || equipmentsLoading || !form.equipmentId">{{ busy ? t('m311') : t('m297') }}</button></div></fieldset></form>
    </dialog>

    <dialog ref="detailDialog" class="modal" aria-labelledby="detail-title" @cancel.prevent="closeDetail">
      <header class="modal-header"><h2 id="detail-title">{{ t('m312') }}</h2><LanguageSwitcher /><div class="list-controls"><button :disabled="busy || detailLoading" @click="loadDetail">{{ t('m128') }}</button><button :disabled="busy" @click="closeDetail">{{ t('m196') }}</button></div></header>
      <p v-if="detailError" class="error" role="alert">{{ detailError }}</p>
      <p v-if="detailLoading" class="empty">{{ t('m197') }}</p>
      <template v-else-if="selected">
        <div class="detail-grid">
          <div class="wide"><span>{{ t('m293') }}</span>{{ selected.maintenanceCode }}</div>
          <div><span>{{ t('m182') }}</span>{{ selected.siteName }} ({{ selected.siteCode }})</div><div><span>{{ t('m313') }}</span>{{ selected.equipmentName }} ({{ selected.equipmentCode }})</div>
          <div class="wide"><span>{{ t('m042') }}</span>{{ selected.title }}</div><div class="wide"><span>{{ t('m046') }}</span>{{ selected.content }}</div>
          <div><span>{{ t('m183') }}</span><b class="badge" :class="selected.status">{{ statuses[selected.status] }}</b></div><div><span>{{ t('m295') }}</span>{{ date(selected.occurredAt) }}</div>
          <div><span>{{ t('m147') }}</span>{{ selected.reportedBy }}</div><div><span>{{ t('m314') }}</span>{{ date(selected.reportedAt) }}</div>
          <div><span>{{ t('m315') }}</span>{{ date(selected.completedAt) }}</div><div class="wide"><span>{{ t('m316') }}</span>{{ selected.result || '-' }}</div>
        </div>
        <section v-if="selected.status !== 'COMPLETED'" class="section"><h3>{{ t('m317') }}</h3>
          <button v-if="selected.status === 'REPORTED'" class="primary" :disabled="busy" @click="processMaintenance()">{{ busy ? t('m318') : t('m319') }}</button>
          <form v-else @submit.prevent="processMaintenance(true)"><label>{{ t('m320') }}<textarea v-model="result" required maxlength="2000" rows="4" :disabled="busy" :placeholder="t('m321')"></textarea></label><div class="actions"><button class="primary" :disabled="busy || !result.trim()">{{ busy ? t('m051') : t('m322') }}</button></div></form>
        </section>
        <section class="section"><h3>{{ t('m323') }}</h3>
          <p v-if="historyLoading" class="empty">{{ t('m324') }}</p>
          <p v-else-if="historyError" class="error" role="alert">{{ historyError }} <button :disabled="busy" @click="loadHistories">{{ t('m179') }}</button></p>
          <p v-else-if="!histories.length" class="empty">{{ t('m325') }}</p>
          <ol v-else class="timeline"><li v-for="history in histories" :key="history.id"><strong>{{ history.previousStatus ? statuses[history.previousStatus] + ' → ' : '' }}{{ statuses[history.newStatus] }}</strong><p>{{ t('m326') }} {{ history.processedBy }} · {{ date(history.processedAt) }}</p><p>{{ history.description }}</p></li></ol>
        </section>
      </template>
    </dialog>
  </div>
</template>


<style scoped>
.maintenance-page { padding: 32px; background: #f5f7fb; color: #172033; text-align: left; min-height: 80vh; color-scheme: light; font-size: 14px; }
.maintenance-page * { box-sizing: border-box; }
h1 { font-size: 30px; margin: 8px 0 12px; color: #172033; } h2 { color: #172033; font-size: 20px; } h3 { font-size: 17px; }
.page-header,.modal-header,.actions { display: flex; align-items: center; justify-content: space-between; gap: 12px; } .page-header { margin-bottom: 24px; }
.eyebrow { font-size: 11px; letter-spacing: 2px; color: #64748b; } .page-header p { color: #64748b; }
button,input,select,textarea { font: inherit; border: 1px solid #d9e0ea; border-radius: 8px; padding: 10px 12px; background: white; color: #172033; }
button { cursor: pointer; white-space: nowrap; } button:disabled { opacity: .5; cursor: not-allowed; } .primary { background: #172033; color: white; border-color: #172033; } .danger { color: #b91c1c; }
label { display: flex; flex-direction: column; gap: 6px; min-width: 0; } input,select,textarea { width: 100%; } :focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; }
.kpi-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 12px; margin-bottom: 22px; } .kpi-card { background: white; border: 1px solid #e7ebf0; border-radius: 12px; padding: 18px; } .kpi-card span { color: #64748b; } .kpi-card strong { display: block; font-size: 28px; margin-top: 10px; }
.search-panel { display: flex; align-items: end; flex-wrap: wrap; gap: 10px; padding: 18px; background: white; border-radius: 12px; margin-bottom: 20px; } .search-panel label { flex: 1 1 130px; }
.list-panel { padding: 20px; background: white; border: 1px solid #e7ebf0; border-radius: 12px; } small { color: #64748b; font-size: 13px; } .table-scroll { overflow-x: auto; } table { border-collapse: collapse; width: 100%; font-size: 13px; } th,td { padding: 13px 10px; border-bottom: 1px solid #e7ebf0; text-align: left; } th { white-space: nowrap; background: #f8fafc; color: #64748b; } td { overflow-wrap: anywhere; min-width: 85px; }
.badge { display: inline-block; white-space: nowrap; border-radius: 20px; padding: 4px 9px; font-size: 12px; background: #eef2f6; color: #475569; } .NORMAL { background: #dcfce7; color: #166534; } .WARNING { background: #fef3c7; color: #92400e; } .ERROR { background: #fee2e2; color: #b91c1c; } .MAINTENANCE { background: #dbeafe; color: #1e40af; }
.empty { padding: 32px; text-align: center; color: #64748b; } .error,.notice { padding: 12px; border-radius: 8px; margin-bottom: 14px; } .error { color: #b91c1c; background: #fef2f2; } .notice { color: #166534; background: #f0fdf4; }
.overlay { position: fixed; inset: 0; z-index: 100; display: flex; align-items: center; justify-content: center; padding: 20px; background: #0f172a99; } .form-overlay { z-index: 110; } .modal { width: min(850px,100%); max-height: 90vh; overflow-y: auto; background: white; padding: 26px; border-radius: 18px; box-shadow: 0 25px 70px #0003; } .modal-header { margin-bottom: 22px; }
.detail-grid,.form-grid { display: grid; grid-template-columns: repeat(2,minmax(0,1fr)); gap: 14px; } .detail-grid>div { padding: 14px; background: #fafbfd; border: 1px solid #e7ebf0; border-radius: 8px; overflow-wrap: anywhere; white-space: pre-wrap; } .detail-grid span { display: block; color: #64748b; font-size: 12px; margin-bottom: 5px; } .wide { grid-column: 1/-1; }
.actions { justify-content: flex-end; margin-top: 20px; } .section { border-top: 1px solid #e7ebf0; margin-top: 24px; padding-top: 12px; } .status-form { display: grid; grid-template-columns: 160px 1fr auto; align-items: end; gap: 10px; } .timeline { list-style: none; padding: 0 0 0 15px; } .timeline li { border-left: 2px solid #cbd5e1; padding: 0 0 24px 18px; position: relative; overflow-wrap: anywhere; } .timeline li::before { content: ''; position: absolute; width: 10px; height: 10px; border-radius: 50%; background: #172033; top: 5px; left: -6px; } .timeline p { color: #64748b; margin-top: 7px; white-space: pre-wrap; } fieldset { padding: 0; margin: 0; border: 0; min-width: 0; }
@media(max-width:900px) { .kpi-grid { grid-template-columns: repeat(3,1fr); } .maintenance-page { padding: 20px; } .status-form { grid-template-columns: 1fr; } }
@media(max-width:600px) { .kpi-grid { grid-template-columns: repeat(2,1fr); } .page-header { align-items: flex-start; flex-direction: column; } .detail-grid,.form-grid { grid-template-columns: 1fr; } .modal { padding: 18px; } }
.modal { border: 0; color: #172033; text-align: left; }
.modal::backdrop { background: #0f172a99; }
.list-header,.list-controls { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.list-header { margin-bottom: 18px; }
.list-controls label { flex-direction: row; align-items: center; white-space: nowrap; }
.list-controls select { width: auto; }
.hint { color: #64748b; margin-bottom: 16px; font-size: 13px; }
.code-cell { max-width: 180px; font-size: 12px; }
.REPORTED { background: #fef3c7; color: #92400e; }
.IN_PROGRESS { background: #dbeafe; color: #1e40af; }
.COMPLETED { background: #dcfce7; color: #166534; }
@media(max-width:600px) { .list-header { align-items: flex-start; flex-direction: column; } }
</style>
