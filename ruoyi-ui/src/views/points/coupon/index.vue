<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="优惠券名称" prop="couponName">
        <el-input v-model="queryParams.couponName" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.points_user_coupon_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['points:coupon:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['points:coupon:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['points:coupon:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="couponList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="couponId" />
      <el-table-column label="名称" align="center" prop="couponName" />
      <el-table-column label="类型" align="center" prop="couponType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.points_coupon_type" :value="scope.row.couponType"/>
        </template>
      </el-table-column>
      <el-table-column label="面值" align="center" prop="discountValue">
        <template slot-scope="scope">
          {{ scope.row.couponType === '1' ? scope.row.discountValue + '%' : scope.row.discountValue }}
        </template>
      </el-table-column>
      <el-table-column label="使用门槛" align="center" prop="minAmount">
        <template slot-scope="scope">满{{ scope.row.minAmount }}可用</template>
      </el-table-column>
      <el-table-column label="适用范围" align="center" prop="useType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.points_coupon_use_type" :value="scope.row.useType"/>
        </template>
      </el-table-column>
      <el-table-column label="发行量" align="center" prop="totalCount">
        <template slot-scope="scope">{{ scope.row.totalCount === -1 ? '不限量' : scope.row.totalCount }}</template>
      </el-table-column>
      <el-table-column label="已发" align="center" prop="issuedCount" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.points_user_coupon_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-s-promotion" @click="handleIssue(scope.row)" v-hasPermi="['points:coupon:issue']">发放</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['points:coupon:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['points:coupon:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="优惠券名称" prop="couponName">
          <el-input v-model="form.couponName" placeholder="请输入名称" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠券类型" prop="couponType">
              <el-select v-model="form.couponType" placeholder="请选择">
                <el-option v-for="dict in dict.type.points_coupon_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面值" prop="discountValue">
              <el-input-number v-model="form.discountValue" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="使用门槛" prop="minAmount">
              <el-input-number v-model="form.minAmount" :min="0" placeholder="0为无门槛"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发行总量" prop="totalCount">
              <el-input-number v-model="form.totalCount" :min="-1" placeholder="-1为不限"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="适用范围" prop="useType">
          <el-radio-group v-model="form.useType">
            <el-radio v-for="dict in dict.type.points_coupon_use_type" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 当非全场通用时，需要选择商品或分类ID -->
        <el-form-item label="关联商品/分类" v-if="form.useType === '1' || form.useType === '2'">
          <el-input v-model="refIdsStr" placeholder="请输入关联的ID，多个逗号隔开" />
        </el-form-item>

        <el-form-item label="有效期类型" prop="timeType">
          <el-radio-group v-model="form.timeType">
            <el-radio v-for="dict in dict.type.points_coupon_time_type" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="有效时间" v-if="form.timeType === '0'">
          <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="开始时间" style="width:48%"/> - 
          <el-date-picker clearable v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="结束时间" style="width:48%"/>
        </el-form-item>
        <el-form-item label="有效天数" v-if="form.timeType === '1'">
          <el-input-number v-model="form.validDays" :min="1" /> 天
        </el-form-item>

        <el-form-item label="每人限领" prop="limitPerUser">
          <el-input-number v-model="form.limitPerUser" :min="1" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 发放弹窗 -->
    <el-dialog title="发放优惠券" :visible.sync="issueOpen" width="500px" append-to-body>
      <el-form>
        <el-form-item label="接收用户ID(多个用逗号隔开)">
          <el-input type="textarea" v-model="issueUserIds" rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitIssue">确 定</el-button>
        <el-button @click="issueOpen=false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCoupon, getCoupon, delCoupon, addCoupon, updateCoupon, issueCoupon } from "@/api/points/coupon";

export default {
  name: "Coupon",
  dicts: ['points_coupon_type', 'points_coupon_use_type', 'points_coupon_time_type', 'points_user_coupon_status'],
  data() {
    return {
      loading: true, showSearch: true, ids: [], single: true, multiple: true, total: 0,
      couponList: [], title: "", open: false, issueOpen: false, currentIssueId: null, issueUserIds: "",
      refIdsStr: "",
      queryParams: { pageNum: 1, pageSize: 10, couponName: undefined, status: undefined },
      form: {},
      rules: {
        couponName: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        discountValue: [{ required: true, message: "面值不能为空", trigger: "blur" }]
      }
    };
  },
  created() { this.getList(); },
  methods: {
    getList() {
      this.loading = true;
      listCoupon(this.queryParams).then(res => {
        this.couponList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    cancel() { this.open = false; this.reset(); },
    reset() {
      this.form = { couponId: null, couponName: null, couponType: '0', minAmount: 0, discountValue: 0, totalCount: -1, limitPerUser: 1, useType: '0', timeType: '0', startTime: null, endTime: null, validDays: null, status: '0' };
      this.refIdsStr = "";
      this.resetForm("form");
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.couponId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() { this.reset(); this.open = true; this.title = "添加优惠券"; },
    handleUpdate(row) {
      this.reset();
      const couponId = row.couponId || this.ids;
      getCoupon(couponId).then(response => {
        this.form = response.data;
        if (this.form.refIds) {
          this.refIdsStr = this.form.refIds.join(',');
        }
        this.open = true;
        this.title = "修改优惠券";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.refIdsStr) {
             this.form.refIds = this.refIdsStr.split(',').map(Number);
          } else {
             this.form.refIds = [];
          }
          if (this.form.couponId != null) {
            updateCoupon(this.form).then(res => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCoupon(this.form).then(res => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const couponIds = row.couponId || this.ids;
      this.$modal.confirm('是否确认删除该优惠券？').then(() => delCoupon(couponIds)).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleIssue(row) {
      this.currentIssueId = row.couponId;
      this.issueUserIds = "";
      this.issueOpen = true;
    },
    submitIssue() {
      if (!this.issueUserIds) {
        this.$modal.msgError("请输入用户ID"); return;
      }
      const uids = this.issueUserIds.split(',').map(Number);
      issueCoupon(this.currentIssueId, uids).then(res => {
        this.$modal.msgSuccess(res.msg || "发放成功");
        this.issueOpen = false;
        this.getList();
      });
    }
  }
};
</script>