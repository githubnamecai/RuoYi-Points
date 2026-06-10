import request from '@/utils/request'

export function listCoupons() {
  return request({ url: '/h5-api/coupon/center', method: 'get' })
}
export function receiveCoupon(couponId) {
  return request({ url: '/h5-api/coupon/receive', method: 'post', data: { couponId }, headers: { 'Content-Type': 'application/x-www-form-urlencoded' } })
}
export function getMyCoupons(status) {
  return request({ url: '/h5-api/coupon/my', method: 'get', params: { status } })
}
export function getAvailableCoupons(goodsId, totalAmount) {
  return request({ url: '/h5-api/coupon/available', method: 'get', params: { goodsId, totalPoints: totalAmount } })
}