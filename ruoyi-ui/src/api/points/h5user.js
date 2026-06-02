import request from '@/utils/request'

export function listH5User(query) {
  return request({ url: '/points/h5user/list', method: 'get', params: query })
}
export function addH5User(data) {
  return request({ url: '/points/h5user', method: 'post', data })
}
export function updateH5User(data) {
  return request({ url: '/points/h5user', method: 'put', data })
}

export function resetPwdH5User(data) {
  return request({ url: '/points/h5user/pwd', method: 'put', data })
}
export function delH5User(id) {
  return request({ url: '/points/h5user/' + id, method: 'delete' })
}
export function changeStatus(id, status) {
  return request({ url: `/points/h5user/status/${id}/${status}`, method: 'put' })
}
export function adjustPoints(data) {
  return request({ url: '/points/h5user/adjust', method: 'post', data })
}
