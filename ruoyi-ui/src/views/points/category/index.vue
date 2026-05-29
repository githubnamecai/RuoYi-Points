<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="分类名称">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入分类名称"
          clearable
          @keyup.enter="getList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="getList">搜索</el-button>
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd(null)">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 树形表格 -->
    <el-table
      v-loading="loading"
      :data="treeList"
      row-key="categoryId"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      default-expand-all
      border
    >
      <el-table-column label="分类名称" prop="categoryName" min-width="200"/>
      <el-table-column label="排序" prop="orderNum" width="80" align="center"/>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
           {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160"/>
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button link type="primary" icon="el-icon-plus" @click="handleAdd(scope.row)">新增子级</el-button>
          <el-button link type="primary" icon="el-icon-edit" @click="handleEdit(scope.row)">修改</el-button>
          <el-button link type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="500px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级分类">
          <el-cascader
            v-model="form.parentId"
            :options="treeselectData"
            :props="{
              value: 'id',
              label: 'label',
              children: 'children',
              checkStrictly: true,
              emitPath: true
            }"
            placeholder="顶级分类"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称"/>
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { treeCategory, treeselect, getCategory, addCategory, updateCategory, delCategory } from '@/api/points/category'

export default {
  name: 'Category',
  data() {
    return {
      loading: false,
      treeList: [],
      treeselectData: [],
      open: false,
      title: '',
      queryParams: { categoryName: '' },
      form: {
        parentId: [],
        categoryName: '',
        orderNum: 0,
        status: 0
      },
      rules: {
        categoryName: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  // ✅ 已修复：这里加了冒号
  methods: {
    // 获取列表
    getList() {
      this.loading = true
      treeCategory(this.queryParams).then(res => {
        this.treeList = res.data
        this.loading = false
      })
    },

    // 加载树形下拉数据
    loadTreeselect() {
      treeselect().then(res => {
        this.treeselectData = res.data
      })
    },

    // 重置表单
    reset() {
      this.form = {
        parentId: [],
        categoryName: '',
        orderNum: 0,
        status: 0
      }
    },

    // 递归获取节点路径
    getNodePath(tree, id, path = []) {
      for (let node of tree) {
        path.push(node.id)
        if (node.id === id) return path
        if (node.children && node.children.length) {
          const result = this.getNodePath(node.children, id, [...path])
          if (result) return result
        }
        path.pop()
      }
      return null
    },

    // 新增
    handleAdd(row) {
      this.reset()
      this.loadTreeselect()
      
      if (row) {
        const path = this.getNodePath(this.treeselectData, row.categoryId)
        this.form.parentId = path || [row.categoryId]
      }
      this.open = true
      this.title = '新增分类'
    },

    // 修改
    handleEdit(row) {
      this.reset()
      this.loadTreeselect()
      getCategory(row.categoryId).then(res => {
        this.form = res.data
        if (this.form.parentId && this.form.parentId !== 0) {
          const path = this.getNodePath(this.treeselectData, this.form.parentId)
          this.form.parentId = path || [this.form.parentId]
        } else {
          this.form.parentId = []
        }
        this.open = true
        this.title = '修改分类'
      })
    },

    // 提交
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const data = { ...this.form }
        if (data.parentId && data.parentId.length > 0) {
          data.parentId = data.parentId[data.parentId.length - 1]
        } else {
          data.parentId = 0
        }

        const req = data.categoryId ? updateCategory : addCategory
        req(data).then(() => {
          this.$message.success('操作成功')
          this.open = false
          this.getList()
        })
      })
    },

    // 删除
    handleDelete(row) {
      this.$confirm('此操作将永久删除该分类, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delCategory(row.categoryId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    }
  }
}
</script>