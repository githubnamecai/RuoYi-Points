import request from '@/utils/request'

export function listCoupon(query) {
  return request({ url: '/points/coupon/list', method: 'get', params: query })
}
export function getCoupon(couponId) {
  return request({ url: '/points/coupon/' + couponId, method: 'get' })
}
export function addCoupon(data) {
  return request({ url: '/points/coupon', method: 'post', data })
}
export function updateCoupon(data) {
  return request({ url: '/points/coupon', method: 'put', data })
}
export function delCoupon(couponId) {
  return request({ url: '/points/coupon/' + couponId, method: 'delete' })
}
export function issueCoupon(couponId, userIds) {
  return request({ url: '/points/coupon/issue/' + couponId, method: 'post', data: userIds })
}
export function listRecord(query) {
  return request({ url: '/points/coupon/record/list', method: 'get', params: query })
}