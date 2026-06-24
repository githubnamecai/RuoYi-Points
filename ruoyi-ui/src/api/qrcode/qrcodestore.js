import request from '@/utils/request'

// 查询二维码点位信息列表
export function listQrcodestore(query) {
  return request({
    url: '/qrcode/qrcodestore/list',
    method: 'get',
    params: query
  })
}

// 查询二维码点位信息详细
export function getQrcodestore(id) {
  return request({
    url: '/qrcode/qrcodestore/' + id,
    method: 'get'
  })
}

// 新增二维码点位信息
export function addQrcodestore(data) {
  return request({
    url: '/qrcode/qrcodestore',
    method: 'post',
    data: data
  })
}

// 修改二维码点位信息
export function updateQrcodestore(data) {
  return request({
    url: '/qrcode/qrcodestore',
    method: 'put',
    data: data
  })
}

// 删除二维码点位信息
export function delQrcodestore(id) {
  return request({
    url: '/qrcode/qrcodestore/' + id,
    method: 'delete'
  })
}
