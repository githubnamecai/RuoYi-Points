<template>
  <div class="home page-content">
    <!-- 顶部积分卡 -->
    <div class="header-card">
      <div class="hello">你好，{{ userStore.nickname }} 👋</div>
      <div class="points">
        <span class="label">我的积分</span>
        <span class="num">{{ userStore.points }}</span>
      </div>
      <div class="actions">
        <span @click="$router.push('/sign')">每日签到</span>
        <span @click="$router.push('/points/detail')">积分明细</span>
      </div>
    </div>

    <!-- 轮播图 -->
    <van-swipe :autoplay="3500" indicator-color="#ff8c00" class="banner">
      <van-swipe-item v-for="(b, i) in banners" :key="i">
        <div class="banner-item" :style="{ background: b.bg }">{{ b.text }}</div>
      </van-swipe-item>
    </van-swipe>

    <!-- 分类切换 -->
    <van-tabs v-model:active="catActive" sticky offset-top="0" line-height="2"
              title-active-color="#ff8c00" color="#ff8c00" @change="onCatChange">
      <van-tab title="全部" name="" />
      <van-tab v-for="c in categories" :key="c.categoryId" :title="c.categoryName" :name="c.categoryId" />
    </van-tabs>

    <!-- 商品瀑布流 -->
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了"
              @load="loadMore" class="goods-list">
      <div class="goods-grid">
        <goods-card v-for="g in list" :key="g.goodsId" :goods="g" />
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listGoods, listCategories } from '@/api/goods'
import { useUserStore } from '@/stores/user'
import GoodsCard from '@/components/GoodsCard.vue'

const userStore = useUserStore()
const list = ref([])
const categories = ref([])
const catActive = ref('')
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)
const finished = ref(false)

const banners = [
  { text: '🎉 邀请新用户得 200 积分', bg: 'linear-gradient(135deg, #ff8c00, #ffb84d)' },
  { text: '✨ 连续签到 7 天奖 50 积分', bg: 'linear-gradient(135deg, #4facfe, #00f2fe)' },
  { text: '🔥 热门商品限时兑换', bg: 'linear-gradient(135deg, #fa709a, #fee140)' }
]

async function loadMore() {
  try {
    const res = await listGoods({
      pageNum: pageNum.value, pageSize,
      categoryId: catActive.value || undefined
    })
    list.value.push(...(res.rows || []))
    finished.value = list.value.length >= res.total
    pageNum.value++
  } finally {
    loading.value = false
  }
}

function onCatChange() {
  list.value = []
  pageNum.value = 1
  finished.value = false
  loadMore()
}

async function loadCategories() {
  try {
    const res = await listCategories()
    // 只展示一级分类（最多 8 个）
    categories.value = (res.data || []).slice(0, 8)
  } catch (e) {}
}

onMounted(async () => {
  loadCategories()
  if (!userStore.userInfo) {
    try { await userStore.fetchUserInfo() } catch (e) {}
  }
})
</script>

<style scoped lang="scss">
.home { background: #f7f7f7; min-height: 100vh; }
.header-card {
  margin: 12px;
  padding: 16px 18px;
  background: linear-gradient(135deg, #ff8c00, #ffb84d);
  border-radius: 14px;
  color: #fff;
  box-shadow: 0 4px 14px rgba(255, 140, 0, 0.3);
}
.hello { font-size: 14px; opacity: 0.9; }
.points {
  margin: 6px 0;
  display: flex; align-items: baseline; gap: 8px;
}
.points .label { font-size: 13px; opacity: 0.9; }
.points .num { font-size: 30px; font-weight: 700; letter-spacing: 1px; }
.actions {
  display: flex; gap: 10px; margin-top: 6px;
}
.actions span {
  font-size: 12px; padding: 4px 10px;
  background: rgba(255, 255, 255, 0.25); border-radius: 12px;
}

.banner { margin: 0 12px 12px; border-radius: 12px; overflow: hidden; }
.banner-item {
  height: 110px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 16px; font-weight: 600;
}

.goods-list { padding: 12px; }
.goods-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
</style>
