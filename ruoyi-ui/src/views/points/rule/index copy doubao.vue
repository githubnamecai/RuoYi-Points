<template>
  <div class="app-container">
    <!-- 搜索表单 -->
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
        <el-button type="primary" icon="el-icon-search" @click="getList">搜索</el-button>
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd"
                   v-hasPermi="['points:rule:edit']">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" prop="ruleId" width="80" />
      <el-table-column label="规则编码" prop="ruleCode" width="160" />
      <el-table-column label="规则名称" prop="ruleName" min-width="160" />
      <el-table-column label="类型" width="90">
        <template slot-scope="scope">
          <el-tag :type="typeColor(scope.row.ruleType)">{{ typeText(scope.row.ruleType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="奖励积分" prop="rewardPoints" width="100" />
      <el-table-column label="每日上限" prop="dailyLimit" width="100" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status==='0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button link type="primary" icon="el-icon-edit" @click="handleEdit(scope.row)"
                     v-hasPermi="['points:rule:edit']">修改</el-button>
          <el-button link type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['points:rule:edit']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页（Vue 2 版本用 .sync 修饰符） -->
    <pagination v-show="total>0" :total="total" :page.sync="q.pageNum"
                :limit.sync="q.pageSize" @pagination="getList" />

    <!-- 弹窗表单 -->
    <el-dialog :title="title" :visible.sync="open" width="560px">
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
      <div slot="footer">
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="open=false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRule, getRule, addRule, updateRule, delRule } from '@/api/points/rule'

export default {
  name: 'PointsRule',
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      open: false,
      title: '',
      q: {
        pageNum: 1,
        pageSize: 10,
        ruleCode: '',
        ruleName: '',
        ruleType: ''
      },
      form: {},
      rules: {
        ruleCode: [{ required: true, message: '规则编码必填', trigger: 'blur' }],
        ruleName: [{ required: true, message: '规则名称必填', trigger: 'blur' }],
        ruleType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        rewardPoints: [{ required: true, message: '请输入奖励积分', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取列表
    getList() {
      this.loading = true
      listRule(this.q).then(res => {
        this.list = res.rows
        this.total = res.total
      }).catch(err => {
        console.error('获取规则列表失败：', err)
      }).finally(() => {
        this.loading = false
      })
    },
    // 类型文本转换
    typeText(t) {
      return { '0':'签到','1':'任务','2':'其他' }[t] || t
    },
    // 类型标签颜色
    typeColor(t) {
      return { '0':'success','1':'primary','2':'info' }[t] || ''
    },
    // 重置表单
    reset() {
      this.form = {
        ruleId: undefined,
        ruleCode: '',
        ruleName: '',
        ruleType: '1',
        rewardPoints: 10,
        dailyLimit: 0,
        status: '0',
        configJson: '',
        remark: ''
      }
      // 重置表单验证
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    // 新增
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增规则'
    },
    // 编辑
    handleEdit(row) {
      this.reset()
      getRule(row.ruleId).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改规则'
      }).catch(err => {
        this.$modal.msgError('获取规则详情失败')
        console.error(err)
      })
    },
    // 提交保存
    submit() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return
        const op = this.form.ruleId ? updateRule : addRule
        op(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        }).catch(err => {
          this.$modal.msgError('保存失败')
          console.error(err)
        })
      })
    },
    // 删除
    handleDelete(row) {
      this.$modal.confirm(`确定删除规则 "${row.ruleName}"？`).then(() => {
        return delRule(row.ruleId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>