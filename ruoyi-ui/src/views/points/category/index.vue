<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="搜索" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd(null)" v-hasPermi="['points:category:add']">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="treeList" row-key="categoryId"
              :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" default-expand-all>
      <el-table-column label="分类名称" prop="categoryName" min-width="200" />
      <el-table-column label="排序" prop="orderNum" width="80" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status==='0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)"
                     v-hasPermi="['points:category:add']">新增</el-button>
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)"
                     v-hasPermi="['points:category:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['points:category:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级分类">
          <el-tree-select v-model="form.parentId" :data="treeselectData" :props="{ value: 'id', label: 'label', children: 'children' }"
                          check-strictly placeholder="顶级分类" />
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
      <template #footer>
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="open=false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Category">
import { listCategory, treeCategory, treeselect, getCategory, addCategory, updateCategory, delCategory } from '@/api/points/category'
const { proxy } = getCurrentInstance()
const loading = ref(false)
const treeList = ref([])
const treeselectData = ref([])
const open = ref(false)
const title = ref('')
const queryParams = ref({ categoryName: '' })
const form = ref({})
const rules = { categoryName: [{ required: true, message: '分类名称必填', trigger: 'blur' }] }

function getList() {
  loading.value = true
  treeCategory(queryParams.value).then(res => { treeList.value = res.data; loading.value = false })
}
function loadTreeselect() { treeselect().then(res => { treeselectData.value = [{ id: 0, label: '顶级分类', children: res.data }] }) }
function reset() { form.value = { parentId: 0, categoryName: '', orderNum: 0, status: '0', icon: '' } }
function handleAdd(row) { reset(); loadTreeselect(); if (row) form.value.parentId = row.categoryId; open.value = true; title.value = '新增分类' }
function handleEdit(row) {
  reset(); loadTreeselect()
  getCategory(row.categoryId).then(res => { form.value = res.data; open.value = true; title.value = '修改分类' })
}
function submit() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const op = form.value.categoryId ? updateCategory : addCategory
    op(form.value).then(() => { proxy.$modal.msgSuccess('保存成功'); open.value = false; getList() })
  })
}
function handleDelete(row) {
  proxy.$modal.confirm(`确定删除"${row.categoryName}"？`).then(() => delCategory(row.categoryId))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

getList()
</script>
