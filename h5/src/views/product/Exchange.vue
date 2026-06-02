<template>
  <div class="exchange">
    <van-nav-bar title="兑换确认" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="goods">
      <!-- 实物商品：地址 -->
      <div v-if="goods.goodsType === '0'" class="card address-card" @click="goAddress">
        <template v-if="address">
          <div class="addr-top">
            <span class="consignee">{{ address.consignee }}</span>
            <span class="phone">{{ address.phone }}</span>
          </div>
          <div class="addr-detail">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
          </div>
          <van-icon name="arrow" class="arrow" />
        </template>
        <template v-else>
          <div class="empty">+ 选择收货地址</div>
        </template>
      </div>

      <!-- 商品卡 -->
      <div class="card goods-card">
        <img :src="formatImg(goods.coverImg)" class="cover"/>
        <div class="info">
          <div class="name">{{ goods.goodsName }}</div>
          <div class="price"><span>{{ goods.points }}</span> 积分 × {{ qty }}</div>
        </div>
        <van-stepper v-model="qty" min="1" :max="Math.min(goods.stock, 99)" integer />
      </div>

      <!-- 备注 -->
      <div class="card">
        <van-field v-model="remark" label="备注" placeholder="选填" />
      </div>
      
      <!-- 优惠券选择 -->
      <div class="card" @click="showCouponPopup = true" style="display:flex; justify-content:space-between; align-items:center;">
        <span style="font-size:14px; color:#333;">优惠券</span>
        <div style="display:flex; align-items:center; color:#999; font-size:13px;">
          <span v-if="selectedCoupon" style="color:#ff8c00;">-{{ getCouponDiscountText(selectedCoupon.coupon) }}</span>
          <span v-else-if="availableCoupons.length > 0" style="color:#ff8c00;">{{ availableCoupons.length }}张可用</span>
          <span v-else>暂无可用</span>
          <van-icon name="arrow" style="margin-left:4px;" />
        </div>
      </div>

      <!-- 合计 -->
      <div class="card total">
        <div class="row"><span>当前余额</span><b class="points">{{ userStore.points }}</b></div>
        <div class="row"><span>合计扣减</span><b class="points">-{{ totalPoints }}</b></div>
        <div class="row"><span>兑换后剩余</span><b class="points">{{ userStore.points - totalPoints }}</b></div>
      </div>
    </div>

    <van-submit-bar
      :price="totalPoints * 100"
      button-text="确认兑换"
      button-color="#ff8c00"
      currency=""
      label="合计:"
      suffix-label="积分"
      :loading="loading"
      :disabled="!canSubmit"
      @submit="submit"
    >
      <template #price>
        <span class="bar-price">{{ totalPoints }}</span>
      </template>
    </van-submit-bar>
    
    <!-- 优惠券弹窗 -->
    <van-popup v-model:show="showCouponPopup" position="bottom" round style="height: 60%; background: #f7f7f7;">
      <div style="padding: 16px; text-align: center; font-weight: bold; font-size: 16px;">选择优惠券</div>
      <div style="padding: 0 12px; height: calc(100% - 100px); overflow-y: auto;">
        <div 
          v-for="item in availableCoupons" 
          :key="item.userCouponId" 
          @click="selectCoupon(item)"
          style="background: #fff; border-radius: 8px; margin-bottom: 12px; padding: 12px; display: flex; align-items: center; justify-content: space-between; position: relative;"
          :style="selectedCoupon?.userCouponId === item.userCouponId ? 'border: 1px solid #ff8c00;' : ''"
        >
          <div style="display: flex; flex-direction: column;">
            <span style="font-size: 15px; font-weight: bold;">{{ item.coupon.couponName }}</span>
            <span style="font-size: 12px; color: #ff8c00; margin-top: 4px;">抵扣: {{ getCouponDiscountText(item.coupon) }}</span>
            <span style="font-size: 11px; color: #999; margin-top: 2px;">{{ item.coupon.minAmount > 0 ? `满${item.coupon.minAmount}积分可用` : '无门槛' }}</span>
          </div>
          <van-icon v-if="selectedCoupon?.userCouponId === item.userCouponId" name="checked" color="#ff8c00" size="20" />
          <van-icon v-else name="circle" color="#eee" size="20" />
        </div>
      </div>
      <div style="position: absolute; bottom: 0; left: 0; width: 100%; padding: 12px; background: #fff; box-sizing: border-box; display: flex; gap: 12px;">
        <van-button block round type="default" @click="selectCoupon(null)" style="flex: 1;">不使用</van-button>
        <van-button block round type="primary" @click="showCouponPopup = false" style="flex: 1; background: linear-gradient(90deg, #ff8c00, #ff5252); border: none;">确定</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { goodsDetail, exchange } from '@/api/goods'
import { listAddresses } from '@/api/user'
import { getAvailableCoupons } from '@/api/coupon'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goods = ref(null)
const qty = ref(1)
const remark = ref('')
const address = ref(null)
const loading = ref(false)

const showCouponPopup = ref(false)
const availableCoupons = ref([])
const selectedCoupon = ref(null)

const originalTotalPoints = computed(() => (goods.value?.points || 0) * qty.value)

const totalPoints = computed(() => {
  let tp = originalTotalPoints.value
  if (selectedCoupon.value) {
    const c = selectedCoupon.value.coupon
    if (c.couponType === '0') {
      tp = Math.max(0, tp - c.discountValue)
    } else if (c.couponType === '1') {
      tp = Math.max(0, Math.floor(tp * (c.discountValue / 100)))
    } else if (c.couponType === '2') {
      tp = Math.max(0, tp - c.discountValue)
    }
  }
  return tp
})
const canSubmit = computed(() => {
  if (!goods.value) return false
  return userStore.points >= totalPoints.value
})

const baseApi = import.meta.env.VITE_APP_BASE_API

function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

async function load() {
  const [g, addrs] = await Promise.all([
    goodsDetail(route.params.id),
    listAddresses().catch(() => ({ data: [] }))
  ])
  goods.value = g.data
  const list = addrs.data || []
  // 优先使用从地址列表 picker 模式带回的选择
  const picked = sessionStorage.getItem('selectedAddress')
  if (picked) {
    try {
      const pickedObj = JSON.parse(picked)
      const found = list.find(a => String(a.addressId) === String(pickedObj.addressId))
      address.value = found || pickedObj
    } catch (e) {
      address.value = list.find(a => a.isDefault === '1') || list[0] || null
    }
    sessionStorage.removeItem('selectedAddress')
  } else {
    const defaultOne = list.find(a => a.isDefault === '1') || list[0]
    address.value = defaultOne || null
  }
  
  // 加载优惠券
  if (goods.value) {
    try {
      const cRes = await getAvailableCoupons(goods.value.goodsId, originalTotalPoints.value)
      availableCoupons.value = cRes.data || []
      // 默认选择第一张可用的优惠券
      if (availableCoupons.value.length > 0) {
        selectedCoupon.value = availableCoupons.value[0]
      }
    } catch (e) {}
  }
}

function selectCoupon(item) {
  selectedCoupon.value = item
  if (item === null) showCouponPopup.value = false
}

function getCouponDiscountText(c) {
  if (!c) return ''
  if (c.couponType === '1') return `${c.discountValue}%折`
  return `￥${c.discountValue}`
}

function goAddress() { router.push('/address?picker=1') }

async function submit() {
  if (!goods.value) return
  if (goods.value.goodsType === '0' && !address.value) {
    await showDialog({ title: '提示', message: '实物商品需选择收货地址，请先选择地址', confirmButtonText: '去选择' })
    goAddress()
    return
  }
  if (userStore.points < totalPoints.value) {
    showToast({
    message: '积分不足',
    className: 'my-toast'
});
    return
  }
  try {
    await showDialog({ title: '确认兑换？', message: `将扣除 ${totalPoints.value} 积分`, showCancelButton: true })
  } catch (e) { return }

  loading.value = true
  try {
    const res = await exchange({
      goodsId: goods.value.goodsId,
      quantity: qty.value,
      addressId: address.value?.addressId,
      remark: remark.value,
      userCouponId: selectedCoupon.value?.userCouponId
    })
    showToast({
    message: '兑换成功',
    className: 'my-toast'
})
    await userStore.fetchUserInfo()
    router.replace('/orders/' + res.data.orderId)
  } finally { loading.value = false }
}

onMounted(load)
onActivated(load)
</script>

<style scoped lang="scss">
.exchange { padding-top: 46px; padding-bottom: 60px; background: #f7f7f7; min-height: 100vh; }
.card { background: #fff; margin: 8px 12px; padding: 12px 14px; border-radius: 12px; position: relative; }
.address-card { padding-right: 28px; }
.address-card .arrow { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); color: #999; }
.addr-top { font-size: 15px; }
.addr-top .consignee { font-weight: 600; margin-right: 10px; }
.addr-top .phone { color: #666; font-size: 13px; }
.addr-detail { font-size: 13px; color: #555; margin-top: 4px; }
.empty { color: #ff8c00; font-size: 14px; }
.goods-card { display: flex; align-items: center; gap: 10px; }
.goods-card .cover { width: 72px; height: 72px; border-radius: 6px; object-fit: cover; }
.goods-card .info { flex: 1; }
.goods-card .name { font-size: 14px; color: #333; line-height: 1.3; }
.goods-card .price { color: #ff8c00; font-size: 13px; margin-top: 4px; }
.goods-card .price span { font-weight: 600; font-size: 15px; }
.total .row { display: flex; justify-content: space-between; padding: 4px 0; font-size: 14px; color: #555; }
.points { color: #ff8c00; font-weight: 600; }
.bar-price { color: #ff8c00; font-weight: 700; font-size: 18px; }
</style>
