<template>
  <div class="app-container">
    <el-form :inline="true" :model="q" size="small">
      <el-form-item label="手机号">
        <el-input v-model="q.phone" clearable @keyup.enter.native="getList" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="q.nickname" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="q.status" clearable placeholder="全部" style="width:120px">
          <el-option label="正常" value="0" />
          <el-option label="冻结" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="getList">搜索</el-button>
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd"
          v-hasPermi="['points:h5user:add']">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" prop="userId" width="100" />
      <el-table-column label="昵称" prop="nickname" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="手机号" prop="phone" width="130">
        <template #default="{ row }">
          {{ maskPhone(row.phone) }}
        </template>
      </el-table-column>
      <el-table-column label="积分余额" prop="pointsBalance" width="100" />
      <el-table-column label="累计获得" prop="totalEarned" width="100" />
      <el-table-column label="累计消耗" prop="totalConsumed" width="100" />
      <el-table-column label="连续签到" prop="continuousDays" width="100" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '冻结' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" prop="registerTime" width="160" />
      <el-table-column label="操作" width="330" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['points:h5user:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['points:h5user:remove']">删除</el-button>
          <el-button size="mini" type="text" @click="openAdjust(scope.row)"
            v-hasPermi="['points:h5user:adjust']">调整积分</el-button>
          <el-button size="mini" type="text" @click="openResetPwd(scope.row)"
            v-hasPermi="['points:h5user:resetPwd']">重置密码</el-button>
          <el-button v-if="scope.row.status === '0'" size="mini" type="text" style="color:#F56C6C"
            @click="changeStatus(scope.row, '1')" v-hasPermi="['points:h5user:freeze']">冻结</el-button>
          <el-button v-else size="mini" type="text" style="color:#67C23A" @click="changeStatus(scope.row, '0')"
            v-hasPermi="['points:h5user:freeze']">解冻</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="q.pageNum" :limit.sync="q.pageSize"
      @pagination="getList" />

    <!-- 调整积分弹窗 -->
    <el-dialog title="调整积分" :visible.sync="adjOpen" width="450px" append-to-body>
      <el-form ref="adjRef" :model="adj" :rules="adjRules" label-width="100px">
        <el-form-item label="用户"><span>{{ adj.nickname }} ({{ maskPhone(adj.phone) }})</span></el-form-item>
        <el-form-item label="当前余额"><span>{{ adj.currentBalance }}</span></el-form-item>
        <el-form-item label="调整方向" prop="changeType">
          <el-radio-group v-model="adj.changeType">
            <el-radio label="0">增加</el-radio>
            <el-radio label="1">扣减</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分" prop="points">
          <el-input-number v-model="adj.points" :min="1" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="adj.reason" type="textarea" :rows="2" placeholder="操作日志会记录此原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdj">确 定</el-button>
        <el-button @click="adjOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增/修改用户弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password"
            :placeholder="title === '添加H5用户' ? '请输入登录密码' : '如需修改密码请使用重置密码功能'" :disabled="title !== '添加H5用户'" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog title="重置密码" :visible.sync="resetPwdOpen" width="450px" append-to-body>
      <el-form ref="resetPwdForm" :model="resetPwdData" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="用户"><span>{{ resetPwdData.nickname }} ({{ maskPhone(resetPwdData.phone)
            }})</span></el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetPwdData.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPwdData.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitResetPwd">确 定</el-button>
        <el-button @click="resetPwdOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listH5User, changeStatus as changeStatusApi, adjustPoints,
  addH5User, updateH5User, delH5User, resetPwdH5User
} from '@/api/points/h5user'

export default {
  name: 'H5User',
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      q: {
        pageNum: 1,
        pageSize: 10,
        phone: '',
        nickname: '',
        status: ''
      },
      // 调整积分
      adjOpen: false,
      adj: {},
      adjRules: {
        changeType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        points: [{ required: true, message: '请输入积分', trigger: 'blur' }]
      },
      // 新增/修改用户
      open: false,
      title: "",
      form: {},
      rules: {
        phone: [
          { required: true, message: "手机号不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }
        ],
        nickname: [{ required: true, message: "昵称不能为空", trigger: "blur" }],
        // idNumber: [{ validator: validateIdNumber, trigger: "blur" }],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
          { min: 6, message: "密码长度不能少于6个字符", trigger: "blur" }
        ]
      },
      // 重置密码
      resetPwdOpen: false,
      resetPwdData: {
        userId: null,
        nickname: '',
        phone: '',
        newPassword: '',
        confirmPassword: ''
      },
      resetPwdRules: {
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          { min: 6, max: 20, message: "密码长度在6到20个字符之间", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "请再次输入新密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (value !== this.resetPwdData.newPassword) {
                callback(new Error("两次输入的密码不一致"))
              } else {
                callback()
              }
            },
            trigger: "blur"
          }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 脱敏手机号：只保留前3位和后4位
    maskPhone(phone) {
      if (!phone) return ''
      return phone.slice(0, 3) + '****' + phone.slice(-4)
    },
    getList() {
      this.loading = true
      listH5User(this.q).then(res => {
        this.list = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    changeStatus(row, status) {
      const txt = status === '0' ? '解冻' : '冻结'
      this.$modal.confirm(`确认${txt}该用户？`).then(() => {
        return changeStatusApi(row.userId, status)
      }).then(() => {
        this.$modal.msgSuccess(`已${txt}`)
        this.getList()
      }).catch(() => { })
    },
    openAdjust(row) {
      this.adj = {
        userId: row.userId,
        nickname: row.nickname,
        phone: row.phone,
        currentBalance: row.pointsBalance,
        changeType: '0',
        points: 100,
        reason: ''
      }
      this.adjOpen = true
    },
    submitAdj() {
      this.$refs.adjRef.validate(valid => {
        if (!valid) return
        adjustPoints({
          userId: this.adj.userId,
          changeType: this.adj.changeType,
          points: this.adj.points,
          reason: this.adj.reason
        }).then(() => {
          this.$modal.msgSuccess('调整成功')
          this.adjOpen = false
          this.getList()
        })
      })
    },
    cancel() {
      this.resetForm("form");
      this.open = false;
    },
    handleAdd() {
      // 1. 先重置表单校验和显示值
      this.resetForm("form");
      // 2. 重新初始化 form 对象，清空所有字段
      this.form = {
        userId: undefined,
        phone: '',
        nickname: '',
        name: '',
        idNumber: '',
        password: ''   // 新增时需要密码
      };
      this.open = true;
      this.title = "添加H5用户";
      // 新增时清空密码字段（防止残留）
      this.form.password = ''
    },
    handleUpdate(row) {
      this.resetForm("form");
      // 复制用户数据，但不包含密码字段（后端不会返回密码）
      this.form = Object.assign({}, row);
      // 确保没有密码字段
      // delete this.form.password
      this.open = true;
      this.title = "修改H5用户";
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != null) {
            // 修改用户，删除可能存在的password字段
            const updateData = { ...this.form }
            delete updateData.password
            updateH5User(updateData).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            // 新增用户，必须包含密码
            if (!this.form.password) {
              this.$modal.msgError("请设置登录密码");
              return;
            }
            addH5User(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const userIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除该用户？').then(function () {
        return delH5User(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    // 重置密码相关
    openResetPwd(row) {
      this.resetPwdData = {
        userId: row.userId,
        nickname: row.nickname,
        phone: row.phone,
        newPassword: '',
        confirmPassword: ''
      }
      this.resetPwdOpen = true
      // 清除表单验证状态
      this.$nextTick(() => {
        if (this.$refs.resetPwdForm) {
          this.$refs.resetPwdForm.clearValidate()
        }
      })
    },
    submitResetPwd() {
      this.$refs.resetPwdForm.validate(valid => {
        if (!valid) return
        resetPwdH5User({
          userId: this.resetPwdData.userId,
          newPassword: this.resetPwdData.newPassword
        }).then(() => {
          this.$modal.msgSuccess("密码重置成功")
          this.resetPwdOpen = false
          this.getList()
        }).catch(() => {
          // 错误已由拦截器处理
        })
      })
    }
  }
}
</script>