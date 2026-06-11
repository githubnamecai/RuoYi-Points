<template>
  <div class="product-detail">
    <van-nav-bar title="商品详情" left-arrow @click-left="$router.back()" fixed />
    <div class="content" v-if="goods">
      <div class="cover-panel">
        <img :src="formatImg(goods.coverImg)" class="cover" />
      </div>
      <div class="card">
        <div class="meta-badges">
          <span class="badge">{{ goods.goodsType === '0' ? '实物权益' : '数字权益' }}</span>
          <span v-if="goods.limitPerUser > 0" class="badge badge-soft">每人限兑 {{ goods.limitPerUser }}</span>
        </div>
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
.product-detail {
  padding-top: 46px;
  padding-bottom: 86px;
  background: transparent;
  min-height: 100vh;
}

.cover-panel {
  margin: 12px 12px 0;
  padding: 16px;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(240, 247, 255, 0.82));
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 18px 36px rgba(22, 53, 110, 0.1);
}

.cover {
  width: 100%;
  max-height: 375px;
  object-fit: contain;
  background: linear-gradient(180deg, #f6f9ff, #eef5ff);
  border-radius: 22px;
  display: block;
}

.card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.8));
  margin: 10px 12px;
  padding: 18px;
  border-radius: 22px;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 16px 30px rgba(22, 53, 110, 0.08);
}

.meta-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(13, 91, 215, 0.1);
  color: #0d5bd7;
  font-size: 12px;
  font-weight: 600;
}

.badge-soft {
  background: rgba(242, 186, 104, 0.14);
  color: #9b6212;
}

.price { display: flex; align-items: baseline; gap: 6px; flex-wrap: wrap; }
.price .num { font-size: 30px; font-weight: 700; color: #0d5bd7; }
.price .num.cash-num { color: #d85267; font-size: 30px; }
.price .unit { font-size: 14px; color: #0d5bd7; }
.price .unit-cash { font-size: 13px; color: #8a95a9; margin-left: 4px; }
.price .original { text-decoration: line-through; color: #8a95a9; font-size: 13px; margin-left: 10px; }
.discount-tag {
  color: #0d5bd7;
  font-size: 12px;
  background: rgba(13, 91, 215, 0.08);
  padding: 4px 10px;
  border-radius: 999px;
  margin-left: 8px;
}

.original-line { font-size: 12px; color: #8a95a9; text-decoration: line-through; margin-top: 6px; }

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
  background: rgba(245, 249, 255, 0.92);
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  padding: 12px 28px;
  border-radius: 999px;
  white-space: nowrap;
  cursor: pointer;
  box-shadow: 0 14px 28px rgba(13, 91, 215, 0.18);
}
.bar-btn.is-loading { opacity: 0.6; pointer-events: none; }
.name { font-size: 18px; font-weight: 700; margin: 10px 0 8px; color: #1a2640; line-height: 1.5; }
.meta { display: flex; gap: 10px; flex-wrap: wrap; color: #7b89a3; font-size: 12px; }
.meta span { padding: 5px 10px; border-radius: 999px; background: rgba(13, 91, 215, 0.06); }
.desc { font-size: 14px; color: #516078; line-height: 1.7; word-break: break-all; }
.desc :deep(img) { max-width: 100%; height: auto; display: block; }
</style>
