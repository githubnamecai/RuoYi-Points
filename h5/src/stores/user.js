import { defineStore } from 'pinia'
import { getUserInfo, logout as logoutApi } from '@/api/user'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null
  }),
  getters: {
    points: state => state.userInfo ? state.userInfo.pointsBalance : 0,
    nickname: state => state.userInfo ? state.userInfo.nickname : '游客',
    avatar: state => state.userInfo ? state.userInfo.avatar : ''
  },
  actions: {
    setToken(token) {
      this.token = token
      setToken(token)
    },
    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
      return this.userInfo
    },
    async logout() {
      try { await logoutApi() } catch (e) {}
      this.token = ''
      this.userInfo = null
      removeToken()
    }
  }
})
