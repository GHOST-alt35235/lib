<template>
  <div class="distribute-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>借阅管理</span>
          <div v-if="isAdmin">
            <el-button type="primary" @click="handleBorrow">办理借阅</el-button>
          </div>
        </div>
      </template>
      <el-table :data="recordList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bookName" label="图书" />
        <el-table-column prop="username" label="借阅用户" width="100" />
        <el-table-column prop="operatorName" label="操作员" width="100" />
        <el-table-column prop="borrowDate" label="借阅时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.borrowDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应还时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.returnDate) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="180" v-if="isAdmin">
          <template #default="{ row }">
            <el-button type="primary" size="small" v-if="row.status === 'BORROWING'" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" v-if="row.status === 'BORROWING'" @click="handleReturn(row.id)">
              归还
            </el-button>
            <el-button type="danger" size="small" v-if="isSuperAdmin" @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="borrowDialogVisible" title="办理借阅" width="500px">
      <el-form :model="borrowForm" label-width="100px">
        <el-form-item label="选择图书">
          <el-select v-model="borrowForm.bookId" placeholder="请选择图书" filterable style="width: 100%">
            <el-option v-for="book in availableBooks" :key="book.id" :label="book.bookName + ' (可借:' + book.availableCount + ')'" :value="book.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="借阅用户">
          <el-select v-model="borrowForm.userId" placeholder="请选择用户" filterable style="width: 100%">
            <el-option v-for="user in userList" :key="user.id" :label="user.username + ' - ' + user.realName" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="应还日期">
          <el-date-picker v-model="borrowForm.dueDate" type="date" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="borrowForm.remark" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBorrow">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑借阅记录" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="选择图书">
          <el-select v-model="editForm.bookId" placeholder="请选择图书" filterable style="width: 100%">
            <el-option v-for="book in allBooks" :key="book.id" :label="book.bookName" :value="book.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="借阅用户">
          <el-select v-model="editForm.userId" placeholder="请选择用户" filterable style="width: 100%">
            <el-option v-for="user in userList" :key="user.id" :label="user.username + ' - ' + user.realName" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="应还日期">
          <el-date-picker v-model="editForm.dueDate" type="date" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { distributeAPI, bookAPI, userAPI } from '../api/supabase'

const recordList = ref([])
const availableBooks = ref([])
const allBooks = ref([])
const userList = ref([])
const borrowDialogVisible = ref(false)
const editDialogVisible = ref(false)

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const isAdmin = computed(() => {
  return userInfo.roles?.some(r => r.roleCode === 'SUPER_ADMIN' || r.roleCode === 'LIBRARIAN')
})

const isSuperAdmin = computed(() => {
  return userInfo.roles?.some(r => r.roleCode === 'SUPER_ADMIN')
})

const borrowForm = ref({
  bookId: null,
  userId: null,
  dueDate: '',
  remark: ''
})

const editForm = ref({
  id: null,
  bookId: null,
  userId: null,
  dueDate: '',
  remark: ''
})

const loadRecords = async () => {
  try {
    const records = await distributeAPI.list({})
    recordList.value = records
  } catch (error) {
    console.error('获取借阅记录失败:', error)
  }
}

const loadBooks = async () => {
  try {
    const books = await bookAPI.list({ status: 1 })
    allBooks.value = books
    availableBooks.value = books.filter(b => b.available_count > 0)
  } catch (error) {
    console.error('获取图书列表失败:', error)
  }
}

const loadUsers = async () => {
  try {
    const users = await userAPI.list()
    userList.value = users.filter(u => u.status === 1)
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const getStatusType = (status) => {
  const types = { BORROWING: 'primary', RETURNED: 'success', OVERDUE: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { BORROWING: '借阅中', RETURNED: '已归还', OVERDUE: '已逾期' }
  return texts[status] || status
}

const handleBorrow = () => {
  borrowForm.value = { bookId: null, userId: null, dueDate: '', remark: '' }
  borrowDialogVisible.value = true
}

const submitBorrow = async () => {
  try {
    await distributeAPI.borrow(borrowForm.value)
    borrowDialogVisible.value = false
    await loadRecords()
    await loadBooks()
  } catch (error) {
    console.error('办理借阅失败:', error)
  }
}

const handleEdit = (row) => {
  editForm.value = {
    id: row.id,
    bookId: row.bookId,
    userId: row.userId,
    dueDate: row.dueDate ? new Date(row.dueDate).toISOString().slice(0, 16).replace('T', ' ') : '',
    remark: row.remark || ''
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    await distributeAPI.update(editForm.value)
    editDialogVisible.value = false
    await loadRecords()
  } catch (error) {
    console.error('编辑借阅记录失败:', error)
  }
}

const handleReturn = async (id) => {
  try {
    await ElMessageBox.confirm('确认归还该图书？', '提示', { type: 'info' })
    await distributeAPI.returnBook(id, '')
    await loadRecords()
    await loadBooks()
  } catch (error) {
    if (error !== 'cancel') console.error('归还图书失败:', error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    await distributeAPI.delete(id)
    await loadRecords()
  } catch (error) {
    if (error !== 'cancel') console.error('删除记录失败:', error)
  }
}

onMounted(() => {
  loadRecords()
  loadBooks()
  if (isAdmin.value) {
    loadUsers()
  }
})
</script>

<style scoped>
.distribute-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
