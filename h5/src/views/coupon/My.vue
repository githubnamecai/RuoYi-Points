<template>
  <div class="page-container">
    <van-nav-bar title="我的优惠券" left-arrow @click-left="$router.back()" fixed placeholder />
    
    <van-tabs v-model:active="activeTab" sticky offset-top="46" color="#ff8c00" @change="onTabChange">
      <van-tab title="未使用" name="0"></van-tab>
      <van-tab title="已使用" name="1"></van-tab>
      <van-tab title="已过期" name="2"></van-tab>
    </van-tabs>

    <van-empty v-if="!loading && list.length === 0" description="暂无优惠券" />
    
    <div class="coupon-list" v-else>
      <div class="coupon-item" :class="{ 'is-disabled': activeTab !== '0' }" v-for="item in list" :key="item.userCouponId">
        <div class="left">
          <div class="amount" v-if="item.coupon.couponType === '1'">
            <span class="num">{{ item.coupon.discountValue }}</span><span class="unit">%折</span>
          </div>
          <div class="amount" v-else>
            <span class="unit">￥</span><span class="num">{{ item.coupon.discountValue }}</span>
          </div>
          <div class="condition">{{ item.coupon.minAmount > 0 ? `满${item.coupon.minAmount}积分可用` : '无门槛' }}</div>
        </div>
        <div class="right">
          <div class="info">
            <div class="title">{{ item.coupon.couponName }}</div>
            <div class="desc">{{ getUseTypeDesc(item.coupon.useType) }}</div>
            <div class="time">{{ formatDate(item.startTime) }} - {{ formatDate(item.endTime) }}</div>
          </div>
          <div class="status-stamp" v-if="activeTab === '1'">已使用</div>
          <div class="status-stamp" v-if="activeTab === '2'">已过期</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyCoupons } from '@/api/coupon'
import dayjs from 'dayjs'

const activeTab = ref('0')
const list = ref([])
const loading = ref(true)

async function fetchCoupons() {
  try {
    loading.value = true
    const res = await getMyCoupons(activeTab.value)
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function onTabChange() {
  list.value = []
  fetchCoupons()
}

function getUseTypeDesc(type) {
  if (type === '0') return '全场通用'
  if (type === '1') return '指定分类可用'
  return '指定商品可用'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY.MM.DD')
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
    position: relative;
    
    .info {
      flex: 1;
      .title { font-size: 15px; font-weight: bold; color: #333; margin-bottom: 4px; }
      .desc { font-size: 12px; color: #ff8c00; margin-bottom: 6px; }
      .time { font-size: 11px; color: #999; }
    }
    
    .status-stamp {
      position: absolute;
      right: 12px;
      top: 12px;
      width: 50px;
      height: 50px;
      border-radius: 50%;
      border: 2px solid #ccc;
      color: #ccc;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: bold;
      transform: rotate(-15deg);
    }
  }
  
  &.is-disabled {
    .left {
      background: #ccc;
    }
    .right .info .title { color: #999; }
    .right .info .desc { color: #999; }
  }
}
</style>