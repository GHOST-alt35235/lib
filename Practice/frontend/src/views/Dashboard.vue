<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card blue">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.bookCount }}</div>
              <div class="stat-label">图书总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card green">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.borrowingCount }}</div>
              <div class="stat-label">借阅中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card orange">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card purple">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.categoryCount }}</div>
              <div class="stat-label">分类数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 欢迎卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>欢迎回来，{{ userInfo.realName || userInfo.username }}！</h2>
              <p>今天是个阅读的好日子，让我们一起畅游书海吧～</p>
            </div>
            <div class="welcome-illustration">
              <el-icon :size="80" color="#667eea"><Reading /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据可视化图表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>借阅趋势分析</span>
            </div>
          </template>
          <div ref="monthlyChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>图书分类分布</span>
            </div>
          </template>
          <div class="category-table-container">
            <el-table :data="categoryData" style="width: 100%" :show-header="false">
              <el-table-column label="分类" width="120">
                <template #default="{ row }">
                  <span class="category-name">{{ row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="80" align="right">
                <template #default="{ row }">{{ row.count }} 本</template>
              </el-table-column>
              <el-table-column label="占比">
                <template #default="{ row }">
                  <el-progress :percentage="getCategoryPercentage(row.count)" :stroke-width="12" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 借阅状态分布和借阅率 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>借阅状态分布</span>
            </div>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>借阅率统计</span>
            </div>
          </template>
          <div class="borrow-rate-container">
            <div class="rate-circle">
              <div class="rate-value">{{ borrowRate.toFixed(1) }}%</div>
              <div class="rate-label">借阅率</div>
            </div>
            <div class="rate-info">
              <div class="info-item">
                <span class="info-label">总图书数</span>
                <span class="info-value">{{ borrowStats.totalBooks }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">借阅中</span>
                <span class="info-value">{{ borrowStats.borrowedBooks }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">馆藏率</span>
                <span class="info-value">{{ (100 - borrowRate).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门图书和最近借阅 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/book-manage')">
              <el-icon :size="32" color="#409eff"><Search /></el-icon>
              <span>浏览图书</span>
            </div>
            <div class="action-item" @click="$router.push('/my-borrow')">
              <el-icon :size="32" color="#67c23a"><Tickets /></el-icon>
              <span>我的借阅</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>热门借阅图书 TOP10</span>
              <el-button type="primary" text @click="$router.push('/book-manage')">查看更多 →</el-button>
            </div>
          </template>
          <el-table :data="topBooks" style="width: 100%" :show-header="false">
            <el-table-column width="60" align="center">
              <template #default="{ $index }">
                <span class="rank" :class="'rank-' + ($index + 1)">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="书名">
              <template #default="{ row }">{{ row.book_name }}</template>
            </el-table-column>
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column width="80" align="center">
              <template #default="{ row }">
                <el-tag type="info" size="small">{{ row.borrowCount || 0 }} 次</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近借阅记录 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>最近借阅</span>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%">
            <el-table-column label="图书">
              <template #default="{ row }">{{ row.books?.book_name || row.book_name || '-' }}</template>
            </el-table-column>
            <el-table-column label="借阅日期" width="160">
              <template #default="{ row }">{{ formatDate(row.borrow_date) }}</template>
            </el-table-column>
            <el-table-column label="应还日期" width="160">
              <template #default="{ row }">{{ formatDate(row.due_date) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { bookAPI, distributeAPI, categoryAPI, userAPI, statisticsAPI } from '../api/supabase'
import { Document, Tickets, User, Folder, Reading, Search } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const stats = ref({
  bookCount: 0,
  borrowingCount: 0,
  userCount: 0,
  categoryCount: 0
})

const borrowRate = ref(0)
const borrowStats = ref({
  totalBooks: 0,
  borrowedBooks: 0
})

const hotBooks = ref([])
const topBooks = ref([])
const recentRecords = ref([])
const monthlyData = ref([])
const categoryData = ref([])
const statusData = ref({ borrowing: 0, returned: 0, overdue: 0 })

let monthlyChart = null
let categoryChart = null
let statusChart = null

const monthlyChartRef = ref(null)
const categoryChartRef = ref(null)
const statusChartRef = ref(null)

const hasRole = (roleCode) => {
  return userInfo.value.roles?.some(role => role.role_code === roleCode)
}

const loadStats = async () => {
  try {
    const promises = [
      bookAPI.list({}),
      categoryAPI.list(),
      statisticsAPI.borrowRate(),
      statisticsAPI.monthlyBorrow(),
      statisticsAPI.categoryStats(),
      statisticsAPI.statusStats()
    ]

    if (hasRole('SUPER_ADMIN') || hasRole('LIBRARIAN')) {
      promises.push(userAPI.list())
      promises.push(distributeAPI.list({}))
    }

    const results = await Promise.all(promises)

    console.log('API返回结果:', results)

    const books = results[0] || []
    const categories = results[1] || []
    
    stats.value.bookCount = books.length || 0
    stats.value.categoryCount = categories.length || 0
    
    const borrowRateData = results[2] || {}
    borrowRate.value = borrowRateData.rate || 0
    borrowStats.value.totalBooks = borrowRateData.total || 0
    borrowStats.value.borrowedBooks = borrowRateData.borrowed || 0

    monthlyData.value = results[3] || []
    categoryData.value = results[4] || []
    statusData.value = results[5] || { borrowing: 0, returned: 0, overdue: 0 }

    topBooks.value = books.slice(0, 5) || []
    hotBooks.value = books.slice(0, 5) || []

    console.log('月度数据:', monthlyData.value)
    console.log('分类数据:', categoryData.value)
    console.log('状态数据:', statusData.value)

    if (results.length > 6) {
      const users = results[6] || []
      const records = results[7] || []
      stats.value.userCount = users.length || 0
      stats.value.borrowingCount = records.filter(r => r.status === 'BORROWING').length || 0
      recentRecords.value = records.slice(0, 5)
    } else {
      stats.value.userCount = '-'
      stats.value.borrowingCount = '-'
      recentRecords.value = []
    }

    // 等待DOM更新后再初始化图表
    await nextTick()
    // 再次等待确保容器尺寸计算完成
    setTimeout(() => {
      console.log('开始初始化图表')
      console.log('月度图表容器:', monthlyChartRef.value)
      console.log('分类图表容器:', categoryChartRef.value)
      console.log('状态图表容器:', statusChartRef.value)
      initCharts()
    }, 100)
    
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const initCharts = () => {
  try {
    console.log('=== 开始初始化图表 ===')
    
    // 清理旧实例
    if (monthlyChart && monthlyChart.dispose) {
      monthlyChart.dispose()
      monthlyChart = null
    }
    if (categoryChart && categoryChart.dispose) {
      categoryChart.dispose()
      categoryChart = null
    }
    if (statusChart && statusChart.dispose) {
      statusChart.dispose()
      statusChart = null
    }

    // 月度借阅趋势图
    if (monthlyChartRef.value) {
      console.log('月度图表容器尺寸:', {
        clientWidth: monthlyChartRef.value.clientWidth,
        clientHeight: monthlyChartRef.value.clientHeight
      })
      
      monthlyChart = echarts.init(monthlyChartRef.value)
      console.log('月度图表数据:', monthlyData.value)
      
      monthlyChart.setOption({
        title: {
          text: '月度借阅量',
          left: 'center',
          textStyle: { fontSize: 14, color: '#666' }
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c} 次'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: monthlyData.value.map(item => item.month)
        },
        yAxis: {
          type: 'value',
          axisLabel: { formatter: '{value} 次' }
        },
        series: [{
          data: monthlyData.value.map(item => item.count),
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
            ])
          },
          itemStyle: { color: '#667eea' }
        }]
      })
      monthlyChart.resize()
      console.log('月度图表初始化完成')
    } else {
      console.log('月度图表容器不存在')
    }

    // 图书分类分布图
    if (categoryChartRef.value) {
      console.log('分类图表容器尺寸:', {
        clientWidth: categoryChartRef.value.clientWidth,
        clientHeight: categoryChartRef.value.clientHeight
      })
      
      categoryChart = echarts.init(categoryChartRef.value)
      console.log('分类图表数据:', categoryData.value)
      
      categoryChart.setOption({
        title: {
          text: '图书分类分布',
          left: 'center',
          textStyle: { fontSize: 14, color: '#666' }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} 本 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 18,
              fontWeight: 'bold'
            }
          },
          labelLine: { show: false },
          data: categoryData.value
        }],
        color: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe', '#11998e', '#38ef7d']
      })
      categoryChart.resize()
      console.log('分类图表初始化完成')
    } else {
      console.log('分类图表容器不存在')
    }

    // 借阅状态分布图
    if (statusChartRef.value) {
      console.log('状态图表容器尺寸:', {
        clientWidth: statusChartRef.value.clientWidth,
        clientHeight: statusChartRef.value.clientHeight
      })
      
      statusChart = echarts.init(statusChartRef.value)
      console.log('状态图表数据:', statusData.value)
      
      statusChart.setOption({
        title: {
          text: '借阅状态分布',
          left: 'center',
          textStyle: { fontSize: 14, color: '#666' }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['借阅中', '已归还', '已逾期']
        },
        yAxis: {
          type: 'value',
          axisLabel: { formatter: '{value} 本' }
        },
        series: [{
          type: 'bar',
          data: [
            { value: statusData.value.borrowing, itemStyle: { color: '#409eff' } },
            { value: statusData.value.returned, itemStyle: { color: '#67c23a' } },
            { value: statusData.value.overdue, itemStyle: { color: '#f56c6c' } }
          ],
          barWidth: '50%',
          borderRadius: [6, 6, 0, 0]
        }]
      })
      statusChart.resize()
      console.log('状态图表初始化完成')
    } else {
      console.log('状态图表容器不存在')
    }
    
    console.log('=== 图表初始化完成 ===')
  } catch (error) {
    console.error('图表初始化失败:', error)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const getStatusType = (status) => {
  const types = { BORROWING: 'primary', RETURNED: 'success', OVERDUE: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { BORROWING: '借阅中', RETURNED: '已归还', OVERDUE: '已逾期' }
  return texts[status] || status
}

const getCategoryPercentage = (count) => {
  const total = categoryData.value.reduce((sum, item) => sum + item.count, 0)
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

const handleResize = () => {
  monthlyChart?.resize()
  categoryChart?.resize()
  statusChart?.resize()
}

onMounted(() => {
  loadStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  monthlyChart?.dispose()
  categoryChart?.dispose()
  statusChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  border-radius: 12px;
  overflow: hidden;
}

.stat-card.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.green {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-card.orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.purple {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-content {
  display: flex;
  align-items: center;
  color: white;
}

.stat-icon {
  width: 70px;
  height: 70px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon .el-icon {
  font-size: 36px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 4px;
}

.welcome-card {
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.welcome-illustration {
  opacity: 0.8;
}

.section-card {
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

.chart-container {
  height: 280px;
  width: 100%;
  min-height: 280px;
}

.borrow-rate-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 60px;
  padding: 20px 0;
}

.rate-circle {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: conic-gradient(#667eea 0deg, #667eea var(--rate, 45deg), #e8ecf0 var(--rate, 45deg));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.rate-circle::before {
  content: '';
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: #fff;
}

.rate-value {
  position: relative;
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
  z-index: 1;
}

.rate-label {
  position: relative;
  font-size: 14px;
  color: #999;
  z-index: 1;
  margin-top: 4px;
}

.rate-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  min-width: 150px;
}

.info-label {
  color: #909399;
  font-size: 14px;
}

.info-value {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  background: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #e6f7ff;
  transform: translateY(-2px);
}

.action-item span {
  margin-top: 8px;
  font-size: 14px;
  color: #303133;
}

.category-table-container {
  padding: 10px;
}

.category-name {
  font-weight: 500;
  color: #303133;
}

.rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-weight: 600;
  font-size: 14px;
  background: #f0f2f5;
  color: #909399;
}

.rank-1 {
  background: #ffd700;
  color: white;
}

.rank-2 {
  background: #c0c0c0;
  color: white;
}

.rank-3 {
  background: #cd7f32;
  color: white;
}
</style>
