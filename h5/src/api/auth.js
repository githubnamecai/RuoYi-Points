import request from '@/utils/request'

export function sendSms(phone) {
  return request({ url: '/h5-api/auth/sms', method: 'post', params: { phone } })
}
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
