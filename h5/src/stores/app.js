import { defineStore } from 'pinia'
import { getH5Config } from '@/api/auth'

/**
 * H5 端全局开关（对应后台「参数设置」中的 h5.login.enabled / h5.register.enabled）
 *
 * 后端接口为最终裁决：即使前端判断有误，登录/注册接口仍会返回关闭提示。
 * 前端只负责隐藏注册入口、展示「系统关闭登录」占位页。
 */
export const useAppStore = defineStore('app', {
  state: () => ({
    /** null 表示尚未拉取过配置 */
    h5Config: null
  }),
  getters: {
    /** 登录开关，默认放开，避免接口异常时误伤正常用户 */
    loginEnabled: state => (state.h5Config ? state.h5Config.loginEnabled : true),
    /** 注册开关，默认放开 */
    registerEnabled: state => (state.h5Config ? state.h5Config.registerEnabled : true)
  },
  actions: {
    /**
     * 拉取开关配置（进程内只请求一次，失败时保留上一次结果）
     * @param {boolean} force 强制刷新
     */
    async loadH5Config(force = false) {
      if (this.h5Config && !force) return this.h5Config
      try {
        const res = await getH5Config()
        this.h5Config = {
          loginEnabled: res.loginEnabled === true,
          registerEnabled: res.registerEnabled === true
        }
      } catch (e) {
        // 失败不缓存，下次导航会重试；getter 默认放开，不阻塞正常使用
        console.error('获取 H5 开关配置失败', e)
      }
      return this.h5Config
    }
  }
})
