<template>
  <div class="dashboard-container">
    <div class="welcome-card modern-card">
      <div class="welcome-content">
        <h1 class="greeting">您好，{{ adminName || '管理员' }}</h1>
        <p class="subtitle">
          欢迎回到 {{ projectTitle }} 后台管理系统。当前共有
          <strong>{{ activeAlertCount }}</strong>
          条待跟进预警，请优先处理严重告警和冷链异常。
        </p>
      </div>
      <div class="welcome-img">
        <img src="https://img.icons8.com/clouds/200/000000/manager.png" alt="welcome" />
      </div>
    </div>

    <el-row :gutter="24" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statistics" :key="stat.label">
        <div class="stat-card modern-card" :class="'stat-' + stat.type">
          <div class="stat-icon">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-desc">{{ stat.desc }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="16">
        <div class="chart-card modern-card">
          <div class="card-header">
            <h3 class="card-title">冷链预警趋势概览</h3>
            <el-radio-group v-model="timeRange" size="small">
              <el-radio-button label="7天" />
              <el-radio-button label="30天" />
            </el-radio-group>
          </div>
          <div class="chart-placeholder">
            <div v-show="chartSeries.values.length" ref="chartRef" class="echarts-view"></div>
            <el-empty v-if="!chartSeries.values.length" description="暂无预警数据" />
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="activity-card modern-card">
          <div class="card-header">
            <h3 class="card-title">最近预警动态</h3>
          </div>
          <el-timeline v-if="activities.length" class="modern-timeline">
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :type="activity.type"
              :timestamp="activity.timestamp"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无动态" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { Bell, OfficeBuilding, ShoppingCart, User } from '@element-plus/icons-vue'
import { computed, getCurrentInstance, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

const { proxy } = getCurrentInstance()

const adminName = ref(proxy.$storage.get('adminName'))
const projectTitle = ref(proxy.$project.projectName)
const timeRange = ref('7天')

const orderTotal = ref(0)
const warehouseTotal = ref(0)
const userTotal = ref(0)
const activeAlertCount = ref(0)
const alertRecords = ref([])
const chartRef = ref(null)
const chartSeries = ref({
  labels: [],
  values: [],
})

let chartInstance = null

const statistics = computed(() => [
  {
    label: '订单总量',
    value: formatNumber(orderTotal.value),
    icon: ShoppingCart,
    type: 'primary',
    desc: '系统累计订单记录',
  },
  {
    label: '仓库总数',
    value: formatNumber(warehouseTotal.value),
    icon: OfficeBuilding,
    type: 'success',
    desc: '当前可用冷链仓节点',
  },
  {
    label: '用户总数',
    value: formatNumber(userTotal.value),
    icon: User,
    type: 'warning',
    desc: '平台注册有效用户',
  },
  {
    label: '活动预警',
    value: formatNumber(activeAlertCount.value),
    icon: Bell,
    type: 'danger',
    desc: '待处理与处理中预警',
  },
])

const activities = computed(() =>
  alertRecords.value.slice(0, 6).map((item) => ({
    content: `${item.jiankongyujingName} · ${item.jiankongObject} · ${item.yujingStatusValue}`,
    timestamp: formatTime(item.jiankongTime),
    type: timelineType(item.yujingStatusTypes, item.yujingLevelTypes),
  })),
)

watch(timeRange, () => {
  buildChartData()
})

watch(
  () => chartSeries.value,
  async () => {
    await nextTick()
    renderChart()
  },
  { deep: true },
)

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  await loadDashboardData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

async function loadDashboardData() {
  try {
    const [orderRes, warehouseRes, userRes, alertPendingRes, alertProcessingRes, alertListRes] = await Promise.all([
      proxy.$http({ url: 'shangpinOrder/page', method: 'get', params: { page: 1, limit: 1, sort: 'id', order: 'desc' } }),
      proxy.$http({ url: 'cangku/page', method: 'get', params: { page: 1, limit: 1, sort: 'id', order: 'desc', cangkuDelete: 1 } }),
      proxy.$http({ url: 'yonghu/page', method: 'get', params: { page: 1, limit: 1, sort: 'id', order: 'desc', yonghuDelete: 1 } }),
      proxy.$http({ url: 'jiankongyujing/page', method: 'get', params: { page: 1, limit: 1, sort: 'id', order: 'desc', yujingStatusTypes: 1 } }),
      proxy.$http({ url: 'jiankongyujing/page', method: 'get', params: { page: 1, limit: 1, sort: 'id', order: 'desc', yujingStatusTypes: 2 } }),
      proxy.$http({ url: 'jiankongyujing/page', method: 'get', params: { page: 1, limit: 200, sort: 'id', order: 'desc' } }),
    ])

    orderTotal.value = readTotal(orderRes)
    warehouseTotal.value = readTotal(warehouseRes)
    userTotal.value = readTotal(userRes)
    activeAlertCount.value = readTotal(alertPendingRes) + readTotal(alertProcessingRes)
    alertRecords.value = readList(alertListRes)
      .filter((item) => item.jiankongTime)
      .sort((a, b) => new Date(b.jiankongTime) - new Date(a.jiankongTime))

    buildChartData()
  } catch (error) {
    proxy.$message.error('首页数据加载失败')
  }
}

function buildChartData() {
  const days = timeRange.value === '30天' ? 30 : 7
  const labels = []
  const countMap = new Map()

  for (let i = days - 1; i >= 0; i -= 1) {
    const current = new Date()
    current.setHours(0, 0, 0, 0)
    current.setDate(current.getDate() - i)
    const key = dateKey(current)
    labels.push(key)
    countMap.set(key, 0)
  }

  alertRecords.value.forEach((item) => {
    const key = dateKey(new Date(item.jiankongTime))
    if (countMap.has(key)) {
      countMap.set(key, countMap.get(key) + 1)
    }
  })

  chartSeries.value = {
    labels: labels.map((label) => label.slice(5)),
    values: labels.map((label) => countMap.get(label) || 0),
  }
}

function renderChart() {
  if (!chartRef.value || !chartSeries.value.values.length) {
    if (chartInstance) {
      chartInstance.dispose()
      chartInstance = null
    }
    return
  }

  if (!chartInstance) {
    chartInstance = proxy.$echarts.init(chartRef.value)
  }

  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
    },
    grid: {
      left: '4%',
      right: '4%',
      top: 32,
      bottom: 24,
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: chartSeries.value.labels,
      axisLine: {
        lineStyle: {
          color: '#cbd5e1',
        },
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#64748b',
      },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: '#e2e8f0',
        },
      },
      axisLabel: {
        color: '#64748b',
      },
    },
    series: [
      {
        name: '预警次数',
        type: 'bar',
        barWidth: timeRange.value === '30天' ? '38%' : '48%',
        data: chartSeries.value.values,
        itemStyle: {
          borderRadius: [10, 10, 0, 0],
          color: new proxy.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#60a5fa' },
            { offset: 1, color: '#2563eb' },
          ]),
        },
      },
      {
        name: '趋势线',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: chartSeries.value.values,
        lineStyle: {
          color: '#ef4444',
          width: 3,
        },
        itemStyle: {
          color: '#ef4444',
        },
      },
    ],
  })
}

function handleResize() {
  if (chartInstance) {
    chartInstance.resize()
  }
}

function readTotal(response) {
  return response?.data?.data?.total || 0
}

function readList(response) {
  return response?.data?.data?.list || []
}

function formatNumber(value) {
  return Number(value || 0).toLocaleString('zh-CN')
}

function formatTime(value) {
  if (!value) return ''
  const date = new Date(value)
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hours = `${date.getHours()}`.padStart(2, '0')
  const minutes = `${date.getMinutes()}`.padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

function dateKey(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function timelineType(status, level) {
  if (status === 3) return 'success'
  if (level === 3) return 'danger'
  if (status === 2 || level === 2) return 'warning'
  return 'primary'
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modern-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  padding: 24px;
  margin-bottom: 24px;
}

.welcome-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #134e4a 0%, #0f766e 100%);
  color: #fff;
  border: none;
  overflow: hidden;
  position: relative;
  min-height: 200px;
  padding: 40px;

  &::before {
    content: '';
    position: absolute;
    top: -50px;
    right: -50px;
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    border-radius: 50%;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30px;
    left: 20%;
    width: 120px;
    height: 120px;
    background: radial-gradient(circle, rgba(255,255,255,0.05) 0%, transparent 70%);
    border-radius: 50%;
  }

  .welcome-content {
    z-index: 2;
    max-width: 70%;

    .greeting {
      font-size: 32px;
      font-weight: 800;
      margin-bottom: 16px;
      letter-spacing: -0.5px;
      text-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .subtitle {
      font-size: 17px;
      opacity: 0.95;
      line-height: 1.6;
      color: #ccfbf1;

      strong {
        font-size: 24px;
        margin: 0 6px;
        color: #fff;
        text-decoration: underline rgba(255,255,255,0.3) 2px;
        text-underline-offset: 4px;
      }
    }
  }

  .welcome-img {
    height: 160px;
    z-index: 2;
    transform: translateY(10px);

    img {
      height: 100%;
      filter: drop-shadow(0 20px 30px rgba(0, 0, 0, 0.3));
      animation: float 4s ease-in-out infinite;
    }
  }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
  }

  .stat-info {
    .stat-label {
      font-size: 14px;
      color: #64748b;
      margin-bottom: 4px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: 700;
      color: #1e293b;
    }

    .stat-desc {
      margin-top: 6px;
      font-size: 12px;
      color: #94a3b8;
    }
  }
}

.stat-primary .stat-icon {
  background: #eff6ff;
  color: #3b82f6;
}

.stat-success .stat-icon {
  background: #ecfdf5;
  color: #10b981;
}

.stat-warning .stat-icon {
  background: #fffbeb;
  color: #f59e0b;
}

.stat-danger .stat-icon {
  background: #fef2f2;
  color: #ef4444;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .card-title {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
  }
}

.chart-placeholder {
  min-height: 320px;
}

.echarts-view {
  width: 100%;
  height: 320px;
}

.modern-timeline {
  margin-top: 10px;

  :deep(.el-timeline-item__content) {
    color: #475569;
    font-weight: 500;
    line-height: 1.7;
  }
}
</style>
