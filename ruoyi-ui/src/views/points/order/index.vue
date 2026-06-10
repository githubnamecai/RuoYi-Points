<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" clearable @keyup.enter.native="handleQuery" />
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
      <el-table-column label="支付金额" prop="payAmount" width="100" />
      <el-table-column label="用户手机" prop="userPhone" width="120" />
      <el-table-column label="类型" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.goodsType === '1' ? 'success' : ''">
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
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['points:order:edit']">修改</el-button>
          <el-button v-if="scope.row.status === '0' && scope.row.goodsType === '0'" size="mini" type="text"
            icon="el-icon-s-promotion" @click="openShip(scope.row)" v-hasPermi="['points:order:ship']">发货</el-button>
          <el-button v-if="scope.row.status === '0' || scope.row.status === '1'" size="mini" type="text"
            icon="el-icon-close" @click="handleClose(scope.row)" v-hasPermi="['points:order:close']">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <el-dialog title="订单发货" :visible.sync="shipOpen" width="480px" append-to-body>
      <el-form ref="shipRef" :model="shipForm" :rules="shipRules" label-width="120px">
        <el-form-item v-if="shipRow && shipRow.payAmount != null" label="支付金额">
          <span style="color:#e53935;font-weight:600;font-size:16px">¥{{ shipRow.payAmount }}</span>
        </el-form-item>
        <el-form-item label="物流公司" prop="expressCompany">
          <el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" />
        </el-form-item>
        <el-form-item v-if="shipRow && shipRow.goodsType === '0'" label="线下支付" prop="offlinePaid">
          <el-radio-group v-model="shipForm.offlinePaid">
            <el-radio label="1">已完成线下支付</el-radio>
            <el-radio label="0">其他支付</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitShip">确认发货</el-button>
        <el-button @click="shipOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="修改订单" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="form.consignee" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入详细收货地址" />
        </el-form-item>
        <el-form-item label="物流公司" prop="expressCompany">
          <el-input v-model="form.expressCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="form.expressNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="状态" clearable>
            <el-option v-for="dict in dict.type.points_order_status" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.goodsType === '0'" label="支付金额" prop="payAmount">
          <el-input-number v-model="form.payAmount" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item v-if="form.goodsType === '0'" label="线下支付" prop="offlinePaid">
          <el-radio-group v-model="form.offlinePaid">
            <el-radio label="1">已完成线下支付</el-radio>
            <el-radio label="0">其他支付</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="订单取消原因" prop="closeReason">
          <el-input v-model="form.closeReason" type="textarea" placeholder="请输入订单取消原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, shipOrder, closeOrder, updateOrder } from '@/api/points/order'

export default {
  name: 'Order',
  dicts: ['points_order_status'],
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
      shipRow: null,
      shipForm: {},
      shipRules: {
        expressCompany: [{ required: true, message: '请输入物流公司', trigger: 'blur' }],
        expressNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
      },
      open: false,
      form: {},
      rules: {
        consignee: [{ required: true, message: '收货人不能为空', trigger: 'blur' }],
        phone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
        address: [{ required: true, message: '收货地址不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(res => {
        this.list = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    statusType(s) {
      return { '0': 'warning', '1': 'primary', '2': 'success', '3': 'danger' }[s] || ''
    },
    statusText(s) {
      return { '0': '待发货', '1': '已发货', '2': '已完成', '3': '已关闭' }[s] || s
    },
    openShip(row) {
      this.shipRow = row
      this.shipForm = { orderId: row.orderId, offlinePaid: '1' }
      this.shipOpen = true
    },
    submitShip() {
      this.$refs.shipRef.validate(valid => {
        if (!valid) return
        const data = Object.assign({}, this.shipForm)
        // 实物订单根据线下支付状态追加 remark
        if (this.shipRow && this.shipRow.goodsType === '0') {
          data.remark = data.offlinePaid === '1' ? '已完成线下支付' : '其他支付'
        }
        delete data.offlinePaid
        shipOrder(data).then(() => {
          this.$modal.msgSuccess('发货成功')
          this.shipOpen = false
          this.shipRow = null
          this.getList()
        })
      })
    },
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
      }).catch(() => { })
    },
    handleExport() {
      this.download('points/order/export', this.queryParams, `订单_${new Date().getTime()}.xlsx`)
    },
    handleUpdate(row) {
      this.form = Object.assign({}, row, { offlinePaid: '1' })
      this.open = true
    },
    cancel() {
      this.open = false
      this.resetForm('form')
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const data = Object.assign({}, this.form)
          // 实物订单根据线下支付状态追加 remark
          if (data.goodsType === '0') {
            const payRemark = data.offlinePaid === '1' ? '已完成线下支付' : '其他支付'
            data.remark=payRemark
          //   data.remark = data.remark ? data.remark + '；' + payRemark : payRemark
          }
          delete data.offlinePaid
          updateOrder(data).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        }
      });
    }
  }
}
</script>
