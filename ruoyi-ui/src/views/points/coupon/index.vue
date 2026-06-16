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
        <el-form-item label="关联范围" v-if="form.useType === '1' || form.useType === '2'">
          <el-button type="primary" plain size="mini" @click="openRefSelector">
            {{ form.useType === '1' ? '选择分类' : '选择商品' }}
          </el-button>
          <div v-if="refSelectedList.length" class="selected-users" style="display:inline-block; margin-left: 10px;">
            <el-tag
              v-for="item in refSelectedList"
              :key="item.id"
              size="small"
              closable
              @close="removeRefItem(item.id)"
              style="margin-right: 6px; margin-bottom: 6px;"
            >
              {{ item.name }}
            </el-tag>
          </div>
          <span v-else class="selected-placeholder" style="margin-left: 10px;">未选择</span>
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
    <el-dialog title="发放优惠券" :visible.sync="issueOpen" width="900px" append-to-body>
      <el-form :inline="true" :model="issueQuery" size="small">
        <el-form-item label="手机号">
          <el-input v-model="issueQuery.phone" clearable placeholder="请输入手机号" @keyup.enter.native="getIssueUserList" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="issueQuery.nickname" clearable placeholder="请输入昵称" @keyup.enter.native="getIssueUserList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleIssueQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetIssueQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-form label-width="90px">
        <el-form-item label="已选用户">
          <div v-if="issueSelectedNames.length" class="selected-users">
            <el-tag
              v-for="(name, index) in issueSelectedNames"
              :key="index"
              size="small"
              closable
              @close="removeIssueUserByName(name)"
            >
              {{ name }}
            </el-tag>
          </div>
          <span v-else class="selected-placeholder">请选择要发放的用户</span>
        </el-form-item>
      </el-form>

      <el-table
        ref="issueUserTable"
        v-loading="issueLoading"
        :data="issueUserList"
        height="360"
        @selection-change="handleIssueSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户ID" prop="userId" width="90" />
        <el-table-column label="昵称" prop="nickname" min-width="120" />
        <el-table-column label="姓名" prop="name" min-width="120" />
        <el-table-column label="手机号" prop="phone" min-width="140" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="issueUserTotal > 0"
        :total="issueUserTotal"
        :page.sync="issueQuery.pageNum"
        :limit.sync="issueQuery.pageSize"
        @pagination="getIssueUserList"
      />

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitIssue">确 定</el-button>
        <el-button @click="issueOpen=false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择分类" :visible.sync="categorySelectOpen" width="700px" append-to-body>
      <el-tree
        ref="categoryTree"
        :data="categoryTreeData"
        node-key="categoryId"
        show-checkbox
        default-expand-all
        :props="{ children: 'children', label: 'categoryName' }"
      />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmCategorySelect">确 定</el-button>
        <el-button @click="categorySelectOpen=false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择商品" :visible.sync="goodsSelectOpen" width="900px" append-to-body>
      <el-form :inline="true" :model="goodsQuery" size="small">
        <el-form-item label="商品名称">
          <el-input v-model="goodsQuery.goodsName" clearable placeholder="请输入商品名称" @keyup.enter.native="handleGoodsQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleGoodsQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetGoodsQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        ref="goodsTable"
        v-loading="goodsLoading"
        :data="goodsList"
        height="360"
        @selection-change="handleGoodsSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="商品ID" prop="goodsId" width="90" />
        <el-table-column label="商品名称" prop="goodsName" min-width="220" />
        <el-table-column label="所需积分" prop="points" width="100" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
              {{ scope.row.status === '1' ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="goodsTotal > 0"
        :total="goodsTotal"
        :page.sync="goodsQuery.pageNum"
        :limit.sync="goodsQuery.pageSize"
        @pagination="getGoodsList"
      />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmGoodsSelect">确 定</el-button>
        <el-button @click="goodsSelectOpen=false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCoupon, getCoupon, delCoupon, addCoupon, updateCoupon, issueCoupon } from "@/api/points/coupon";
import { listH5User } from "@/api/points/h5user";
import { treeCategory } from "@/api/points/category";
import { listGoods, getGoods } from "@/api/points/goods";

export default {
  name: "Coupon",
  dicts: ['points_coupon_type', 'points_coupon_use_type', 'points_coupon_time_type', 'points_user_coupon_status'],
  data() {
    return {
      loading: true, showSearch: true, ids: [], single: true, multiple: true, total: 0,
      couponList: [], title: "", open: false, issueOpen: false, currentIssueId: null,
      refIdsStr: "",
      issueLoading: false,
      issueUserList: [],
      issueUserTotal: 0,
      issueSelectedMap: {},
      issueQuery: { pageNum: 1, pageSize: 10, phone: "", nickname: "", status: "0" },
      refSelectedList: [],
      categorySelectOpen: false,
      categoryTreeData: [],
      goodsSelectOpen: false,
      goodsLoading: false,
      goodsList: [],
      goodsTotal: 0,
      goodsSelectedMap: {},
      goodsQuery: { pageNum: 1, pageSize: 10, goodsName: "", status: "1" },
      queryParams: { pageNum: 1, pageSize: 10, couponName: undefined, status: undefined },
      form: {},
      rules: {
        couponName: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        discountValue: [{ required: true, message: "面值不能为空", trigger: "blur" }]
      }
    };
  },
  created() { this.getList(); },
  computed: {
    issueSelectedIds() {
      return Object.keys(this.issueSelectedMap).map(id => Number(id));
    },
    issueSelectedNames() {
      return Object.values(this.issueSelectedMap).map(item => item.nickname || item.phone || `用户${item.userId}`);
    }
  },
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
        this.refSelectedList = [];
        if (this.form.refIds) this.refIdsStr = this.form.refIds.join(',');
        this.open = true;
        this.title = "修改优惠券";
        this.$nextTick(() => {
          this.syncRefSelectedNames();
        });
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.refIdsStr) this.form.refIds = this.refIdsStr.split(',').map(Number);
          else this.form.refIds = [];
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
    openRefSelector() {
      if (this.form.useType === '1') {
        this.openCategorySelector();
      } else if (this.form.useType === '2') {
        this.openGoodsSelector();
      }
    },
    openCategorySelector() {
      this.categorySelectOpen = true;
      treeCategory({ status: '0' }).then(res => {
        this.categoryTreeData = res.data || [];
        this.$nextTick(() => {
          if (this.$refs.categoryTree) {
            const ids = this.refIdsStr ? this.refIdsStr.split(',').map(Number) : [];
            this.$refs.categoryTree.setCheckedKeys(ids);
          }
        });
      });
    },
    confirmCategorySelect() {
      if (!this.$refs.categoryTree) return;
      const nodes = this.$refs.categoryTree.getCheckedNodes(false, true) || [];
      const list = nodes.map(n => ({ id: n.categoryId, name: n.categoryName }));
      this.refSelectedList = list;
      this.refIdsStr = list.map(i => i.id).join(',');
      this.categorySelectOpen = false;
    },
    openGoodsSelector() {
      this.goodsSelectOpen = true;
      this.goodsSelectedMap = {};
      const ids = this.refIdsStr ? this.refIdsStr.split(',').map(Number) : [];
      if (ids.length) {
        Promise.all(ids.map(id => getGoods(id).then(r => r.data).catch(() => null))).then(items => {
          items.filter(Boolean).forEach(g => {
            this.$set(this.goodsSelectedMap, g.goodsId, g);
          });
          this.getGoodsList();
        });
      } else {
        this.getGoodsList();
      }
    },
    handleGoodsQuery() {
      this.goodsQuery.pageNum = 1;
      this.getGoodsList();
    },
    resetGoodsQuery() {
      this.goodsQuery = { pageNum: 1, pageSize: 10, goodsName: "", status: "1" };
      this.getGoodsList();
    },
    getGoodsList() {
      this.goodsLoading = true;
      listGoods(this.goodsQuery).then(res => {
        this.goodsList = res.rows || [];
        this.goodsTotal = res.total || 0;
        this.goodsLoading = false;
        this.$nextTick(() => {
          this.restoreGoodsSelection();
        });
      }).catch(() => {
        this.goodsLoading = false;
      });
    },
    restoreGoodsSelection() {
      if (!this.$refs.goodsTable) return;
      this.goodsList.forEach(row => {
        const checked = !!this.goodsSelectedMap[row.goodsId];
        this.$refs.goodsTable.toggleRowSelection(row, checked);
      });
    },
    handleGoodsSelectionChange(selection) {
      const pageIds = this.goodsList.map(item => item.goodsId);
      pageIds.forEach(id => {
        delete this.goodsSelectedMap[id];
      });
      selection.forEach(item => {
        this.$set(this.goodsSelectedMap, item.goodsId, item);
      });
    },
    confirmGoodsSelect() {
      const list = Object.values(this.goodsSelectedMap).map(g => ({ id: g.goodsId, name: g.goodsName }));
      this.refSelectedList = list;
      this.refIdsStr = list.map(i => i.id).join(',');
      this.goodsSelectOpen = false;
    },
    removeRefItem(id) {
      this.refSelectedList = this.refSelectedList.filter(i => i.id !== id);
      this.refIdsStr = this.refSelectedList.map(i => i.id).join(',');
    },
    syncRefSelectedNames() {
      const ids = this.refIdsStr ? this.refIdsStr.split(',').map(Number) : [];
      if (!ids.length) {
        this.refSelectedList = [];
        return;
      }
      if (this.form.useType === '1') {
        treeCategory({ status: '0' }).then(res => {
          const map = {};
          const walk = (arr) => {
            (arr || []).forEach(n => {
              map[n.categoryId] = n.categoryName;
              if (n.children && n.children.length) walk(n.children);
            });
          };
          walk(res.data || []);
          this.refSelectedList = ids.map(id => ({ id, name: map[id] || `分类${id}` }));
        });
      } else if (this.form.useType === '2') {
        Promise.all(ids.map(id => getGoods(id).then(r => r.data).catch(() => null))).then(items => {
          const map = {};
          items.filter(Boolean).forEach(g => { map[g.goodsId] = g.goodsName; });
          this.refSelectedList = ids.map(id => ({ id, name: map[id] || `商品${id}` }));
        });
      }
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
      this.issueSelectedMap = {};
      this.issueQuery.pageNum = 1;
      this.issueQuery.phone = "";
      this.issueQuery.nickname = "";
      this.issueOpen = true;
      this.getIssueUserList();
    },
    handleIssueQuery() {
      this.issueQuery.pageNum = 1;
      this.getIssueUserList();
    },
    resetIssueQuery() {
      this.issueQuery = { pageNum: 1, pageSize: 10, phone: "", nickname: "", status: "0" };
      this.getIssueUserList();
    },
    getIssueUserList() {
      this.issueLoading = true;
      listH5User(this.issueQuery).then(res => {
        this.issueUserList = res.rows || [];
        this.issueUserTotal = res.total || 0;
        this.issueLoading = false;
        this.$nextTick(() => {
          this.restoreIssueSelection();
        });
      }).catch(() => {
        this.issueLoading = false;
      });
    },
    restoreIssueSelection() {
      if (!this.$refs.issueUserTable) return;
      this.issueUserList.forEach(row => {
        const checked = !!this.issueSelectedMap[row.userId];
        this.$refs.issueUserTable.toggleRowSelection(row, checked);
      });
    },
    handleIssueSelectionChange(selection) {
      const pageIds = this.issueUserList.map(item => item.userId);
      pageIds.forEach(id => {
        delete this.issueSelectedMap[id];
      });
      selection.forEach(item => {
        this.$set(this.issueSelectedMap, item.userId, item);
      });
    },
    removeIssueUserByName(name) {
      const target = Object.values(this.issueSelectedMap).find(item => (item.nickname || item.phone || `用户${item.userId}`) === name);
      if (!target) return;
      this.$delete(this.issueSelectedMap, target.userId);
      this.$nextTick(() => {
        this.restoreIssueSelection();
      });
    },
    submitIssue() {
      if (!this.issueSelectedIds.length) {
        this.$modal.msgError("请选择发放用户"); return;
      }
      issueCoupon(this.currentIssueId, this.issueSelectedIds).then(res => {
        this.$modal.msgSuccess(res.msg || "发放成功");
        this.issueOpen = false;
        this.getList();
      });
    }
  }
};
</script>
