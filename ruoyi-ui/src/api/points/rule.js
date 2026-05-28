import request from '@/utils/request'

export function listRule(query) {
  return request({ url: '/points/rule/list', method: 'get', params: query })
}
export function getRule(id) {
  return request({ url: '/points/rule/' + id, method: 'get' })
}
export function addRule(data) {
  return request({ url: '/points/rule', method: 'post', data })
}
export function updateRule(data) {
  return request({ url: '/points/rule', method: 'put', data })
}
export function delRule(ids) {
  return request({ url: '/points/rule/' + ids, method: 'delete' })
}
export function getSignConfig() {
  return request({ url: '/points/rule/sign/config', method: 'get' })
}
export function updateSignConfig(data) {
  return request({ url: '/points/rule/sign/config', method: 'put', data })
}
