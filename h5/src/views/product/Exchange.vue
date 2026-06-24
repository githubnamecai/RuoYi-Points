<template>
  <div class="exchange">
    <van-nav-bar title="兑换确认" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="goods">
      <!-- 实物商品：地址 -->
      <div v-if="goods.goodsType === '0'" class="card address-card" @click="goAddress">
        <template v-if="address">
          <div class="addr-top">
            <span class="consignee">{{ address.consignee }}</span>
            <span class="phone">{{ maskPhone(address.phone) }}</span>
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
          <div class="goods-type">{{ goods.goodsType === '0' ? '实物权益' : '数字权益' }}</div>
          <div class="name">{{ goods.goodsName }}</div>
          <div v-if="goods.specification" class="specification">规格：{{ goods.specification }}</div>
          <div v-if="goods.goodsType === '0'" class="price">
            <span>¥{{ unitPayPrice }}</span> /件
            <span v-if="goods.discountPrice" class="discount-info">原价¥{{ unitOriginalPrice }}</span>
          </div>
          <div v-else class="price"><span>{{ goods.points }}</span> 积分 × {{ qty }}</div>
        </div>
        <van-stepper v-model="qty" min="1" :max="Math.min(goods.stock, 99)" integer />
      </div>

      <!-- 备注 -->
      <div class="card">
        <van-field v-model="remark" label="备注" placeholder="选填" />
      </div>
      
      <!-- 优惠券选择（仅实物商品） -->
      <div v-if="goods.goodsType === '0'" class="card coupon-entry" @click="showCouponPopup = true">
        <span class="entry-label">优惠券</span>
        <div class="entry-value">
          <span v-if="selectedCoupon" class="coupon-value">-{{ getCouponDiscountText(selectedCoupon.coupon) }}</span>
          <span v-else-if="availableCoupons.length > 0" style="color:#ff8c00;">{{ availableCoupons.length }}张可用</span>
          <span v-else>暂无可用</span>
          <van-icon name="arrow" class="entry-arrow" />
        </div>
      </div>

      <!-- 合计 -->
      <div class="card total" :class="{ 'is-warning': goods.goodsType !== '0' && !canSubmit }">
        <!-- 虚拟商品：积分信息 -->
        <template v-if="goods.goodsType !== '0'">
          <div class="row"><span>当前余额</span><b class="points">{{ userStore.points }}</b></div>
          <div class="row"><span>合计扣减</span><b class="points">-{{ totalPoints }}</b></div>
          <div class="row"><span>兑换后剩余</span><b class="points">{{ userStore.points - totalPoints }}</b></div>
          <div v-if="!canSubmit" class="state-tips">当前积分不足，暂时无法完成兑换</div>
        </template>
        <!-- 实物商品：金额信息 -->
        <template v-else>
          <div class="row"><span>商品金额</span><b class="cash">¥{{ displayGoodsAmount.toFixed(2) }}</b></div>
          <div v-if="!selectedCoupon && goods.discountPrice" class="row"><span>原价合计</span><b class="cash-origin">¥{{ totalOriginalPrice.toFixed(2) }}</b></div>
          <div v-if="couponDiscountAmount > 0" class="row"><span>优惠券抵扣</span><b class="cash-discount">-¥{{ couponDiscountAmount.toFixed(2) }}</b></div>
          <div class="row"><span>实付金额</span><b class="cash-final">¥{{ payAmount.toFixed(2) }}</b></div>
        </template>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="bar-info">
        <template v-if="goods?.goodsType === '0'">
          <span class="bar-price">¥{{ payAmount.toFixed(2) }}</span>
          <span v-if="couponDiscountAmount > 0" class="bar-discount">已优惠¥{{ couponDiscountAmount.toFixed(2) }}</span>
        </template>
        <template v-else>
          <span class="bar-price points-color">{{ totalPoints }}</span>
          <span class="bar-suffix">积分</span>
        </template>
      </div>
      <div class="bar-btn" :class="{ 'is-loading': loading, 'is-disabled': goods?.goodsType !== '0' && !canSubmit }" @click="submit">确认兑换</div>
    </div>
    
    <!-- 优惠券弹窗 -->
    <van-popup v-model:show="showCouponPopup" position="bottom" round class="coupon-popup" style="height: 60%;">
      <div class="popup-title">选择优惠券</div>
      <div class="popup-body">
        <div 
          v-for="item in availableCoupons" 
          :key="item.userCouponId" 
          @click="selectCoupon(item)"
          class="popup-coupon-item"
          :class="{ active: selectedCoupon?.userCouponId === item.userCouponId }"
        >
          <div class="popup-coupon-info">
            <span class="popup-coupon-name">{{ item.coupon.couponName }}</span>
            <span class="popup-coupon-discount">抵扣: {{ getCouponDiscountText(item.coupon) }}</span>
            <span class="popup-coupon-rule">{{ item.coupon.minAmount > 0 ? `满${item.coupon.minAmount}积分可用` : '无门槛' }}</span>
          </div>
          <van-icon v-if="selectedCoupon?.userCouponId === item.userCouponId" name="checked" color="#0d5bd7" size="20" />
          <van-icon v-else name="circle" color="#eee" size="20" />
        </div>
      </div>
      <div class="popup-actions">
        <van-button block round type="default" @click="selectCoupon(null)" class="popup-action">不使用</van-button>
        <van-button block round type="primary" @click="showCouponPopup = false" class="popup-action">确定</van-button>
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
import { maskPhone } from '@/utils/mask'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goods = ref(null)
const qty = ref(1)
const remark = ref('')
const address = ref(null)
const loading = ref(false)
const defaultImg = 'https://via.placeholder.com/300x300?text=Goods'

const showCouponPopup = ref(false)
const availableCoupons = ref([])
const selectedCoupon = ref(null)

const originalTotalPoints = computed(() => (goods.value?.points || 0) * qty.value)

const unitOriginalPrice = computed(() => {
  if (!goods.value || goods.value.goodsType !== '0') return 0
  return +((goods.value.price ?? 0) || 0)
})

const unitPayPrice = computed(() => {
  if (!goods.value || goods.value.goodsType !== '0') return 0
  return +(((goods.value.discountPrice ?? goods.value.price) ?? 0) || 0)
})

// 实物商品：计算总金额
const totalOriginalPrice = computed(() => {
  if (!goods.value || goods.value.goodsType !== '0') return 0
  return unitOriginalPrice.value * qty.value
})

const totalDiscountPrice = computed(() => {
  if (!goods.value || goods.value.goodsType !== '0') return 0
  return unitPayPrice.value * qty.value
})

const displayGoodsAmount = computed(() => {
  if (!goods.value || goods.value.goodsType !== '0') return 0
  return selectedCoupon.value ? totalOriginalPrice.value : totalDiscountPrice.value
})

// 实物商品：计算优惠券抵扣金额
const couponDiscountAmount = computed(() => {
  if (!selectedCoupon.value || !goods.value || goods.value.goodsType !== '0') return 0
  const c = selectedCoupon.value.coupon
  const tp = totalOriginalPrice.value
  if (c.couponType === '0') { // 满减
    return Math.min(tp, c.discountValue)
  } else if (c.couponType === '1') { // 折扣
    return +(tp * (1 - c.discountValue / 100)).toFixed(2)
  } else if (c.couponType === '2') { // 无门槛
    return Math.min(tp, c.discountValue)
  }
  return 0
})

// 实物商品：实付金额 = 总金额 - 优惠券抵扣
const payAmount = computed(() => {
  if (!goods.value || goods.value.goodsType !== '0') return 0
  if (selectedCoupon.value) {
    return Math.max(0, +(totalOriginalPrice.value - couponDiscountAmount.value).toFixed(2))
  }
  return Math.max(0, +totalDiscountPrice.value.toFixed(2))
})

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

/**
 * 格式化商品图片地址。
 */
function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

/**
 * 初始化加载商品、地址与可用优惠券。
 */
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
  
  // 加载优惠券（仅实物商品）
  if (goods.value && goods.value.goodsType === '0') {
    try {
      const cRes = await getAvailableCoupons(goods.value.goodsId, totalOriginalPrice.value)
      availableCoupons.value = cRes.data || []
    } catch (e) {}
  }
}

/**
 * 选择优惠券（传 null 表示不使用）。
 */
function selectCoupon(item) {
  selectedCoupon.value = item
  if (item === null) showCouponPopup.value = false
}

/**
 * 获取优惠券展示文案。
 */
function getCouponDiscountText(c) {
  if (!c) return ''
  if (c.couponType === '1') return `${c.discountValue}%折`
  return `￥${c.discountValue}`
}

/**
 * 跳转到地址选择页（picker 模式）。
 */
function goAddress() { router.push('/address?picker=1') }

/**
 * 提交兑换（保持现有业务逻辑，仅增强确认提示信息）。
 */
async function submit() {
  if (!goods.value) return
  const isReal = goods.value.goodsType === '0'

  // 实物商品：校验地址
  if (!address.value) {
    await showDialog({ title: '提示', message: '商品需选择收货地址，请先选择地址', confirmButtonText: '去选择' })
    goAddress()
    return
  }

  // 虚拟商品：校验积分
  if (!isReal && userStore.points < totalPoints.value) {
    showToast({ message: '积分不足', className: 'my-toast' })
    return
  }

  // 确认弹窗
  try {
    const msg = isReal
      ? `支付金额：¥${payAmount.value.toFixed(2)}\n支付方式：上门收费`
      : `将扣除 ${totalPoints.value} 积分\n支付方式：积分`
    await showDialog({ title: '确认兑换？', message: msg, showCancelButton: true })
  } catch (e) { return }

  loading.value = true
  try {
    const res = await exchange({
      goodsId: goods.value.goodsId,
      quantity: qty.value,
      addressId: address.value?.addressId,
      remark: remark.value,
      userCouponId: isReal ? selectedCoupon.value?.userCouponId : undefined,
      payAmount: isReal ? payAmount.value : undefined
    })
    showToast({ message: '兑换成功', className: 'my-toast' })
    await userStore.fetchUserInfo()
    router.replace('/orders/' + res.data.orderId)
  } finally { loading.value = false }
}

onMounted(load)
onActivated(load)
</script>

<style scoped lang="scss">
.exchange {
  padding-top: 46px;
  padding-bottom: 96px;
  background: transparent;
  min-height: 100vh;
}

.card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.8));
  margin: 10px 12px;
  padding: 14px 16px;
  border-radius: 22px;
  position: relative;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 16px 30px rgba(22, 53, 110, 0.08);
}

.address-card { padding-right: 34px; }
.address-card .arrow { position: absolute; right: 14px; top: 50%; transform: translateY(-50%); color: #8a95a9; }
.addr-top { font-size: 15px; color: #1a2640; }
.addr-top .consignee { font-weight: 700; margin-right: 10px; }
.addr-top .phone { color: #66758e; font-size: 13px; }
.addr-detail { font-size: 13px; color: #516078; margin-top: 6px; line-height: 1.55; padding-right: 12px; }
.empty { color: #0d5bd7; font-size: 14px; font-weight: 600; }

.goods-card { display: flex; align-items: center; gap: 12px; }
.goods-card .cover {
  width: 76px;
  height: 76px;
  border-radius: 16px;
  object-fit: cover;
  background: linear-gradient(180deg, #f6f9ff, #edf4ff);
}

.goods-card .info { flex: 1; }
.goods-type {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(13, 91, 215, 0.08);
  color: #0d5bd7;
  font-size: 11px;
  font-weight: 600;
}

.goods-card .name { font-size: 15px; color: #1a2640; line-height: 1.45; font-weight: 700; margin-top: 8px; }
.goods-card .specification {
  margin-top: 5px;
  font-size: 12px;
  line-height: 1.45;
  color: #6f7d95;
}
.goods-card .price { color: #0d5bd7; font-size: 13px; margin-top: 6px; }
.goods-card .price span { font-weight: 700; font-size: 16px; }
.goods-card .price .discount-info {
  font-size: 11px;
  color: #8a95a9;
  margin-left: 6px;
  font-weight: 600;
  text-decoration: line-through;
}

.coupon-entry {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.entry-label {
  font-size: 14px;
  color: #1a2640;
  font-weight: 600;
}

.entry-value {
  display: flex;
  align-items: center;
  color: #8a95a9;
  font-size: 13px;
}

.coupon-value {
  color: #0d5bd7;
  font-weight: 600;
}

.entry-arrow {
  margin-left: 4px;
  color: #a3acc0;
}

.total .row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 14px; color: #516078; }
.total.is-warning { border-color: rgba(230, 107, 125, 0.2); }
.points { color: #0d5bd7; font-weight: 700; }
.cash { color: #d85267; font-weight: 700; }
.cash-origin { color: #8a95a9; font-weight: 600; text-decoration: line-through; }
.cash-discount { color: #0d5bd7; font-weight: 700; }
.cash-final { color: #d85267; font-weight: 700; font-size: 18px; }

.state-tips {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 14px;
  background: rgba(230, 107, 125, 0.1);
  color: #d85267;
  font-size: 12px;
  line-height: 1.5;
}

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
  background: rgba(245, 249, 255, 0.92);
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 12px calc(10px + env(safe-area-inset-bottom, 0px));
  box-shadow: 0 -10px 24px rgba(22, 53, 110, 0.08);
  backdrop-filter: blur(16px);
}
.bar-info { display: flex; align-items: baseline; gap: 4px; flex: 1; }
.bar-price { color: #d85267; font-weight: 700; font-size: 22px; }
.bar-price.points-color { color: #0d5bd7; }
.bar-discount { color: #0d5bd7; font-size: 12px; margin-left: 4px; }
.bar-suffix { color: #0d5bd7; font-size: 13px; margin-left: 2px; }
.bar-btn {
  background: linear-gradient(135deg, #123f96, #1f73ef 70%, #58b9ff);
  color: #fff; font-size: 15px; font-weight: 700;
  padding: 12px 28px; border-radius: 999px; white-space: nowrap; cursor: pointer;
  box-shadow: 0 14px 28px rgba(13, 91, 215, 0.18);
}
.bar-btn.is-loading { opacity: 0.6; pointer-events: none; }
.bar-btn.is-disabled { opacity: 0.4; pointer-events: none; }

.coupon-popup {
  background: rgba(245, 249, 255, 0.96) !important;
  backdrop-filter: blur(18px);
}

.popup-title {
  padding: 18px 16px 12px;
  text-align: center;
  font-weight: 700;
  font-size: 16px;
  color: #1a2640;
}

.popup-body {
  padding: 0 12px;
  height: calc(100% - 106px);
  overflow-y: auto;
}

.popup-coupon-item {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.82));
  border-radius: 18px;
  margin-bottom: 12px;
  padding: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  border: 1px solid rgba(124, 147, 187, 0.12);
}

.popup-coupon-item.active {
  border-color: rgba(13, 91, 215, 0.3);
  box-shadow: 0 10px 22px rgba(13, 91, 215, 0.1);
}

.popup-coupon-info {
  display: flex;
  flex-direction: column;
}

.popup-coupon-name {
  font-size: 15px;
  font-weight: 700;
  color: #1a2640;
}

.popup-coupon-discount {
  font-size: 12px;
  color: #0d5bd7;
  margin-top: 6px;
}

.popup-coupon-rule {
  font-size: 11px;
  color: #8a95a9;
  margin-top: 4px;
}

.popup-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.92);
  box-sizing: border-box;
  display: flex;
  gap: 12px;
  border-top: 1px solid rgba(124, 147, 187, 0.12);
}

.popup-action {
  flex: 1;
}
</style>
