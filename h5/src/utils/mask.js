/**
 * 手机号脱敏：保留前3后4，中间4位用 * 替换。
 * @param {string} phone 原始手机号
 * @returns {string} 脱敏后的手机号
 */
export function maskPhone(phone) {
  if (!phone) return ''
  const value = String(phone).trim()
  if (!/^\d{11}$/.test(value)) return value
  return value.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 身份证号脱敏：保留前4后4，中间用 * 替换（兼容 18 位含 X）。
 * @param {string} idNumber 原始身份证号
 * @returns {string} 脱敏后的身份证号
 */
export function maskIdNumber(idNumber) {
  if (!idNumber) return ''
  const value = String(idNumber).trim()
  if (value.length < 8) return value
  return value.replace(/(\w{4})\w+(\w{4})$/, '$1**********$2')
}

/**
 * 密码脱敏：全部用 * 替换（长度与原值一致）。
 * @param {string} password 原始密码
 * @returns {string} 脱敏后的密码
 */
export function maskPassword(password) {
  if (!password) return ''
  const value = String(password)
  return '*'.repeat(value.length)
}

