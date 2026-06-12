<template>
  <div class="order-detail">
    <van-nav-bar title="订单详情" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="order">
      <div class="status-card">
        <div class="status-text">{{ statusText(order.status) }}</div>
        <div class="status-sub">{{ statusSub(order) }}</div>
      </div>

      <div v-if="order.address" class="card">
        <div class="addr-line"><b>{{ order.consignee }}</b> {{ maskPhone(order.phone) }}</div>
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
import { maskPhone } from '@/utils/mask'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const baseApi = import.meta.env.VITE_APP_BASE_API
const defaultImg = 'https://via.placeholder.com/300x300?text=Goods'

/**
 * 格式化订单详情商品图片地址。
 */
function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

/**
 * 获取订单主状态文案。
 */
function statusText(s) { return { '0':'待发货','1':'待收货','2':'已完成','3':'已关闭' }[s] || s }

/**
 * 获取订单状态补充说明。
 */
function statusSub(o) {
  return { '0':'商家正在备货', '1':'商品已发出，请耐心等待', '2':'交易已完成，感谢使用', '3':o.closeReason || '订单已关闭' }[o.status]
}

/**
 * 加载订单详情数据。
 */
async function load() {
  const res = await orderDetail(route.params.id)
  order.value = res.data
}

/**
 * 确认收货并刷新详情。
 */
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
.order-detail {
  padding-top: 46px;
  padding-bottom: 92px;
  background: transparent;
  min-height: 100vh;
}

.status-card {
  background: linear-gradient(135deg, #0c4ead 0%, #1765e3 52%, #57b9ff 100%);
  color: #fff;
  padding: 22px 20px;
  margin: 12px;
  border-radius: 24px;
  box-shadow: 0 20px 38px rgba(13, 91, 215, 0.2);
  position: relative;
  overflow: hidden;
}

.status-card::before,
.status-card::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
}

.status-card::before {
  width: 140px;
  height: 140px;
  top: -50px;
  right: -34px;
}

.status-card::after {
  width: 92px;
  height: 92px;
  bottom: -28px;
  left: -16px;
}

.status-text { font-size: 22px; font-weight: 700; position: relative; z-index: 1; }
.status-sub { font-size: 13px; opacity: 0.9; margin-top: 6px; position: relative; z-index: 1; }

.card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(249, 251, 255, 0.82));
  margin: 10px 12px;
  border-radius: 22px;
  padding: 14px 16px;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 16px 30px rgba(22, 53, 110, 0.08);
}

.addr-line { line-height: 1.7; font-size: 14px; color: #1f2c45; }
.goods-card { display: flex; gap: 10px; align-items: center; }
.goods-card img {
  width: 68px;
  height: 68px;
  object-fit: cover;
  border-radius: 16px;
  background: linear-gradient(180deg, #f6f9ff, #edf4ff);
}
.goods-card .info { flex: 1; }
.goods-card .name { font-size: 15px; color: #1a2640; font-weight: 700; line-height: 1.45; }
.goods-card .qty { font-size: 12px; color: #7f8ba0; margin-top: 6px; }
.goods-card .points {
  color: #0d5bd7;
  font-weight: 700;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(13, 91, 215, 0.08);
}
.info-list .row {
  display: flex; justify-content: space-between;
  padding: 8px 0; font-size: 13px; color: #516078;
}
.info-list .row span:first-child { color: #8a95a9; }
.info-list .row span:last-child {
  max-width: 56%;
  text-align: right;
  word-break: break-all;
}

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: rgba(245, 249, 255, 0.92);
  padding: 10px 16px calc(10px + env(safe-area-inset-bottom, 0px));
  box-shadow: 0 -10px 24px rgba(22, 53, 110, 0.08);
  backdrop-filter: blur(16px);
}
</style>
