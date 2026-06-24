<template>
  <div class="login-page">
    <div class="logo">
      <div class="icon">★</div>
      <div class="title">金山好物</div>
      <div class="subtitle">签到 · 兑换 · 福利</div>
    </div>

    <van-form @submit="onSubmit" class="form">
      <div v-if="route.query.redirect" class="login-tip">
        <van-icon name="info-o" /> 请先登录
      </div>

      <!-- 短信和密码登录 -->
      <!-- <van-tabs v-model:active="loginType" title-active-color="#ff8c00" color="#ff8c00" background="transparent">
        <van-tab title="短信登录" name="sms"></van-tab>
        <van-tab title="密码登录" name="password"></van-tab>
      </van-tabs> -->
     
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
          v-if="loginType === 'sms'"
          v-model="smsCode"
          center
          label="验证码"
          placeholder="请输入验证码"
          maxlength="6"
          :rules="[{ required: true, message: '请输入验证码' }]"
        >
          <template #button>
            <van-button size="small" type="primary" :disabled="counting" @click="sendCode">
              {{ counting ? `${count}s 后重发` : '获取验证码' }}
            </van-button>
          </template>
        </van-field>
       
        <van-field
          v-if="loginType === 'password'"
          v-model="password"
          type="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
        <van-field
          v-if="loginType === 'password'"
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
      <div class="tip" v-if="loginType === 'sms'">未注册手机号将自动注册，开发环境可使用万能码 <b>123456</b></div>
     

      <div class="submit">
        <van-button block round type="primary" native-type="submit" :loading="loading">
          登 录
        </van-button>
      </div>

      <div class="register-link" @click="goRegister">
        没有账号？<span>立即注册</span>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login, getCaptchaImage, sendSms } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const phone = ref('')
const password = ref('')
const captchaCode = ref('')
const captchaImg = ref('')
const captchaUuid = ref('')
const loading = ref(false)

const loginType = ref('password')
const smsCode = ref('')
const counting = ref(false)
const count = ref(60)

/** 发送短信验证码 */
async function sendCode() {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    showToast({ message: '请输入正确的手机号', className: 'my-toast' })
    return
  }
  try {
    await sendSms(phone.value)
    showToast({ message: '验证码已发送', className: 'my-toast' })
    counting.value = true
    count.value = 60
    const timer = setInterval(() => {
      count.value--
      if (count.value <= 0) { clearInterval(timer); counting.value = false }
    }, 1000)
  } catch (e) {}
}

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

onMounted(() => {
  loadCaptcha()
})

/** 提交登录表单 */
async function onSubmit() {
  loading.value = true
  try {
    const res = await login({
      loginType: loginType.value,
      phone: phone.value,
      password: password.value,
      code: smsCode.value,
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
