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
  background: linear-gradient(135deg, #ff8c00, #ffb84d);
  border-radius: 14px;
  padding: 18px 20px;
  color: #fff;
  margin-bottom: 12px;
}
.balance-card .label { font-size: 13px; opacity: 0.9; }
.balance-card .num { font-size: 30px; font-weight: 700; margin-top: 4px; }
.month-title {
  font-size: 12px; color: #999; padding: 8px 4px 6px; font-weight: 600;
}
.item {
  background: #fff; border-radius: 10px;
  padding: 12px 14px; margin-bottom: 8px;
  display: flex; justify-content: space-between; align-items: center;
}
.item .title { font-size: 14px; font-weight: 600; color: #333; }
.item .desc { font-size: 12px; color: #888; margin-top: 3px; }
.item .time { font-size: 11px; color: #bbb; margin-top: 3px; }
.item .right { text-align: right; }
.change.add { color: #ff8c00; font-weight: 600; font-size: 17px; }
.change.sub { color: #999; font-weight: 600; font-size: 17px; }
.balance { font-size: 11px; color: #bbb; margin-top: 3px; }
</style>
