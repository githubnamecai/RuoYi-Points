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
    showToast({
    message: '签到成功',
    className: 'my-toast'
})
    await userStore.fetchUserInfo()
    load()
  } catch (e) {}
}

async function onCellClick(cell) {
  if (!cell.day) return
  if (cell.signed) { 
  showToast({
  message: '已签到',
  className: 'my-toast'
});
  return }
  if (cell.today && !info.value.signedToday) { onSign(); return }
  if (cell.repairable && info.value.config?.repairEnabled === '1') {
    try {
      await showDialog({
        title: '补签',
        message: `补签 ${cell.ymd} 将扣除 ${info.value.config.repairCost} 积分，确认补签？`,
        showCancelButton: true
      })
      await repairSign(cell.ymd)
      showToast({
      message: '补签成功',
      className: 'my-toast'
});
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
  padding: 20px 20px 18px;
  background: linear-gradient(135deg, #0c4ead 0%, #1f72ed 55%, #66bcff 100%);
  border-radius: 24px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 20px 38px rgba(13, 91, 215, 0.2);
  position: relative;
  overflow: hidden;
}

.head-card::before,
.head-card::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
}

.head-card::before {
  width: 130px;
  height: 130px;
  top: -48px;
  right: -30px;
}

.head-card::after {
  width: 88px;
  height: 88px;
  bottom: -20px;
  left: -18px;
}

.head-card .left,
.head-card .right {
  position: relative;
  z-index: 1;
}

.head-card .title { font-size: 22px; font-weight: 700; }
.head-card .sub { font-size: 12px; opacity: 0.88; margin-top: 8px; }
.head-card .right {
  min-width: 88px;
  text-align: center;
  padding: 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(10px);
}

.head-card .days { font-size: 36px; font-weight: 700; line-height: 1; }
.head-card .label { font-size: 12px; opacity: 0.9; margin-top: 6px; }

.card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(249, 251, 255, 0.76));
  margin: 10px 12px;
  padding: 16px;
  border-radius: 22px;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 14px 28px rgba(22, 53, 110, 0.08);
}

.progress-title {
  font-weight: 700;
  margin-bottom: 14px;
  color: #1a2640;
}

.progress-list {
  display: flex; justify-content: space-between; align-items: center; gap: 6px;
  position: relative;
}

.progress-list::before {
  content: "";
  position: absolute;
  left: 18px;
  right: 18px;
  top: 15px;
  height: 2px;
  background: linear-gradient(90deg, rgba(13, 91, 215, 0.14), rgba(13, 91, 215, 0.22));
}

.progress-item { text-align: center; flex: 1; position: relative; z-index: 1; }

.progress-item .dot {
  width: 30px; height: 30px; line-height: 30px;
  border-radius: 50%; margin: 0 auto;
  background: #ecf2ff;
  color: #6f7f9d;
  font-weight: 700;
  font-size: 13px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
}
.progress-item.reached .dot {
  background: linear-gradient(135deg, #0f5cd6, #62baff);
  color: #fff;
  box-shadow: 0 8px 16px rgba(13, 91, 215, 0.22);
}

.progress-item .reward {
  font-size: 11px;
  color: #0d5bd7;
  margin-top: 6px;
}

.sign-card { padding: 18px; }

.sign-card :deep(.van-button) {
  height: 46px;
}

.month-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 14px;
  font-weight: 700;
  color: #1a2640;
}

.month-stat { color: #0d5bd7; font-size: 13px; font-weight: 600; }

.weekdays {
  display: grid; grid-template-columns: repeat(7, 1fr); text-align: center;
  color: #8691a7; font-size: 12px; padding: 8px 0 10px;
}

.days-grid {
  display: grid; grid-template-columns: repeat(7, 1fr); gap: 8px;
}

.day-cell {
  position: relative;
  aspect-ratio: 1;
  display: flex; align-items: center; justify-content: center;
  border-radius: 16px;
  font-size: 13px;
  color: #2f3f5d;
  background: linear-gradient(180deg, #f8fbff, #edf4ff);
  border: 1px solid rgba(125, 145, 182, 0.14);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.day-cell.signed {
  background: linear-gradient(180deg, rgba(13, 91, 215, 0.14), rgba(13, 91, 215, 0.08));
  color: #0d5bd7;
  font-weight: 700;
  border-color: rgba(13, 91, 215, 0.18);
}

.day-cell.signed .check {
  position: absolute;
  bottom: 4px;
  right: 4px;
  font-size: 12px;
  color: #0d5bd7;
}

.day-cell.repairable {
  border: 1px dashed rgba(240, 166, 58, 0.82);
  color: #c07a19;
  background: linear-gradient(180deg, rgba(240, 166, 58, 0.08), rgba(240, 166, 58, 0.02));
}

.day-cell.today {
  background: linear-gradient(135deg, #0d5bd7, #55b6ff) !important;
  color: #fff !important;
  box-shadow: 0 10px 18px rgba(13, 91, 215, 0.22);
}

.legend {
  margin-top: 14px;
  font-size: 12px;
  color: #8691a7;
  display: flex;
  gap: 14px;
}

.legend .dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; margin-right: 4px; vertical-align: middle; }
.legend .dot.signed { background: #0d5bd7; }
.legend .dot.repair { background: #fff; border: 1px dashed #f0a63a; }
</style>
