<template>
  <router-view v-slot="{ Component }">
    <transition name="fade">
      <component :is="Component" />
    </transition>
  </router-view>
  <van-tabbar v-if="showTabbar" v-model="active" route active-color="#ff8c00">
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
</script>

<style>
.fade-enter-active, .fade-leave-active { transition: opacity .15s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
