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
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="warning" icon="el-icon-download" @click="handleExport"
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
        <template slot-scope="scope">
          <el-tag :type="scope.row.goodsType==='1' ? 'success' : ''">
            {{ scope.row.goodsType === '1' ? '虚拟' : '实物' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货地址" prop="address" min-width="200" show-overflow-tooltip />
      <el-table-column label="物流单号" prop="expressNo" width="150" />
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status==='0' && scope.row.goodsType==='0'" link type="primary"
                     icon="el-icon-truck" @click="openShip(scope.row)"
                     v-hasPermi="['points:order:ship']">发货</el-button>
          <el-button v-if="scope.row.status==='0' || scope.row.status==='1'" link type="danger"
                     icon="el-icon-close" @click="handleClose(scope.row)"
                     v-hasPermi="['points:order:close']">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page="queryParams.pageNum"
                :limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="订单发货" :visible.sync="shipOpen" width="480px">
      <el-form ref="shipRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="expressCompany">
          <el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitShip">确认发货</el-button>
        <el-button @click="shipOpen=false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, shipOrder, closeOrder } from '@/api/points/order'

export default {
  name: 'Order',
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        userPhone: '',
        status: ''
      },
      shipOpen: false,
      shipForm: {},
      shipRules: {
        expressCompany: [{ required: true, message: '请输入物流公司', trigger: 'blur' }],
        expressNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取订单列表
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(res => {
        this.list = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置搜索条件
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 状态对应的标签类型
    statusType(s) {
      const typeMap = {
        '0': 'warning',
        '1': 'primary',
        '2': 'success',
        '3': 'danger'
      }
      return typeMap[s] || ''
    },
    // 状态对应的文本
    statusText(s) {
      const textMap = {
        '0': '待发货',
        '1': '已发货',
        '2': '已完成',
        '3': '已关闭'
      }
      return textMap[s] || s
    },
    // 打开发货弹窗
    openShip(row) {
      this.shipForm = { orderId: row.orderId }
      this.shipOpen = true
    },
    // 提交发货
    submitShip() {
      this.$refs.shipRef.validate(valid => {
        if (!valid) return
        shipOrder(this.shipForm).then(() => {
          this.$modal.msgSuccess('发货成功')
          this.shipOpen = false
          this.getList()
        })
      })
    },
    // 关闭订单
    handleClose(row) {
      this.$prompt('请输入关闭原因', '关闭订单', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '关闭原因不能为空'
      }).then(({ value }) => {
        closeOrder(row.orderId, value).then(() => {
          this.$modal.msgSuccess('已关闭，积分已退还')
          this.getList()
        })
      }).catch(() => {})
    },
    // 导出订单
    handleExport() {
      this.download('points/order/export', this.queryParams, `订单_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>