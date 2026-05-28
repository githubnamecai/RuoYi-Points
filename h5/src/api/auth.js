import request from '@/utils/request'

export function sendSms(phone) {
  return request({ url: '/h5-api/auth/sms', method: 'post', params: { phone } })
}
export function login(phone, code) {
  return request({ url: '/h5-api/auth/login', method: 'post', data: { phone, code } })
}
