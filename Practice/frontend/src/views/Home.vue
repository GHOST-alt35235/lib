<template>
  <div class="home-container">
    <el-container>
      <el-aside width="220px">
        <div class="logo">
          <el-icon><Reading /></el-icon>
          <span>图书管理系统</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          class="el-menu-vertical"
          background-color="#1f2937"
          text-color="#9ca3af"
          active-text-color="#fff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/book-manage" v-if="hasRole('SUPER_ADMIN') || hasRole('LIBRARIAN') || hasRole('TEACHER')">
            <el-icon><Document /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/my-borrow">
            <el-icon><Tickets /></el-icon>
            <span>我的借阅</span>
          </el-menu-item>
          <el-menu-item index="/category-manage" v-if="hasRole('LIBRARIAN') || hasRole('SUPER_ADMIN')">
            <el-icon><Folder /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/distribute-manage" v-if="hasRole('LIBRARIAN') || hasRole('SUPER_ADMIN')">
            <el-icon><RefreshLeft /></el-icon>
            <span>借阅管理</span>
          </el-menu-item>
          <el-menu-item index="/user-manage" v-if="hasRole('SUPER_ADMIN')">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-content">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentPageTitle">{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
            <div class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realName?.[0] || 'U' }}</el-avatar>
              <span class="username">{{ userInfo.realName || userInfo.username }}</span>
              <el-dropdown @command="handleCommand">
                <el-button text type="info" style="margin-left: 10px;">
                  <el-icon><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="logout">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authAPI } from '../api/axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { House, Document, Tickets, Folder, RefreshLeft, User, ArrowDown, SwitchButton, Reading } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const userInfo = ref({
  username: '',
  realName: '',
  roles: []
})

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  const titles = {
    '/book-manage': '图书管理',
    '/my-borrow': '我的借阅',
    '/category-manage': '分类管理',
    '/distribute-manage': '借阅管理',
    '/user-manage': '用户管理'
  }
  return titles[route.path]
})

const hasRole = (roleCode) => {
  return userInfo.value.roles?.some(role => role.roleCode === roleCode)
}

const loadUserInfo = async () => {
  try {
    const response = await authAPI.getUserInfo()
    userInfo.value = response.data.data
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        type: 'warning'
      })
      await authAPI.logout()
      localStorage.removeItem('userInfo')
      router.push('/login')
    } catch (error) {
      if (error !== 'cancel') {
        console.error('退出登录失败:', error)
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
    }
  }
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
  loadUserInfo()
})
</script>

<style scoped>
.home-container {
  height: 100vh;
  background-color: #f3f4f6;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #1f2937;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  background-color: #111827;
}

.logo .el-icon {
  font-size: 24px;
}

.el-menu {
  border: none;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.username {
  margin-left: 12px;
  color: #1f2937;
  font-weight: 500;
}

.el-main {
  background-color: #f3f4f6;
  padding: 24px;
  overflow-y: auto;
}
</style>
