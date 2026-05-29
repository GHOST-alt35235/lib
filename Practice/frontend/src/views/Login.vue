<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="48"><Reading /></el-icon>
        </div>
        <h1>图书管理系统</h1>
        <p>欢迎回来，请登录您的账号</p>
      </div>
      <el-form :model="form" label-width="0" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-button type="primary" text @click="goToRegister">立即注册</el-button>
        </div>
      </el-form>
      <div class="demo-accounts">
        <p>测试账号：</p>
        <div class="account-item">
          <span class="role">管理员：</span>
          <span class="account">admin / 123456</span>
        </div>
        <div class="account-item">
          <span class="role">图书管理员：</span>
          <span class="account">lib1 / 123456</span>
        </div>
        <div class="account-item">
          <span class="role">教师：</span>
          <span class="account">teacher1 / 123456</span>
        </div>
        <div class="account-item">
          <span class="role">学生：</span>
          <span class="account">student1 / 123456</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../api/axios'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const response = await axios.post('/auth/login', form, { showMsg: false })
    if (response.data.code === 200) {
      localStorage.setItem('userInfo', JSON.stringify(response.data.data))
      ElMessage.success('登录成功！')
      router.push('/dashboard')
    } else {
      ElMessage.error(response.data.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  color: white;
  margin-bottom: 16px;
}

.login-header h1 {
  margin: 0;
  font-size: 28px;
  color: #1f2937;
  font-weight: 600;
}

.login-header p {
  margin: 8px 0 0 0;
  color: #6b7280;
  font-size: 14px;
}

.login-form {
  margin-top: 32px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-btn:hover {
  opacity: 0.9;
}

.login-footer {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

.demo-accounts {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

.demo-accounts p {
  margin: 0 0 12px 0;
  color: #9ca3af;
  font-size: 13px;
}

.account-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #374151;
  margin-bottom: 6px;
}

.account-item .role {
  color: #667eea;
  font-weight: 500;
  min-width: 60px;
}

.account-item .account {
  background: #f3f4f6;
  padding: 2px 10px;
  border-radius: 4px;
  font-family: monospace;
}
</style>
