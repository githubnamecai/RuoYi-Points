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
      <div class="coupon-item" :class="[`status-${activeTab}`, { 'is-disabled': activeTab !== '0' }]" v-for="item in list" :key="item.userCouponId">
        <div class="left">
          <div class="status-chip">{{ activeTab === '0' ? '可使用' : activeTab === '1' ? '已使用' : '已过期' }}</div>
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
    position: relative;
    
    .info {
      flex: 1;
      .title { font-size: 16px; font-weight: 700; color: #1a2640; margin-bottom: 6px; }
      .desc { font-size: 12px; color: #0d5bd7; margin-bottom: 8px; }
      .time { font-size: 11px; color: #8a95a9; }
    }
    
    .status-stamp {
      position: absolute;
      right: 12px;
      top: 12px;
      width: 56px;
      height: 56px;
      border-radius: 50%;
      border: 2px solid rgba(166, 175, 191, 0.75);
      color: rgba(166, 175, 191, 0.95);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 700;
      transform: rotate(-15deg);
    }
  }
  
  &.is-disabled {
    .left {
      background: linear-gradient(160deg, #a1abc0, #bac4d7);
    }
    .right .info .title { color: #8f98ab; }
    .right .info .desc { color: #9ea7b8; }
  }

  &.status-1 .left {
    background: linear-gradient(160deg, #7a89a8, #a3b0c6);
  }

  &.status-2 .left {
    background: linear-gradient(160deg, #bc8d8d, #d7b7b7);
  }

  &.status-2 .right .status-stamp {
    border-color: rgba(211, 141, 141, 0.74);
    color: rgba(211, 141, 141, 0.9);
  }
}

.page-container :deep(.van-tabs__wrap) {
  background: rgba(245, 249, 255, 0.86);
  backdrop-filter: blur(12px);
}
</style>
