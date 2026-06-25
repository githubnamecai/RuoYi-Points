<template>
  <div class="home page-content">
    <!-- 顶部搜索与背景 -->
    <div class="header-bg">
      <div class="hero-card">
        <div class="hero-top">
          <div class="hero-copy">
            <div class="hero-title">东方有线权益中心</div>
            <div class="hero-sub">精选好礼、5G、宽带与电视服务一站浏览与兑换</div>
            <!-- <div class="hero-badges">
               <span>游客可浏览</span>
               <span>登录后兑换</span>
            </div> -->
          </div>
        </div>
        <van-search v-model="keyword" placeholder="搜索商品 / 服务权益" shape="round" background="transparent" @search="onSearch" />
      </div>
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
      <div class="king-kong glass-panel">
        <div class="nav-item" @click="$router.push('/activity')">
          <div class="icon-wrap hot"><van-icon name="fire-o" /></div>
          <span>热门活动</span>
        </div>
        <div class="nav-item" @click="$router.push('/sign')">
          <div class="icon-wrap sign"><van-icon name="calendar-o" /></div>
          <span>每日签到</span>
        </div>
        <div class="nav-item" @click="$router.push('/orders')">
          <div class="icon-wrap order"><van-icon name="orders-o" /></div>
          <span>我的订单</span>
        </div>
        <div class="nav-item" @click="$router.push('/coupon/center')">
          <div class="icon-wrap coupon"><van-icon name="coupon-o" /></div>
          <span>领券中心</span>
        </div>
        <div class="nav-item" @click="$router.push('/address')">
          <div class="icon-wrap address"><van-icon name="location-o" /></div>
          <span>地址管理</span>
        </div>
      </div>

      <!-- 分类切换 -->
      <div class="tabs-panel">
        <div class="section-head">
          <div class="section-title">精选权益</div>
          <div class="section-note">积分兑好礼</div>
        </div>
        <van-tabs
          v-model:active="catActive"
          sticky
          offset-top="0"
          line-height="2"
          title-active-color="var(--primary-color)"
          color="var(--primary-color)"
          @change="onCatChange"
          id="categoryTabs"
          shrink
        >
          <van-tab v-for="tab in topTabs" :key="tab.key" :title="tab.label" :name="tab.key" />
        </van-tabs>

        <div v-if="showSubTabs" class="sub-tabs-row">
          <div
            class="sub-tab"
            :class="{ active: !subCatActive }"
            @click="onSubCatChange('')"
          >
            全部
          </div>
          <div
            v-for="sub in currentSubCategories"
            :key="sub.categoryId"
            class="sub-tab"
            :class="{ active: subCatActive === sub.categoryId }"
            @click="onSubCatChange(sub.categoryId)"
          >
            {{ sub.categoryName }}
          </div>
        </div>
      </div>

      <!-- 商品瀑布流 -->
      <van-list
        v-model:loading="loading"
        v-model:error="error"
        :finished="finished"
        :immediate-check="false"
        error-text="加载失败，点击重试"
        finished-text="没有更多了"
        @load="loadMore"
        class="goods-list"
      >
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
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRoute } from 'vue-router'
import { listGoods, listCategories } from '@/api/goods'
import { addScan } from '@/api/scan'
import GoodsCard from '@/components/GoodsCard.vue'
import { collectClientInfo, formatDateYYYYMMDD } from '@/utils/clientInfo'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const list = ref([])
const categories = ref([])
const catActive = ref('all')
const subCatActive = ref('')
const keyword = ref('')
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)
const finished = ref(false)
const error = ref(false)
const initialized = ref(false)
const categoryLoaded = ref(false)
const requesting = ref(false)

const banners = [
  { img: 'https://img.yzcdn.cn/vant/ipad.jpeg', bg: 'linear-gradient(135deg, #ff8c00, #ffb84d)' },
  { img: 'https://img.yzcdn.cn/vant/apple-1.jpg', bg: 'linear-gradient(135deg, #4facfe, #00f2fe)' }
]

const topTabs = computed(() => {
  const categoryTabs = (categories.value || []).map(item => ({
    key: String(item.categoryId),
    label: item.categoryName,
    categoryId: item.categoryId
  }))
  return [{ key: 'all', label: '全部', categoryId: undefined }, ...categoryTabs]
})

const currentTopCategory = computed(() => {
  const currentTab = topTabs.value.find(tab => tab.key === catActive.value)
  if (!currentTab || currentTab.key === 'all') return null
  return categories.value.find(item => String(item.categoryId) === String(currentTab.categoryId)) || null
})

const currentSubCategories = computed(() => {
  return currentTopCategory.value?.children || []
})

const showSubTabs = computed(() => {
  return catActive.value !== 'all' && currentSubCategories.value.length > 0
})

const currentCategoryId = computed(() => {
  if (subCatActive.value) return subCatActive.value
  if (currentTopCategory.value) return currentTopCategory.value.categoryId
  return undefined
})

/**
 * 页面访问/扫码进入统计上报。
 */
async function reportScan() {
  const idParam = route.query?.id
  const hasId = idParam !== undefined && idParam !== null && String(idParam).trim() !== ''
  const qrcodeId = hasId ? Number(idParam) : 1
  const visitType = hasId ? 1 : 0
  const { deviceModel, osVersion, browserName, userAgent } = await collectClientInfo()

  await addScan({
    qrcodeId: Number.isFinite(qrcodeId) ? qrcodeId : 1,
    visitType,
    startTime: formatDateYYYYMMDD(new Date()),
    ip: '',
    deviceModel,
    osVersion,
    browserName,
    userAgent,
    userId: userStore.userInfo?.userId,
    userName: userStore.userInfo?.name,
    nickName: userStore.userInfo?.nickname
  }).catch(() => {})
}

/**
 * 根据关键词重新搜索商品。
 */
function onSearch() {
  resetListState()
  startFirstLoad()
}

/**
 * 加载商品分页数据。
 */
async function loadMore() {
  if (requesting.value || finished.value) return
  try {
    requesting.value = true
    error.value = false
    loading.value = true
    const res = await listGoods({
      pageNum: pageNum.value, pageSize,
      categoryId: currentCategoryId.value,
      goodsName: keyword.value || undefined
    })
    const rows = res.rows || []
    list.value.push(...rows)
    // H5 首页按“本次返回条数”判断是否还有下一页，避免 total 异常导致第一页直接结束
    finished.value = rows.length < pageSize
    pageNum.value++
    initialized.value = true
  } catch (e) {
    // 失败后进入错误态，避免 van-list 与激活钩子反复触发请求
    error.value = true
  } finally {
    requesting.value = false
    loading.value = false
  }
}

/**
 * 切换分类后重置并重新加载。
 */
function onCatChange() {
  subCatActive.value = ''
  resetListState()
  startFirstLoad()
}

/**
 * 切换二级分类后重置并重新加载。
 */
function onSubCatChange(categoryId) {
  subCatActive.value = categoryId
  resetListState()
  startFirstLoad()
}

/**
 * 加载首页一级分类。
 */
async function loadCategories() {
  try {
    const res = await listCategories()
    categories.value = res.data || []
    if (catActive.value !== 'all') {
      const exists = categories.value.some(item => String(item.categoryId) === String(catActive.value))
      if (!exists) {
        catActive.value = 'all'
        subCatActive.value = ''
      }
    }
    categoryLoaded.value = true
  } catch (e) {}
}

/**
 * 重置首页商品列表状态。
 */
function resetListState() {
  list.value = []
  pageNum.value = 1
  finished.value = false
  error.value = false
}

/**
 * 首次或主动搜索时启动一次加载。
 */
function startFirstLoad() {
  if (loading.value) return
  loadMore()
}
// 首次加载
onMounted(async () => {
  reportScan()
  if (!categoryLoaded.value) {
    await loadCategories()
  }
  startFirstLoad()
})
// 关键：从其他页面返回时（keep-alive 缓存激活），重新检测并加载
onActivated(() => {
  // 仅在从未成功初始化过且当前也不在错误态时补拉一次，避免失败后无限重试
  if (!initialized.value && list.value.length === 0 && !loading.value && !error.value) {
    startFirstLoad()
  }
})
</script>

<style scoped lang="scss">
.home {
  background: transparent;
  min-height: 100vh;
  position: relative;
  padding-bottom: 58px;
}

.header-bg {
  padding: 10px 12px 0;
  background: transparent;
}

.hero-card {
  padding: 16px;
  border-radius: 26px;
  background: linear-gradient(135deg, rgba(10, 67, 161, 0.96) 0%, rgba(20, 93, 210, 0.92) 52%, rgba(78, 174, 255, 0.78) 100%);
  box-shadow: 0 22px 42px rgba(13, 91, 215, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.22);
  position: relative;
  overflow: hidden;
}

.hero-card::before,
.hero-card::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
}

.hero-card::before {
  width: 140px;
  height: 140px;
  top: -48px;
  right: -28px;
}

.hero-card::after {
  width: 96px;
  height: 96px;
  bottom: -32px;
  left: -20px;
}

.hero-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.hero-copy {
  flex: 1;
  min-width: 0;
}

.hero-title {
  font-size: 21px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.02em;
}

.hero-sub {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.82);
}

.hero-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.hero-badges span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.14);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
}

.hero-card :deep(.van-search) {
  padding: 0;
  position: relative;
  z-index: 1;
}

.hero-card :deep(.van-search__content) {
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding-left: 10px;
}

.hero-card :deep(.van-field__control),
.hero-card :deep(.van-icon-search) {
  color: rgba(255, 255, 255, 0.9);
}

.hero-card :deep(.van-field__control::placeholder) {
  color: rgba(255, 255, 255, 0.64);
}

.main-content {
  margin-top: 8px;
}

.banner {
  margin: 0 12px 14px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 16px 34px rgba(22, 53, 110, 0.12);
}

.banner-img {
  width: 100%;
  height: 184px;
  object-fit: cover;
  display: block;
}

.banner-item {
  height: 184px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 16px; font-weight: 700;
  letter-spacing: 0.04em;
}

.king-kong {
  display: flex;
  justify-content: space-between;
  margin: 0 12px 14px;
  padding: 14px 10px 18px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.88), rgba(248, 251, 255, 0.72));
  box-shadow: 0 16px 34px rgba(22, 53, 110, 0.08);
  border: 1px solid rgba(135, 160, 197, 0.12);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  width: 20%;
}

.icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.14);
}

.icon-wrap.hot { background: linear-gradient(135deg, #ff9b78, #f55d74); }
.icon-wrap.sign { background: linear-gradient(135deg, #4e7dff, #6ac1ff); }
.icon-wrap.order { background: linear-gradient(135deg, #35b888, #67d49b); }
.icon-wrap.coupon { background: linear-gradient(135deg, #9a72ff, #d689ff); }
.icon-wrap.address { background: linear-gradient(135deg, #5b88ff, #7fc8ff); }

.nav-item span {
  font-size: 12px;
  color: #25324a;
  text-align: center;
  line-height: 1.35;
}

.tabs-panel {
  margin: 0 12px 6px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 0 4px;
}

.section-note {
  font-size: 12px;
  color: #7b89a3;
}

.tabs-panel :deep(.van-tabs__wrap) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 10px 22px rgba(22, 53, 110, 0.06);
  backdrop-filter: blur(10px);
}

.tabs-panel :deep(.van-tab) {
  color: #62708a;
}

.sub-tabs-row {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 10px 4px 2px;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.sub-tabs-row::-webkit-scrollbar {
  display: none;
}

.sub-tab {
  flex: 0 0 auto;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.76);
  color: #62708a;
  font-size: 12px;
  line-height: 1;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 8px 18px rgba(22, 53, 110, 0.05);
}

.sub-tab.active {
  background: linear-gradient(135deg, rgba(13, 91, 215, 0.12), rgba(87, 185, 255, 0.18));
  color: #0d5bd7;
  border-color: rgba(13, 91, 215, 0.18);
  font-weight: 700;
}

.goods-list {
  padding: 8px 12px 0;
}

.goods-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.goods-list :deep(.van-list__finished-text),
.goods-list :deep(.van-list__loading-text) {
  color: #8691a7;
}
</style>
