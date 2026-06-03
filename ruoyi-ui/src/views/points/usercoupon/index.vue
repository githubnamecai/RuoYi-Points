<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="H5用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入H5用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠券ID" prop="couponId">
        <el-input
          v-model="queryParams.couponId"
          placeholder="请输入优惠券ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['points:coupon:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['points:coupon:edit']"
        >修改</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['points:coupon:remove']"
        >删除</el-button>
      </el-col> -->
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['points:coupon:export']"
        >导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="couponList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="领券记录ID" align="center" prop="userCouponId" />
      <el-table-column label="H5用户ID" align="center" prop="userId" />
      <el-table-column label="优惠券ID" align="center" prop="couponId" />
      <el-table-column label="获取方式" align="center" prop="receiveType" />
      <el-table-column label="使用状态" align="center" prop="status" />
      <el-table-column label="使用ID的订单" align="center" prop="orderId" />
      <el-table-column label="生效时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用时间" align="center" prop="useTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.useTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="领取时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['points:coupon:edit']"
          >修改</el-button>
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['points:coupon:remove']"
          >删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改用户优惠券记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="H5用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入H5用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券ID" prop="couponId">
              <el-input v-model="form.couponId" placeholder="请输入优惠券ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="使用ID的订单" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入使用ID的订单" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="生效时间" prop="startTime">
              <el-date-picker clearable
                v-model="form.startTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过期时间" prop="endTime">
              <el-date-picker clearable
                v-model="form.endTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择过期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="使用时间" prop="useTime">
              <el-date-picker clearable
                v-model="form.useTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择使用时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUserCoupon, getUserCoupon, addUserCoupon, updateUserCoupon,receiveUserCoupon,setstatusUserCoupon } from "@/api/points/usercoupon"

export default {
  name: "UserCoupon",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户优惠券记录表格数据
      couponList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        couponId: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "H5用户ID不能为空", trigger: "blur" }
        ],
        couponId: [
          { required: true, message: "优惠券ID不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "生效时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "过期时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户优惠券记录列表 */
    getList() {
      this.loading = true
      listUserCoupon(this.queryParams).then(response => {
        this.couponList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        userCouponId: null,
        userId: null,
        couponId: null,
        receiveType: null,
        status: null,
        orderId: null,
        startTime: null,
        endTime: null,
        useTime: null,
        createTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userCouponId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加用户优惠券记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const userCouponId = row.userCouponId || this.ids
      getUserCoupon(userCouponId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户优惠券记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userCouponId != null) {
            updateUserCoupon(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addUserCoupon(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    // handleDelete(row) {
    //   const userCouponIds = row.userCouponId || this.ids
    //   this.$modal.confirm('是否确认删除用户优惠券记录编号为"' + userCouponIds + '"的数据项？').then(function() {
    //     return delCoupon(userCouponIds)
    //   }).then(() => {
    //     this.getList()
    //     this.$modal.msgSuccess("删除成功")
    //   }).catch(() => {})
    // },
    /** 导出按钮操作 */
    // handleExport() {
    //   this.download('points/coupon/export', {
    //     ...this.queryParams
    //   }, `coupon_${new Date().getTime()}.xlsx`)
    // }
  }
}
</script>
