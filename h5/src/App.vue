<template>
  <router-view v-slot="{ Component }">
    <transition name="fade">
      <keep-alive :include="cachedViews">
        <component :is="Component" />
      </keep-alive>
    </transition>
  </router-view>
  <van-tabbar v-if="showTabbar" v-model="active" route active-color="var(--primary-color)" inactive-color="var(--text-tertiary)" class="app-tabbar">
    <van-tabbar-item icon="shop-o" to="/home">商城</van-tabbar-item>
    <van-tabbar-item icon="calendar-o" to="/sign">签到</van-tabbar-item>
    <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
    <van-tabbar-item icon="user-o" to="/user">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = ref(0)
const showTabbar = computed(() => route.meta && route.meta.tabbar)

// 需要缓存的页面组件 name 列表
const cachedViews = ['Home']
</script>

<style>
.fade-enter-active, .fade-leave-active { transition: opacity .15s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.app-tabbar {
  background: rgba(245, 249, 255, 0.84) !important;
  backdrop-filter: blur(18px);
  box-shadow: 0 -10px 30px rgba(15, 53, 110, 0.1);
}

.app-tabbar::after {
  border-top: 1px solid rgba(125, 145, 182, 0.18) !important;
}

.app-tabbar .van-tabbar-item {
  color: var(--text-tertiary);
}

.app-tabbar .van-tabbar-item--active {
  color: var(--primary-color);
}
</style>
