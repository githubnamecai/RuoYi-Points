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
        <img :src="goods.coverImg" class="cover" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { goodsDetail, exchange } from '@/api/goods'
import { listAddresses } from '@/api/user'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goods = ref(null)
const qty = ref(1)
const remark = ref('')
const address = ref(null)
const loading = ref(false)

const totalPoints = computed(() => (goods.value?.points || 0) * qty.value)
const canSubmit = computed(() => {
  if (!goods.value) return false
  if (goods.value.goodsType === '0' && !address.value) return false
  return userStore.points >= totalPoints.value
})

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
}

function goAddress() { router.push('/address?picker=1') }

async function submit() {
  if (!goods.value) return
  if (goods.value.goodsType === '0' && !address.value) {
    showToast('请选择收货地址'); return
  }
  if (userStore.points < totalPoints.value) {
    showToast('积分不足'); return
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
      remark: remark.value
    })
    showToast('兑换成功')
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
