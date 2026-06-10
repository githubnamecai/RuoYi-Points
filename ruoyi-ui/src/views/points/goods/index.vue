<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd"
          v-hasPermi="['points:goods:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" :disabled="multiple" @click="handleDelete()"
          v-hasPermi="['points:goods:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="ID" prop="goodsId" width="80" />
      <!-- <el-table-column label="封面" width="80">
        <template slot-scope="scope">
          <el-image v-if="scope.row.coverImg" :src="scope.row.coverImg"
            style="width:48px;height:48px;border-radius:4px" />
        </template>
      </el-table-column> -->
      <el-table-column label="封面图" align="center" prop="coverImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.coverImg" :width="50" :height="50" :border-radius="4" />
        </template>
      </el-table-column>

      <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="分类" prop="categoryName" width="120" />
      <el-table-column label="类型" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.goodsType === '1' ? 'success' : ''">
            {{ scope.row.goodsType === '1' ? '虚拟' : '实物' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="积分" prop="points" width="90" />
      <el-table-column label="金额" prop="price" width="100" />
      <el-table-column label="优惠金额" prop="discountPrice" width="100" />
      <el-table-column label="原价" prop="originalPrice" width="100" />
      <el-table-column label="库存" prop="stock" width="90" />
      <el-table-column label="销量" prop="sales" width="90" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" active-value="1" inactive-value="0"
            @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['points:goods:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['points:goods:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="goodsRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="form.goodsName" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-cascader v-model="form.categoryId" :options="categoryOptions"
            :props="{ value: 'id', label: 'label', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="选择分类" clearable style="width: 100%" />
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
        <el-form-item v-if="form.goodsType === '1'" label="积分" prop="points">
          <el-input-number v-model="form.points" :min="0" />
        </el-form-item>
        <el-form-item v-if="form.goodsType === '0'" label="金额" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item v-if="form.goodsType === '0'" label="优惠金额" prop="discountPrice">
          <el-input-number v-model="form.discountPrice" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="0.01" />
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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGoods, getGoods, addGoods, updateGoods, delGoods, changeGoodsStatus } from '@/api/points/goods'
import { treeselect as listCategoryTreeselect } from '@/api/points/category'

export default {
  name: 'Goods',
  data() {
    return {
      list: [],
      open: false,
      loading: true,
      showSearch: true,
      ids: [],
      multiple: true,
      total: 0,
      title: '',
      categoryOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: '',
        status: '',
        goodsType: ''
      },
      form: {},
      rules: {
        goodsName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        goodsType: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
        points: [{ required: true, message: '请输入积分', trigger: 'blur' }],
        price: [{ required: true, message: '请输入金额', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getCategories()
  },
  // watch: {
  //   'form.goodsType'(val) {
  //     if (val === '0') {
  //       this.form.points = 0
  //     } else if (val === '1') {
  //       this.form.price = 0
  //       this.form.discountPrice = 0
  //     }
  //   }
  // },
  methods: {
    getList() {
      this.loading = true
      listGoods(this.queryParams).then(res => {
        this.list = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    getCategories() {
      listCategoryTreeselect().then(res => {
        this.categoryOptions = res.data
      })
    },
    reset() {
      this.form = {
        goodsId: undefined,
        goodsName: '',
        categoryId: undefined,
        goodsType: undefined,
        coverImg: '',
        points: 0,
        price: 0,
        discountPrice: 0,
        originalPrice: 0,
        stock: 0,
        sort: 0,
        status: '1',
        description: '',
        limitPerUser: 0
      }
      this.resetForm('goodsRef')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(sel) {
      this.ids = sel.map(s => s.goodsId)
      this.multiple = !sel.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增商品'
    },
    handleUpdate(row) {
      this.reset()
      getGoods(row.goodsId).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改商品'
      })
    },
    submitForm() {
      this.$refs.goodsRef.validate(valid => {
        if (!valid) return
        // 清除隐藏字段的校验残留
        // if (this.form.goodsType === '0') {
        //   this.$refs.goodsRef.clearValidate(['points'])
        // } else if (this.form.goodsType === '1') {
        //   this.$refs.goodsRef.clearValidate(['price', 'discountPrice'])
        // }
        const op = this.form.goodsId ? updateGoods : addGoods
        op(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleDelete(row) {
      const delIds = row ? [row.goodsId] : this.ids
      this.$modal.confirm('确定删除选中商品？').then(() => {
        return delGoods(delIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => { })
    },
    handleStatusChange(row) {
      changeGoodsStatus(row.goodsId, row.status).then(() => {
        this.$modal.msgSuccess('状态已更新')
      }).catch(() => {
        row.status = row.status === '1' ? '0' : '1'
      })
    }
  }
}
</script>
