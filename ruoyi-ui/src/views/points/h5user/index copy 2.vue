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
      <el-table-column label="身份证号" prop="idNumber" />
      <el-table-column label="手机号" prop="phone" width="130" />
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
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['points:h5user:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['points:h5user:remove']">删除</el-button>
          <el-button size="mini" type="text" @click="openAdjust(scope.row)"
            v-hasPermi="['points:h5user:adjust']">调整积分</el-button>
          <el-button v-if="scope.row.status === '0'" size="mini" type="text" style="color:#F56C6C"
            @click="changeStatus(scope.row, '1')" v-hasPermi="['points:h5user:freeze']">冻结</el-button>
          <el-button v-else size="mini" type="text" style="color:#67C23A" @click="changeStatus(scope.row, '0')"
            v-hasPermi="['points:h5user:freeze']">解冻</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="q.pageNum" :limit.sync="q.pageSize" @pagination="getList" />

    <el-dialog title="调整积分" :visible.sync="adjOpen" width="450px" append-to-body>
      <el-form ref="adjRef" :model="adj" :rules="adjRules" label-width="100px">
        <el-form-item label="用户"><span>{{ adj.nickname }} ({{ adj.phone }})</span></el-form-item>
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
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入登录密码" />
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
import { listH5User, changeStatus as changeStatusApi, adjustPoints, addH5User, updateH5User, delH5User } from '@/api/points/h5user'

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
      adjOpen: false,
      adj: {},
      adjRules: {
        changeType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        points: [{ required: true, message: '请输入积分', trigger: 'blur' }]
      },
      open: false,
      title: "",
      form: {},
      rules: {
        phone: [
          { required: true, message: "手机号不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
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
      this.open = false;
      this.resetForm("form");
    },
    handleAdd() {
      this.resetForm("form");
      this.open = true;
      this.title = "添加H5用户";
    },
    handleUpdate(row) {
      this.resetForm("form");
      this.form = Object.assign({}, row);
      this.open = true;
      this.title = "修改H5用户";
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != null) {
            updateH5User(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
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
    }
  }
}
</script>
