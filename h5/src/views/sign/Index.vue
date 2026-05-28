<template>
  <div class="sign page-content" v-if="info">
    <!-- 顶部头卡 -->
    <div class="head-card">
      <div class="left">
        <div class="title">每日签到</div>
        <div class="sub">连续签到享更多奖励 🎁</div>
      </div>
      <div class="right">
        <div class="days">{{ info.continuousDays || 0 }}</div>
        <div class="label">连续天数</div>
      </div>
    </div>

    <!-- 连续签到进度条 -->
    <div class="card progress-card" v-if="rewards.length">
      <div class="progress-title">连续签到奖励</div>
      <div class="progress-list">
        <div v-for="r in rewards" :key="r.day"
             :class="['progress-item', info.continuousDays >= r.day ? 'reached' : '']">
          <div class="dot">{{ r.day }}</div>
          <div class="reward">+{{ r.points }}</div>
        </div>
      </div>
    </div>

    <!-- 签到按钮 -->
    <div class="card sign-card">
      <van-button
        :type="info.signedToday ? 'default' : 'primary'"
        :disabled="info.signedToday"
        block round
        @click="onSign"
      >
        {{ info.signedToday ? '今日已签到' : `签到 +${info.config?.basePoints || 0} 积分` }}
      </van-button>
    </div>

    <!-- 日历 -->
    <div class="card calendar-card">
      <div class="month-bar">
        <span>{{ year }}年{{ month }}月签到</span>
        <span class="month-stat">本月已签 {{ info.monthlyDays || 0 }} 天</span>
      </div>
      <div class="weekdays">
        <span v-for="w in weekDays" :key="w">{{ w }}</span>
      </div>
      <div class="days-grid">
        <div v-for="cell in days" :key="cell.key"
             :class="['day-cell', cell.signed ? 'signed' : '', cell.repairable ? 'repairable' : '', cell.today ? 'today' : '']"
             @click="onCellClick(cell)">
          <span v-if="cell.day">{{ cell.day }}</span>
          <van-icon v-if="cell.signed" name="success" class="check" />
        </div>
      </div>
      <div class="legend">
        <span><i class="dot signed"></i>已签</span>
        <span v-if="info.config?.repairEnabled === '1'">
          <i class="dot repair"></i>可补签({{ info.config?.repairCost }}积分)
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showDialog, showToast } from 'vant'
import { signInfo, doSign, repairSign } from '@/api/sign'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const info = ref(null)
const year = ref(new Date().getFullYear())
const month = ref(new Date().getMonth() + 1)
const weekDays = ['日','一','二','三','四','五','六']

const rewards = computed(() => {
  try { return JSON.parse(info.value?.config?.continuousReward || '[]') }
  catch (e) { return [] }
})

const days = computed(() => {
  const list = []
  const firstDay = new Date(year.value, month.value - 1, 1).getDay()
  const lastDate = new Date(year.value, month.value, 0).getDate()
  // 头部空格
  for (let i = 0; i < firstDay; i++) list.push({ key: 'b' + i, day: '' })
  const todayDate = new Date()
  const isCurMonth = todayDate.getFullYear() === year.value && todayDate.getMonth() + 1 === month.value
  const todayD = todayDate.getDate()
  const signed = new Set(info.value?.signedDates || [])
  const repairable = new Set(info.value?.repairableDates || [])

  for (let d = 1; d <= lastDate; d++) {
    const ymd = `${year.value}-${String(month.value).padStart(2,'0')}-${String(d).padStart(2,'0')}`
    list.push({
      key: ymd,
      day: d,
      ymd,
      signed: signed.has(ymd),
      repairable: repairable.has(ymd),
      today: isCurMonth && d === todayD
    })
  }
  return list
})

async function load() {
  const res = await signInfo()
  info.value = res.data
}

async function onSign() {
  try {
    await doSign()
    showToast('签到成功')
    await userStore.fetchUserInfo()
    load()
  } catch (e) {}
}

async function onCellClick(cell) {
  if (!cell.day) return
  if (cell.signed) { showToast('已签到'); return }
  if (cell.today && !info.value.signedToday) { onSign(); return }
  if (cell.repairable && info.value.config?.repairEnabled === '1') {
    try {
      await showDialog({
        title: '补签',
        message: `补签 ${cell.ymd} 将扣除 ${info.value.config.repairCost} 积分，确认补签？`,
        showCancelButton: true
      })
      await repairSign(cell.ymd)
      showToast('补签成功')
      await userStore.fetchUserInfo()
      load()
    } catch (e) {}
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
.sign { background: #f7f7f7; min-height: 100vh; }
.head-card {
  margin: 12px;
  padding: 18px 20px;
  background: linear-gradient(135deg, #ff8c00, #ffb84d);
  border-radius: 14px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 14px rgba(255,140,0,0.3);
}
.head-card .title { font-size: 20px; font-weight: 700; }
.head-card .sub { font-size: 12px; opacity: 0.9; margin-top: 6px; }
.head-card .right { text-align: center; }
.head-card .days { font-size: 34px; font-weight: 700; line-height: 1; }
.head-card .label { font-size: 12px; opacity: 0.9; margin-top: 6px; }

.card { background: #fff; margin: 8px 12px; padding: 14px; border-radius: 12px; }
.progress-title { font-weight: 600; margin-bottom: 12px; }
.progress-list {
  display: flex; justify-content: space-between; align-items: center; gap: 6px;
  position: relative;
}
.progress-item { text-align: center; flex: 1; position: relative; }
.progress-item .dot {
  width: 30px; height: 30px; line-height: 30px;
  border-radius: 50%; margin: 0 auto;
  background: #eee; color: #888; font-weight: 600; font-size: 13px;
}
.progress-item.reached .dot { background: #ff8c00; color: #fff; }
.progress-item .reward { font-size: 11px; color: #ff8c00; margin-top: 4px; }

.sign-card { padding: 18px; }

.month-bar { display: flex; justify-content: space-between; margin-bottom: 10px; font-weight: 600; }
.month-stat { color: #ff8c00; font-size: 13px; font-weight: normal; }
.weekdays {
  display: grid; grid-template-columns: repeat(7, 1fr); text-align: center;
  color: #999; font-size: 12px; padding: 6px 0;
}
.days-grid {
  display: grid; grid-template-columns: repeat(7, 1fr); gap: 4px;
}
.day-cell {
  position: relative;
  aspect-ratio: 1;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px;
  font-size: 13px;
  color: #333;
  background: transparent;
}
.day-cell.signed { background: #fff5e6; color: #ff8c00; font-weight: 600; }
.day-cell.signed .check {
  position: absolute; bottom: 2px; right: 2px; font-size: 12px;
  color: #ff8c00;
}
.day-cell.repairable { border: 1px dashed #ff8c00; color: #ff8c00; }
.day-cell.today { background: #ff8c00 !important; color: #fff !important; }
.legend { margin-top: 10px; font-size: 12px; color: #999; display: flex; gap: 14px; }
.legend .dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; margin-right: 4px; vertical-align: middle; }
.legend .dot.signed { background: #ff8c00; }
.legend .dot.repair { background: #fff; border: 1px dashed #ff8c00; }
</style>
