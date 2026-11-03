<template>
  <div class="login-page">
    <div class="logo">
      <div class="icon">★</div>
      <div class="title">金山好物</div>
      <div class="subtitle">签到 · 兑换 · 福利</div>
    </div>

    <van-form v-if="configReady && loginEnabled" @submit="onSubmit" class="form">
      <div v-if="route.query.redirect" class="login-tip">
        <van-icon name="info-o" /> 请先登录
      </div>

      <div class="form-title">账号登录</div>

      <van-cell-group inset>
        <van-field
          v-model="phone"
          type="tel"
          label="手机号"
          placeholder="请输入手机号"
          maxlength="11"
          :rules="[{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
        />
        <van-field
          v-model="password"
          type="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
        <van-field
          v-model="captchaCode"
          center
          label="验证码"
          placeholder="请输入验证码"
          maxlength="6"
          :rules="[{ required: true, message: '请输入验证码' }]"
        >
          <template #button>
            <img
              v-if="captchaImg"
              :src="captchaImg"
              class="captcha-img"
              @click="loadCaptcha"
            />
          </template>
        </van-field>
      </van-cell-group>

      <div class="submit">
        <van-button block round type="primary" native-type="submit" :loading="loading">
          登 录
        </van-button>
      </div>

      <div v-if="registerEnabled" class="register-link" @click="goRegister">
        没有账号？<span>立即注册</span>
      </div>
    </van-form>

    <!-- 后台「H5端-登录开关」关闭时展示 -->
    <div v-if="configReady && !loginEnabled" class="closed-box">
      <div class="closed-icon">!</div>
      <div class="closed-title">系统关闭登录</div>
      <div class="closed-tip">当前暂未开放登录，请稍后再试</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login, getCaptchaImage } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()
const phone = ref('')
const password = ref('')
const captchaCode = ref('')
const captchaImg = ref('')
const captchaUuid = ref('')
const loading = ref(false)
/** 开关配置是否已拉取完成（避免先渲染表单再切换的闪烁） */
const configReady = ref(false)
/** 登录开关：true 正常登录，false 系统关闭登录 */
const loginEnabled = computed(() => appStore.loginEnabled)
/** 注册开关：false 时隐藏注册入口 */
const registerEnabled = computed(() => appStore.registerEnabled)

/** 加载验证码 */
async function loadCaptcha() {
  try {
    const res = await getCaptchaImage()
    if (res.captchaEnabled) {
      captchaImg.value = 'data:image/jpg;base64,' + res.img
      captchaUuid.value = res.uuid
    } else {
      // 验证码未启用，隐藏验证码输入
      captchaImg.value = ''
      captchaUuid.value = ''
    }
  } catch (e) {
    console.error('获取验证码失败', e)
  }
}

onMounted(async () => {
  await appStore.loadH5Config()
  configReady.value = true
  // 登录已关闭时不再请求验证码
  if (loginEnabled.value) loadCaptcha()
})

/** 提交登录表单 */
async function onSubmit() {
  if (!loginEnabled.value) {
    showToast({ message: '系统关闭登录', className: 'my-toast' })
    return
  }
  loading.value = true
  try {
    const res = await login({
      phone: phone.value,
      password: password.value,
      captchaCode: captchaCode.value,
      uuid: captchaUuid.value
    })
    userStore.setToken(res.token)
    await userStore.fetchUserInfo()
    showToast({ message: '登录成功', className: 'my-toast' })
    router.replace(route.query.redirect || '/home')
  } catch (e) {
    // 验证码错误或过期时刷新
    loadCaptcha()
    captchaCode.value = ''
  } finally {
    loading.value = false
  }
}

/** 跳转注册页 */
function goRegister() {
  if (!registerEnabled.value) return
  router.push({ path: '/register', query: route.query.redirect ? { redirect: route.query.redirect } : {} })
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #edf4ff 0%, #f7fbff 42%, #ffffff 100%);
  padding: 56px 12px 24px;
}

.logo {
  text-align: center;
  margin-bottom: 28px;
}

.logo .icon {
  width: 68px; height: 68px; line-height: 68px;
  margin: 0 auto 12px;
  border-radius: 20px;
  background: linear-gradient(135deg, #123f96, #1f73ef 70%, #58b9ff);
  color: #fff; font-size: 32px; font-weight: 700;
  box-shadow: 0 16px 30px rgba(13, 91, 215, 0.2);
}
.logo .title { font-size: 24px; font-weight: 700; color: #1a2640; }
.logo .subtitle { color: #7f8ba0; font-size: 12px; margin-top: 6px; }

.form {
  padding: 20px 6px 24px;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.84));
  box-shadow: 0 22px 42px rgba(22, 53, 110, 0.08);
  border: 1px solid rgba(124, 147, 187, 0.12);
}

.form-title {
  font-size: 16px;
  font-weight: 700;
  color: #1a2640;
  text-align: center;
  margin-bottom: 20px;
}

.login-tip {
  background: rgba(13, 91, 215, 0.08);
  color: #0d5bd7;
  font-size: 13px;
  text-align: center;
  padding: 10px 0;
  border-radius: 14px;
  margin: 0 16px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.captcha-img {
  height: 42px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid rgba(124, 147, 187, 0.16);
}
.submit { padding: 24px 16px 12px; }
.tip { padding: 8px 24px; font-size: 12px; color: #8a95a9; }
.tip b { color: #0d5bd7; }
.register-link {
  text-align: center;
  font-size: 14px;
  color: #8a95a9;
  padding-top: 4px;
  span {
    color: #0d5bd7;
    font-weight: 600;
  }
}

/* 系统关闭登录 */
.closed-box {
  margin: 8px 18px 0;
  padding: 40px 24px;
  border-radius: 28px;
  text-align: center;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(249, 251, 255, 0.84));
  box-shadow: 0 22px 42px rgba(22, 53, 110, 0.08);
  border: 1px solid rgba(124, 147, 187, 0.12);
}
.closed-icon {
  width: 64px; height: 64px; line-height: 64px;
  margin: 0 auto 18px;
  border-radius: 50%;
  background: rgba(13, 91, 215, 0.08);
  color: #0d5bd7;
  font-size: 32px;
  font-weight: 700;
}
.closed-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a2640;
}
.closed-tip {
  margin-top: 8px;
  font-size: 13px;
  color: #8a95a9;
}

.form :deep(.van-cell-group) {
  background: transparent;
}

.form :deep(.van-field) {
  margin: 0 12px 12px;
  padding: 14px 14px;
  border-radius: 18px;
  background: rgba(242, 247, 255, 0.9);
}

.form :deep(.van-field__label) {
  width: 60px;
  color: #516078 !important;
}

.form :deep(.van-field__control) {
  color: #1a2640;
}

.submit :deep(.van-button) {
  height: 46px;
}
</style>
