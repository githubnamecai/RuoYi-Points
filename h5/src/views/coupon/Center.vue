<template>
  <div class="page-container">
    <van-nav-bar title="领券中心" left-arrow @click-left="$router.back()" fixed placeholder />
    
    <van-empty v-if="!loading && list.length === 0" description="暂无优惠券可领取" />
    
    <div class="coupon-list" v-else>
      <div class="coupon-item" v-for="item in list" :key="item.couponId">
        <div class="left">
          <div class="status-chip">可领取</div>
          <div class="amount" v-if="item.couponType === '1'">
            <span class="num">{{ item.discountValue }}</span><span class="unit">%折</span>
          </div>
          <div class="amount" v-else>
            <span class="unit">￥</span><span class="num">{{ item.discountValue }}</span>
          </div>
          <div class="condition">{{ item.minAmount > 0 ? `满${item.minAmount}积分可用` : '无门槛' }}</div>
        </div>
        <div class="right">
          <div class="info">
            <div class="title">{{ item.couponName }}</div>
            <div class="desc">{{ getUseTypeDesc(item.useType) }}</div>
            <div class="time" v-if="item.timeType === '0'">{{ formatDate(item.endTime) }} 前有效</div>
            <div class="time" v-else>领取后 {{ item.validDays }} 天有效</div>
          </div>
          <van-button size="small" type="primary" round class="receive-btn" @click="doReceive(item.couponId)">立即领取</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listCoupons, receiveCoupon } from '@/api/coupon'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const list = ref([])
const loading = ref(true)

async function fetchCoupons() {
  try {
    loading.value = true
    const res = await listCoupons()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function doReceive(id) {
  try {
    await receiveCoupon(id)
    showToast({
    message: '领取成功',
    className: 'my-toast'
})
  } catch (e) {
    // 错误在 request 拦截器中处理
  }
}

function getUseTypeDesc(type) {
  if (type === '0') return '全场通用'
  if (type === '1') return '指定分类可用'
  return '指定商品可用'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD')
}

onMounted(() => {
  fetchCoupons()
})
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100vh;
  background: transparent;
}

.coupon-list {
  padding: 12px;
}

.coupon-item {
  display: flex;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.8));
  border-radius: 22px;
  overflow: hidden;
  margin-bottom: 14px;
  box-shadow: 0 16px 34px rgba(22, 53, 110, 0.08);
  border: 1px solid rgba(124, 147, 187, 0.12);
  position: relative;
  
  &::before, &::after {
    content: '';
    position: absolute;
    width: 14px;
    height: 14px;
    background: #eef3fb;
    border-radius: 50%;
    left: 94px;
    z-index: 2;
  }
  &::before { top: -6px; }
  &::after { bottom: -6px; }

  .left {
    width: 100px;
    background: linear-gradient(160deg, #0d5bd7 0%, #2c8dff 62%, #69beff 100%);
    color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 14px 0;

    .status-chip {
      margin-bottom: 10px;
      padding: 4px 10px;
      border-radius: 999px;
      font-size: 11px;
      font-weight: 600;
      background: rgba(255, 255, 255, 0.16);
      color: rgba(255, 255, 255, 0.9);
      border: 1px solid rgba(255, 255, 255, 0.16);
    }
    
    .amount {
      display: flex;
      align-items: baseline;
      .unit { font-size: 14px; }
      .num { font-size: 30px; font-weight: 700; }
    }
    .condition {
      font-size: 12px;
      margin-top: 6px;
      opacity: 0.9;
      text-align: center;
      padding: 0 8px;
    }
  }
  
  .right {
    flex: 1;
    padding: 14px 14px 14px 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-left: 1px dashed rgba(124, 147, 187, 0.2);
    
    .info {
      flex: 1;
      .title { font-size: 16px; font-weight: 700; color: #1a2640; margin-bottom: 6px; }
      .desc { font-size: 12px; color: #0d5bd7; margin-bottom: 8px; }
      .time { font-size: 11px; color: #8a95a9; }
    }
    
    .receive-btn {
      width: 82px;
      height: 32px;
      background: linear-gradient(135deg, #123f96, #1f73ef 70%, #58b9ff);
      border: none;
      box-shadow: 0 10px 18px rgba(13, 91, 215, 0.18);
    }
  }
}

.page-container :deep(.van-empty) {
  padding-top: 110px;
}
</style>
