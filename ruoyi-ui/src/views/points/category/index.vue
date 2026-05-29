<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="搜索" clearable @keyup.enter.native="getList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="getList">搜索</el-button>
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd(null)" v-hasPermi="['points:category:add']">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="treeList" row-key="categoryId"
              :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" default-expand-all>
      <el-table-column label="分类名称" prop="categoryName" min-width="200" />
      <el-table-column label="排序" prop="orderNum" width="80" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status==='0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
                     v-hasPermi="['points:category:add']">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)"
                     v-hasPermi="['points:category:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C"
                     @click="handleDelete(scope.row)"
                     v-hasPermi="['points:category:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级分类">
          <el-cascader
            v-model="form.parentId"
            :options="treeselectData"
            :props="{ value: 'id', label: 'label', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="顶级分类"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="open=false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCategory, treeCategory, treeselect, getCategory, addCategory, updateCategory, delCategory } from '@/api/points/category'

export default {
  name: 'Category',
  data() {
    return {
      loading: false,
      treeList: [],
      treeselectData: [],
      open: false,
      title: '',
      queryParams: {
        categoryName: ''
      },
      form: {},
      rules: {
        categoryName: [{ required: true, message: '分类名称必填', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      treeCategory(this.queryParams).then(res => {
        this.treeList = res.data
        this.loading = false
      })
    },
    loadTreeselect() {
      treeselect().then(res => {
        this.treeselectData = [{ id: 0, label: '顶级分类', children: res.data }]
      })
    },
    reset() {
      this.form = {
        parentId: 0,
        categoryName: '',
        orderNum: 0,
        status: '0',
        icon: ''
      }
    },
    handleAdd(row) {
      this.reset()
      this.loadTreeselect()
      if (row) this.form.parentId = row.categoryId
      this.open = true
      this.title = '新增分类'
    },
    handleEdit(row) {
      this.reset()
      this.loadTreeselect()
      getCategory(row.categoryId).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改分类'
      })
    },
    submit() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return
        const op = this.form.categoryId ? updateCategory : addCategory
        op(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确定删除"${row.categoryName}"？`).then(() => {
        return delCategory(row.categoryId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
