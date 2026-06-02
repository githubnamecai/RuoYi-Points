<template>
  <div class="user-page">
    <!-- 头部信息卡 -->
    <div class="header">
      <div class="profile" @click="goEditProfile">
        <van-image round width="64" height="64" :src="avatar" fit="cover" error-icon="user-circle-o" />
        <div class="info">
          <div class="nickname">
            {{ nickname }}
            <van-icon name="edit" size="14" class="edit-icon" />
            <span class="edit-profile-text">个人资料</span>   <!-- 新增文字 -->
          </div>
          <div class="phone">{{ maskedPhone }}</div>
        </div>
      </div>
      <div @click="goEditPassword" class="change-pwd-btn">     <!-- 新增 class 便于样式 -->
        <van-icon name="lock" size="14" />                     <!-- 新增图标 -->
        <span>修改密码</span>
      </div>
      <div class="points-card" @click="goPointsDetail">
        <div class="points-left">
          <div class="label">我的积分</div>

          <div class="value">{{ points }}</div>
        </div>
        <div class="points-right">
          <van-icon name="balance-o" size="20" />
          <span>明细</span>
          <van-icon name="arrow" />
        </div>
      </div>

      <div class="stats-row">
        <div class="stat" @click="goSign">
          <div class="stat-value">{{ userInfo.continuousDays || 0 }}</div>
          <div class="stat-label">连续签到</div>
        </div>
        <div class="divider" />
        <div class="stat" @click="goOrders('1')">
          <div class="stat-value">{{ stats.toShip }}</div>
          <div class="stat-label">待发货</div>
        </div>
        <div class="divider" />
        <div class="stat" @click="goOrders('2')">
          <div class="stat-value">{{ stats.shipping }}</div>
          <div class="stat-label">已发货</div>
        </div>
      </div>
    </div>

    <!-- 我的订单 -->
    <van-cell-group inset class="menu-group">
      <van-cell title="我的订单" is-link value="查看全部" @click="goOrders('')" />
      <div class="order-tabs">
        <div class="ot-item" @click="goOrders('1')">
          <van-icon name="balance-list-o" size="24" />
          <span>待发货</span>
        </div>
        <div class="ot-item" @click="goOrders('2')">
          <van-icon name="logistics" size="24" />
          <span>已发货</span>
        </div>
        <div class="ot-item" @click="goOrders('3')">
          <van-icon name="success" size="24" />
          <span>已完成</span>
        </div>
        <div class="ot-item" @click="goOrders('4')">
          <van-icon name="close" size="24" />
          <span>已关闭</span>
        </div>
      </div>
    </van-cell-group>

    <!-- 功能菜单 -->
    <van-cell-group inset class="menu-group">
      <van-cell title="积分明细" icon="balance-o" is-link @click="goPointsDetail" />
      <van-cell title="每日签到" icon="calendar-o" is-link @click="goSign" />
      <van-cell title="收货地址" icon="location-o" is-link @click="goAddress" />
      <van-cell title="兑换规则" icon="description" is-link @click="showRule = true" />
      <van-cell title="关于我们" icon="info-o" is-link @click="showAbout = true" />
    </van-cell-group>

    <div class="logout-wrap">
      <van-button block round type="danger" @click="doLogout">退出登录</van-button>
    </div>

    <!-- 资料编辑 -->
    <van-dialog v-model:show="showProfile" title="修改资料" show-cancel-button @confirm="saveProfile">
      <div class="profile-form">
        <van-field v-model="form.nickname" label="昵称" placeholder="请输入昵称" maxlength="20" />
         <van-field v-model="form.name" label="姓名" placeholder="请输入姓名" maxlength="20" />
        <van-field v-model="form.avatar" label="头像URL" placeholder="可选" />
      </div>
    </van-dialog>

    <!-- 修改密码 -->
    <van-dialog v-model:show="showPassword" title="修改密码" show-cancel-button @confirm="savePasswordProfile">
      <div class="profile-form">
        <van-field v-model="form.password" label="密码" placeholder="请输入密码" maxlength="20" />
      </div>
    </van-dialog>

    <!-- 规则 -->
    <van-popup v-model:show="showRule" position="bottom" round closeable style="height: 60%">
      <div class="popup-content">
        <h3>积分兑换规则</h3>
        <p>1. 每日签到可获得积分奖励，连续签到有额外奖励；</p>
        <p>2. 实物商品需选择收货地址，下单后由平台发货；</p>
        <p>3. 虚拟商品兑换成功后立即到账，自动完成订单；</p>
        <p>4. 实物订单发货后请及时确认收货；</p>
        <p>5. 订单关闭后扣除的积分将原路退回。</p>
      </div>
    </van-popup>

    <!-- 关于 -->
    <van-popup v-model:show="showAbout" position="bottom" round closeable style="height: 40%">
      <div class="popup-content">
        <h3>关于权益平台</h3>
        <p>本平台基于 RuoYi-Vue 框架二次开发，提供积分管理、商品兑换、每日签到等功能。</p>
        <p style="color: #999; font-size: 12px; margin-top: 24px;">Version 1.0.0</p>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
// import 'vant/es/toast/style'  // 加上这一行
import { useUserStore } from '@/stores/user'
import { updateProfile, listOrders,updateResetpwd } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const showProfile = ref(false)
const showPassword = ref(false)

const showRule = ref(false)
const showAbout = ref(false)
const stats = reactive({ toShip: 0, shipping: 0 })
const form = reactive({ nickname: '', avatar: '' })

const userInfo = computed(() => userStore.userInfo || {})
const nickname = computed(() => userStore.nickname || '未设置昵称')
const avatar = computed(() => userStore.avatar || '')
const points = computed(() => userStore.points)
const maskedPhone = computed(() => {
  const p = userInfo.value.phone || ''
  if (p.length === 11) return p.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  return p
})

async function loadStats() {
  try {
    const [toShipRes, shippingRes] = await Promise.all([
      listOrders({ status: '1', pageNum: 1, pageSize: 1 }),
      listOrders({ status: '2', pageNum: 1, pageSize: 1 })
    ])
    stats.toShip = toShipRes.total || 0
    stats.shipping = shippingRes.total || 0
  } catch (e) {
    // 静默处理
  }
}

async function refresh() {
  await userStore.fetchUserInfo().catch(() => { })
  loadStats()
}

function goEditProfile() {
  form.nickname = userInfo.value.nickname || ''
  form.avatar = userInfo.value.avatar || ''
  form.name = userInfo.value.name || ''
  showProfile.value = true
}

function goEditPassword() {
  form.nickname = userInfo.value.nickname || ''
  form.avatar = userInfo.value.avatar || ''
  form.name = userInfo.value.name || ''
  showPassword.value = true
}


async function saveProfile() {
  if (!form.nickname || !form.nickname.trim()) {
  showToast({
  message: '昵称不能为空',
  className: 'my-toast'
})
    return
  }
  await updateProfile({ nickname: form.nickname.trim(),name:form.name.trim(), avatar: form.avatar })
  showToast({
  message: '保存成功',
  className: 'my-toast'
})
  await userStore.fetchUserInfo()
}

async function savePasswordProfile() {
  if (!form.password || !form.password.trim()) {
  showToast({
  message: '密码不能为空',
  className: 'my-toast'
})
    return
  }
  await updateResetpwd({ password: form.password.trim() })
  showToast({
  message: '保存成功',
  className: 'my-toast'
})
  await userStore.fetchUserInfo()
}

function goPointsDetail() { router.push('/points/detail') }
function goSign() { router.push('/sign') }
function goAddress() { router.push('/address') }
function goOrders(status) {
  router.push({ path: '/orders', query: status ? { status } : {} })
}

async function doLogout() {
  try {
    await showConfirmDialog({ title: '提示', message: '确定退出登录？' })
    userStore.logout()
    router.replace('/login')
  } catch (e) { /* 取消 */ }
}

onMounted(refresh)
onActivated(refresh)
</script>
<style>
/* 全局样式（不要加 scoped） */
.my-toast {
  background: rgba(0, 0, 0, 0.7) !important;
}
</style>
<style scoped lang="scss">
/* 原有样式不变，新增以下样式 */
.nickname {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
  
  .edit-profile-text {
    font-size: 12px;
    opacity: 0.85;
    margin-left: 2px;
  }
}

.change-pwd-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  margin-top: 4px;  /* 与 profile 区域垂直对齐微调 */
}

.user-page {
  min-height: 100vh;
  padding-bottom: 80px;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #ff8c00 0%, #ffb347 100%);
  padding: 32px 16px 16px;
  color: #fff;
  border-radius: 0 0 16px 16px;
}

.profile {
  display: flex;
  align-items: center;
  gap: 14px;

  .info {
    flex: 1;
  }

  .nickname {
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 6px;

    .edit-icon {
      opacity: 0.85;
    }
  }

  .phone {
    font-size: 13px;
    opacity: 0.85;
    margin-top: 4px;
  }
}

.points-card {
  margin-top: 18px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .label {
    font-size: 13px;
    opacity: 0.9;
  }

  .value {
    font-size: 26px;
    font-weight: 700;
    margin-top: 4px;
  }

  .points-right {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
  }
}

.stats-row {
  display: flex;
  align-items: center;
  margin-top: 16px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  padding: 14px 0;

  .stat {
    flex: 1;
    text-align: center;

    .stat-value {
      font-size: 18px;
      font-weight: 700;
    }

    .stat-label {
      font-size: 12px;
      opacity: 0.9;
      margin-top: 2px;
    }
  }

  .divider {
    width: 1px;
    height: 24px;
    background: rgba(255, 255, 255, 0.35);
  }
}

.menu-group {
  margin-top: 12px;
  border-radius: 12px;
  overflow: hidden;
}

.order-tabs {
  display: flex;
  background: #fff;
  padding: 14px 0;

  .ot-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #555;
    cursor: pointer;

    .van-icon {
      color: var(--primary-color, #ff8c00);
    }
  }
}

.logout-wrap {
  margin: 24px 16px 16px;
}

.profile-form {
  padding: 12px 0;
}

.popup-content {
  padding: 24px 20px;

  h3 {
    margin: 0 0 16px;
    font-size: 16px;
  }

  p {
    line-height: 1.8;
    color: #555;
    font-size: 14px;
    margin: 4px 0;
  }
}
</style>
