<template>
  <div class="address-edit-page">
    <van-nav-bar
      :title="isEdit ? '编辑收货地址' : '新增收货地址'"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <van-form @submit="onSubmit">
      <van-cell-group inset class="form-group">
        <van-field
          v-model="form.consignee"
          label="收货人"
          placeholder="请输入收货人姓名"
          :rules="[{ required: true, message: '请输入收货人' }]"
          maxlength="32"
        />
        <van-field
          v-model="form.phone"
          label="手机号"
          type="tel"
          placeholder="请输入手机号"
          :rules="[
            { required: true, message: '请输入手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
          ]"
          maxlength="11"
        />
        <van-field
          v-model="areaText"
          label="所在地区"
          placeholder="请选择省/市/区"
          is-link
          readonly
          :rules="[{ required: true, message: '请选择所在地区' }]"
          @click="showArea = true"
        />
        <van-field
          v-model="form.detail"
          label="详细地址"
          type="textarea"
          rows="2"
          autosize
          maxlength="200"
          show-word-limit
          placeholder="街道、楼牌号等"
          :rules="[{ required: true, message: '请输入详细地址' }]"
        />
      </van-cell-group>

      <van-cell-group inset class="form-group">
        <van-cell title="设为默认地址" center>
          <template #right-icon>
            <van-switch v-model="defaultOn" />
          </template>
        </van-cell>
      </van-cell-group>

      <div class="footer">
        <van-button block round type="primary" native-type="submit" :loading="submitting">
          保存
        </van-button>
        <van-button
          v-if="isEdit"
          block
          plain
          type="danger"
          style="margin-top: 10px"
          @click="onDelete"
        >
          删除地址
        </van-button>
      </div>
    </van-form>

    <!-- 省市区选择 -->
    <van-popup v-model:show="showArea" position="bottom" round>
      <van-area
        :area-list="areaList"
        :value="areaCode"
        @confirm="onAreaConfirm"
        @cancel="showArea = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { areaList } from '@vant/area-data'
import {
  listAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/user'

const router = useRouter()
const route = useRoute()

const form = reactive({
  addressId: null,
  consignee: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: '0'
})
const defaultOn = ref(false)
const showArea = ref(false)
const submitting = ref(false)
const areaCode = ref('')

const isEdit = computed(() => !!route.query.id)
const areaText = computed(() => {
  const parts = [form.province, form.city, form.district].filter(Boolean)
  return parts.join(' / ')
})

async function loadEdit() {
  if (!isEdit.value) return
  // 通过列表接口取出当前地址
  const res = await listAddresses()
  const list = res.data || []
  const item = list.find(a => String(a.addressId) === String(route.query.id))
  if (item) {
    Object.assign(form, {
      addressId: item.addressId,
      consignee: item.consignee || '',
      phone: item.phone || '',
      province: item.province || '',
      city: item.city || '',
      district: item.district || '',
      detail: item.detail || '',
      isDefault: item.isDefault || '0'
    })
    defaultOn.value = item.isDefault === '1'
  } else {
    showToast({
    message: '地址不存在',
    className: 'my-toast'
})
  }
}

function onAreaConfirm({ selectedOptions }) {
  if (!selectedOptions || selectedOptions.length < 3) return
  form.province = selectedOptions[0]?.text || ''
  form.city = selectedOptions[1]?.text || ''
  form.district = selectedOptions[2]?.text || ''
  areaCode.value = selectedOptions[2]?.value || ''
  showArea.value = false
}

async function onSubmit() {
  submitting.value = true
  try {
    const payload = {
      consignee: form.consignee.trim(),
      phone: form.phone.trim(),
      province: form.province,
      city: form.city,
      district: form.district,
      detail: form.detail.trim(),
      isDefault: defaultOn.value ? '1' : '0'
    }
    if (isEdit.value) {
      payload.addressId = form.addressId
      await updateAddress(payload)
    } else {
      const res = await addAddress(payload)
      // 后端可能返回新的id，便于"设为默认"操作
      if (res && res.data && defaultOn.value && res.data.addressId) {
        await setDefaultAddress(res.data.addressId).catch(() => {})
      }
    }
    setTimeout(() => {
      showToast({
      message: isEdit.value ? '修改成功' : '新增成功',
      className: 'my-toast'
})
    }, 100)
    setTimeout(() => router.back(), 500)
  } finally {
    submitting.value = false
  }
}

async function onDelete() {
  try {
    await showConfirmDialog({ title: '提示', message: '确定删除该地址？' })
    await deleteAddress(form.addressId)
    showToast({
    message: '删除成功',
    className: 'my-toast'
})
    setTimeout(() => router.back(), 300)
  } catch (e) { /* 取消 */ }
}

onMounted(loadEdit)
</script>

<style scoped lang="scss">
.address-edit-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}
.form-group {
  margin-top: 12px;
  border-radius: 12px;
  overflow: hidden;
}
.footer {
  margin: 24px 16px 16px;
}
</style>
