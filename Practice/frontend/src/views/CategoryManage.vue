<template>
  <div class="category-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书分类管理</span>
          <el-button type="primary" @click="handleAdd(null)">添加分类</el-button>
        </div>
      </template>
      <el-table :data="categoryList" border row-key="id" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleAdd(row)">添加子分类</el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="父分类" v-if="form.parentId !== 0">
          <el-input v-model="parentCategoryName" disabled />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryAPI } from '../api/axios'

const categoryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const parentCategoryName = ref('')

const form = ref({
  id: null,
  categoryName: '',
  parentId: 0,
  sortOrder: 0,
  description: '',
  status: 1
})

const loadCategories = async () => {
  try {
    const response = await categoryAPI.tree()
    categoryList.value = response.data.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const handleAdd = (parent) => {
  form.value = {
    id: null,
    categoryName: '',
    parentId: parent ? parent.id : 0,
    sortOrder: 0,
    description: '',
    status: 1
  }
  parentCategoryName.value = parent ? parent.categoryName : ''
  dialogTitle.value = '添加分类'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogTitle.value = '编辑分类'
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      type: 'warning'
    })
    await categoryAPI.delete(id)
    await loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    if (form.value.id) {
      await categoryAPI.update(form.value)
    } else {
      await categoryAPI.add(form.value)
    }
    dialogVisible.value = false
    await loadCategories()
  } catch (error) {
    console.error('保存分类失败:', error)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
