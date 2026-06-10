<template>
  <div class="page-container">
    <van-nav-bar title="领券中心" left-arrow @click-left="$router.back()" fixed placeholder />
    
    <van-empty v-if="!loading && list.length === 0" description="暂无优惠券可领取" />
    
    <div class="coupon-list" v-else>
      <div class="coupon-item" v-for="item in list" :key="item.couponId">
        <div class="left">
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
  background: #f7f7f7;
}
.coupon-list {
  padding: 12px;
}
.coupon-item {
  display: flex;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: relative;
  
  &::before, &::after {
    content: '';
    position: absolute;
    width: 12px;
    height: 12px;
    background: #f7f7f7;
    border-radius: 50%;
    left: 94px;
    z-index: 2;
  }
  &::before { top: -6px; }
  &::after { bottom: -6px; }

  .left {
    width: 100px;
    background: linear-gradient(135deg, #ff8c00, #ffb84d);
    color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 12px 0;
    
    .amount {
      display: flex;
      align-items: baseline;
      .unit { font-size: 14px; }
      .num { font-size: 28px; font-weight: bold; }
    }
    .condition {
      font-size: 12px;
      margin-top: 4px;
      opacity: 0.9;
    }
  }
  
  .right {
    flex: 1;
    padding: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-left: 1px dashed #eee;
    
    .info {
      flex: 1;
      .title { font-size: 15px; font-weight: bold; color: #333; margin-bottom: 4px; }
      .desc { font-size: 12px; color: #ff8c00; margin-bottom: 6px; }
      .time { font-size: 11px; color: #999; }
    }
    
    .receive-btn {
      width: 72px;
      height: 28px;
      background: linear-gradient(90deg, #ff8c00, #ff5252);
      border: none;
    }
  }
}
</style>