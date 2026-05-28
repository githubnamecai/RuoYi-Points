import request from '@/utils/request'

export function listGoods(params) {
  return request({ url: '/h5-api/goods/list', method: 'get', params })
}
export function goodsDetail(id) {
  return request({ url: '/h5-api/goods/' + id, method: 'get' })
}
export function listCategories() {
  return request({ url: '/h5-api/goods/categories', method: 'get', loading: false })
}
export function exchange(data) {
  return request({ url: '/h5-api/exchange', method: 'post', data })
}
