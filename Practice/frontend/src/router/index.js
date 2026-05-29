import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import UserManage from '../views/UserManage.vue'
import CategoryManage from '../views/CategoryManage.vue'
import BookManage from '../views/BookManage.vue'
import DistributeManage from '../views/DistributeManage.vue'
import MyBorrow from '../views/MyBorrow.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/home',
    component: Home,
    redirect: '/dashboard',
    children: [
      { path: '/dashboard', component: Dashboard },
      { path: '/user-manage', component: UserManage, meta: { roles: ['SUPER_ADMIN'] } },
      { path: '/category-manage', component: CategoryManage, meta: { roles: ['SUPER_ADMIN', 'LIBRARIAN'] } },
      { path: '/book-manage', component: BookManage, meta: { roles: ['SUPER_ADMIN', 'LIBRARIAN', 'TEACHER'] } },
      { path: '/distribute-manage', component: DistributeManage, meta: { roles: ['SUPER_ADMIN', 'LIBRARIAN'] } },
      { path: '/my-borrow', component: MyBorrow }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userInfoStr = localStorage.getItem('userInfo')
  
  if (!userInfoStr && to.path !== '/login' && to.path !== '/register') {
    next('/login')
    return
  }
  
  if (userInfoStr && to.meta.roles && to.meta.roles.length > 0) {
    const userInfo = JSON.parse(userInfoStr)
    const userRoles = userInfo.roles?.map(r => r.roleCode) || []
    const hasPermission = to.meta.roles.some(role => userRoles.includes(role))
    
    if (!hasPermission) {
      ElMessage.error('您没有权限访问该页面')
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router
