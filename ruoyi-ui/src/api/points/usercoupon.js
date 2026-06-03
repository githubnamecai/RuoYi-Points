import request from '@/utils/request'

export function listUserCoupon(query) {
  return request({ url: '/points/usercoupon/list', method: 'get', params: query })
}
export function getUserCoupon(userCouponId) {
  return request({ url: '/points/usercoupon/' + userCouponId, method: 'get' })
}
export function addUserCoupon(data) {
  return request({ url: '/points/usercoupon', method: 'post', data })
}
export function updateUserCoupon(data) {
  return request({ url: '/points/usercoupon', method: 'put', data })
}
export function receiveUserCoupon(userId, couponId) {
  return request({ url: '/points/usercoupon/receive/' + couponId, method: 'post', data: { userId } })
}
export function setstatusUserCoupon(userCouponId,status, orderId) {
  return request({ url: '/points/usercoupon/setstatus/' + userCouponId, method: 'post', data: { status, orderId } })
}