<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
          <el-option label="上架" value="1" />
          <el-option label="下架" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="goodsType">
        <el-select v-model="queryParams.goodsType" placeholder="全部" clearable style="width: 140px">
          <el-option label="实物" value="0" />
          <el-option label="虚拟" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['points:goods:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple"
          @click="handleDelete()" v-hasPermi="['points:goods:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="ID" prop="goodsId" width="80" />
      <el-table-column label="封面" width="80">
        <template #default="scope">
          <el-image v-if="scope.row.coverImg" :src="scope.row.coverImg" style="width:48px;height:48px;border-radius:4px"/>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="分类" prop="categoryName" width="120" />
      <el-table-column label="类型" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.goodsType==='1' ? 'success' : ''">
            {{ scope.row.goodsType === '1' ? '虚拟' : '实物' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="积分" prop="points" width="90" />
      <el-table-column label="库存" prop="stock" width="90" />
      <el-table-column label="销量" prop="sales" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-switch v-model="scope.row.status" active-value="1" inactive-value="0"
                     @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['points:goods:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['points:goods:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="goodsRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="form.goodsName" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-tree-select v-model="form.categoryId" :data="categoryOptions" :props="{ value: 'id', label: 'label', children: 'children' }"
                          value-key="id" placeholder="选择分类" check-strictly />
        </el-form-item>
        <el-form-item label="类型" prop="goodsType">
          <el-radio-group v-model="form.goodsType">
            <el-radio label="0">实物</el-radio>
            <el-radio label="1">虚拟</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图">
          <image-upload v-model="form.coverImg" :limit="1" />
        </el-form-item>
        <el-form-item label="积分" prop="points">
          <el-input-number v-model="form.points" :min="0" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="限兑数量">
          <el-input-number v-model="form.limitPerUser" :min="0" /> <span style="margin-left:8px;color:#999">0=不限制</span>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="1">上架</el-radio>
            <el-radio label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="详情">
          <editor v-model="form.description" :min-height="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Goods">
import { listGoods, getGoods, addGoods, updateGoods, delGoods, changeGoodsStatus } from '@/api/points/goods'
import { treeselect as listCategoryTreeselect } from '@/api/points/category'

const { proxy } = getCurrentInstance()

const list = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const categoryOptions = ref([])

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, goodsName: '', status: '', goodsType: '' },
  rules: {
    goodsName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
    categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    points: [{ required: true, message: '请输入积分', trigger: 'blur' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listGoods(queryParams.value).then(res => {
    list.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function getCategories() {
  listCategoryTreeselect().then(res => { categoryOptions.value = res.data })
}

function reset() {
  form.value = { goodsId: undefined, goodsName: '', categoryId: undefined, goodsType: '0',
    coverImg: '', points: 0, stock: 0, sort: 0, status: '1', description: '', limitPerUser: 0 }
  proxy.resetForm('goodsRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryForm'); handleQuery() }
function handleSelectionChange(sel) { ids.value = sel.map(s => s.goodsId); multiple.value = !sel.length }
function handleAdd() { reset(); open.value = true; title.value = '新增商品' }
function handleUpdate(row) {
  reset()
  getGoods(row.goodsId).then(res => { form.value = res.data; open.value = true; title.value = '修改商品' })
}
function submitForm() {
  proxy.$refs.goodsRef.validate(valid => {
    if (!valid) return
    const op = form.value.goodsId ? updateGoods : addGoods
    op(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功'); open.value = false; getList()
    })
  })
}
function cancel() { open.value = false; reset() }
function handleDelete(row) {
  const delIds = row ? [row.goodsId] : ids.value
  proxy.$modal.confirm('确定删除选中商品？').then(() => delGoods(delIds))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}
function handleStatusChange(row) {
  changeGoodsStatus(row.goodsId, row.status).then(() => proxy.$modal.msgSuccess('状态已更新')).catch(() => {
    row.status = row.status === '1' ? '0' : '1'
  })
}

getList(); getCategories()
</script>
