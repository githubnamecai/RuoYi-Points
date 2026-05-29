<template>
  <div class="app-container">
    <el-row :gutter="16" style="margin-bottom:12px">
      <el-col :span="4"><el-card><div class="stat-label">累计发放</div><div class="stat-num">{{ stat.totalIssued || 0 }}</div></el-card></el-col>
      <el-col :span="4"><el-card><div class="stat-label">累计消耗</div><div class="stat-num">{{ stat.totalConsumed || 0 }}</div></el-card></el-col>
      <el-col :span="4"><el-card><div class="stat-label">用户持有</div><div class="stat-num">{{ stat.totalBalance || 0 }}</div></el-card></el-col>
      <el-col :span="4"><el-card><div class="stat-label">用户数</div><div class="stat-num">{{ stat.userCount || 0 }}</div></el-card></el-col>
      <el-col :span="4"><el-card><div class="stat-label">今日发放</div><div class="stat-num">{{ stat.todayIssued || 0 }}</div></el-card></el-col>
      <el-col :span="4"><el-card><div class="stat-label">今日消耗</div><div class="stat-num">{{ stat.todayConsumed || 0 }}</div></el-card></el-col>
    </el-row>

    <el-form :inline="true" :model="q">
      <el-form-item label="手机号"><el-input v-model="q.phone" clearable @keyup.enter="getList" /></el-form-item>
      <el-form-item label="变动类型">
        <el-select v-model="q.changeType" clearable placeholder="全部" style="width:120px">
          <el-option label="增加" value="0" /><el-option label="扣减" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源">
        <el-select v-model="q.sourceType" clearable placeholder="全部" style="width:140px">
          <el-option label="签到" value="SIGN_IN" /><el-option label="兑换" value="EXCHANGE" />
          <el-option label="退还" value="REFUND" /><el-option label="后台调整" value="ADMIN_ADJUST" />
          <el-option label="连续奖励" value="CONTINUOUS_BONUS" /><el-option label="补签" value="SIGN_REPAIR" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button type="warning" icon="Download" @click="handleExport"
                   v-hasPermi="['points:detail:export']">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" prop="detailId" width="100" />
      <el-table-column label="用户昵称" prop="nickname" />
      <el-table-column label="手机号" prop="phone" width="130" />
      <el-table-column label="类型" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.changeType==='0' ? 'success' : 'danger'">
            {{ scope.row.changeType === '0' ? '增加' : '扣减' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" prop="sourceType" width="140" />
      <el-table-column label="积分" prop="changePoints" width="80" />
      <el-table-column label="余额" prop="balance" width="100" />
      <el-table-column label="描述" prop="description" show-overflow-tooltip />
      <el-table-column label="时间" prop="createTime" width="160" />
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="q.pageNum" v-model:limit="q.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="PointsDetail">
import { listDetail, statPoints } from '@/api/points/detail'
const { proxy } = getCurrentInstance()
const list = ref([]); const total = ref(0); const loading = ref(false); const stat = ref({})
const q = ref({ pageNum: 1, pageSize: 10, phone: '', changeType: '', sourceType: '' })
function getList() {
  loading.value = true
  listDetail(q.value).then(res => { list.value = res.rows; total.value = res.total; loading.value = false })
}
function getStat() { statPoints().then(res => stat.value = res.data) }
function handleExport() { proxy.download('points/detail/export', q.value, `积分明细_${new Date().getTime()}.xlsx`) }
getList(); getStat()
</script>

<style scoped>
.stat-label { color: #909399; font-size: 13px; }
.stat-num { font-size: 22px; font-weight: 600; margin-top: 6px; color: #ff8c00; }
</style>
