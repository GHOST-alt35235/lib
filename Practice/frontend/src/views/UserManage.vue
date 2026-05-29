<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </template>
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role.id" size="small" style="margin-right: 5px">
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="form.id" />
        </el-form-item>
        <el-form-item label="密码" v-if="!form.id">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
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
import { userAPI, roleAPI } from '../api/supabase'

const userList = ref([])
const roleList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const selectedRoles = ref([])

const form = ref({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  status: 1,
  roleIds: []
})

const loadUsers = async () => {
  try {
    const users = await userAPI.list()
    userList.value = users
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

const loadRoles = async () => {
  try {
    const roles = await roleAPI.list()
    roleList.value = roles
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

const handleAdd = () => {
  form.value = {
    id: null,
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    status: 1,
    roleIds: []
  }
  selectedRoles.value = []
  dialogTitle.value = '添加用户'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row, password: '', roleIds: [] }
  selectedRoles.value = row.roles.map(r => r.id)
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await userAPI.delete(id)
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    form.value.roleIds = selectedRoles.value
    if (form.value.id) {
      await userAPI.update(form.value)
    } else {
      await userAPI.add(form.value)
    }
    dialogVisible.value = false
    await loadUsers()
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
