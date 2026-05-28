import request from '@/utils/request'

// 列表
export function listCategory(query) {
  return request({ url: '/points/category/list', method: 'get', params: query })
}
// 树
export function treeCategory(query) {
  return request({ url: '/points/category/tree', method: 'get', params: query })
}
// 下拉
export function treeselect(query) {
  return request({ url: '/points/category/treeselect', method: 'get', params: query })
}
// 详情
export function getCategory(id) {
  return request({ url: '/points/category/' + id, method: 'get' })
}
// 新增
export function addCategory(data) {
  return request({ url: '/points/category', method: 'post', data })
}
// 修改
export function updateCategory(data) {
  return request({ url: '/points/category', method: 'put', data })
}
// 删除
export function delCategory(id) {
  return request({ url: '/points/category/' + id, method: 'delete' })
}
