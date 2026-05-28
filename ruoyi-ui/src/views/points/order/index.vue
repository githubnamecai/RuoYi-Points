<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="userPhone">
        <el-input v-model="queryParams.userPhone" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="全部" style="width:140px">
          <el-option label="待发货" value="0" />
          <el-option label="已发货" value="1" />
          <el-option label="已完成" value="2" />
          <el-option label="已关闭" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="warning" icon="Download" @click="handleExport"
                   v-hasPermi="['points:order:export']">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="订单号" prop="orderNo" width="180" show-overflow-tooltip />
      <el-table-column label="商品" prop="goodsName" min-width="160" show-overflow-tooltip />
      <el-table-column label="数量" prop="quantity" width="70" />
      <el-table-column label="消耗积分" prop="pointsUsed" width="100" />
      <el-table-column label="用户手机" prop="userPhone" width="120" />
      <el-table-column label="类型" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.goodsType==='1' ? 'success' : ''">
            {{ scope.row.goodsType === '1' ? '虚拟' : '实物' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货地址" prop="address" min-width="200" show-overflow-tooltip />
      <el-table-column label="物流单号" prop="expressNo" width="150" />
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.status==='0' && scope.row.goodsType==='0'" link type="primary"
                     icon="Van" @click="openShip(scope.row)"
                     v-hasPermi="['points:order:ship']">发货</el-button>
          <el-button v-if="scope.row.status==='0' || scope.row.status==='1'" link type="danger"
                     icon="Close" @click="handleClose(scope.row)"
                     v-hasPermi="['points:order:close']">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="订单发货" v-model="shipOpen" width="480px">
      <el-form ref="shipRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="expressCompany">
          <el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitShip">确认发货</el-button>
        <el-button @click="shipOpen=false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Order">
import { listOrder, shipOrder, closeOrder } from '@/api/points/order'
const { proxy } = getCurrentInstance()
const list = ref([]); const total = ref(0); const loading = ref(false)
const queryParams = ref({ pageNum: 1, pageSize: 10, orderNo: '', userPhone: '', status: '' })
const shipOpen = ref(false); const shipForm = ref({})
const shipRules = {
  expressCompany: [{ required: true, message: '请输入物流公司', trigger: 'blur' }],
  expressNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}
function getList() {
  loading.value = true
  listOrder(queryParams.value).then(res => { list.value = res.rows; total.value = res.total; loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryForm'); handleQuery() }
function statusType(s) { return { '0':'warning','1':'primary','2':'success','3':'danger' }[s] || '' }
function statusText(s) { return { '0':'待发货','1':'已发货','2':'已完成','3':'已关闭' }[s] || s }

function openShip(row) { shipForm.value = { orderId: row.orderId }; shipOpen.value = true }
function submitShip() {
  proxy.$refs.shipRef.validate(valid => {
    if (!valid) return
    shipOrder(shipForm.value).then(() => { proxy.$modal.msgSuccess('发货成功'); shipOpen.value = false; getList() })
  })
}
function handleClose(row) {
  proxy.$prompt('请输入关闭原因', '关闭订单', { confirmButtonText: '确认', cancelButtonText: '取消',
    inputPattern: /\S+/, inputErrorMessage: '关闭原因不能为空' }).then(({ value }) => {
    closeOrder(row.orderId, value).then(() => { proxy.$modal.msgSuccess('已关闭，积分已退还'); getList() })
  }).catch(() => {})
}
function handleExport() {
  proxy.download('points/order/export', queryParams.value, `订单_${new Date().getTime()}.xlsx`)
}
getList()
</script>
