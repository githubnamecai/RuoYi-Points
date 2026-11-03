import request from '@/utils/request'

export function login(data) {
  return request({ url: '/h5-api/auth/login', method: 'post', data })
}

/** 获取图形验证码 */
export function getCaptchaImage() {
  return request({ url: '/captchaImage', method: 'get', loading: false })
}

/** 注册 */
export function register(data) {
  return request({ url: '/h5-api/auth/register', method: 'post', data })
}

/** 获取 H5 端开关配置（登录开关 / 注册开关），无需登录 */
export function getH5Config() {
  return request({ url: '/h5-api/auth/config', method: 'get', loading: false, silent: true })
}
