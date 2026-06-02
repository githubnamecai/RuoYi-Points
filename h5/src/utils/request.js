import axios from 'axios'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getToken, removeToken } from './auth'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.PROD ? (import.meta.env.VITE_API_BASE || '') : '',
  timeout: 15000
})

let loadingCount = 0

service.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    // 同时设置两个 Header，方便后端拦截器兼容
    config.headers.Authorization = 'Bearer ' + token
    config.headers['h5-token'] = token
  }
  if (config.loading !== false) {
    if (loadingCount === 0) showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
    loadingCount++
  }
  return config
}, err => Promise.reject(err))

service.interceptors.response.use(res => {
  if (res.config.loading !== false) {
    loadingCount--
    if (loadingCount <= 0) { loadingCount = 0; closeToast() }
  }
  const data = res.data
  // RuoYi 标准响应 { code, msg, data, rows, total }
  if (data.code === 200 || data.code === undefined) {
    return data
  }
  if (data.code === 401) {
    removeToken()
      showToast({
      message: data.msg || '登录已失效',
      className: 'my-toast'
})
    router.replace('/login')
    return Promise.reject(data)
  }
  showToast({
  message: data.msg || '请求异常',
  className: 'my-toast'
})
  return Promise.reject(data)
}, err => {
  loadingCount = 0
  closeToast()
  const status = err.response ? err.response.status : 0
  if (status === 401) {
    removeToken()
    router.replace('/login')
  }
  showToast({
  message: err.response?.data?.msg || err.message || '网络错误',
  className: 'my-toast'
})
  return Promise.reject(err)
})

export default service
