import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: () => import('@/views/login/Index.vue'), meta: { title: '登录', public: true } },
  { path: '/home', component: () => import('@/views/home/Index.vue'), meta: { title: '积分商城', tabbar: true } },
  { path: '/sign', component: () => import('@/views/sign/Index.vue'), meta: { title: '每日签到', tabbar: true } },
  { path: '/orders', component: () => import('@/views/order/List.vue'), meta: { title: '我的订单', tabbar: true } },
  { path: '/orders/:id', component: () => import('@/views/order/Detail.vue'), meta: { title: '订单详情' } },
  { path: '/user', component: () => import('@/views/user/Index.vue'), meta: { title: '个人中心', tabbar: true } },
  { path: '/product/:id', component: () => import('@/views/product/Detail.vue'), meta: { title: '商品详情' } },
  { path: '/product/:id/exchange', component: () => import('@/views/product/Exchange.vue'), meta: { title: '兑换确认' } },
  { path: '/points/detail', component: () => import('@/views/points/Detail.vue'), meta: { title: '积分明细' } },
  { path: '/address', component: () => import('@/views/address/List.vue'), meta: { title: '收货地址' } },
  { path: '/address/edit', component: () => import('@/views/address/Edit.vue'), meta: { title: '编辑地址' } },
  { path: '/coupon/center', component: () => import('@/views/coupon/Center.vue'), meta: { title: '领券中心' } },
  { path: '/coupon/my', component: () => import('@/views/coupon/My.vue'), meta: { title: '我的优惠券' } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '积分商城'
  if (to.meta.public) return next()
  const userStore = useUserStore()
  if (!userStore.token) {
  showToast({
  message: '请先登录',
  className: 'my-toast'
})
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  next()
})

export default router
