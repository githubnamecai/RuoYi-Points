<template>
  <div class="login-page">
    <div class="logo">
      <div class="icon">★</div>
      <div class="title">积分商城</div>
      <div class="subtitle">签到 · 兑换 · 福利</div>
    </div>

    <van-form @submit="onSubmit" class="form">
      <van-tabs v-model:active="loginType" title-active-color="#ff8c00" color="#ff8c00" background="transparent">
        <van-tab title="短信登录" name="sms"></van-tab>
        <van-tab title="密码登录" name="password"></van-tab>
      </van-tabs>

      <van-cell-group inset style="margin-top: 20px;">
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
          v-model="code"
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
      </van-cell-group>
      <div class="tip" v-if="loginType === 'sms'">未注册手机号将自动注册，开发环境可使用万能码 <b>123456</b></div>
      <div class="submit">
        <van-button block round type="primary" native-type="submit" :loading="loading">
          {{ loginType === 'sms' ? '登录 / 注册' : '登 录' }}
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { sendSms, login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const phone = ref('')
const code = ref('')
const password = ref('')
const loginType = ref('sms')
const loading = ref(false)
const counting = ref(false)
const count = ref(60)

async function sendCode() {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    showToast({
    message: '请输入正确的手机号',
    className: 'my-toast'
})
    return
  }
  try {
    await sendSms(phone.value)
    showToast('验证码已发送')
//     showToast({
//     message: '验证码已发送',
//     className: 'my-toast'
// })
    counting.value = true
    count.value = 60
    const timer = setInterval(() => {
      count.value--
      if (count.value <= 0) { clearInterval(timer); counting.value = false }
    }, 1000)
  } catch (e) {}
}

async function onSubmit() {
  loading.value = true
  try {
    const res = await login({
      loginType: loginType.value,
      phone: phone.value,
      code: code.value,
      password: password.value
    })
    userStore.setToken(res.token)
    await userStore.fetchUserInfo()
    showToast({
    message: '登录成功',
    className: 'my-toast'
})
    router.replace(route.query.redirect || '/home')
  } finally { loading.value = false }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #fff5e6 0%, #fff 35%);
  padding-top: 60px;
}
.logo { text-align: center; margin-bottom: 40px; }
.logo .icon {
  width: 64px; height: 64px; line-height: 64px;
  margin: 0 auto 12px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ff8c00, #ffb84d);
  color: #fff; font-size: 32px; font-weight: 700;
  box-shadow: 0 6px 16px rgba(255, 140, 0, 0.35);
}
.logo .title { font-size: 22px; font-weight: 700; color: #333; }
.logo .subtitle { color: #999; font-size: 12px; margin-top: 4px; }
.form { padding: 0 8px; }
.tip { padding: 8px 24px; font-size: 12px; color: #999; }
.tip b { color: #ff8c00; }
.submit { padding: 24px 16px; }
</style>
