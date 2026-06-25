/**
 * 使用超时控制执行 fetch。
 * @param {string} url 请求地址
 * @param {number} timeoutMs 超时时间（毫秒）
 * @returns {Promise<Response>} fetch 响应
 */
async function fetchWithTimeout(url, timeoutMs) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)
  try {
    return await fetch(url, { method: 'GET', signal: controller.signal, cache: 'no-store' })
  } finally {
    clearTimeout(timer)
  }
}

/**
 * 通过第三方公网 IP 服务获取客户端公网 IP（尽力而为，受 CORS/网络环境影响）。
 * 优先使用 ipify，其次 ipapi、ipinfo（无需 token 的公开接口）。
 * @returns {Promise<string>} IPv4/IPv6 字符串，失败返回空串
 */
export async function getPublicIp() {
  const candidates = [
    async () => {
      const res = await fetchWithTimeout('https://api.ipify.org?format=json', 2500)
      const data = await res.json()
      return data?.ip || ''
    },
    async () => {
      const res = await fetchWithTimeout('https://ipapi.co/json/', 2500)
      const data = await res.json()
      return data?.ip || ''
    },
    async () => {
      const res = await fetchWithTimeout('https://ipinfo.io/json', 2500)
      const data = await res.json()
      return data?.ip || ''
    }
  ]

  for (const getter of candidates) {
    try {
      const ip = await getter()
      if (ip && typeof ip === 'string') return ip.trim()
    } catch (e) {}
  }
  return ''
}

