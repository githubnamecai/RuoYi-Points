<template>
  <div class="app-container">
    <el-form :inline="true" :model="q">
      <el-form-item label="手机号"><el-input v-model="q.phone" clearable @keyup.enter="getList" /></el-form-item>
      <el-form-item label="昵称"><el-input v-model="q.nickname" clearable /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="q.status" clearable placeholder="全部" style="width:120px">
          <el-option label="正常" value="0" /><el-option label="冻结" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" prop="userId" width="100" />
      <el-table-column label="昵称" prop="nickname" />
      <el-table-column label="手机号" prop="phone" width="130" />
      <el-table-column label="积分余额" prop="pointsBalance" width="100" />
      <el-table-column label="累计获得" prop="totalEarned" width="100" />
      <el-table-column label="累计消耗" prop="totalConsumed" width="100" />
      <el-table-column label="连续签到" prop="continuousDays" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status==='0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '冻结' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" prop="registerTime" width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openAdjust(scope.row)"
                     v-hasPermi="['points:h5user:adjust']">调整积分</el-button>
          <el-button v-if="scope.row.status==='0'" link type="danger"
                     @click="changeStatus(scope.row, '1')"
                     v-hasPermi="['points:h5user:freeze']">冻结</el-button>
          <el-button v-else link type="success" @click="changeStatus(scope.row, '0')"
                     v-hasPermi="['points:h5user:freeze']">解冻</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="q.pageNum" v-model:limit="q.pageSize" @pagination="getList" />

    <el-dialog title="调整积分" v-model="adjOpen" width="450px">
      <el-form ref="adjRef" :model="adj" :rules="adjRules" label-width="100px">
        <el-form-item label="用户"><span>{{ adj.nickname }} ({{ adj.phone }})</span></el-form-item>
        <el-form-item label="当前余额"><span>{{ adj.currentBalance }}</span></el-form-item>
        <el-form-item label="调整方向" prop="changeType">
          <el-radio-group v-model="adj.changeType">
            <el-radio label="0">增加</el-radio>
            <el-radio label="1">扣减</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分" prop="points">
          <el-input-number v-model="adj.points" :min="1" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="adj.reason" type="textarea" :rows="2" placeholder="操作日志会记录此原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitAdj">确 定</el-button>
        <el-button @click="adjOpen=false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="H5User">
import { listH5User, changeStatus as changeStatusApi, adjustPoints } from '@/api/points/h5user'
const { proxy } = getCurrentInstance()
const list = ref([]); const total = ref(0); const loading = ref(false)
const q = ref({ pageNum: 1, pageSize: 10, phone: '', nickname: '', status: '' })
const adjOpen = ref(false); const adj = ref({})
const adjRules = {
  changeType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  points: [{ required: true, message: '请输入积分', trigger: 'blur' }]
}
function getList() {
  loading.value = true
  listH5User(q.value).then(res => { list.value = res.rows; total.value = res.total; loading.value = false })
}
function changeStatus(row, status) {
  const txt = status === '0' ? '解冻' : '冻结'
  proxy.$modal.confirm(`确认${txt}该用户？`).then(() => changeStatusApi(row.userId, status))
    .then(() => { proxy.$modal.msgSuccess(`已${txt}`); getList() }).catch(() => {})
}
function openAdjust(row) {
  adj.value = { userId: row.userId, nickname: row.nickname, phone: row.phone,
    currentBalance: row.pointsBalance, changeType: '0', points: 100, reason: '' }
  adjOpen.value = true
}
function submitAdj() {
  proxy.$refs.adjRef.validate(valid => {
    if (!valid) return
    adjustPoints({ userId: adj.value.userId, changeType: adj.value.changeType,
                   points: adj.value.points, reason: adj.value.reason }).then(() => {
      proxy.$modal.msgSuccess('调整成功'); adjOpen.value = false; getList()
    })
  })
}
getList()
</script>
