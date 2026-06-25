<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="二维码" prop="qrcodeId">
        <el-select v-model="queryParams.qrcodeId" placeholder="请选择二维码" clearable @change="handleQuery">
          <el-option v-for="item in qrcodestoreList" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备型号" prop="deviceModel">
        <el-input v-model="queryParams.deviceModel" placeholder="请输入设备型号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="系统版本" prop="osVersion">
        <el-input v-model="queryParams.osVersion" placeholder="请输入系统版本" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="浏览器名称" prop="browserName">
        <el-input v-model="queryParams.browserName" placeholder="请输入浏览器名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['scan:scan:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['scan:scan:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['scan:scan:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['scan:scan:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="scanList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="二维码" align="center" prop="qrcodeId">
        <template #default="{ row }">
          <span>{{ getScanName(row.qrcodeId).name }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="用户姓名" align="center" prop="userName" />
      <el-table-column label="用户昵称" align="center" prop="nickName" />
      <el-table-column label="手机号" align="center" prop="phone" /> -->
      <!-- <el-table-column label="访问方式" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.visitType === 0 ? '链接跳转' : '扫码访问' }}</span>
        </template>
      </el-table-column> -->
      <el-table-column label="扫码时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IP地址" align="center" prop="ip" />
      <el-table-column label="设备型号" align="center" prop="deviceModel" />
      <el-table-column label="系统版本" align="center" prop="osVersion" />
      <el-table-column label="浏览器名称" align="center" prop="browserName" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['scan:scan:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['scan:scan:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改扫码统计对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">

            <el-form-item label="二维码" prop="qrcodeId">
              <el-select v-model="form.qrcodeId" placeholder="请选择二维码" clearable >
                <el-option v-for="item in qrcodestoreList" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="24">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID" />
            </el-form-item>
          </el-col> -->
          <!-- <el-col :span="24">
            <el-form-item label="用户姓名" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col> -->
          <el-col :span="24">
            <el-form-item label="扫码时间" prop="startTime">
              <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择扫码时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="IP地址" prop="ip">
              <el-input v-model="form.ip" placeholder="请输入IP地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设备型号" prop="deviceModel">
              <el-input v-model="form.deviceModel" placeholder="请输入设备型号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="系统版本" prop="osVersion">
              <el-input v-model="form.osVersion" placeholder="请输入系统版本" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="浏览器名称" prop="browserName">
              <el-input v-model="form.browserName" placeholder="请输入浏览器名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="navigator获取标识" prop="userAgent">
              <el-input v-model="form.userAgent" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注" />
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
import { listScan, getScan, delScan, addScan, updateScan } from "@/api/scan/scan"
import { listQrcodestore } from "@/api/qrcode/qrcodestore"

export default {
  name: "Scan",
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
      // 扫码统计表格数据
      scanList: [],
      qrcodestoreList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        qrcodeId: null,
        visitType: null,
        deviceModel: null,
        osVersion: null,
        browserName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    this.getList()
  },
  computed: {
    scanidMap() {
      const map = {};
      console.log("测试" + this.qrcodestoreList);
      this.qrcodestoreList.forEach(item => {
        console.log("测试" + item.name);
        map[item.id] = {
          name: item.name,
        };
      });
      return map;
    },
  },
  methods: {
    /** 查询扫码统计列表 */
    async getList() {
      this.loading = true;
      try {
        // 并行等待两个接口全部请求完成
        const [scanRes, qrcodeRes] = await Promise.all([
          listScan(this.queryParams),
          listQrcodestore()
        ]);
        this.scanList = scanRes.rows;
        this.total = scanRes.total;
        this.qrcodestoreList = qrcodeRes.rows;
      } catch (err) {
        console.error(err);
      } finally {
        // 两个接口都执行完毕后再关闭loading
        this.loading = false;
      }
    },
    getScanName(scanId) {
      return this.scanidMap[scanId] || '';
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
        qrcodeId: null,
        userId: null,
        userName: null,
        nickName: null,
        phone: null,
        visitType: null,
        startTime: null,
        ip: null,
        deviceModel: null,
        osVersion: null,
        browserName: null,
        userAgent: null,
        remark: null
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
      this.title = "添加扫码统计"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getScan(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改扫码统计"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateScan(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addScan(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除扫码统计编号为"' + ids + '"的数据项？').then(function () {
        return delScan(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => { })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('scan/scan/export', {
        ...this.queryParams
      }, `scan_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
