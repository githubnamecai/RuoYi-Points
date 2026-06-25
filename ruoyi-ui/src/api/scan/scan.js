import request from '@/utils/request'

// 查询扫码统计列表
export function listScan(query) {
  return request({
    url: '/scan/scan/list',
    method: 'get',
    params: query
  })
}

// 查询扫码统计详细
export function getScan(id) {
  return request({
    url: '/scan/scan/' + id,
    method: 'get'
  })
}

// 新增扫码统计
export function addScan(data) {
  return request({
    url: '/scan/scan',
    method: 'post',
    data: data
  })
}

// 修改扫码统计
export function updateScan(data) {
  return request({
    url: '/scan/scan',
    method: 'put',
    data: data
  })
}

// 删除扫码统计
export function delScan(id) {
  return request({
    url: '/scan/scan/' + id,
    method: 'delete'
  })
}
