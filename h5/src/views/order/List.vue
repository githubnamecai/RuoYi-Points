<template>
  <div class="order-list page-content">
    <van-tabs v-model:active="activeTab" sticky offset-top="0"
              title-active-color="#ff8c00" color="#ff8c00" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="待发货" name="0" />
      <van-tab title="已发货" name="1" />
      <van-tab title="已完成" name="2" />
      <van-tab title="已关闭" name="3" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="loadMore" finished-text="没有更多了">
        <div v-if="list.length === 0 && !loading" class="empty">
          <van-empty description="暂无订单" />
        </div>
        <div v-for="o in list" :key="o.orderId" class="order-card" @click="$router.push('/orders/' + o.orderId)">
          <div class="order-no">
            <span>订单号: {{ o.orderNo }}</span>
            <span class="status" :class="'s' + o.status">{{ statusText(o.status) }}</span>
          </div>
          <div class="goods">
            <!-- <img :src="o.goodsCover" /> -->
            <img :src="formatImg(o.goodsCover)" />
            <div class="info">
              <div class="name">{{ o.goodsName }}</div>
              <div class="meta">数量: {{ o.quantity }} · {{ o.goodsType === '1' ? '虚拟' : '实物' }}</div>
            </div>
            <div class="points">-{{ o.pointsUsed }}积分</div>
          </div>
          <div v-if="o.payAmount != null" class="pay-amount">支付金额: <span>¥{{ o.payAmount }}</span></div>
          <div class="actions" v-if="o.status === '1'">
            <van-button size="small" type="primary" plain @click.stop="confirm(o)">确认收货</van-button>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref,onMounted  } from 'vue'
import { showDialog, showToast } from 'vant'
import { listOrders, confirmReceipt } from '@/api/user'

const list = ref([])
const activeTab = ref('')
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const baseApi = import.meta.env.VITE_APP_BASE_API
// 2. 页面初始化自动调用 ✅
onMounted(() => {
  onTabChange() // 直接在这里调用
})
function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

function statusText(s) {
  return { '0':'待发货','1':'已发货','2':'已完成','3':'已关闭' }[s] || s
}

function reset() {
  list.value = []
  pageNum.value = 1
  finished.value = false
}

async function loadMore() {
  try {
    const res = await listOrders({
      pageNum: pageNum.value, pageSize,
      status: activeTab.value || undefined
    })
    list.value.push(...(res.rows || []))
    finished.value = list.value.length >= res.total
    pageNum.value++
  } finally { loading.value = false }
}

function onTabChange() { 
  reset(); 
  loading.value = true;
  loadMore(); 
}
function onRefresh() {
  reset()
  loading.value = true
  loadMore().finally(() => refreshing.value = false)
}

async function confirm(o) {
  try {
    await showDialog({ title: '确认收货？', message: '确认已收到商品？', showCancelButton: true })
    await confirmReceipt(o.orderId)
    showToast({
    message: '已确认收货',
    className: 'my-toast'
})
    onRefresh()
  } catch (e) {}
}
</script>

<style scoped lang="scss">
.order-list { background: #f7f7f7; min-height: 100vh; }
.empty { padding-top: 60px; }
.order-card {
  background: #fff;
  margin: 8px 12px;
  border-radius: 12px;
  padding: 12px 14px;
}
.order-no {
  display: flex; justify-content: space-between;
  font-size: 12px; color: #888;
  padding-bottom: 8px; border-bottom: 1px dashed #eee;
}
.order-no .status { color: #ff8c00; font-weight: 600; }
.order-no .status.s2 { color: #07c160; }
.order-no .status.s3 { color: #999; }

.goods { display: flex; gap: 10px; padding: 10px 0; align-items: center; }
.goods img { width: 64px; height: 64px; border-radius: 6px; object-fit: cover; background: #f5f5f5; }
.goods .info { flex: 1; }
.goods .name { font-size: 14px; color: #333; line-height: 1.3; }
.goods .meta { font-size: 12px; color: #999; margin-top: 4px; }
.goods .points { color: #ff8c00; font-weight: 600; }
.pay-amount { font-size: 12px; color: #999; text-align: right; padding-top: 4px; }
.pay-amount span { color: #e53935; font-weight: 600; }

.actions { text-align: right; padding-top: 4px; border-top: 1px dashed #eee; }
</style>
