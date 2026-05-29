import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

instance.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200 && res.code !== 201) {
      if (res.code === 401) {
        ElMessage.error('未登录或登录已过期，请重新登录')
        router.push('/login')
        return Promise.reject(new Error(res.msg || '未登录'))
      }
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    if (res.msg && response.config.showMsg !== false) {
      ElMessage.success(res.msg)
    }
    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 400: ElMessage.error('请求参数错误'); break
        case 401: ElMessage.error('未授权，请重新登录'); router.push('/login'); break
        case 403: ElMessage.error('拒绝访问'); break
        case 404: ElMessage.error('请求的资源不存在'); break
        case 500: ElMessage.error('服务器内部错误'); break
        default: ElMessage.error('请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default instance

export const authAPI = {
  login: (data) => instance.post('/auth/login', data, { showMsg: false }),
  register: (data) => instance.post('/auth/register', data),
  logout: () => instance.post('/auth/logout'),
  getUserInfo: () => instance.get('/auth/user-info'),
  getRoleList: () => instance.get('/auth/role/list')
}

export const userAPI = {
  list: () => instance.get('/user/list'),
  getById: (id) => instance.get(`/user/${id}`),
  add: (data) => instance.post('/user/add', data),
  update: (data) => instance.put('/user/update', data),
  delete: (id) => instance.delete(`/user/${id}`),
  assignRoles: (userId, roleIds) => instance.put(`/user/roles/${userId}`, roleIds)
}

export const roleAPI = {
  list: () => instance.get('/role/list'),
  getById: (id) => instance.get(`/role/${id}`),
  add: (data) => instance.post('/role/add', data),
  update: (data) => instance.put('/role/update', data),
  delete: (id) => instance.delete(`/role/${id}`)
}

export const categoryAPI = {
  list: () => instance.get('/category/list'),
  tree: () => instance.get('/category/tree'),
  getById: (id) => instance.get(`/category/${id}`),
  add: (data) => instance.post('/category/add', data),
  update: (data) => instance.put('/category/update', data),
  delete: (id) => instance.delete(`/category/${id}`)
}

export const bookAPI = {
  list: (params) => instance.get('/book/list', { params }),
  getById: (id) => instance.get(`/book/${id}`),
  add: (data) => instance.post('/book/add', data),
  update: (data) => instance.put('/book/update', data),
  delete: (id) => instance.delete(`/book/${id}`),
  updateStatus: (id, status) => instance.put(`/book/status/${id}?status=${status}`)
}

export const distributeAPI = {
  list: (params) => instance.get('/distribute/list', { params }),
  getById: (id) => instance.get(`/distribute/${id}`),
  borrow: (data) => instance.post('/distribute/borrow', data),
  returnBook: (id, remark) => instance.put(`/distribute/return/${id}?remark=${remark || ''}`),
  update: (data) => instance.put('/distribute/update', data),
  delete: (id) => instance.delete(`/distribute/${id}`),
  myBorrow: () => instance.get('/distribute/my-borrow')
}

export const uploadAPI = {
  image: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return instance.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const statisticsAPI = {
  borrowRate: () => instance.get('/statistics/borrow-rate'),
  categoryStats: () => instance.get('/statistics/category-stats'),
  monthlyBorrow: () => instance.get('/statistics/monthly-borrow'),
  statusDistribution: () => instance.get('/statistics/status-distribution'),
  topBorrowedBooks: () => instance.get('/statistics/top-borrowed-books')
}
