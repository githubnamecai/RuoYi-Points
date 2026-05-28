import request from '@/utils/request'

export function listOrder(query) {
  return request({ url: '/points/order/list', method: 'get', params: query })
}
export function getOrder(id) {
  return request({ url: '/points/order/' + id, method: 'get' })
}
export function shipOrder(data) {
  return request({ url: '/points/order/ship', method: 'put', data })
}
export function closeOrder(id, reason) {
  return request({ url: `/points/order/close/${id}`, method: 'put', params: { reason } })
}
