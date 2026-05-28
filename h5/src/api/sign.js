import request from '@/utils/request'

export function signInfo() {
  return request({ url: '/h5-api/sign/info', method: 'get' })
}
export function doSign() {
  return request({ url: '/h5-api/sign', method: 'post' })
}
export function repairSign(date) {
  return request({ url: '/h5-api/sign/repair', method: 'post', params: { date } })
}
