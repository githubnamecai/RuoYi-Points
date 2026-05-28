import request from '@/utils/request'

export function getUserInfo() {
  return request({ url: '/h5-api/user/info', method: 'get', loading: false })
}
export function updateProfile(data) {
  return request({ url: '/h5-api/user/profile', method: 'put', data })
}
export function listPointsDetail(params) {
  return request({ url: '/h5-api/user/points/detail', method: 'get', params })
}
export function listOrders(params) {
  return request({ url: '/h5-api/user/orders', method: 'get', params })
}
export function orderDetail(id) {
  return request({ url: '/h5-api/user/orders/' + id, method: 'get' })
}
export function confirmReceipt(id) {
  return request({ url: '/h5-api/user/orders/' + id + '/confirm', method: 'put' })
}
export function listAddresses() {
  return request({ url: '/h5-api/user/addresses', method: 'get' })
}
export function addAddress(data) {
  return request({ url: '/h5-api/user/addresses', method: 'post', data })
}
export function updateAddress(data) {
  return request({ url: '/h5-api/user/addresses', method: 'put', data })
}
export function deleteAddress(id) {
  return request({ url: '/h5-api/user/addresses/' + id, method: 'delete' })
}
export function setDefaultAddress(id) {
  return request({ url: '/h5-api/user/addresses/' + id + '/default', method: 'put' })
}
export function logout() {
  // 当前无对应后端接口，仅清本地
  return Promise.resolve({})
}
