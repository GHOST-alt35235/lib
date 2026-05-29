<template>
  <div class="my-borrow">
    <el-card>
      <template #header>
        <span>我的借阅</span>
      </template>
      <el-table :data="recordList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bookName" label="图书" />
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
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { distributeAPI } from '../api/supabase'

const recordList = ref([])

const loadRecords = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const records = await distributeAPI.myBorrow(userInfo.id)
    recordList.value = records
  } catch (error) {
    console.error('获取借阅记录失败:', error)
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

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.my-borrow {
  padding: 20px;
}
</style>
