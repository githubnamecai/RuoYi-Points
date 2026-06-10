<template>
  <div class="product-detail">
    <van-nav-bar title="商品详情" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="goods">
      <img :src="formatImg(goods.coverImg)" class="cover" />
      <div class="card">
        <!-- 实物商品：显示金额 + 优惠价 -->
        <template v-if="goods.goodsType === '0'">
          <div class="price">
            <span class="num cash-num">¥{{ goods.price || 0 }}</span>
            <span class="unit-cash">金额</span>
            <span v-if="goods.discountPrice" class="discount-tag">东方有线优惠价 ¥{{ goods.discountPrice }}</span>
          </div>
          <div v-if="goods.originalPrice > 0" class="original-line">原价 ¥{{ goods.originalPrice }}</div>
        </template>
        <!-- 虚拟商品：显示积分 -->
        <template v-else>
          <div class="price">
            <span class="num">{{ goods.points }}</span>
            <span class="unit">积分</span>
            <span v-if="goods.originalPrice > 0" class="original">原价¥{{ goods.originalPrice }}</span>
          </div>
        </template>
        <div class="name">{{ goods.goodsName }}</div>
        <div class="meta">
          <span>库存: {{ goods.stock }}</span>
          <span>已兑: {{ goods.sales || 0 }}</span>
          <span v-if="goods.limitPerUser > 0">限兑: {{ goods.limitPerUser }}</span>
        </div>
      </div>

      <div class="card">
        <div class="section-title">商品详情</div>
        <div class="desc" v-html="formatDescription(goods.description)"></div>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="bar-info">
        <template v-if="goods?.goodsType === '0'">
          <span class="bar-price">¥{{ goods?.price || 0 }}</span>
          <span v-if="goods?.discountPrice" class="bar-discount">优惠价¥{{ goods.discountPrice }}</span>
        </template>
        <template v-else>
          <span class="bar-price points-color">{{ goods?.points || 0 }}</span>
          <span class="bar-suffix">积分</span>
        </template>
      </div>
      <div class="bar-btn" :class="{ 'is-loading': loading }" @click="goExchange">立即兑换</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { goodsDetail } from '@/api/goods'

const baseApi = import.meta.env.VITE_APP_BASE_API
const baseApiIp = import.meta.env.VITE_APP_BASE_API_IP
const defaultImg = 'https://via.placeholder.com/300x300?text=Goods'

const route = useRoute()
const router = useRouter()
const goods = ref(null)
const loading = ref(false)

function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}

// 处理富文本中的图片路径
function formatDescription(html) {
  if (!html) return '<div style="color:#999">暂无详情</div>'
  // 将 src="/dev-api 替换为 src="baseApiIp + /dev-api
  return html.replace(/src="\/dev-api/g, `src="${baseApiIp}/dev-api`)
}

async function load() {
  const res = await goodsDetail(route.params.id)
  goods.value = res.data
}

function goExchange() {
  router.push('/product/' + route.params.id + '/exchange')
}

onMounted(load)
</script>

<style scoped lang="scss">
.product-detail { padding-top: 46px; padding-bottom: 50px; background: #f7f7f7; min-height: 100vh; }
.cover {
  width: 100%;
  max-height: 375px;
  object-fit: contain;
  background: #fff;
  display: block;
}
.card {
  background: #fff; margin: 8px 12px; padding: 14px 16px;
  border-radius: 12px;
}
.price { display: flex; align-items: baseline; gap: 4px; }
.price .num { font-size: 28px; font-weight: 700; color: #ff8c00; }
.price .num.cash-num { color: #e53935; font-size: 28px; }
.price .unit { font-size: 14px; color: #ff8c00; }
.price .unit-cash { font-size: 13px; color: #999; margin-left: 4px; }
.price .original { text-decoration: line-through; color: #999; font-size: 13px; margin-left: 10px; }
.discount-tag { color: #ff8c00; font-size: 12px; background: #fff3e0; padding: 2px 8px; border-radius: 4px; margin-left: 8px; }
.original-line { font-size: 12px; color: #999; text-decoration: line-through; margin-top: 4px; }
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
  background: #fff; display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
}
.bar-info { display: flex; align-items: baseline; gap: 4px; flex: 1; }
.bar-price { color: #e53935; font-weight: 700; font-size: 20px; }
.bar-price.points-color { color: #ff8c00; }
.bar-discount { color: #ff8c00; font-size: 12px; margin-left: 4px; }
.bar-suffix { color: #ff8c00; font-size: 13px; margin-left: 2px; }
.bar-btn {
  background: #ff8c00; color: #fff; font-size: 15px; font-weight: 600;
  padding: 10px 28px; border-radius: 20px; white-space: nowrap; cursor: pointer;
}
.bar-btn.is-loading { opacity: 0.6; pointer-events: none; }
.name { font-size: 16px; font-weight: 600; margin: 8px 0 6px; color: #333; }
.meta { display: flex; gap: 14px; color: #999; font-size: 12px; }
.section-title { font-weight: 600; margin-bottom: 8px; }
.desc { font-size: 14px; color: #555; line-height: 1.6; word-break: break-all; }
.desc :deep(img) { max-width: 100%; height: auto; display: block; }
</style>
