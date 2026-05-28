<template>
  <div class="app-container">
    <el-form :inline="true" :model="q">
      <el-form-item label="规则编码"><el-input v-model="q.ruleCode" clearable @keyup.enter="getList" /></el-form-item>
      <el-form-item label="规则名称"><el-input v-model="q.ruleName" clearable /></el-form-item>
      <el-form-item label="类型">
        <el-select v-model="q.ruleType" clearable placeholder="全部" style="width:120px">
          <el-option label="签到" value="0" />
          <el-option label="任务" value="1" />
          <el-option label="其他" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
                   v-hasPermi="['points:rule:edit']">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" prop="ruleId" width="80" />
      <el-table-column label="规则编码" prop="ruleCode" width="160" />
      <el-table-column label="规则名称" prop="ruleName" min-width="160" />
      <el-table-column label="类型" width="90">
        <template #default="scope">
          <el-tag :type="typeColor(scope.row.ruleType)">{{ typeText(scope.row.ruleType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="奖励积分" prop="rewardPoints" width="100" />
      <el-table-column label="每日上限" prop="dailyLimit" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status==='0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)"
                     v-hasPermi="['points:rule:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['points:rule:edit']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="q.pageNum"
                v-model:limit="q.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="规则编码" prop="ruleCode">
          <el-input v-model="form.ruleCode" :disabled="!!form.ruleId" placeholder="如 SIGN_IN" />
        </el-form-item>
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" />
        </el-form-item>
        <el-form-item label="类型" prop="ruleType">
          <el-radio-group v-model="form.ruleType">
            <el-radio label="0">签到</el-radio>
            <el-radio label="1">任务</el-radio>
            <el-radio label="2">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="奖励积分" prop="rewardPoints">
          <el-input-number v-model="form.rewardPoints" :min="0" />
        </el-form-item>
        <el-form-item label="每日上限次数">
          <el-input-number v-model="form.dailyLimit" :min="0" />
          <span style="margin-left:8px;color:#999">0=不限制</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="动态配置JSON">
          <el-input v-model="form.configJson" type="textarea" :rows="4"
                    placeholder='可选 JSON，如 {"key":"value"}' />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="open=false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="PointsRule">
import { listRule, getRule, addRule, updateRule, delRule } from '@/api/points/rule'
const { proxy } = getCurrentInstance()
const list = ref([]); const total = ref(0); const loading = ref(false)
const open = ref(false); const title = ref('')
const q = ref({ pageNum: 1, pageSize: 10, ruleCode: '', ruleName: '', ruleType: '' })
const form = ref({})
const rules = {
  ruleCode: [{ required: true, message: '规则编码必填', trigger: 'blur' }],
  ruleName: [{ required: true, message: '规则名称必填', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  rewardPoints: [{ required: true, message: '请输入奖励积分', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listRule(q.value).then(res => { list.value = res.rows; total.value = res.total; loading.value = false })
}
function typeText(t) { return { '0':'签到','1':'任务','2':'其他' }[t] || t }
function typeColor(t) { return { '0':'success','1':'primary','2':'info' }[t] || '' }
function reset() {
  form.value = { ruleId: undefined, ruleCode: '', ruleName: '', ruleType: '1',
    rewardPoints: 10, dailyLimit: 0, status: '0', configJson: '', remark: '' }
}
function handleAdd() { reset(); open.value = true; title.value = '新增规则' }
function handleEdit(row) {
  reset()
  getRule(row.ruleId).then(res => { form.value = res.data; open.value = true; title.value = '修改规则' })
}
function submit() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const op = form.value.ruleId ? updateRule : addRule
    op(form.value).then(() => { proxy.$modal.msgSuccess('保存成功'); open.value = false; getList() })
  })
}
function handleDelete(row) {
  proxy.$modal.confirm(`确定删除规则 "${row.ruleName}"？`).then(() => delRule(row.ruleId))
    .then(() => { proxy.$modal.msgSuccess('删除成功'); getList() }).catch(() => {})
}
getList()
</script>
