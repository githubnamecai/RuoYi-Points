<template>
  <div class="address-list-page">
    <van-nav-bar
      :title="pickerMode ? '选择收货地址' : '收货地址'"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <div class="content">
      <van-empty
        v-if="!loading && list.length === 0"
        description="暂无收货地址，去添加一个吧"
      />

      <div v-else class="list">
        <div
          v-for="item in list"
          :key="item.addressId"
          class="addr-card"
          @click="onPick(item)"
        >
          <div class="row1">
            <span class="consignee">{{ item.consignee }}</span>
            <span class="phone">{{ item.phone }}</span>
            <van-tag v-if="item.isDefault === '1'" type="primary" plain class="tag">默认</van-tag>
          </div>
          <div class="row2">
            {{ item.province }} {{ item.city }} {{ item.district }} {{ item.detail }}
          </div>
          <div class="row3" @click.stop>
            <van-checkbox
              :model-value="item.isDefault === '1'"
              shape="square"
              :disabled="item.isDefault === '1'"
              @update:model-value="(v) => setDefault(item, v)"
              @click.stop
            >
              设为默认
            </van-checkbox>
            <div class="actions">
              <van-button
                size="mini"
                plain
                icon="edit"
                @click="goEdit(item)"
              >编辑</van-button>
              <van-button
                size="mini"
                plain
                type="danger"
                icon="delete-o"
                @click="del(item)"
              >删除</van-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer">
      <van-button block round type="primary" icon="plus" @click="goAdd">
        新增收货地址
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { listAddresses, deleteAddress, setDefaultAddress } from '@/api/user'

const router = useRouter()
const route = useRoute()
const list = ref([])
const loading = ref(false)

const pickerMode = computed(() => route.query.picker === '1')

async function load() {
  loading.value = true
  try {
    const res = await listAddresses()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function onPick(item) {
  if (!pickerMode.value) return
  // 通过 sessionStorage 把选中地址带回 Exchange 页面
  sessionStorage.setItem('selectedAddress', JSON.stringify(item))
  router.back()
}

function goAdd() {
  router.push('/address/edit')
}
function goEdit(item) {
  router.push({ path: '/address/edit', query: { id: item.addressId } })
}

async function del(item) {
  try {
    await showConfirmDialog({ title: '提示', message: '确定删除该地址？' })
    await deleteAddress(item.addressId)
    showToast('删除成功')
    load()
  } catch (e) { /* 取消 */ }
}

async function setDefault(item, val) {
  if (!val) return
  await setDefaultAddress(item.addressId)
  showToast('设置成功')
  load()
}

onMounted(load)
onActivated(load)
</script>

<style scoped lang="scss">
.address-list-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}
.content {
  padding: 12px;
}
.addr-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  .row1 {
    display: flex;
    align-items: center;
    gap: 10px;
    .consignee { font-size: 16px; font-weight: 600; }
    .phone { color: #666; font-size: 14px; }
    .tag { margin-left: auto; }
  }
  .row2 {
    margin-top: 8px;
    color: #555;
    font-size: 14px;
    line-height: 1.5;
    word-break: break-all;
  }
  .row3 {
    margin-top: 12px;
    padding-top: 10px;
    border-top: 1px dashed #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .actions {
      display: flex;
      gap: 8px;
    }
  }
}
.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.05);
}
</style>
