<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="queryParams.address" placeholder="请输入地址" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="联系电话" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="对接人" prop="contactPerson">
        <el-input
          v-model="queryParams.contactPerson"
          placeholder="请输入对接人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['qrcode:qrcodestore:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['qrcode:qrcodestore:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['qrcode:qrcodestore:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['qrcode:qrcodestore:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="qrcodestoreList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />

      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="地址" align="center" prop="address" />
      <!-- <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="用途" align="center" prop="purpose" /> -->
      <!-- <el-table-column label="二维码访问链接" align="center" prop="qrcodeUrl" /> -->
      <!-- <el-table-column label="二维码码照片" align="center" prop="qrPhoto" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.qrPhoto" :width="50" :height="50" />
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="对接人" align="center" prop="contactPerson" /> -->
      <el-table-column label="扫码次数" align="center" prop="scanCount" />
      <el-table-column label="排序号" align="center" prop="sortNum" />
      <el-table-column label="创建人" align="center" prop="createBy" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="更新人" align="center" prop="updateBy" />
      <el-table-column label="更新时间" align="center" prop="updateTime" />
      <el-table-column label="备注信息" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['qrcode:qrcodestore:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['qrcode:qrcodestore:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改二维码点位信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="24">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用途" prop="purpose">
              <el-input v-model="form.purpose" placeholder="请输入用途" />
            </el-form-item>
          </el-col> -->
          <!-- <el-col :span="24">
            <el-form-item label="二维码访问链接" prop="qrcodeUrl">
              <el-input v-model="form.qrcodeUrl" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="对接人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入对接人" />
            </el-form-item>
          </el-col> -->
          <el-col :span="24">
            <el-form-item label="排序号" prop="sortNum">
              <!-- v-model绑定数字，step步长1，min最小0 -->
              <el-input-number v-model="form.sortNum" :min="0" :step="1" placeholder="数字越小越靠前" style="width: 100%" />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="24">
            <el-form-item label="扩展字段1" prop="extInfo">
              <el-input v-model="form.extInfo" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col> -->
          <el-col :span="24">
            <el-form-item label="备注信息" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listQrcodestore, getQrcodestore, delQrcodestore, addQrcodestore, updateQrcodestore } from "@/api/qrcode/qrcodestore"

export default {
  name: "Qrcodestore",
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
      // 二维码点位信息表格数据
      qrcodestoreList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        address: null,
        phone: null,
        contactPerson: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询二维码点位信息列表 */
    getList() {
      this.loading = true
      listQrcodestore(this.queryParams).then(response => {
        this.qrcodestoreList = response.rows
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
        id: null,
        name: null,
        address: null,
        phone: null,
        purpose: null,
        qrcodeUrl: null,
        qrPhoto: null,
        contactPerson: null,
        scanCount: null,
        sortNum: null,
        extInfo: null,
        createBy: null,
        createName: null,
        createTime: null,
        updateBy: null,
        updateName: null,
        updateTime: null,
        remark: null,
        delFlag: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加二维码点位信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getQrcodestore(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改二维码点位信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateQrcodestore(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addQrcodestore(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除二维码点位信息编号为"' + ids + '"的数据项？').then(function () {
        return delQrcodestore(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => { })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('qrcode/qrcodestore/export', {
        ...this.queryParams
      }, `qrcodestore_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
