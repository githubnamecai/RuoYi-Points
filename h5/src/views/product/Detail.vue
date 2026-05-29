<template>
  <div class="product-detail">
    <van-nav-bar title="商品详情" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="goods">
      <img :src="formatImg(goods.coverImg)" class="cover" />
      <div class="card">
        <div class="price">
          <span class="num">{{ goods.points }}</span>
          <span class="unit">积分</span>
          <span v-if="goods.originalPrice > 0" class="original">¥{{ goods.originalPrice }}</span>
        </div>
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

    <van-submit-bar
      :price="goods?.points * 100"
      button-text="立即兑换"
      button-color="#ff8c00"
      currency=""
      label="所需积分:"
      suffix-label="积分"
      :loading="loading"
      @submit="goExchange"
    >
      <template #price>
        <span class="bar-price">{{ goods?.points || 0 }}</span>
      </template>
    </van-submit-bar>
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
.price .unit { font-size: 14px; color: #ff8c00; }
.price .original { text-decoration: line-through; color: #999; font-size: 13px; margin-left: 10px; }
.name { font-size: 16px; font-weight: 600; margin: 8px 0 6px; color: #333; }
.meta { display: flex; gap: 14px; color: #999; font-size: 12px; }
.section-title { font-weight: 600; margin-bottom: 8px; }
.desc { font-size: 14px; color: #555; line-height: 1.6; word-break: break-all; }
.desc :deep(img) { max-width: 100%; height: auto; display: block; }
.bar-price { color: #ff8c00; font-weight: 700; font-size: 18px; }
</style>
