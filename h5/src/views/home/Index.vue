<template>
  <div class="home page-content">
    <!-- 顶部搜索与背景 -->
    <div class="header-bg">
      <van-search v-model="keyword" placeholder="搜索商品" shape="round" background="transparent" @search="onSearch" />
    </div>

    <div class="main-content">
      <!-- 轮播图 -->
      <van-swipe :autoplay="3500" indicator-color="#ff8c00" class="banner">
        <van-swipe-item v-for="(b, i) in banners" :key="i">
          <img v-if="b.img" :src="b.img" class="banner-img" />
          <div v-else class="banner-item" :style="{ background: b.bg }">{{ b.text }}</div>
        </van-swipe-item>
      </van-swipe>

      <!-- 金刚区图标导航 -->
      <div class="king-kong">
        <div class="nav-item" @click="scrollToCategory">
          <div class="icon-wrap" style="background: linear-gradient(135deg, #ff8a80, #ff5252)"><van-icon name="apps-o" /></div>
          <span>商品分类</span>
        </div>
        <div class="nav-item" @click="$router.push('/sign')">
          <div class="icon-wrap" style="background: linear-gradient(135deg, #8c9eff, #536dfe)"><van-icon name="calendar-o" /></div>
          <span>每日签到</span>
        </div>
        <div class="nav-item" @click="$router.push('/orders')">
          <div class="icon-wrap" style="background: linear-gradient(135deg, #b9f6ca, #00e676)"><van-icon name="orders-o" /></div>
          <span>我的订单</span>
        </div>
        <div class="nav-item" @click="$router.push('/coupon/center')">
          <div class="icon-wrap" style="background: linear-gradient(135deg, #ff9a9e, #fecfef)"><van-icon name="coupon-o" /></div>
          <span>领券中心</span>
        </div>
        <div class="nav-item" @click="$router.push('/address')">
          <div class="icon-wrap" style="background: linear-gradient(135deg, #b388ff, #7c4dff)"><van-icon name="location-o" /></div>
          <span>地址管理</span>
        </div>
      </div>

      <!-- 分类切换 -->
      <van-tabs v-model:active="catActive" sticky offset-top="0" line-height="2"
                title-active-color="#ff8c00" color="#ff8c00" @change="onCatChange" id="categoryTabs">
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
  </div>
</template>

<script setup>
// 商城页已添加组件名称定义
defineOptions({ name: 'Home' })
import { ref, onMounted,onActivated  } from 'vue'
import { listGoods, listCategories } from '@/api/goods'
import { useUserStore } from '@/stores/user'
import GoodsCard from '@/components/GoodsCard.vue'

const userStore = useUserStore()
const list = ref([])
const categories = ref([])
const catActive = ref('')
const keyword = ref('')
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)
const finished = ref(false)

const banners = [
  { img: 'https://img.yzcdn.cn/vant/ipad.jpeg', bg: 'linear-gradient(135deg, #ff8c00, #ffb84d)' },
  { img: 'https://img.yzcdn.cn/vant/apple-1.jpg', bg: 'linear-gradient(135deg, #4facfe, #00f2fe)' }
]

function onSearch() {
  list.value = []
  pageNum.value = 1
  finished.value = false
  loading.value = true
  loadMore()
}

function scrollToCategory() {
  const el = document.getElementById('categoryTabs')
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}

async function loadMore() {
  try {
    const res = await listGoods({
      pageNum: pageNum.value, pageSize,
      categoryId: catActive.value || undefined,
      goodsName: keyword.value || undefined
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
  loading.value = true
  loadMore()
}

async function loadCategories() {
  try {
    const res = await listCategories()
    // 只展示一级分类（最多 8 个）
    categories.value = (res.data || []).slice(0, 8)
  } catch (e) {}
}
// 首次加载
onMounted(async () => {
  loadCategories()
  if (!userStore.userInfo) {
    try { await userStore.fetchUserInfo() } catch (e) {}
  }
})
// 关键：从其他页面返回时（keep-alive 缓存激活），重新检测并加载
onActivated(() => {
  // 如果列表为空，重新加载数据
  if (list.value.length === 0 && !loading.value) {
    finished.value = false
    loading.value = true
    loadMore()
  }
})
</script>

<style scoped lang="scss">
.home { background: #f7f7f7; min-height: 100vh; position: relative; padding-bottom: 50px; }
.header-bg {
  background: linear-gradient(180deg, #fceadd 0%, #f7f7f7 100%);
  padding: 10px 12px;
  position: sticky;
  top: 0;
  z-index: 99;
}
.main-content {
  margin-top: -10px;
}

.banner { margin: 0 12px 12px; border-radius: 12px; overflow: hidden; }
.banner-img { width: 100%; height: 140px; object-fit: cover; display: block; }
.banner-item {
  height: 140px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 16px; font-weight: 600;
}

.king-kong {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px 20px;
  background: transparent;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
.nav-item span {
  font-size: 12px;
  color: #333;
}

.goods-list { padding: 12px; }
.goods-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
</style>
