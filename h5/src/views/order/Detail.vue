<template>
  <div class="order-detail">
    <van-nav-bar title="订单详情" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="order">
      <div class="status-card">
        <div class="status-text">{{ statusText(order.status) }}</div>
        <div class="status-sub">{{ statusSub(order) }}</div>
      </div>

      <div v-if="order.address" class="card">
        <div class="addr-line"><b>{{ order.consignee }}</b> {{ order.phone }}</div>
        <div class="addr-line">{{ order.address }}</div>
      </div>

      <div class="card goods-card">
        <!-- <img :src="order.goodsCover" /> -->
        <img :src="formatImg(order.goodsCover)" />
        <div class="info">
          <div class="name">{{ order.goodsName }}</div>
          <div class="qty">数量: {{ order.quantity }}</div>
        </div>
        <div class="points">-{{ order.pointsUsed }}</div>
      </div>

      <div class="card info-list">
        <div class="row"><span>订单编号</span><span>{{ order.orderNo }}</span></div>
        <div class="row"><span>下单时间</span><span>{{ order.createTime }}</span></div>
        <div v-if="order.payAmount != null" class="row"><span>支付金额</span><span style="color:#e53935;font-weight:600">¥{{ order.payAmount }}</span></div>
        <div v-if="order.expressCompany" class="row"><span>物流公司</span><span>{{ order.expressCompany }}</span></div>
        <div v-if="order.expressNo" class="row"><span>物流单号</span><span>{{ order.expressNo }}</span></div>
        <div v-if="order.shipTime" class="row"><span>发货时间</span><span>{{ order.shipTime }}</span></div>
        <div v-if="order.finishTime" class="row"><span>完成时间</span><span>{{ order.finishTime }}</span></div>
        <div v-if="order.closeReason" class="row"><span>关闭原因</span><span>{{ order.closeReason }}</span></div>
      </div>
    </div>

    <div class="bottom-bar" v-if="order && order.status === '1'">
      <van-button block round type="primary" @click="confirm">确认收货</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { orderDetail, confirmReceipt } from '@/api/user'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const baseApi = import.meta.env.VITE_APP_BASE_API

function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

function statusText(s) { return { '0':'待发货','1':'待收货','2':'已完成','3':'已关闭' }[s] || s }
function statusSub(o) {
  return { '0':'商家正在备货', '1':'商品已发出，请耐心等待', '2':'交易已完成，感谢使用', '3':o.closeReason || '订单已关闭' }[o.status]
}

async function load() {
  const res = await orderDetail(route.params.id)
  order.value = res.data
}
async function confirm() {
  try {
    await showDialog({ title: '确认收货？', showCancelButton: true })
    await confirmReceipt(order.value.orderId)
    showToast({
    message: '已确认收货',
    className: 'my-toast'
})
    load()
  } catch (e) {}
}

onMounted(load)
</script>

<style scoped lang="scss">
.order-detail { padding-top: 46px; padding-bottom: 80px; background: #f7f7f7; min-height: 100vh; }
.status-card {
  background: linear-gradient(135deg, #ff8c00, #ffb84d);
  color: #fff;
  padding: 20px;
  margin: 12px;
  border-radius: 12px;
}
.status-text { font-size: 20px; font-weight: 700; }
.status-sub { font-size: 13px; opacity: 0.9; margin-top: 4px; }
.card { background: #fff; margin: 8px 12px; border-radius: 12px; padding: 12px 14px; }
.addr-line { line-height: 1.6; font-size: 14px; }
.goods-card { display: flex; gap: 10px; align-items: center; }
.goods-card img { width: 64px; height: 64px; object-fit: cover; border-radius: 6px; background: #f5f5f5; }
.goods-card .info { flex: 1; }
.goods-card .name { font-size: 14px; color: #333; }
.goods-card .qty { font-size: 12px; color: #999; margin-top: 4px; }
.goods-card .points { color: #ff8c00; font-weight: 600; }
.info-list .row {
  display: flex; justify-content: space-between;
  padding: 6px 0; font-size: 13px; color: #555;
}
.info-list .row span:first-child { color: #999; }
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; padding: 10px 16px;
  box-shadow: 0 -1px 4px rgba(0,0,0,0.05);
}
</style>
