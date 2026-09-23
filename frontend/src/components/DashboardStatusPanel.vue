<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, required: true },
  items: { type: Array, required: true },
  to: { type: String, required: true }
})
const total = computed(() => props.items.reduce((sum, item) => sum + item.count, 0))
const percent = (count) => total.value ? count / total.value * 100 : 0
const number = (value) => value.toLocaleString('ko-KR')
const gradient = computed(() => {
  if (!total.value) return '#e8edf4'
  let position = 0
  const stops = props.items.map(item => {
    const start = position
    position += percent(item.count)
    return `${item.color} ${start}% ${position}%`
  })
  return `conic-gradient(${stops.join(', ')})`
})
</script>

<template>
  <section class="status-panel">
    <header><div><h3>{{ title }}</h3><p>{{ subtitle }}</p></div><RouterLink :to="to" :aria-label="`${title} 관리 화면 보기`">관리 ↗</RouterLink></header>
    <div class="chart-layout">
      <div class="ring" :style="{ background: gradient }" aria-hidden="true"><div class="ring-center"><span>전체</span><strong>{{ number(total) }}</strong><small>{{ total ? '상태별 분포' : '등록 데이터 없음' }}</small></div></div>
      <ul class="legend"><li v-for="item in items" :key="item.key">
        <div class="legend-label"><span><i :style="{ background: item.color }" aria-hidden="true"></i>{{ item.label }}</span><span><strong>{{ number(item.count) }}</strong><small>{{ percent(item.count).toFixed(1) }}%</small></span></div>
        <div class="track" aria-hidden="true"><div :style="{ width: `${percent(item.count)}%`, background: item.color }"></div></div>
      </li></ul>
    </div>
  </section>
</template>

<style scoped>
.status-panel { border: 1px solid #e3e9f1; border-radius: 16px; background: white; padding: 24px; min-width: 0; }
header { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
h3 { margin: 0; color: #192b44; font-size: 16px; } header p { font-size: 12px; color: #64748b; margin: 5px 0 0; }
a { color: #3b63a0; font-size: 12px; text-decoration: none; white-space: nowrap; padding: 2px; } a:hover { text-decoration: underline; } a:focus-visible { outline: 2px solid #2563eb; outline-offset: 3px; }
.chart-layout { display: flex; align-items: center; gap: 25px; margin-top: 26px; min-height: 185px; }
.ring { flex: 0 0 140px; width: 140px; height: 140px; padding: 14px; border-radius: 50%; }
.ring-center { border-radius: 50%; height: 100%; background: white; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.ring-center span { font-size: 11px; color: #64748b; } .ring-center strong { font-size: 26px; color: #192b44; line-height: 1.3; font-variant-numeric: tabular-nums; } .ring-center small { font-size: 10px; color: #64748b; }
.legend { flex: 1; min-width: 0; list-style: none; padding: 0; margin: 0; display: grid; gap: 11px; }
.legend-label { display: flex; justify-content: space-between; gap: 8px; font-size: 12px; color: #475569; }
.legend-label span { display: flex; align-items: center; gap: 7px; } i { width: 7px; height: 7px; border-radius: 50%; flex-shrink: 0; }
.legend-label strong { color: #192b44; font-variant-numeric: tabular-nums; } .legend-label small { font-size: 10px; min-width: 40px; text-align: right; color: #64748b; font-variant-numeric: tabular-nums; }
.track { height: 4px; background: #f0f3f7; margin-top: 5px; border-radius: 5px; overflow: hidden; } .track div { height: 100%; border-radius: inherit; }
@media(max-width:1100px) and (min-width:801px) { .chart-layout { gap: 15px; } .ring { flex-basis: 115px; width: 115px; height: 115px; padding: 12px; } .ring-center strong { font-size: 22px; } .status-panel { padding: 20px; } }
@media(max-width:420px) { .chart-layout { flex-direction: column; gap: 22px; } .legend { width: 100%; } .status-panel { padding: 20px; } }
</style>
