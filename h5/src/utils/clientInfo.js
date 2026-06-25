/**
 * 格式化为 yyyy-MM-dd（与后端 @JsonFormat(pattern="yyyy-MM-dd") 对齐）。
 * @param {Date} date 日期对象
 * @returns {string} 格式化后的日期字符串
 */
export function formatDateYYYYMMDD(date) {
  const d = date instanceof Date ? date : new Date(date)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/**
 * 基于 UA 字符串粗略识别浏览器名称。
 * @param {string} ua userAgent
 * @returns {string} 浏览器名称
 */
export function parseBrowserName(ua) {
  const u = (ua || '').toLowerCase()
  if (u.includes('micromessenger')) return 'WeChat'
  if (u.includes('dingtalk')) return 'DingTalk'
  if (u.includes('qqbrowser')) return 'QQBrowser'
  if (u.includes('edg/')) return 'Edge'
  if (u.includes('firefox/')) return 'Firefox'
  if (u.includes('chrome/') && !u.includes('edg/')) return 'Chrome'
  if (u.includes('safari/') && !u.includes('chrome/')) return 'Safari'
  return 'Unknown'
}

/**
 * 基于 UA 字符串粗略识别系统版本信息。
 * @param {string} ua userAgent
 * @returns {string} 系统版本/平台信息
 */
export function parseOsVersion(ua) {
  const raw = ua || ''
  const u = raw.toLowerCase()
  const ios = raw.match(/OS (\d+[_\.\d]*) like Mac OS X/i)
  if (ios) return `iOS ${ios[1].replace(/_/g, '.')}`
  const android = raw.match(/Android (\d+(?:\.\d+)?)/i)
  if (android) return `Android ${android[1]}`
  const win = raw.match(/Windows NT (\d+(?:\.\d+)?)/i)
  if (win) return `Windows NT ${win[1]}`
  const mac = raw.match(/Mac OS X (\d+[_\.\d]*)/i)
  if (mac) return `macOS ${mac[1].replace(/_/g, '.')}`
  if (u.includes('linux')) return 'Linux'
  return 'Unknown'
}

/**
 * 基于 UA 字符串粗略识别设备型号（无法保证完全准确）。
 * @param {string} ua userAgent
 * @returns {string} 设备型号
 */
export function parseDeviceModel(ua) {
  const raw = ua || ''
  if (/iPhone/i.test(raw)) return 'iPhone'
  if (/iPad/i.test(raw)) return 'iPad'
  const androidModel = raw.match(/Android [^;]+;\s?([^;]+)\s?Build/i)
  if (androidModel && androidModel[1]) return androidModel[1].trim()
  if (/Android/i.test(raw)) return 'Android'
  if (/Windows/i.test(raw)) return 'Windows PC'
  if (/Macintosh/i.test(raw)) return 'Mac'
  return 'Unknown'
}

/**
 * 采集客户端信息（尽力而为，不依赖外部第三方服务）。
 * @returns {Promise<{deviceModel: string, osVersion: string, browserName: string, userAgent: string}>} 客户端信息
 */
export async function collectClientInfo() {
  const userAgent = navigator.userAgent || ''

  let deviceModel = parseDeviceModel(userAgent)
  let osVersion = parseOsVersion(userAgent)
  let browserName = parseBrowserName(userAgent)

  const uaData = navigator.userAgentData
  if (uaData && typeof uaData.getHighEntropyValues === 'function') {
    try {
      const hints = await uaData.getHighEntropyValues(['model', 'platform', 'platformVersion', 'uaFullVersion'])
      if (hints?.model) deviceModel = hints.model
      const platform = hints?.platform || uaData.platform
      const platformVersion = hints?.platformVersion
      if (platform && platformVersion) osVersion = `${platform} ${platformVersion}`
      if (platform && !platformVersion) osVersion = platform
      if (hints?.uaFullVersion && browserName !== 'Unknown') browserName = browserName
    } catch (e) {}
  }

  return { deviceModel, osVersion, browserName, userAgent }
}

