<template>
  <div class="book-manage">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.bookName" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable style="width: 200px;">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.categoryName"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="全部" :value="null" />
            <el-option label="在架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadBooks">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>图书列表</span>
          <el-button type="primary" @click="handleAdd" v-if="hasManagePermission">
            <el-icon><Plus /></el-icon>
            添加图书
          </el-button>
        </div>
      </template>
      <el-table :data="bookList" border style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bookName" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="160" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="totalCount" label="总数" width="70" align="center" />
        <el-table-column prop="availableCount" label="可借" width="70" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.availableCount > 0 ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
              {{ row.availableCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="90" align="center">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '在架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right" v-if="hasManagePermission">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名" prop="bookName">
          <el-input v-model="form.bookName" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="form.publishDate"
            type="date"
            placeholder="请选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.categoryName"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="总数" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="可借数" prop="availableCount">
          <el-input-number v-model="form.availableCount" :min="0" :max="form.totalCount" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入存放位置" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit } from '@element-plus/icons-vue'
import { bookAPI, categoryAPI } from '../api/supabase'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const bookList = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加图书')
const submitLoading = ref(false)
const formRef = ref(null)

const searchForm = ref({
  bookName: '',
  author: '',
  categoryId: null,
  status: null
})

const form = ref({
  id: null,
  isbn: '',
  bookName: '',
  author: '',
  publisher: '',
  publishDate: '',
  categoryId: null,
  description: '',
  totalCount: 0,
  availableCount: 0,
  price: 0,
  location: '',
  status: 1
})

const rules = {
  bookName: [{ required: true, message: '请输入书名', trigger: 'blur' }]
}

const hasManagePermission = computed(() => {
  return userInfo.roles?.some(r => r.roleCode === 'SUPER_ADMIN' || r.roleCode === 'LIBRARIAN' || r.roleCode === 'TEACHER')
})

const loadBooks = async () => {
  try {
    const params = {}
    if (searchForm.value.bookName) params.bookName = searchForm.value.bookName
    if (searchForm.value.author) params.author = searchForm.value.author
    if (searchForm.value.categoryId) params.categoryId = searchForm.value.categoryId
    if (searchForm.value.status !== null) params.status = searchForm.value.status

    const books = await bookAPI.list(params)
    bookList.value = books || []
  } catch (error) {
    console.error('加载图书列表失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const cats = await categoryAPI.list()
    categories.value = cats || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const resetSearch = () => {
  searchForm.value = {
    bookName: '',
    author: '',
    categoryId: null,
    status: null
  }
  loadBooks()
}

const handleAdd = () => {
  form.value = {
    id: null,
    isbn: '',
    bookName: '',
    author: '',
    publisher: '',
    publishDate: '',
    categoryId: null,
    description: '',
    totalCount: 0,
    availableCount: 0,
    price: 0,
    location: '',
    status: 1
  }
  dialogTitle.value = '添加图书'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogTitle.value = '编辑图书'
  dialogVisible.value = true
}

const toggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await ElMessageBox.confirm(`确定要${newStatus === 1 ? '上架' : '下架'}该图书吗？`, '提示', {
      type: 'warning'
    })
    await bookAPI.updateStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.value.id) {
          await bookAPI.update(form.value)
          ElMessage.success('更新成功')
        } else {
          await bookAPI.add(form.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadBooks()
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadBooks()
  loadCategories()
})
</script>

<style scoped>
.book-manage {
  padding: 0;
}

.search-card {
  border-radius: 12px;
  border: none;
}

.search-form {
  margin: 0;
}

.table-card {
  border-radius: 12px;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}
</style>
