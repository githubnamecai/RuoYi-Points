<template>
  <div class="app-container">
    <el-card shadow="never">
      <template slot="header">签到配置</template>
      <el-form ref="formRef" :model="form" label-width="140px" style="max-width:680px">
        <el-form-item label="签到开关">
          <el-switch v-model="form.enabled" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="基础积分">
          <el-input-number v-model="form.basePoints" :min="0" />
        </el-form-item>
        <el-form-item label="连续签到奖励">
          <el-table :data="form.continuousRewardList" size="small" border style="max-width:560px">
            <el-table-column label="连续天数">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.day" :min="1" />
              </template>
            </el-table-column>
            <el-table-column label="奖励积分">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.points" :min="0" />
              </template>
            </el-table-column>
            <el-table-column width="80">
              <template slot-scope="scope">
                <el-button link type="danger" @click="removeRow(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button size="small" style="margin-top:8px" @click="addRow">+ 新增奖励梯度</el-button>
        </el-form-item>
        <el-form-item label="允许补签">
          <el-switch v-model="form.repairEnabled" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="补签消耗积分">
          <el-input-number v-model="form.repairCost" :min="0" />
        </el-form-item>
        <el-form-item label="补签最大天数">
          <el-input-number v-model="form.repairMaxDays" :min="1" :max="30" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save" v-hasPermi="['points:sign:edit']">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getSignConfig, updateSignConfig } from '@/api/points/rule'

export default {
  name: 'SignConfig',
  data() {
    return {
      form: {
        enabled: '1',
        basePoints: 10,
        continuousRewardList: [],
        repairEnabled: '1',
        repairCost: 50,
        repairMaxDays: 3
      }
    }
  },
  mounted() {
    this.load()
  },
  methods: {
    load() {
      getSignConfig().then(res => {
        const d = res.data
        let list = []
        try {
          list = d.continuousReward ? JSON.parse(d.continuousReward) : []
        } catch (e) {}
        this.form = { ...d, continuousRewardList: list }
      })
    },
    addRow() {
      this.form.continuousRewardList.push({ day: 7, points: 50 })
    },
    removeRow(i) {
      this.form.continuousRewardList.splice(i, 1)
    },
    save() {
      const payload = { ...this.form, continuousReward: JSON.stringify(this.form.continuousRewardList) }
      delete payload.continuousRewardList
      updateSignConfig(payload)
        .then(() => {
          this.$modal.msgSuccess('保存成功')
        })
        .then(() => {
          this.load()
        })
    }
  }
}
</script>