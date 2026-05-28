import request from '@/utils/request'

export function listGoods(query) {
  return request({ url: '/points/goods/list', method: 'get', params: query })
}
export function getGoods(id) {
  return request({ url: '/points/goods/' + id, method: 'get' })
}
export function addGoods(data) {
  return request({ url: '/points/goods', method: 'post', data })
}
export function updateGoods(data) {
  return request({ url: '/points/goods', method: 'put', data })
}
export function delGoods(ids) {
  return request({ url: '/points/goods/' + ids, method: 'delete' })
}
export function changeGoodsStatus(id, status) {
  return request({ url: `/points/goods/status/${id}/${status}`, method: 'put' })
}
