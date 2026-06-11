<template>
  <div class="points-detail">
    <van-nav-bar title="积分明细" left-arrow @click-left="$router.back()" fixed />
    <div class="content">
      <div class="balance-card">
        <div class="label">我的积分</div>
        <div class="num">{{ userStore.points }}</div>
      </div>

      <van-list v-model:loading="loading" :finished="finished" @load="loadMore" :immediate-check="false" finished-text="到底啦">
        <div v-for="(group, key) in groups" :key="key" class="month-group">
          <div class="month-title">{{ key }}</div>
          <div v-for="item in group" :key="item.detailId" class="item">
            <div class="left">
              <div class="title">{{ sourceLabel(item.sourceType) }}</div>
              <div class="desc">{{ item.description || '-' }}</div>
              <div class="time">{{ item.createTime }}</div>
            </div>
            <div class="right">
              <div :class="['change', item.changeType === '0' ? 'add' : 'sub']">
                {{ item.changeType === '0' ? '+' : '-' }}{{ item.changePoints }}
              </div>
              <div class="balance">余额 {{ item.balance }}</div>
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { listPointsDetail } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const list = ref([])
const pageNum = ref(1)
const pageSize = 20
const loading = ref(false)
const finished = ref(false)

const SOURCE_MAP = {
  SIGN_IN: '每日签到',
  EXCHANGE: '兑换商品',
  REFUND: '订单退还',
  ADMIN_ADJUST: '后台调整',
  CONTINUOUS_BONUS: '连续签到奖励',
  SIGN_REPAIR: '补签消耗',
  INVITE: '邀请奖励'
}
function sourceLabel(t) { return SOURCE_MAP[t] || t }

const groups = computed(() => {
  const obj = {}
  for (const item of list.value) {
    const key = (item.createTime || '').substring(0, 7) // YYYY-MM
    if (!obj[key]) obj[key] = []
    obj[key].push(item)
  }
  return obj
})

async function loadMore() {
  try {
    const res = await listPointsDetail({ pageNum: pageNum.value, pageSize })
    list.value.push(...(res.rows || []))
    finished.value = list.value.length >= res.total
    pageNum.value++
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  userStore.fetchUserInfo()
  loading.value = true
  loadMore()
})
</script>

<style scoped lang="scss">
.points-detail { padding-top: 46px; background: #f7f7f7; min-height: 100vh; }
.content { padding: 12px; }
.balance-card {
  background: linear-gradient(135deg, #0c4ead 0%, #1765e3 52%, #57b9ff 100%);
  border-radius: 24px;
  padding: 20px;
  color: #fff;
  margin-bottom: 14px;
  box-shadow: 0 20px 38px rgba(13, 91, 215, 0.2);
  position: relative;
  overflow: hidden;
}

.balance-card::before,
.balance-card::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
}

.balance-card::before {
  width: 150px;
  height: 150px;
  top: -58px;
  right: -44px;
}

.balance-card::after {
  width: 92px;
  height: 92px;
  bottom: -28px;
  left: -22px;
}

.balance-card .label { font-size: 13px; opacity: 0.9; }

.balance-card .num {
  font-size: 34px;
  font-weight: 700;
  margin-top: 8px;
  position: relative;
  z-index: 1;
}

.month-title {
  font-size: 12px;
  color: #7b89a3;
  padding: 10px 6px 8px;
  font-weight: 700;
}

.item {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.8));
  border-radius: 18px;
  padding: 14px 16px;
  margin-bottom: 10px;
  display: flex; justify-content: space-between; align-items: center;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 12px 22px rgba(22, 53, 110, 0.06);
}

.item .title { font-size: 15px; font-weight: 700; color: #1a2640; }
.item .desc { font-size: 12px; color: #69768d; margin-top: 4px; }
.item .time { font-size: 11px; color: #9ca5b6; margin-top: 5px; }
.item .right { text-align: right; }

.change {
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 16px;
}

.change.add {
  color: #25b07d;
  background: rgba(37, 176, 125, 0.12);
}

.change.sub {
  color: #e66b7d;
  background: rgba(230, 107, 125, 0.1);
}

.balance { font-size: 11px; color: #9ca5b6; margin-top: 6px; }
</style>
