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
const defaultImg = 'https://via.placeholder.com/300x300?text=Goods'

/**
 * 页面初始化时加载订单列表。
 */
onMounted(() => {
  onTabChange()
})

/**
 * 格式化订单商品图片地址。
 */
function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

/**
 * 获取订单状态文案。
 */
function statusText(s) {
  return { '0':'待发货','1':'已发货','2':'已完成','3':'已关闭' }[s] || s
}

/**
 * 重置列表加载状态。
 */
function reset() {
  list.value = []
  pageNum.value = 1
  finished.value = false
}

/**
 * 加载订单分页数据。
 */
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

/**
 * 切换订单状态标签后重新加载数据。
 */
function onTabChange() { 
  reset(); 
  loading.value = true;
  loadMore(); 
}

/**
 * 下拉刷新订单列表。
 */
function onRefresh() {
  reset()
  loading.value = true
  loadMore().finally(() => refreshing.value = false)
}

/**
 * 确认收货并刷新订单状态。
 */
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
.order-list {
  background: transparent;
  min-height: 100vh;
}

.order-list :deep(.van-tabs__wrap) {
  background: rgba(245, 249, 255, 0.88);
  backdrop-filter: blur(14px);
}

.empty { padding-top: 60px; }

.order-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(249, 251, 255, 0.82));
  margin: 10px 12px;
  border-radius: 22px;
  padding: 14px 16px;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 16px 30px rgba(22, 53, 110, 0.08);
}

.order-no {
  display: flex; justify-content: space-between;
  font-size: 12px; color: #7f8ba0;
  padding-bottom: 10px; border-bottom: 1px dashed rgba(124, 147, 187, 0.2);
}

.order-no .status {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(13, 91, 215, 0.08);
  color: #0d5bd7;
  font-weight: 700;
}

.order-no .status.s2 {
  background: rgba(37, 176, 125, 0.12);
  color: #25b07d;
}

.order-no .status.s3 {
  background: rgba(166, 175, 191, 0.16);
  color: #8f98ab;
}

.goods { display: flex; gap: 10px; padding: 10px 0; align-items: center; }
.goods img {
  width: 68px;
  height: 68px;
  border-radius: 16px;
  object-fit: cover;
  background: linear-gradient(180deg, #f6f9ff, #edf4ff);
}
.goods .info { flex: 1; }
.goods .name { font-size: 14px; color: #1a2640; line-height: 1.45; font-weight: 700; }
.goods .meta { font-size: 12px; color: #7f8ba0; margin-top: 6px; }
.goods .points {
  color: #0d5bd7;
  font-weight: 700;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(13, 91, 215, 0.08);
}
.pay-amount { font-size: 12px; color: #7f8ba0; text-align: right; padding-top: 6px; }
.pay-amount span { color: #d85267; font-weight: 700; }

.actions {
  text-align: right;
  padding-top: 10px;
  border-top: 1px dashed rgba(124, 147, 187, 0.2);
}

.actions :deep(.van-button) {
  min-width: 96px;
}
</style>
