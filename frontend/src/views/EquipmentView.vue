<script setup>
import { useI18n } from 'vue-i18n'
import { useUiMessage } from '../i18n'
const { t } = useI18n()

import { computed, onMounted, ref } from 'vue'
import { getSites } from '../api/Site'
import { getEquipments, getEquipment, getEquipmentHistories, createEquipment, updateEquipment, updateEquipmentStatus, deleteEquipment } from '../api/Equipment'

const statuses = computed(() => ({ NORMAL: t('m081'), WARNING: t('m082'), ERROR: t('m083'), MAINTENANCE: t('m084'), OFFLINE: t('m085'), INACTIVE: t('m086') }))
const types = computed(() => ({ CCTV: 'CCTV', LPR_CAMERA: t('m152'), VEHICLE_DETECTOR: t('m153'), TOLL_EQUIPMENT: t('m154'), VMS: t('m155'), NETWORK: t('m156'), SERVER: t('m157'), ETC: t('m096') }))
const equipments = ref([])
const allEquipments = ref([])
const sites = ref([])
const loading = ref(false)
const busy = ref(false)
const errorMessage = useUiMessage()
const notice = useUiMessage()
const filters = ref({ name: '', siteId: '', status: '', equipmentType: '' })
const selected = ref(null)
const histories = ref([])
const detailOpen = ref(false)
const detailLoading = ref(false)
const detailError = useUiMessage()
const historyError = useUiMessage()
const newStatus = ref('')
const reason = ref('')
const formOpen = ref(false)
const editingId = ref(null)
const formError = useUiMessage()
const emptyForm = () => ({ siteId: '', equipmentCode: '', name: '', equipmentType: 'CCTV', manufacturer: '', modelName: '', serialNumber: '', installLocation: '', installedAt: '', ipAddress: '', description: '' })
const form = ref(emptyForm())
const optionalFields = computed(() => ([ ['manufacturer', t('m158'), 100], ['modelName', t('m159'), 100], ['serialNumber', t('m160'), 100], ['installLocation', t('m161'), 200], ['ipAddress', t('m162'), 45] ]))
const arrayData = (response) => Array.isArray(response.data) ? response.data : []
const message = (error, fallback) => error.response?.data?.message || fallback
const date = (value) => value ? value.replace('T', ' ').slice(0, 19) : '-'
const kpis = computed(() => [
  { label: t('m102'), count: allEquipments.value.length },
  ...Object.entries(statuses.value).filter(([key]) => key !== 'INACTIVE').map(([key, label]) => ({ label, count: allEquipments.value.filter(e => e.status === key).length }))
])

let listRequest = 0
async function loadList() {
  const request = ++listRequest
  loading.value = true
  errorMessage.value = ''
  try {
    const params = Object.fromEntries(Object.entries(filters.value).filter(([, value]) => value !== ''))
    const response = await getEquipments(params)
    if (request === listRequest) equipments.value = arrayData(response)
  } catch (error) {
    if (request === listRequest) errorMessage.value = message(error, t('m163'))
  } finally {
    if (request === listRequest) loading.value = false
  }
}
async function refresh() {
  await loadList()
  try {
    const [equipmentResponse, siteResponse] = await Promise.all([getEquipments(), getSites()])
    allEquipments.value = arrayData(equipmentResponse)
    sites.value = arrayData(siteResponse)
  } catch (error) {
    errorMessage.value = message(error, t('m164'))
  }
}
function resetSearch() {
  filters.value = { name: '', siteId: '', status: '', equipmentType: '' }
  loadList()
}
let detailRequest = 0
async function loadHistories(id) {
  historyError.value = ''
  try {
    const response = await getEquipmentHistories(id)
    if (selected.value?.id === id) histories.value = arrayData(response)
  } catch (error) {
    if (selected.value?.id === id) historyError.value = message(error, t('m165'))
  }
}
async function openDetail(equipment) {
  const request = ++detailRequest
  selected.value = null
  histories.value = []
  detailOpen.value = true
  detailLoading.value = true
  detailError.value = ''
  historyError.value = ''
  newStatus.value = ''
  reason.value = ''
  try {
    const response = await getEquipment(equipment.id)
    if (request !== detailRequest) return
    selected.value = response.data
    await loadHistories(equipment.id)
  } catch (error) {
    if (request === detailRequest) detailError.value = message(error, t('m166'))
  } finally {
    if (request === detailRequest) detailLoading.value = false
  }
}
function closeDetail() {
  if (busy.value) return
  ++detailRequest
  detailOpen.value = false
  selected.value = null
}
function openForm(equipment = null) {
  editingId.value = equipment?.id ?? null
  form.value = emptyForm()
  if (equipment) {
    for (const key of Object.keys(form.value)) form.value[key] = equipment[key] ?? ''
  }
  formError.value = ''
  formOpen.value = true
}
async function save() {
  if (busy.value) return
  busy.value = true
  formError.value = ''
  notice.value = ''
  try {
    const payload = { ...form.value, siteId: Number(form.value.siteId), name: form.value.name.trim(), equipmentCode: form.value.equipmentCode.trim(), installedAt: form.value.installedAt || null }
    if (editingId.value) delete payload.equipmentCode
    const response = editingId.value ? await updateEquipment(editingId.value, payload) : await createEquipment(payload)
    if (selected.value?.id === response.data.id) selected.value = response.data
    formOpen.value = false
    notice.value = editingId.value ? t('m167') : t('m168')
    await refresh()
  } catch (error) {
    formError.value = message(error, t('m169'))
  } finally { busy.value = false }
}
async function changeStatus() {
  if (busy.value || !selected.value || !newStatus.value) return
  busy.value = true
  detailError.value = ''
  try {
    const response = await updateEquipmentStatus(selected.value.id, newStatus.value, reason.value)
    selected.value = response.data
    newStatus.value = ''
    reason.value = ''
    notice.value = t('m170')
    await loadHistories(response.data.id)
    await refresh()
  } catch (error) { detailError.value = message(error, t('m171')) }
  finally { busy.value = false }
}
async function removeEquipment() {
  if (busy.value || !selected.value || !window.confirm(t('m172'))) return
  busy.value = true
  detailError.value = ''
  try {
    await deleteEquipment(selected.value.id)
    detailOpen.value = false
    selected.value = null
    notice.value = t('m173')
    await refresh()
  } catch (error) { detailError.value = message(error, t('m174')) }
  finally { busy.value = false }
}
onMounted(refresh)
</script>

<template>
  <div class="equipment-page">
    <header class="page-header">
      <div><p class="eyebrow">{{ t('m175') }}</p><h1>{{ t('m176') }}</h1><p>{{ t('m177') }}</p></div>
      <button class="primary" :disabled="busy" @click="openForm()">{{ t('m178') }}</button>
    </header>
    <p v-if="notice" class="notice" role="status">{{ notice }}</p>
    <div v-if="errorMessage" class="error" role="alert">{{ errorMessage }} <button @click="refresh">{{ t('m179') }}</button></div>
    <div class="kpi-grid"><div v-for="kpi in kpis" :key="kpi.label" class="kpi-card"><span>{{ kpi.label }}</span><strong>{{ kpi.count }}</strong></div></div>
    <form class="search-panel" @submit.prevent="loadList">
      <label>{{ t('m180') }}<input v-model="filters.name" :placeholder="t('m181')" maxlength="100" /></label>
      <label>{{ t('m182') }}<select v-model="filters.siteId"><option value="">{{ t('m099') }}</option><option v-for="site in sites" :key="site.id" :value="site.id">{{ site.name }} ({{ site.siteCode }})</option></select></label>
      <label>{{ t('m183') }}<select v-model="filters.status"><option value="">{{ t('m184') }}</option><option v-for="(label, key) in statuses" :key="key" :value="key">{{ label }}</option></select></label>
      <label>{{ t('m185') }}<select v-model="filters.equipmentType"><option value="">{{ t('m186') }}</option><option v-for="(label, key) in types" :key="key" :value="key">{{ label }}</option></select></label>
      <button class="primary" :disabled="loading">{{ t('m060') }}</button><button type="button" @click="resetSearch">{{ t('m061') }}</button>
    </form>
    <section class="list-panel" :aria-busy="loading">
      <h2>{{ t('m187') }} <small>{{ t('counts.items', equipments.length) }}</small></h2>
      <p v-if="loading" class="empty">{{ t('m188') }}</p>
      <div v-else class="table-scroll"><table><thead><tr><th>{{ t('m189') }}</th><th>{{ t('m180') }}</th><th>{{ t('m190') }}</th><th>{{ t('m185') }}</th><th>{{ t('m161') }}</th><th>{{ t('m191') }}</th><th>{{ t('m183') }}</th><th>{{ t('m192') }}</th></tr></thead>
        <tbody><tr v-for="equipment in equipments" :key="equipment.id"><td>{{ equipment.equipmentCode }}</td><td>{{ equipment.name }}</td><td>{{ equipment.siteName }}</td><td>{{ types[equipment.equipmentType] }}</td><td>{{ equipment.installLocation || '-' }}</td><td>{{ equipment.ipAddress || '-' }}</td><td><span class="badge" :class="equipment.status">{{ statuses[equipment.status] }}</span></td><td><button @click="openDetail(equipment)">{{ t('m192') }}</button></td></tr>
        <tr v-if="!equipments.length"><td colspan="8" class="empty">{{ t('m193') }}</td></tr></tbody></table></div>
    </section>

    <div v-if="detailOpen" class="overlay" @click.self="closeDetail">
      <section class="modal" role="dialog" aria-modal="true" aria-labelledby="detail-title" @keydown.esc="closeDetail">
        <header class="modal-header"><h2 id="detail-title">{{ t('m194') }}</h2><button :disabled="busy" :aria-label="t('m195')" @click="closeDetail">{{ t('m196') }}</button></header>
        <p v-if="detailLoading" class="empty">{{ t('m197') }}</p>
        <p v-if="detailError" class="error" role="alert">{{ detailError }}</p>
        <template v-if="selected && !detailLoading">
          <div class="detail-grid">
            <div><span>{{ t('m189') }}</span>{{ selected.equipmentCode }}</div><div><span>{{ t('m180') }}</span>{{ selected.name }}</div>
            <div><span>{{ t('m190') }}</span>{{ selected.siteName }} ({{ selected.siteCode }})</div><div><span>{{ t('m185') }}</span>{{ types[selected.equipmentType] }}</div>
            <div v-for="[key, label] in optionalFields" :key="key"><span>{{ label }}</span>{{ selected[key] || '-' }}</div>
            <div><span>{{ t('m198') }}</span>{{ date(selected.installedAt) }}</div><div><span>{{ t('m199') }}</span><b class="badge" :class="selected.status">{{ statuses[selected.status] }}</b></div>
            <div><span>{{ t('m200') }}</span>{{ date(selected.createdAt) }} / {{ date(selected.updatedAt) }}</div>
            <div class="wide"><span>{{ t('m201') }}</span>{{ selected.description || '-' }}</div>
          </div>
          <div class="actions"><button :disabled="busy" @click="openForm(selected)">{{ t('m202') }}</button><button class="danger" :disabled="busy" @click="removeEquipment">{{ t('m203') }}</button></div>
          <section class="section"><h3>{{ t('m204') }}</h3><form class="status-form" @submit.prevent="changeStatus">
            <label>{{ t('m205') }}<select v-model="newStatus" required :disabled="busy"><option value="">{{ t('m206') }}</option><option v-for="(label, key) in statuses" :key="key" :value="key" :disabled="key === selected.status">{{ label }}</option></select></label>
            <label>{{ t('m207') }}<input v-model="reason" maxlength="500" :placeholder="t('m208')" :disabled="busy" /></label><button class="primary" :disabled="busy || !newStatus || newStatus === selected.status">{{ t('m204') }}</button>
          </form></section>
          <section class="section"><h3>{{ t('m209') }}</h3>
            <p v-if="historyError" class="error" role="alert">{{ historyError }} <button @click="loadHistories(selected.id)">{{ t('m179') }}</button></p>
            <p v-else-if="!histories.length" class="empty">{{ t('m210') }}</p>
            <ol class="timeline"><li v-for="history in histories" :key="history.id"><strong>{{ statuses[history.previousStatus] }} → {{ statuses[history.newStatus] }}</strong><p>{{ t('m211') }} {{ history.changedBy }} · {{ date(history.changedAt) }}</p><p>{{ history.reason || t('m212') }}</p></li></ol>
          </section>
        </template>
      </section>
    </div>

    <div v-if="formOpen" class="overlay form-overlay" @click.self="!busy && (formOpen = false)">
      <section class="modal" role="dialog" aria-modal="true" aria-labelledby="form-title" @keydown.esc="!busy && (formOpen = false)">
        <header class="modal-header"><h2 id="form-title">{{ editingId ? t('m202') : t('m213') }}</h2><button :disabled="busy" @click="formOpen = false">{{ t('m196') }}</button></header>
        <p v-if="formError" class="error" role="alert">{{ formError }}</p>
        <p v-if="!sites.length" class="error">{{ t('m214') }} <RouterLink to="/sites">{{ t('m215') }}</RouterLink></p>
        <form @submit.prevent="save"><fieldset :disabled="busy"><div class="form-grid">
          <label>{{ t('m216') }}<select v-model="form.siteId" required><option value="">{{ t('m217') }}</option><option v-for="site in sites" :key="site.id" :value="site.id">{{ site.name }} ({{ site.siteCode }})</option></select></label>
          <label>{{ t('m218') }}<input v-model="form.equipmentCode" required maxlength="30" :disabled="!!editingId" :placeholder="t('m219')" /></label>
          <label>{{ t('m220') }}<input v-model="form.name" required maxlength="100" /></label>
          <label>{{ t('m221') }}<select v-model="form.equipmentType" required><option v-for="(label, key) in types" :key="key" :value="key">{{ label }}</option></select></label>
          <label v-for="[key, label, max] in optionalFields" :key="key">{{ label }}<input v-model="form[key]" :maxlength="max" /></label>
          <label>{{ t('m198') }}<input v-model="form.installedAt" type="datetime-local" step="1" /></label>
          <label class="wide">{{ t('m201') }}<textarea v-model="form.description" maxlength="500" rows="3"></textarea></label>
        </div><div class="actions"><button type="button" @click="formOpen = false">{{ t('m050') }}</button><button class="primary" :disabled="!sites.length">{{ busy ? t('m051') : t('m222') }}</button></div></fieldset></form>
      </section>
    </div>
  </div>
</template>

<style scoped>
.equipment-page { padding: 32px; background: #f5f7fb; color: #172033; text-align: left; min-height: 80vh; color-scheme: light; font-size: 14px; }
.equipment-page * { box-sizing: border-box; }
h1 { font-size: 30px; margin: 8px 0 12px; color: #172033; } h2 { color: #172033; font-size: 20px; } h3 { font-size: 17px; }
.page-header,.modal-header,.actions { display: flex; align-items: center; justify-content: space-between; gap: 12px; } .page-header { margin-bottom: 24px; }
.eyebrow { font-size: 11px; letter-spacing: 2px; color: #64748b; } .page-header p { color: #64748b; }
button,input,select,textarea { font: inherit; border: 1px solid #d9e0ea; border-radius: 8px; padding: 10px 12px; background: white; color: #172033; }
button { cursor: pointer; white-space: nowrap; } button:disabled { opacity: .5; cursor: not-allowed; } .primary { background: #172033; color: white; border-color: #172033; } .danger { color: #b91c1c; }
label { display: flex; flex-direction: column; gap: 6px; min-width: 0; } input,select,textarea { width: 100%; } :focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; }
.kpi-grid { display: grid; grid-template-columns: repeat(6,1fr); gap: 12px; margin-bottom: 22px; } .kpi-card { background: white; border: 1px solid #e7ebf0; border-radius: 12px; padding: 18px; } .kpi-card span { color: #64748b; } .kpi-card strong { display: block; font-size: 28px; margin-top: 10px; }
.search-panel { display: flex; align-items: end; flex-wrap: wrap; gap: 10px; padding: 18px; background: white; border-radius: 12px; margin-bottom: 20px; } .search-panel label { flex: 1 1 130px; }
.list-panel { padding: 20px; background: white; border: 1px solid #e7ebf0; border-radius: 12px; } small { color: #64748b; font-size: 13px; } .table-scroll { overflow-x: auto; } table { border-collapse: collapse; width: 100%; font-size: 13px; } th,td { padding: 13px 10px; border-bottom: 1px solid #e7ebf0; text-align: left; } th { white-space: nowrap; background: #f8fafc; color: #64748b; } td { overflow-wrap: anywhere; min-width: 85px; }
.badge { display: inline-block; white-space: nowrap; border-radius: 20px; padding: 4px 9px; font-size: 12px; background: #eef2f6; color: #475569; } .NORMAL { background: #dcfce7; color: #166534; } .WARNING { background: #fef3c7; color: #92400e; } .ERROR { background: #fee2e2; color: #b91c1c; } .MAINTENANCE { background: #dbeafe; color: #1e40af; }
.empty { padding: 32px; text-align: center; color: #64748b; } .error,.notice { padding: 12px; border-radius: 8px; margin-bottom: 14px; } .error { color: #b91c1c; background: #fef2f2; } .notice { color: #166534; background: #f0fdf4; }
.overlay { position: fixed; inset: 0; z-index: 100; display: flex; align-items: center; justify-content: center; padding: 20px; background: #0f172a99; } .form-overlay { z-index: 110; } .modal { width: min(850px,100%); max-height: 90vh; overflow-y: auto; background: white; padding: 26px; border-radius: 18px; box-shadow: 0 25px 70px #0003; } .modal-header { margin-bottom: 22px; }
.detail-grid,.form-grid { display: grid; grid-template-columns: repeat(2,minmax(0,1fr)); gap: 14px; } .detail-grid>div { padding: 14px; background: #fafbfd; border: 1px solid #e7ebf0; border-radius: 8px; overflow-wrap: anywhere; white-space: pre-wrap; } .detail-grid span { display: block; color: #64748b; font-size: 12px; margin-bottom: 5px; } .wide { grid-column: 1/-1; }
.actions { justify-content: flex-end; margin-top: 20px; } .section { border-top: 1px solid #e7ebf0; margin-top: 24px; padding-top: 12px; } .status-form { display: grid; grid-template-columns: 160px 1fr auto; align-items: end; gap: 10px; } .timeline { list-style: none; padding: 0 0 0 15px; } .timeline li { border-left: 2px solid #cbd5e1; padding: 0 0 24px 18px; position: relative; overflow-wrap: anywhere; } .timeline li::before { content: ''; position: absolute; width: 10px; height: 10px; border-radius: 50%; background: #172033; top: 5px; left: -6px; } .timeline p { color: #64748b; margin-top: 7px; white-space: pre-wrap; } fieldset { padding: 0; margin: 0; border: 0; min-width: 0; }
@media(max-width:900px) { .kpi-grid { grid-template-columns: repeat(3,1fr); } .equipment-page { padding: 20px; } .status-form { grid-template-columns: 1fr; } }
@media(max-width:600px) { .kpi-grid { grid-template-columns: repeat(2,1fr); } .page-header { align-items: flex-start; flex-direction: column; } .detail-grid,.form-grid { grid-template-columns: 1fr; } .modal { padding: 18px; } }
</style>
