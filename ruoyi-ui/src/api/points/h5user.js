import request from '@/utils/request'

export function listH5User(query) {
  return request({ url: '/points/h5user/list', method: 'get', params: query })
}
export function getH5User(id) {
  return request({ url: '/points/h5user/' + id, method: 'get' })
}
export function changeStatus(id, status) {
  return request({ url: `/points/h5user/status/${id}/${status}`, method: 'put' })
}
export function adjustPoints(data) {
  return request({ url: '/points/h5user/adjust', method: 'post', data })
}
