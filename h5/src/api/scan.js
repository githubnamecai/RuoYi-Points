import request from '@/utils/request'

/**
 * 提交扫码/访问记录。
 * @param {object} data 扫码数据
 * @returns {Promise<any>} 后端响应
 */
export function addScan(data) {
  const base = String(import.meta.env.VITE_APP_BASE_API || '').trim().replace(/\/$/, '')
  const url = base ? `${base}/scan/scan` : '/scan/scan'
  return request({ url, method: 'post', data, loading: false })
}
