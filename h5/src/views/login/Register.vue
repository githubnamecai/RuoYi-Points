<template>
  <div class="register-page">
    <div class="logo">
      <div class="icon">★</div>
      <div class="title">注册账号</div>
      <div class="subtitle">注册后即可享受积分福利</div>
    </div>

    <van-form @submit="onSubmit" class="form">
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
          placeholder="至少8位，含大小写字母和数字"
          :rules="[
            { required: true, message: '请输入密码' },
            { validator: val => val.length >= 8, message: '密码至少8位' },
            { validator: val => /[a-z]/.test(val), message: '需包含小写字母' },
            { validator: val => /[A-Z]/.test(val), message: '需包含大写字母' },
            { validator: val => /[0-9]/.test(val), message: '需包含数字' }
          ]"
        />
        <van-field
          v-model="confirmPwd"
          type="password"
          label="确认密码"
          placeholder="请再次输入密码"
          :rules="[
            { required: true, message: '请确认密码' },
            { validator: val => val === password, message: '两次密码不一致' }
          ]"
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
          注 册
        </van-button>
      </div>

      <div class="login-link" @click="goLogin">
        已有账号？<span>去登录</span>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register, getCaptchaImage } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const phone = ref('')
const password = ref('')
const confirmPwd = ref('')
const captchaCode = ref('')
const captchaImg = ref('')
const captchaUuid = ref('')
const loading = ref(false)

/** 加载验证码 */
async function loadCaptcha() {
  try {
    const res = await getCaptchaImage()
    if (res.captchaEnabled) {
      captchaImg.value = 'data:image/jpg;base64,' + res.img
      captchaUuid.value = res.uuid
    } else {
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

async function onSubmit() {
  if (password.value !== confirmPwd.value) {
    showToast({ message: '两次密码不一致', className: 'my-toast' })
    return
  }
  loading.value = true
  try {
    await register({
      phone: phone.value,
      password: password.value,
      captchaCode: captchaCode.value,
      uuid: captchaUuid.value
    })
    showToast({ message: '注册成功，请登录', className: 'my-toast' })
    router.replace({ path: '/login', query: route.query.redirect ? { redirect: route.query.redirect } : {} })
  } catch (e) {
    loadCaptcha()
    captchaCode.value = ''
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.replace({ path: '/login', query: route.query.redirect ? { redirect: route.query.redirect } : {} })
}
</script>

<style scoped lang="scss">
.register-page {
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
.captcha-img {
  height: 32px;
  border-radius: 4px;
  cursor: pointer;
}
.submit { padding: 24px 16px 12px; }
.login-link {
  text-align: center;
  font-size: 14px;
  color: #999;
  padding-top: 4px;
  span {
    color: #ff8c00;
    font-weight: 500;
  }
}
</style>
