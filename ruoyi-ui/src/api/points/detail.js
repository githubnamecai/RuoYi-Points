import request from '@/utils/request'

export function listDetail(query) {
  return request({ url: '/points/detail/list', method: 'get', params: query })
}
export function statPoints() {
  return request({ url: '/points/detail/stat', method: 'get' })
}
